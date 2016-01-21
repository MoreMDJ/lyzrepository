package com.ynyes.lyz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdSubdistrict;
import com.ynyes.lyz.entity.TdUser;

@Service
public class TdPriceCountService {

	@Autowired
	private TdCouponService tdCouponService;

	@Autowired
	private TdPayTypeService tdPayTypeService;

	@Autowired
	private TdSubdistrictService tdSubDistrictService;

	@Autowired
	private TdUserService tdUserService;

	/**
	 * 计算订单价格和能使用的最大的预存款的方法
	 * 
	 * @author dengxiao
	 */
	public Map<String, Object> countPrice(TdOrder order, TdUser user) {
		// 判断传入的参数是否为空，如果为空就没有计算的必要了
		if (null == order || null == user) {
			return null;
		}

		// 创建一个用于存储最后结果的map
		Map<String, Object> results = new HashMap<>();

		// 创建一个变量用于表示能够使用的最大预存款
		Double max_use = 0.00;

		// 创建一个标识符用于判断能否使用预存款和优惠券
		Boolean canUseBalance = true;

		// 每次重新计算的时候，清空优惠券的使用说明
		order.setCashCoupon(0.00);
		order.setProductCoupon("");

		List<TdOrderGoods> goodsList = order.getOrderGoodsList();
		// 如果订单里面没有商品，则也没有计算的必要
		if (null == goodsList || goodsList.size() <= 0) {
			return null;
		}

		// 初始化订单金额
		order.setTotalPrice(0.00);
		order.setTotalGoodsPrice(0.00);

		// 开始计算原始商品总额和原始订单金额
		for (TdOrderGoods orderGoods : goodsList) {
			Double total = 0.00;
			if (null != orderGoods) {
				Double price = orderGoods.getPrice();
				Long quantity = orderGoods.getQuantity();
				// 进行空值判定
				if (null == price) {
					price = 0.00;
				}
				if (null == quantity) {
					quantity = 0L;
				}
				total += (price * quantity);
			}
			// 给订单设置属性
			order.setTotalPrice(order.getTotalPrice() + total);
			order.setTotalGoodsPrice(order.getTotalGoodsPrice() + total);
		}

		// 计算订单的运费
		order.setDeliverFee(0.00);

		// 获取订单的收货街道
		List<TdSubdistrict> subDistrict_list = tdSubDistrictService
				.findByDistrictNameAndNameOrderBySortIdAsc(order.getDisctrict(), order.getSubdistrict());
		if (null != subDistrict_list && subDistrict_list.size() > 0) {
			TdSubdistrict subdistrict = subDistrict_list.get(0);
			if (null != subdistrict) {
				Double fee = subdistrict.getDeliveryFee();
				if (null != fee) {
					order.setDeliverFee(fee);
				}
			}
		}

		// 如果订单的配送方式是到店支付，则不计算运费
		String title = order.getDeliverTypeTitle();
		if (null != title && "门店自提".equals(title)) {
			order.setDeliverFee(0.00);
			// 同时判断能否使用券和预存款
			Long payTypeId = order.getPayTypeId();
			if (null != payTypeId) {
				TdPayType type = tdPayTypeService.findOne(payTypeId);
				// 如果支付方式属于线下支付，则不能够使用预存款和券
				if (null != type && null != type.getIsOnlinePay() && !type.getIsOnlinePay()) {
					order.setCashCouponId("");
					order.setProductCouponId("");
					canUseBalance = false;
				}
			}
		}
		// 将运费的费用添加到订单总额中
		order.setTotalPrice(order.getTotalPrice() + order.getDeliverFee());

		// 开始计算使用的现金券的价值
		String cashCouponId = order.getCashCouponId();
		if (null == order.getCashCoupon()) {
			order.setCashCoupon(0.00);
		}
		// 拆分现金券
		if (null != cashCouponId && !"".equals(cashCouponId)) {
			String[] coupons = cashCouponId.split(",");
			if (null != coupons && coupons.length > 0) {
				for (String sCouponId : coupons) {
					Long id = Long.parseLong(sCouponId);
					// 获取该张优惠券实体信息
					TdCoupon coupon = tdCouponService.findOne(id);
					// 如果该张优惠券存在，具有金额，同时没有过期，没有使用，则有效
					if (null != coupon && null != coupon.getPrice() && null != coupon.getIsOutDate()
							&& null != coupon.getIsUsed() && !coupon.getIsOutDate() && !coupon.getIsUsed()) {
						Double price = coupon.getPrice();
						order.setTotalPrice(order.getTotalPrice() - price);
						order.setCashCoupon(order.getCashCoupon() + price);
					}
				}
			}
		}

		// 开始计算产品券产品券的价值
		String productCouponId = order.getProductCouponId();
		// 拆分产品券
		if (null != productCouponId && !"".equals(productCouponId)) {
			String[] coupons = productCouponId.split(",");
			if (null != coupons && coupons.length > 0) {
				for (String sCouponId : coupons) {
					Long id = Long.parseLong(sCouponId);
					// 获取该张优惠券的实体信息
					TdCoupon coupon = tdCouponService.findOne(id);
					// 如果该张优惠券存在，具有指定商品，同时没有过期，没有使用，则有效
					if (null != coupon && null != coupon.getGoodsId() && null != coupon.getIsOutDate()
							&& null != coupon.getIsUsed() && !coupon.getIsOutDate() && !coupon.getIsUsed()) {
						Long goodsId = coupon.getGoodsId();
						// 遍历所有的已选商品，查找与优惠券对应的商品，获取其价格
						for (TdOrderGoods orderGoods : goodsList) {
							if (null != orderGoods) {
								Long the_id = orderGoods.getGoodsId();
								// 如果商品的id相同，则表示找到了指定的商品，可以获得商品的价格
								if (null != the_id && the_id.longValue() == goodsId.longValue()) {
									Double price = orderGoods.getPrice();
									if (null == price) {
										price = 0.00;
									}
									order.setTotalPrice(order.getTotalPrice() - price);
								}
							}
						}
					}
				}
			}
		}

		// 开始计算最大能使用的预存款的额度
		if (canUseBalance) {
			Double balance = user.getBalance();
			if (null == balance) {
				balance = 0.00;
			}
			// 如果预存款小于订单金额，则能够使用的最大预存款额度为用户的预存款
			if (balance < order.getTotalPrice()) {
				max_use = balance;
			}
			// 其他情况则为订单的金额
			else {
				max_use = order.getTotalPrice();
			}
		} else {
			max_use = 0.00;
		}

		results.put("result", order);
		results.put("max", max_use);
		results.put("isCoupon", canUseBalance);

		return results;
	}

	/**
	 * 根据用户的已选，计算每个品牌能够使用的优惠券的额度和已使用的额度
	 * 
	 * @param Double[]的规则为【可使用额度，已使用额度】
	 * 
	 * @author dengxiao
	 */
	public Map<Long, Double[]> getPermit(TdOrder order) {
		// 如果参数为NULL，则没有计算的必要了
		if (null == order) {
			return null;
		}

		Map<Long, Double[]> map = new HashMap<>();

		List<TdOrderGoods> goodsList = order.getOrderGoodsList();

		// 如果订单没有商品，则也没有计算的价值
		if (null == goodsList) {
			return null;
		}

		for (TdOrderGoods orderGoods : goodsList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				if (null != brandId) {
					// 创建一个变量表示该件商品能够使用优惠券的额度
					Double permitCash = 0.00;

					Double price = orderGoods.getPrice();
					if (null == price) {
						price = 0.00;
					}
					Double realPrice = orderGoods.getRealPrice();
					if (null == realPrice) {
						realPrice = 0.00;
					}
					Long quantity = orderGoods.getQuantity();
					if (null == quantity) {
						quantity = 0L;
					}
					Long couponNumber = orderGoods.getCouponNumber();
					if (null == couponNumber) {
						couponNumber = 0L;
					}
					permitCash = (price - realPrice) * (quantity - couponNumber);

					// 获取指定品牌下的【可使用额度，已使用额度】数组
					Double[] permits = map.get(brandId);
					if (null == permits || permits.length != 2) {
						permits = new Double[2];
						permits[0] = 0.00;
						permits[1] = 0.00;
					}
					if (null == permits[0]) {
						permits[0] = 0.00;
					}
					// 计算最大额度
					permits[0] += permitCash;
					map.put(brandId, permits);
				}
			}
		}

		// 计算已使用的现金券额度
		String cashCouponId = order.getCashCouponId();
		if (null != cashCouponId && !"".equals(cashCouponId)) {
			String[] coupons = cashCouponId.split(",");
			if (null != coupons && coupons.length > 0) {
				for (String sCouponId : coupons) {
					if (null != sCouponId) {
						Long id = Long.parseLong(sCouponId);
						if (null != id) {
							// 查找到指定id的现金券
							TdCoupon coupon = tdCouponService.findOne(id);
							if (null != coupon) {
								// 获取优惠券的品牌
								Long brandId = coupon.getBrandId();
								if (null != brandId) {
									// 获取优惠券的价格
									Double price = coupon.getPrice();
									// 获取指定品牌下的【可使用额度，已使用额度】数组
									Double[] permits = map.get(brandId);
									if (null == permits || permits.length != 2) {
										permits = new Double[2];
										permits[0] = 0.00;
										permits[1] = 0.00;
									}
									if (null == permits[1]) {
										permits[1] = 0.00;
									}
									// 计算最大额度
									permits[1] += price;
									map.put(brandId, permits);
								}
							}
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 计算订单中的优惠券是否被使用
	 * 
	 * @author dengxiao
	 */
	public TdOrder checkCouponIsUsed(TdOrder order) {
		// 如果参数为空，就没有计算的必要了
		if (null == order) {
			return null;
		}

		// 判定现金券是否被使用
		order.setCashCouponId(this.couponIsUsed(order.getCashCouponId()));
		// 判定产品券是否被使用
		order.setProductCouponId(this.couponIsUsed(order.getProductCouponId()));

		return order;
	}

	/**
	 * 计算一些优惠券是否被使用
	 * 
	 * @author dengxiao
	 */
	public String couponIsUsed(String couponsId) {
		if (null == couponsId) {
			return "";
		}
		// 定义一个字符串变量用于存储最后的使用优惠券的情况
		String realUsed = "";
		if (!"".equals(couponsId)) {
			String[] coupons = couponsId.split(",");
			if (null != coupons && coupons.length > 0) {
				for (String sCouponId : coupons) {
					if (null != sCouponId) {
						Long id = Long.parseLong(sCouponId);
						if (null != id) {
							TdCoupon coupon = tdCouponService.findOne(id);
							if (null != coupon && null != coupon.getIsOutDate() && null != coupon.getIsUsed()
									&& !coupon.getIsOutDate() && !coupon.getIsUsed()) {
								realUsed += coupon.getId() + ",";
							}
						}
					}
				}
			}
		}

		return realUsed;
	}

	/**
	 * 进行资金和优惠券退还的方法
	 * 
	 * @author dengxiao
	 */
	public void cashAndCouponBack(TdOrder order, TdUser user) {
		// 如果参数为NULL，则没有继续的必要了
		if (null == order || null == user) {
			return;
		}

		Double balance = user.getBalance();
		if (null == balance) {
			balance = 0.00;
		}
		Double unCashBalanceUsed = order.getUnCashBalanceUsed();
		if (null == unCashBalanceUsed) {
			unCashBalanceUsed = 0.00;
		}
		Double cashBalanceUsed = order.getCashBalanceUsed();
		if (null == cashBalanceUsed) {
			cashBalanceUsed = 0.00;
		}

		// 获取订单中的产品券使用id记录
		String productCouponId = order.getProductCouponId();
		if (null == productCouponId) {
			productCouponId = "";
		}
		// 获取订单中的现金券使用id记录
		String cashCouponId = order.getCashCouponId();
		if (null == cashCouponId) {
			cashCouponId = "";
		}

		// 开始返还用户的总额
		user.setBalance(user.getBalance() + unCashBalanceUsed + cashBalanceUsed);
		// 开始返还用户的不可提现余额
		user.setUnCashBalance(user.getUnCashBalance() + unCashBalanceUsed);
		// 开始返还用户的可提现余额
		user.setCashBalance(user.getCashBalance() + cashBalanceUsed);

		tdUserService.save(user);

		// 拆分使用的现金券的id
		if (null != cashCouponId && !"".equals(cashCouponId)) {
			String[] cashs = cashCouponId.split(",");
			if (null != cashs && cashs.length > 0) {
				for (String sId : cashs) {
					if (null != sId) {
						Long id = Long.valueOf(sId);
						TdCoupon coupon = tdCouponService.findOne(id);
						if (null != coupon) {
							coupon.setIsUsed(false);
							tdCouponService.save(coupon);
						}
					}
				}
			}
		}

		// 拆分使用的产品券
		if (null != productCouponId && !"".equals(productCouponId)) {
			String[] products = productCouponId.split(",");
			if (null != products && products.length > 0) {
				for (String sId : products) {
					if (null != sId) {
						Long id = Long.valueOf(sId);
						TdCoupon coupon = tdCouponService.findOne(id);
						if (null != coupon) {
							coupon.setIsUsed(false);
							tdCouponService.save(coupon);
						}
					}
				}
			}
		}
	}
}
