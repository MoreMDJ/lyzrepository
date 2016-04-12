package com.ynyes.lyz.service;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.util.Calendar;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdReturnNote;
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

	@Autowired
	private TdOrderService tdOrderService;

	@Autowired
	private TdGoodsService tdGoodsService;

	@Autowired
	private TdCityService tdCityService;

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
					// 到店支付可以使用预存款和优惠卷
					if (!"到店支付".equals(type.getTitle())) {
						order.setCashCouponId("");
						order.setProductCouponId("");
						canUseBalance = false;
					}

				}
			}
		}
		// 将运费的费用添加到订单总额中
		order.setTotalPrice(order.getTotalPrice() + order.getDeliverFee());

		// // 判断能否使用预存款和优惠券（支付方式为到店支付的情况下不能够使用预存款和优惠券）
		// String payTypeTitle = order.getPayTypeTitle();
		// if (null != payTypeTitle && payTypeTitle.equalsIgnoreCase("到店支付")) {
		// order.setCashCouponId("");
		// order.setProductCouponId("");
		// canUseBalance = false;
		// }

		if (canUseBalance) {
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
						// 如果该张优惠券存在，具有金额，具有真实使用价值，同时没有过期，没有使用，则有效
						if (null != coupon && null != coupon.getPrice() && null != coupon.getRealPrice()
								&& null != coupon.getIsOutDate() && null != coupon.getIsUsed() && !coupon.getIsOutDate()
								&& !coupon.getIsUsed()) {
							Double price = coupon.getRealPrice();
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
						// 如果该张优惠券存在，具有指定商品，具有真实价值，同时没有过期，没有使用，则有效
						if (null != coupon && null != coupon.getGoodsId() && null != coupon.getRealPrice()
								&& null != coupon.getIsOutDate() && null != coupon.getIsUsed() && !coupon.getIsOutDate()
								&& !coupon.getIsUsed()) {
							// 遍历所有的已选商品，查找与优惠券对应的商品，获取其价格
							order.setTotalPrice(order.getTotalPrice() - coupon.getRealPrice());
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

		// 根据当前预存款使用额，判断当前订单的金额
		if (canUseBalance) {
			if (null == order.getActualPay()) {
				// 如果当前使用预存款为null，则代表没有使用预存款
				order.setActualPay(0.00);
			}
		} else {
			order.setActualPay(0.00);
		}

		// 判断是否使用的预存款大于了订单的总金额
		if (order.getActualPay() > order.getTotalPrice()) {
			order.setActualPay(order.getTotalPrice());
		}

		order.setTotalPrice(order.getTotalPrice() - order.getActualPay());

		tdOrderService.save(order);

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

		user = tdUserService.save(user);

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

	/**
	 * 计算一个订单中所有的商品，包括促销赠品
	 * 
	 * @author DengXiao
	 */
	public Map<Long, Double[]> getAllOrderGoods(TdOrder order) {
		// result参数中key代表商品的id，Double数组中依次存储：0. 商品的购买单价；1. 商品的购买数量；
		// 2. 商品的总价；3.退货单价
		Map<Long, Double[]> result = new HashMap<>();
		if (null != order) {
			List<TdOrderGoods> goodsList = order.getOrderGoodsList();
			List<TdOrderGoods> presentedList = order.getPresentedList();
			if (null != goodsList && goodsList.size() > 0) {
				for (TdOrderGoods orderGoods : goodsList) {
					if (null != orderGoods) {
						Long goodsId = orderGoods.getGoodsId();
						Long quantity = orderGoods.getQuantity();
						Double price = orderGoods.getPrice();
						if (null == quantity) {
							quantity = 0L;
						}
						if (null == price) {
							price = 0.00;
						}
						if (null != goodsId && null == result.get(goodsId)) {
							Double[] val = new Double[3];
							val[0] = price;
							val[1] = new Double(quantity);
							val[2] = price * quantity;
							result.put(goodsId, val);
						} else if (null != goodsId && null != result.get(goodsId)) {
							Double[] val = result.get(goodsId);
							val[0] = price;
							val[1] = val[1] + new Double(quantity);
							val[2] = val[0] * val[1];
							result.put(goodsId, val);
						}
					}
				}
			}

			if (null != presentedList && presentedList.size() > 0) {
				for (TdOrderGoods orderGoods : presentedList) {
					if (null != orderGoods) {
						Long goodsId = orderGoods.getGoodsId();
						Long quantity = orderGoods.getQuantity();
						Double price = orderGoods.getGiftPrice();
						if (null == quantity) {
							quantity = 0L;
						}
						if (null == price) {
							price = 0.00;
						}
						if (null != goodsId && null == result.get(goodsId)) {
							Double[] val = new Double[3];
							val[0] = price;
							val[1] = new Double(quantity);
							val[2] = val[0] * val[1];
							result.put(goodsId, val);
						} else if (null != goodsId && null != result.get(goodsId)) {
							Double[] val = result.get(goodsId);
							val[0] = price;
							val[1] = val[1] + new Double(quantity);
							val[2] = val[0] * val[1];
							result.put(goodsId, val);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 获取订单总价格的方法
	 * 
	 * @author DengXiao
	 */
	public Double getRealPrice(TdOrder order) {
		if (null == order) {
			return null;
		}
		Double total = 0.00;
		List<TdOrderGoods> goodsList = order.getOrderGoodsList();
		if (null == goodsList) {
			return null;
		}
		for (TdOrderGoods orderGoods : goodsList) {
			if (null != orderGoods && null != orderGoods.getPrice()) {
				total += (orderGoods.getPrice() * orderGoods.getQuantity());
			}
		}
		return total;
	}

	/**
	 * 计算订单商品实际价值的方法
	 * 
	 * @author DengXiao
	 */
	public Double getTotalOrderGoodsPrice(TdOrder order) {
		Map<Long, Double[]> map = this.getAllOrderGoods(order);
		// 创建一个变量存储商品实际总价
		Double totalPrice = 0.00;
		// 获取订单实际总价
		for (Double[] counts : map.values()) {
			if (null != counts && counts.length == 3) {
				Double single_total = counts[2];
				if (null == single_total) {
					single_total = 0.00;
				}
				totalPrice += single_total;
			}
		}
		return totalPrice;
	}

	/**
	 * 计算订单单品退货单价的方法
	 * 
	 * @author DengXiao
	 */
	public Map<Long, Double> getReturnUnitPrice(TdOrder order) {
		Map<Long, Double> res = new HashMap<>();
		if (null == order) {
			return res;
		}
		// 获取订单的总价格
		Double realPrice = this.getRealPrice(order);
		// 获取订单的商品实际价值
		Double totalOrderGoodsPrice = this.getTotalOrderGoodsPrice(order);

		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);

		// 获取订单商品数量、总价队列
		Map<Long, Double[]> allOrderGoods = this.getAllOrderGoods(order);
		if (null != allOrderGoods && null != realPrice && 0.0 != realPrice && null != totalOrderGoodsPrice
				&& 0.0 != totalOrderGoodsPrice) {
			for (Long goodsId : allOrderGoods.keySet()) {
				// 获取指定商品的购买单价、购买数量、购买总价、退货单价队列
				if (null != goodsId) {
					Double[] infos = allOrderGoods.get(goodsId);
					// 判断是否获取到一个正确的队列
					if (null != infos && infos.length == 3) {
						// 取出商品的总价
						Double goodsTotalPrice = infos[2];
						if (null != goodsTotalPrice) {
							// 开始计算该件商品的价值比例
							Double point = goodsTotalPrice / totalOrderGoodsPrice;

							// 通过这个比例计算实际上的商品退货单价
							Double GoodsRealTotal = realPrice * point;
							if (null != GoodsRealTotal && null != infos[1] && 0.0 != infos[1]) {
								Double unit = GoodsRealTotal / infos[1];
								String sUnit = nf.format(unit);
								unit = Double.parseDouble(sUnit);
								res.put(goodsId, unit);
							}
						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * 获取指定订单的优惠券使用情况
	 * 
	 * @author DengXiao
	 */
	public Map<String, Object> countCouponCondition(Long orderId) {
		Map<String, Object> result = new HashMap<>();
		// 默认未使用指定产品现金券
		result.put("useProCashCoupon", false);
		// 默认未使用产品券
		result.put("useProCoupon", false);
		// 默认未使用通用现金券
		result.put("useCashCoupon", false);

		TdOrder order = tdOrderService.findOne(orderId);
		if (null == order) {
			return result;
		}
		String cashCouponId = order.getCashCouponId();
		String productCouponId = order.getProductCouponId();

		// 定义一个变量用于表示使用的通用现金券的真实总额
		Double cashTotal = 0.00;

		// 开始拆分使用的现金券
		if (null != cashCouponId && !"".equals(cashCouponId)) {
			String[] ids = cashCouponId.split(",");
			if (null != ids && ids.length > 0) {
				for (String sId : ids) {
					if (null != sId && !"".equals(sId)) {
						Long id = Long.parseLong(sId);
						TdCoupon coupon = tdCouponService.findOne(id);
						if (null != coupon) {
							Long categoryId = coupon.getTypeCategoryId();
							if (null != categoryId && 1L == categoryId.longValue()) {
								if (null == coupon.getRealPrice()) {
									coupon.setRealPrice(0.00);
								}
								cashTotal += coupon.getRealPrice();
								result.put("useCashCoupon", true);
							}
							if (null != categoryId && 2L == categoryId.longValue()) {
								Long goodsId = coupon.getGoodsId();
								Double price = coupon.getPrice();
								if (null == price) {
									price = 0.00;
								}
								if (null != goodsId && null != result.get("proCash" + goodsId)) {
									Double usePrice = (Double) result.get("proCash" + goodsId);
									if (null == usePrice) {
										usePrice = 0.00;
									}
									result.put("proCash" + goodsId, usePrice + price);
								}
								if (null != goodsId && null == result.get("proCash" + goodsId)) {
									result.put("proCash" + goodsId, price);
								}
								result.put("useProCashCoupon", true);
							}
						}
					}
				}
			}
		}

		// 开始拆分使用的产品券
		if (null != productCouponId && !"".equals(productCouponId)) {
			String[] ids = productCouponId.split(",");
			if (null != ids && ids.length > 0) {
				for (String sId : ids) {
					if (null != sId && !"".equals(sId)) {
						Long id = Long.parseLong(sId);
						TdCoupon coupon = tdCouponService.findOne(id);
						if (null != coupon) {
							Long goodsId = coupon.getGoodsId();
							if (null != goodsId && null != result.get("pro" + goodsId)) {
								Integer number = (Integer) result.get("pro" + goodsId);
								result.put("pro" + goodsId, number + 1);
							}
							if (null != goodsId && null == result.get("pro" + goodsId)) {
								result.put("pro" + goodsId, 1);
							}
							result.put("useProCoupon", true);
						}
					}
				}
			}
		}

		result.put("cashTotal", cashTotal);
		return result;
	}

	/**
	 * 开始退还钱/券的方法
	 * 
	 * @param params的规则为【商品id】-【退货数量】-【退货单价】
	 * @author DengXiao
	 */
	public void returnCashOrCoupon(Long orderId, String params) {
		TdOrder order = tdOrderService.findOne(orderId);
		Long userId = order.getUserId();
		TdUser user = tdUserService.findOne(userId);
		String cityName = user.getCityName();
		TdCity city = tdCityService.findByCityName(cityName);
		if (null != order && null != params && !"".equals(params)
				&& !(null != order.getIsRefund() && order.getIsRefund())) {
			Map<String, Object> result = this.countCouponCondition(orderId);

			Boolean useProCashCoupon = (Boolean) result.get("useProCashCoupon");
			Boolean useProCoupon = (Boolean) result.get("useProCoupon");
			Boolean useCashCoupon = (Boolean) result.get("useCashCoupon");

			Double cashTotal = (Double) result.get("cashTotal");

			// 获取一年后的时间（新的券的有效时间）
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.YEAR, 1);
			Date endTime = cal.getTime();

			// 开始拆分退货参数
			String[] param = params.split(",");
			if (null != param && param.length > 0) {
				for (String group : param) {
					if (null != group && !"".equals(group)) {
						String[] singles = group.split("-");
						// 判断singles是否为一个正确的参数
						if (null != singles && singles.length == 3) {
							String sGoodsId = singles[0];
							Long goodsId = null;
							String sNumber = singles[1];
							Long number = 0L;
							String sUnit = singles[2];
							Double unit = 0.00;
							if (null != sGoodsId && !"".equals(sGoodsId)) {
								goodsId = Long.parseLong(sGoodsId);
							}
							if (null != sNumber && !"".equals(sNumber)) {
								number = Long.parseLong(sNumber);
							}
							if (null != sUnit && !"".equals(sUnit)) {
								unit = Double.parseDouble(sUnit);
							}

							// 计算该商品的退货总额
							Double total = number * unit;
							if (null != goodsId) {
								TdGoods goods = tdGoodsService.findOne(goodsId);
								// 退还指定产品现金券
								if (useProCashCoupon) {
									Double useProCash = (Double) result.get("proCash" + goodsId);
									if (null == useProCash) {
										useProCash = 0.00;
									}
									// 创建一个变量用于表示退还的券的金额
									Double proCashPrice = 0.0;
									if (total > useProCash) {
										proCashPrice = useProCash;
									} else {
										proCashPrice = total;
									}
									total -= proCashPrice;

									// 开始新生成一个指定产品优惠券
									TdCoupon proCashCoupon = new TdCoupon();
									proCashCoupon.setTypeId(3L);
									proCashCoupon.setTypeCategoryId(2L);
									if (null != goods) {
										proCashCoupon.setBrandId(goods.getBrandId());
										proCashCoupon.setBrandTitle(goods.getBrandTitle());
									}
									proCashCoupon.setPicUri(goods.getCoverImageUri());
									proCashCoupon.setGoodsId(goods.getId());
									proCashCoupon.setGoodsName(goods.getTitle());
									proCashCoupon.setTypeTitle("退货返还的优惠券");
									proCashCoupon.setPrice(proCashPrice);
									if (null != city) {
										proCashCoupon.setCityName(city.getCityName());
										proCashCoupon.setCityId(city.getId());
									}
									proCashCoupon.setIsDistributted(true);
									proCashCoupon.setGetNumber(1L);
									proCashCoupon.setGetTime(new Date());
									proCashCoupon.setAddTime(new Date());
									proCashCoupon.setExpireTime(endTime);
									proCashCoupon.setUsername(order.getUsername());
									proCashCoupon.setIsUsed(false);
									proCashCoupon.setIsOutDate(false);
									proCashCoupon.setMobile(order.getUsername());
									proCashCoupon.setSku(goods.getCode());
									tdCouponService.save(proCashCoupon);
								}
								// 开始退还产品券
								if (total > 0) {
									if (useProCoupon) {
										// 查找本产品是否使用了产品券
										Integer useNumber = (Integer) result.get("pro" + goodsId);
										if (null != useNumber && useNumber > 0) {
											// 开始计算退还几张券
											for (int i = 0; i < useNumber; i++) {
												if (total < unit) {
													break;
												}
												TdCoupon proCoupon = new TdCoupon();
												proCoupon.setTypeId(3L);
												proCoupon.setTypeCategoryId(3L);
												if (null != goods) {
													proCoupon.setBrandId(goods.getBrandId());
													proCoupon.setBrandTitle(goods.getBrandTitle());
												}
												proCoupon.setPicUri(goods.getCoverImageUri());
												proCoupon.setGoodsId(goods.getId());
												proCoupon.setPrice(0.0);
												proCoupon.setTypeTitle("退货返还的优惠券");
												proCoupon.setGoodsName(goods.getTitle());
												proCoupon.setIsDistributted(true);
												if (null != city) {
													proCoupon.setCityName(city.getCityName());
													proCoupon.setCityId(city.getId());
												}
												proCoupon.setGetTime(new Date());
												proCoupon.setAddTime(new Date());
												proCoupon.setGetNumber(1L);
												proCoupon.setExpireTime(endTime);
												proCoupon.setUsername(order.getUsername());
												proCoupon.setIsUsed(false);
												proCoupon.setIsOutDate(false);
												proCoupon.setMobile(order.getUsername());
												proCoupon.setSku(goods.getCode());
												tdCouponService.save(proCoupon);
												total -= unit;
											}
										}
									}
								}
								// 开始退还通用现金券
								if (total > 0) {
									if (useCashCoupon) {
										// 声明一个变量用来表示退还的通用现金券的面额
										Double cashPrice = 0.00;
										if (cashTotal > total) {
											cashPrice = total;
										} else {
											cashPrice = cashTotal;
										}

										TdCoupon cashCoupon = new TdCoupon();
										cashCoupon.setTypeId(3L);
										cashCoupon.setTypeCategoryId(1L);
										if (null != goods) {
											cashCoupon.setBrandId(goods.getBrandId());
											cashCoupon.setBrandTitle(goods.getBrandTitle());
										}
										cashCoupon.setPicUri(goods.getCoverImageUri());
										cashCoupon.setGoodsName(goods.getTitle());
										cashCoupon.setPrice(cashPrice);
										cashCoupon.setTypeTitle("退货返还的优惠券");
										cashCoupon.setGetNumber(1L);
										if (null != city) {
											cashCoupon.setCityName(city.getCityName());
											cashCoupon.setCityId(city.getId());
										}
										cashCoupon.setAddTime(new Date());
										cashCoupon.setIsDistributted(true);
										cashCoupon.setGetTime(new Date());
										cashCoupon.setExpireTime(endTime);
										cashCoupon.setUsername(order.getUsername());
										cashCoupon.setIsUsed(false);
										cashCoupon.setIsOutDate(false);
										cashCoupon.setMobile(order.getUsername());
										tdCouponService.save(cashCoupon);

										total -= cashPrice;

									}
								}
								// 如果还有金额没有退还，那就只能够退还不可提现余额了
								if (total > 0) {
									user.setUnCashBalance(user.getUnCashBalance() + total);
									user.setBalance(user.getBalance() + total);
									tdUserService.save(user);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 根据WMS返回信息退还券和钱
	 * 
	 * @author DengXiao
	 */
	public void actAccordingWMS(TdReturnNote returnNote, Long orderId) {
		if (null != returnNote) {
			List<TdOrderGoods> returnGoodsList = returnNote.getReturnGoodsList();
			if (null != returnGoodsList && returnGoodsList.size() > 0) {
				String params = "";
				for (TdOrderGoods goods : returnGoodsList) {
					if (null != goods && null != goods.getGoodsId() && null != goods.getQuantity()
							&& null != goods.getPrice()) {
						params += goods.getGoodsId() + "-" + goods.getQuantity() + "-" + goods.getPrice() + ",";
					}
				}
				this.returnCashOrCoupon(orderId, params);
			}
		}
	}

}
