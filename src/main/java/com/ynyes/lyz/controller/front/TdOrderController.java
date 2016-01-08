package com.ynyes.lyz.controller.front;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdActivityGiftList;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDeliveryType;
import com.ynyes.lyz.entity.TdDistrict;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdShippingAddress;
import com.ynyes.lyz.entity.TdSubdistrict;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdCartGoodsService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdDeliveryTypeService;
import com.ynyes.lyz.service.TdDistrictService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdOrderGoodsService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdPayTypeService;
import com.ynyes.lyz.service.TdPriceListItemService;
import com.ynyes.lyz.service.TdShippingAddressService;
import com.ynyes.lyz.service.TdSubdistrictService;
import com.ynyes.lyz.service.TdUserService;

@Controller
@RequestMapping(value = "/order")
public class TdOrderController {

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdShippingAddressService tdShippingAddressService;

	@Autowired
	private TdCommonService tdCommonService;

	@Autowired
	private TdDeliveryTypeService tdDeliveryTypeService;

	@Autowired
	private TdPayTypeService tdPayTypeService;

	@Autowired
	private TdDiySiteService tdDiySiteService;

	@Autowired
	private TdCouponService tdCouponService;

	@Autowired
	private TdSubdistrictService tdSubdistrictService;

	@Autowired
	private TdCityService tdCityService;

	@Autowired
	private TdCartGoodsService tdCartGoodsService;

	@Autowired
	private TdDistrictService tdDistrictService;

	@Autowired
	private TdGoodsService tdGoodsService;

	@Autowired
	private TdOrderGoodsService tdOrderGoodsService;

	@Autowired
	private TdPriceListItemService tdPriceListItemService;

	@Autowired
	private TdOrderService tdOrderService;

	/**
	 * 跳转到填写订单的页面
	 * 
	 * @author dengxiao
	 */
	@RequestMapping
	public String writeOrderInfo(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 从session中获取一系列的信息
		Long addressId = (Long) req.getSession().getAttribute("order_addressId");
		Long payTypeId = (Long) req.getSession().getAttribute("order_payTypeId");
		String remark = (String) req.getSession().getAttribute("order_remark");
		Long diySiteId = (Long) req.getSession().getAttribute("order_diySiteId");
		Long deliveryId = (Long) req.getSession().getAttribute("order_deliveryId");
		String deliveryDate = (String) req.getSession().getAttribute("order_deliveryDate");
		String deliveryDetail = (String) req.getSession().getAttribute("order_deliveryDetail");
		Long deliveryDetailId = (Long) req.getSession().getAttribute("order_deliveryDetailId");

		// 获取确定使用的现金券
		@SuppressWarnings("unchecked")
		List<TdCoupon> no_product_used = (List<TdCoupon>) req.getSession().getAttribute("order_noProductCouponUsed");
		// 获取确定使用的产品券
		@SuppressWarnings("unchecked")
		List<TdCoupon> product_used = (List<TdCoupon>) req.getSession().getAttribute("order_productCouponUsed");

		// 创建一个变量用于存储物流配送费用
		Double deliveryFee = 0.00;

		// 创建一个布尔变量代表当前能否使用优惠券，默认值为true，代表能使用优惠券
		Boolean isCoupon = true;

		TdShippingAddress address = tdShippingAddressService.findOne(addressId);
		if (null == address) {
			// 获取用户默认收货地址
			List<TdShippingAddress> addressList = user.getShippingAddressList();
			if (null != addressList) {
				for (TdShippingAddress address_item : addressList) {
					if (null != address_item && null != address_item.getIsDefaultAddress()
							&& address_item.getIsDefaultAddress()) {
						map.addAttribute("address", address_item);
						req.getSession().setAttribute("order_addressId", address_item.getId());
						// 获取配送费用
						Long subdistrictId = address_item.getSubdistrictId();
						TdSubdistrict subdistrict = tdSubdistrictService.findOne(subdistrictId);
						if (null != subdistrict) {
							deliveryFee = subdistrict.getDeliveryFee();
							if (null == deliveryFee) {
								deliveryFee = 0.00;
							}
						}
					}
				}
			}
		} else {
			req.getSession().setAttribute("order_addressId", address.getId());
			map.addAttribute("address", address);
			// 获取配送费用
			Long subdistrictId = address.getSubdistrictId();
			TdSubdistrict subdistrict = tdSubdistrictService.findOne(subdistrictId);
			if (null != subdistrict) {
				deliveryFee = subdistrict.getDeliveryFee();
				if (null == deliveryFee) {
					deliveryFee = 0.00;
				}
			}
		}

		// 删除数据库中已有的用户已选
		List<TdCartGoods> list = tdCartGoodsService.findByUsername(username);
		tdCartGoodsService.deleteAll(list);

		// 获取已选商品（整合后的）
		List<TdCartGoods> selected_list = tdCommonService.getAllContainsColorPackage(req);
		// 将整合后的已选商品存储到数据库中
		for (TdCartGoods cartGoods : selected_list) {
			cartGoods.setUsername(username);
			tdCartGoodsService.save(cartGoods);
		}
		map.addAttribute("selected_list", selected_list);

		// 获取默认的配送方式（1代表送货上门，2代表门店自提）
		if (null == deliveryId) {
			deliveryId = 1L;
			req.getSession().setAttribute("order_deliveryId", deliveryId);
			map.addAttribute("deliveryId", deliveryId);
		} else {
			map.addAttribute("deliveryId", deliveryId);
		}

		// 获取默认的门店（用户归属门店）
		if (null == diySiteId) {
			TdDiySite diySite = tdCommonService.getDiySite(req);
			if (null != diySite) {
				req.getSession().setAttribute("order_diySiteId", diySite.getId());
				map.addAttribute("diySite", diySite);
			}
		}else{
			TdDiySite diySite = tdDiySiteService.findOne(diySiteId);
			map.addAttribute("diySite", diySite);
		}
		// 获取默认支付方式
		if (null == payTypeId) {
			List<TdPayType> pay_type_list = tdPayTypeService.findAllOrderBySortIdAsc();
			if (null != pay_type_list && pay_type_list.size() > 0) {
				if (null != pay_type_list.get(0)) {
					req.getSession().setAttribute("order_payTypeId", pay_type_list.get(0).getId());
				}
				map.addAttribute("pay_type", pay_type_list.get(0));
			}
		} else {
			TdPayType payType = tdPayTypeService.findOne(payTypeId);
			if (null != payType) {
				req.getSession().setAttribute("order_payTypeId", payType.getId());
			}
			map.addAttribute("pay_type", payType);
		}

		// 如果配送方式是2（门店自提）并且支付方式为预存款或货到付款，则更换支付方式为到店支付，并且清空已选的优惠券
		if (2L == deliveryId) {
			TdPayType type = tdPayTypeService.findOne(payTypeId);
			if ("预存款".equals(type.getTitle()) || "货到付款".equals(type.getTitle())) {
				TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("到店支付");
				req.getSession().setAttribute("order_payTypeId", payTypeId);
				// 清空所有的优惠券使用的优惠券
				req.getSession().setAttribute("order_noProductCouponUsed", new ArrayList<TdCoupon>());
				req.getSession().setAttribute("order_productCouponUsed", new ArrayList<TdCoupon>());
				no_product_used = new ArrayList<>();
				product_used = new ArrayList<>();
				map.addAttribute("pay_type", payType);
				isCoupon = false;
			}
		}

		// 如果配送方式是1（送货上门）并且支付方式为到店支付，则更换为货到付款
		if (1L == deliveryId) {
			payTypeId = (Long) req.getSession().getAttribute("order_payTypeId");
			TdPayType type = tdPayTypeService.findOne(payTypeId);
			if ("到店支付".equals(type.getTitle())) {
				TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("货到付款");
				req.getSession().setAttribute("order_payTypeId", payType.getId());
				map.addAttribute("pay_type", payType);
			}
		}

		// 如果配送方式是2（门店自提）并且支付方式为货到付款或预存款，则更换为到店支付
		if (2L == deliveryId) {
			payTypeId = (Long) req.getSession().getAttribute("order_payTypeId");
			TdPayType type = tdPayTypeService.findOne(payTypeId);
			if ("货到付款".equals(type.getTitle()) || "预存款".equals(type.getTitle())) {
				TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("到店支付");
				req.getSession().setAttribute("order_payTypeId", payType.getId());
				map.addAttribute("pay_type", payType);
			}
		}

		// 获取默认时间，默认为第二天的11:30-12:30
		if (null == deliveryDate) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 1);
			Date date = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String order_deliveryDate = sdf.format(date);
			req.getSession().setAttribute("order_deliveryDate", order_deliveryDate);
			req.getSession().setAttribute("order_deliveryDetail", "11:30-12:30");
			req.getSession().setAttribute("order_deliveryDetailId", 11L);
			map.addAttribute("order_deliveryDate", order_deliveryDate);
			map.addAttribute("order_deliveryDetail", "11:30-12:30");
			map.addAttribute("order_deliveryDetailId", 11L);
		} else {
			map.addAttribute("order_deliveryDate", deliveryDate);
			map.addAttribute("order_deliveryDetail", deliveryDetail);
			map.addAttribute("deliveryDetailId", deliveryDetailId);
		}

		// 创建一个变量用于存储此单的总金额
		Double totalPrice = 0.00;

		// 创建一个变量用于存储此单能够使用的现金券的最大额（除去使用了产品券的部分）
		Double maxPrivilege = 0.00;

		// 检查用户所有的优惠券是否存在过期的
		tdCommonService.checkUserCoupon(req);

		// 获取用户所有未使用的现金券（同时，这个集合也可以存储此单能够使用的指定产品现金券）
		List<TdCoupon> no_product_coupon_list = tdCouponService
				.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseOrderByGetTimeDesc(username, 1L);
		if (null == no_product_coupon_list) {
			no_product_coupon_list = new ArrayList<>();
		}
		// 创建一个集合用于存储当前用户能够在此单使用的产品券
		List<TdCoupon> product_coupon_list = new ArrayList<>();
		// 遍历已选，一个一个的查找用户能够使用的产品券或指定产品现金券，同时计算此单的总金额
		for (TdCartGoods cartGoods : selected_list) {
			if (null != cartGoods) {
				// 创建一个变量用于判断指定的商品使用产品券的数量
				Long usedNumber = 0L;
				if (null != product_used) {
					for (TdCoupon coupon : product_used) {
						if (null != coupon && null != coupon.getGoodsId()
								&& coupon.getGoodsId() == cartGoods.getGoodsId()) {
							usedNumber += 1L;
						}
					}
				}

				if (null != cartGoods.getPrice()) {
					// 创建一个变量用于存储指定商品未使用产品券的数量
					Long unUsed = 0L;
					if (null == cartGoods.getPrice()) {
						cartGoods.setPrice(0.00);
					}
					if (null == cartGoods.getQuantity()) {
						cartGoods.setQuantity(0L);
					}
					if ((cartGoods.getQuantity() - usedNumber) > 0) {
						unUsed = (cartGoods.getQuantity() - usedNumber);
					}
					if (null == cartGoods.getRealPrice()) {
						cartGoods.setRealPrice(0.00);
					}
					// 总金额变化
					totalPrice += (cartGoods.getPrice() * unUsed);
					// 计算未使用产品券的部分能够使用的现金券的额度
					maxPrivilege = ((cartGoods.getPrice() - cartGoods.getRealPrice()) * unUsed);
				}
			}

			List<TdCoupon> product_coupon_by_goodsId = tdCouponService
					.findByUsernameAndIsUsedFalseAndTypeCategoryId3LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
							username, cartGoods.getGoodsId());
			if (null != product_coupon_by_goodsId && product_coupon_by_goodsId.size() > 0) {
				product_coupon_list.addAll(product_coupon_by_goodsId);
			}

			List<TdCoupon> no_product_coupon_by_goodsId = tdCouponService
					.findByUsernameAndIsUsedFalseAndTypeCategoryId2LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
							username, cartGoods.getGoodsId());
			if (null != no_product_coupon_by_goodsId && no_product_coupon_by_goodsId.size() > 0) {
				no_product_coupon_list.addAll(no_product_coupon_by_goodsId);
			}
		}

		// 创建一个变量用于存储现金券所减少的金额
		Double privilegePrice = 0.00;
		// 遍历所有已选的现金券，计算现金券减少金额的总量
		if (null != no_product_used) {
			for (TdCoupon coupon : no_product_used) {
				if (null != coupon && null != coupon.getPrice()) {
					privilegePrice += coupon.getPrice();
				}
			}
		}

		// 判断实际减少额是否大于最大减少额
		if (privilegePrice > maxPrivilege) {
			privilegePrice = maxPrivilege;
		}

		// 总金额变化
		totalPrice -= privilegePrice;

		map.addAttribute("no_product_used", no_product_used);
		map.addAttribute("product_used", product_used);

		map.addAttribute("no_product_coupon_list", no_product_coupon_list);
		map.addAttribute("product_coupon_list", product_coupon_list);

		map.addAttribute("remark", remark);
		map.addAttribute("user", user);

		map.addAttribute("totalPrice", totalPrice);
		map.addAttribute("deliveryFee", deliveryFee);

		map.addAttribute("isCoupon", isCoupon);
		return "/client/order_pay";

	}

	/**
	 * 跳转到查看已选商品的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/selected")
	public String orderSelected(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 从数据库中获取所有的已选商品
		List<TdCartGoods> all_selected = tdCartGoodsService.findByUsername(username);
		// 创建一个集合用于存储所有的赠品
		List<TdCartGoods> presented = new ArrayList<>();
		// 获取当前所有的赠品
		presented = tdCommonService.getPresent(req, all_selected, presented);

		// 以下操作是为了避免脏数据刷新影响到了数据库中的数据
		for (TdCartGoods tdCartGoods : all_selected) {
			tdCartGoodsService.delete(tdCartGoods.getId());
		}

		all_selected = tdCommonService.getAllContainsColorPackage(req);
		tdCartGoodsService.save(all_selected);

		all_selected = tdCartGoodsService.findByUsername(username);
		// 创建一个集合用于存储所有的小辅料
		List<TdActivityGiftList> gift_list = new ArrayList<>();
		// 获取已选【id：数量】对
		Map<Long, Long> group = tdCommonService.getGroup(req);
		gift_list = tdCommonService.getGift(req, group, gift_list);

		// 以下操作是为了避免脏数据刷新影响到了数据库中的数据
		for (TdCartGoods tdCartGoods : all_selected) {
			tdCartGoodsService.delete(tdCartGoods.getId());
		}

		all_selected = tdCommonService.getAllContainsColorPackage(req);
		tdCartGoodsService.save(all_selected);

		// 获取所有商品的总量和总价
		Long totalNumber = 0L;
		Double totalPrice = 0.00;
		if (null != all_selected) {
			for (TdCartGoods cartGoods : all_selected) {
				if (null != cartGoods) {
					Double unit_price = cartGoods.getPrice();
					Long quantity = cartGoods.getQuantity();
					if (null != quantity) {
						totalNumber += quantity;
						if (null != unit_price) {
							totalPrice += (unit_price * quantity);
						}
					}
				}
			}
		}

		// 还需遍历赠品已得到正确的商品总量
		if (null != presented) {
			for (TdCartGoods cartGoods : presented) {
				if (null != cartGoods) {
					Long quantity = cartGoods.getQuantity();
					if (null != quantity) {
						totalNumber += quantity;
					}
				}
			}
		}

		map.addAttribute("totalNumber", totalNumber);
		map.addAttribute("totalPrice", totalPrice);

		map.addAttribute("all_selected", all_selected);
		map.addAttribute("presented", presented);
		return "/client/order_list";
	}

	/**
	 * 对用户填写的留言信息进行存储的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/remark/save")
	@ResponseBody
	public Map<String, Object> remarkSave(HttpServletRequest req, String remark) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		req.getSession().setAttribute("order_remark", remark);
		res.put("remark", remark);
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到选择配送方式的页面的方法
	 * 
	 * @author dengxiao
	 * 
	 */
	@RequestMapping(value = "/delivery")
	public String selectDelivery(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsername(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 获取用户的城市
		TdCity city = tdCityService.findOne(user.getCityId());

		SimpleDateFormat hh = new SimpleDateFormat("HH");
		SimpleDateFormat mm = new SimpleDateFormat("mm");
		SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

		// 获取当前选择的门店
		Long diySiteId = (Long) req.getSession().getAttribute("order_diySiteId");
		// 获取当前选择的配送方式（0. 送货上门；1. 门店自提）
		Long deliveryId = (Long) req.getSession().getAttribute("order_deliveryId");
		// 获取当前选择的配送时间
		String deliveryDate = (String) req.getSession().getAttribute("order_deliveryDate");
		// 获取当前选择的配送时间段
		String deliveryDetail = (String) req.getSession().getAttribute("order_deliveryDetail");
		// 获取当前选择的配送时间段的id
		Long deliveryDetailId = (Long) req.getSession().getAttribute("order_deliveryDetailId");

		// 获取所有的配送方式
		List<TdDeliveryType> delivery_list = tdDeliveryTypeService.findAllOrderBySortIdAsc();
		map.addAttribute("delivery_list", delivery_list);

		// 获取配送时间的限制（时间选择机制：如果是上午下单，最早的配送时间是当前下午，如果是下午下单，最早配送时间是第二天的上午）
		Date now = new Date();
		String h = hh.format(now);
		String m = mm.format(now);
		Long hour = Long.parseLong(h);
		Long minute = Long.parseLong(m);

		Date limitDate = now;

		Long delay = city.getDelayHour();
		if (null == delay) {
			delay = 0L;
		}

		Long tempHour = hour + delay;
		if (24 <= tempHour) {
			tempHour -= 24;
		}

		// 判断能否当天配送
		if (tempHour > city.getFinishHour() || (tempHour == city.getFinishHour() && minute > city.getFinishMinute())) {
			limitDate = new Date(now.getTime() + (1000 * 60 * 60 * 24));
			tempHour = 9L;
		}

		// 获取限制时间
		map.addAttribute("limitHour", tempHour);

		map.addAttribute("limitDay", yyyyMMdd.format(limitDate));

		String earlyDate = yyyyMMdd.format(limitDate) + " " + tempHour + ":30-" + (tempHour + 1) + ":30";

		// 获取指定城市下所有的门店
		List<TdDiySite> diy_list = tdDiySiteService.findByRegionIdAndIsEnableOrderBySortIdAsc(city.getId());
		// 获取默认门店
		TdDiySite diySite = tdDiySiteService.findOne(diySiteId);

		map.addAttribute("diySite", diySite);
		map.addAttribute("diy_list", diy_list);
		map.addAttribute("earlyDate", earlyDate);
		map.addAttribute("diySiteId", diySiteId);
		map.addAttribute("deliveryId", deliveryId);
		map.addAttribute("deliveryDate", deliveryDate);
		map.addAttribute("deliveryDetail", deliveryDetail);
		map.addAttribute("deliveryDetailId", deliveryDetailId);

		return "/client/order_delivery";
	}

	/**
	 * 存储新的配送方式的信息的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/delivery/save")
	public String saveDelivery(HttpServletRequest req, Long type, String date, Long detailTime, Long diySite) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		req.getSession().setAttribute("order_deliveryId", type);
		req.getSession().setAttribute("order_deliveryDate", date);
		req.getSession().setAttribute("order_deliveryDetailId", detailTime);
		req.getSession().setAttribute("order_deliveryDetail", detailTime + ":30-" + (detailTime + 1) + ":30");
		req.getSession().setAttribute("order_diySiteId", diySite);

		return "redirect:/order";
	}

	/**
	 * 跳转到选择支付方式的页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/paytype")
	public String selectPayType(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 获取当前选择的支付方式
		Long payTypeId = (Long) req.getSession().getAttribute("order_payTypeId");

		map.addAttribute("payTypeId", payTypeId);

		// 获取所有的支付方式
		List<TdPayType> pay_type_list = tdPayTypeService.findByIsOnlinePayTrueAndIsEnableTrueOrderBySortIdAsc();

		// 获取配送方式
		Long deliveryId = (Long) req.getSession().getAttribute("order_deliveryId");

		List<TdPayType> type_list = new ArrayList<>();

		if (1L == deliveryId) {
			type_list.addAll(pay_type_list);
			// 查询是否存在货到付款或到店支付的支付方式
			TdPayType cashOnDelivery = tdPayTypeService.findByTitleAndIsEnableTrue("货到付款");
			if (null != cashOnDelivery) {
				map.addAttribute("cashOndelivery", cashOnDelivery);
			}
		}

		if (2L == deliveryId) {
			for (TdPayType tdPayType : pay_type_list) {
				if (null != tdPayType) {
					String title = tdPayType.getTitle();
					if (null != title) {
						if (!"预存款".equals(title.trim()) && !"货到付款".equals(title.trim())) {
							type_list.add(tdPayType);
						}
					}
				}
			}
			// 查询是否存在货到付款或到店支付的支付方式
			TdPayType cashOnDelivery = tdPayTypeService.findByTitleAndIsEnableTrue("到店支付");
			if (null != cashOnDelivery) {
				map.addAttribute("cashOndelivery", cashOnDelivery);
			}
		}
		map.addAttribute("pay_type_list", type_list);
		return "/client/order_pay_type";
	}

	/**
	 * 跳转到选择优惠券的页面
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/coupon/{type}")
	public String selectCoupon(HttpServletRequest req, ModelMap map, @PathVariable Long type) {
		// 根据type的值不同跳转到不同的页面：0. 现金券；1. 产品券
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 获取所有已选的商品
		List<TdCartGoods> selected_list = tdCommonService.getAllContainsColorPackage(req);

		// 获取用户所有未使用的现金券（同时，这个集合也可以存储此单能够使用的指定产品现金券）
		List<TdCoupon> no_product_coupon_list = tdCouponService
				.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseOrderByGetTimeDesc(username, 1L);
		if (null == no_product_coupon_list) {
			no_product_coupon_list = new ArrayList<>();
		}

		// 创建一个集合用于存储当前用户能够在此单使用的产品券
		List<TdCoupon> product_coupon_list = new ArrayList<>();
		// 遍历已选，一个一个的查找用户能够使用的产品券或指定产品现金券
		for (TdCartGoods cartGoods : selected_list) {
			if (null != cartGoods) {
				List<TdCoupon> product_coupon_by_goodsId = tdCouponService
						.findByUsernameAndIsUsedFalseAndTypeCategoryId3LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
								username, cartGoods.getGoodsId());
				if (null != product_coupon_by_goodsId && product_coupon_by_goodsId.size() > 0) {
					product_coupon_list.addAll(product_coupon_by_goodsId);
				}

				List<TdCoupon> no_product_coupon_by_goodsId = tdCouponService
						.findByUsernameAndIsUsedFalseAndTypeCategoryId2LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
								username, cartGoods.getGoodsId());
				if (null != no_product_coupon_by_goodsId && no_product_coupon_by_goodsId.size() > 0) {
					no_product_coupon_list.addAll(no_product_coupon_by_goodsId);
				}
			}
		}

		// 获取确定使用的现金券
		@SuppressWarnings("unchecked")
		List<TdCoupon> no_product_used = (List<TdCoupon>) req.getSession().getAttribute("order_noProductCouponUsed");
		// 获取确定使用的产品券
		@SuppressWarnings("unchecked")
		List<TdCoupon> product_used = (List<TdCoupon>) req.getSession().getAttribute("order_productCouponUsed");

		// 跳转到选择现金券的页面
		if (0L == type) {
			map.addAttribute("no_product_coupon_list", no_product_coupon_list);
			map.addAttribute("no_product_used", no_product_used);
			return "/client/order_cash_coupon";
		} else {
			map.addAttribute("product_coupon_list", product_coupon_list);
			map.addAttribute("product_used", product_used);
			return "/client/order_product_coupon";
		}
	}

	/**
	 * 选择/取消选择优惠券的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/operate/coupon")
	@ResponseBody
	public Map<String, Object> operateCoupon(HttpServletRequest req, Long id, Long type, Long status) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 获取确定使用的现金券
		@SuppressWarnings("unchecked")
		List<TdCoupon> no_product_used = (List<TdCoupon>) req.getSession().getAttribute("order_noProductCouponUsed");
		// 获取确定使用的产品券
		@SuppressWarnings("unchecked")
		List<TdCoupon> product_used = (List<TdCoupon>) req.getSession().getAttribute("order_productCouponUsed");

		if (null == no_product_used) {
			no_product_used = new ArrayList<>();
		}
		if (null == product_used) {
			product_used = new ArrayList<>();
		}

		// 获取指定id的优惠券
		TdCoupon coupon = tdCouponService.findOne(id);
		if (0L == type) {
			if (0L == status) {
				no_product_used.add(coupon);
			}
			if (1L == status) {
				for (int i = 0; i < no_product_used.size(); i++) {
					TdCoupon tdCoupon = no_product_used.get(i);
					if (null != tdCoupon && null != tdCoupon.getId() && tdCoupon.getId() == id) {
						no_product_used.remove(i);
					}
				}
			}
		}

		if (1L == type) {
			if (0L == status) {
				product_used.add(coupon);
			}
			if (1L == status) {
				for (int i = 0; i < product_used.size(); i++) {
					TdCoupon tdCoupon = product_used.get(i);
					if (null != tdCoupon && null != tdCoupon.getId() && tdCoupon.getId() == id) {
						product_used.remove(i);
					}
				}
			}
		}

		req.getSession().setAttribute("order_productCouponUsed", product_used);
		req.getSession().setAttribute("order_noProductCouponUsed", no_product_used);
		res.put("status", 0);
		return res;
	}

	/**
	 * 确认选择的支付方式的方法（最后跳转回到填写订单的页面）
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/confirm/paytype")
	public String confirmPayType(HttpServletRequest req, Long id) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		if (null != id) {
			req.getSession().setAttribute("order_payTypeId", id);
		}
		return "redirect:/order";
	}

	/**
	 * 添加收货地址的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/add/address")
	public String orderAddAddress(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		// 获取用户的城市
		Long cityId = user.getCityId();
		if (null != cityId) {
			// 查找指定城市下的所有行政区划
			List<TdDistrict> district_list = tdDistrictService.findByCityIdOrderBySortIdAsc(cityId);
			map.addAttribute("district_list", district_list);
			if (null != district_list && district_list.size() > 0) {
				// 默认行政区划为集合的第一个
				TdDistrict district = district_list.get(0);
				// 根据指定的行政区划查找其下的行政街道
				if (null != district) {
					List<TdSubdistrict> subdistrict_list = tdSubdistrictService
							.findByDistrictIdOrderBySortIdAsc(district.getId());
					map.addAttribute("subdistrict_list", subdistrict_list);
				}
			}
		}
		map.addAttribute("user", user);
		return "/client/order_add_address";
	}

	/**
	 * 选择行政区划而改变了行政街道的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/change/district")
	public String changeDistrict(ModelMap map, Long districtId) {
		List<TdSubdistrict> subdistrict_list = tdSubdistrictService.findByDistrictIdOrderBySortIdAsc(districtId);
		map.addAttribute("subdistrict_list", subdistrict_list);
		return "/client/order_address_subdistrict";
	}

	/**
	 * 保存新的收货地址的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/new/address")
	public String orderNewAddress(HttpServletRequest req, ModelMap map, String receiveName, String receiveMobile,
			Long district, Long subdistrict, String detail) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		TdDistrict tdDistrict = tdDistrictService.findOne(district);
		TdSubdistrict tdSubdistrict = tdSubdistrictService.findOne(subdistrict);

		TdShippingAddress address = new TdShippingAddress();
		address.setCity(user.getCityName());
		address.setCityId(user.getCityId());
		address.setDetailAddress(detail);
		address.setDisctrict(tdDistrict.getName());
		address.setSubdistrict(tdSubdistrict.getName());
		address.setDistrictId(district);
		address.setSubdistrictId(subdistrict);
		address.setIsDefaultAddress(true);
		address.setReceiverMobile(receiveMobile);
		address.setReceiverName(receiveName);
		address.setSortId(1.0);
		address = tdShippingAddressService.save(address);

		List<TdShippingAddress> addressList = user.getShippingAddressList();
		if (null == addressList) {
			addressList = new ArrayList<>();
		}

		addressList.add(address);
		tdUserService.save(user);

		req.getSession().setAttribute("order_addressId", address.getId());

		return "redirect:/order";
	}

	/**
	 * 选择新的收货地址的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/change/address")
	public String changeAddress(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsername(username);
		if (null == user) {
			return "redirect:/login";
		}

		
		List<TdShippingAddress> address_list = user.getShippingAddressList();
		map.addAttribute("address_list", address_list);
		return "/client/order_change_address";
	}

	/**
	 * 确认选择收货地址的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/address/check/{id}")
	public String addressCheck(HttpServletRequest req, ModelMap map, @PathVariable Long id) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		req.getSession().setAttribute("order_addressId", id);
		return "redirect:/order";
	}

	/**
	 * 确认下单的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/check")
	@ResponseBody
	public Map<String, Object> orderPay(HttpServletRequest req) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 获取收货地址id
		Long addressId = (Long) req.getSession().getAttribute("order_addressId");
		// 获取支付方式id
		Long payTypeId = (Long) req.getSession().getAttribute("order_payTypeId");
		// 获取备注信息
		String remark = (String) req.getSession().getAttribute("order_remark");
		// 获取配送门店id
		Long diySiteId = (Long) req.getSession().getAttribute("order_diySiteId");
		// 获取配送方式id（0代表送货上门，1代表门店自提）
		Long deliveryId = (Long) req.getSession().getAttribute("order_deliveryId");
		// 获取配送日期
		String deliveryDate = (String) req.getSession().getAttribute("order_deliveryDate");
		// 获取配送时间点
		Long deliveryDetailId = (Long) req.getSession().getAttribute("order_deliveryDetailId");

		if (null == addressId) {
			res.put("message", "请选择/添加收货地址");
			return res;
		}

		if (null == payTypeId) {
			res.put("message", "请选择支付方式");
			return res;
		}

		// 创建一个订单用于存储华润订单信息
		TdOrder order_hr = new TdOrder();
		// 创建一个订单用于存储乐易装订单信息
		TdOrder order_lyz = new TdOrder();

		String username = (String) req.getSession().getAttribute("username");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		Double hr_total = 0.0;
		Double lyz_total = 0.0;
		Double total_price = 0.0;
		Double max_privilege = 0.0;
		Double total_privilege = 0.0;

		TdDiySite site = tdCommonService.getDiySite(req);

		/* 以下代码用于生成订单号 */
		Date date = new Date();
		String sDate = sdf.format(date);
		Random random = new Random();
		Integer suiji = random.nextInt(900) + 100;
		String orderNum = sDate + suiji;
		/* 生成订单号结束 */
		order_hr.setOrderNumber("HR" + orderNum);
		order_lyz.setOrderNumber("LYZ" + orderNum);

		order_hr.setRemark(remark);
		order_lyz.setRemark(remark);

		order_hr.setUsername(username);
		order_lyz.setUsername(username);

		// 获取指定id的收货地址
		TdShippingAddress address = tdShippingAddressService.findOne(addressId);
		if (null != address) {
			order_hr.setShippingAddress(address.getDetailAddress());
			order_lyz.setShippingAddress(address.getDetailAddress());

			order_hr.setShippingName(address.getReceiverName());
			order_lyz.setShippingName(address.getReceiverName());

			order_hr.setShippingPhone(address.getReceiverMobile());
			order_lyz.setShippingPhone(address.getReceiverMobile());
		}

		order_hr.setPayTypeId(payTypeId);
		order_lyz.setPayTypeId(payTypeId);

		// 获取指定的支付方式
		TdPayType payType = tdPayTypeService.findOne(payTypeId);
		if (null != payType) {
			order_hr.setPayTypeTitle(payType.getTitle());
			order_lyz.setPayTypeTitle(payType.getTitle());
		}

		if (null != deliveryId && 0L == deliveryId) {
			order_hr.setDeliverTypeTitle("送货上门");
			order_lyz.setDeliverTypeTitle("送货上门");
		}

		if (null != deliveryId && 1L == deliveryId) {
			order_hr.setDeliverTypeTitle("门店自提");
			order_lyz.setDeliverTypeTitle("门店自提");

			order_hr.setDiySiteId(diySiteId);
			order_lyz.setDiySiteId(diySiteId);

			TdDiySite diySite = tdDiySiteService.findOne(diySiteId);
			if (null != diySite) {
				order_hr.setDiySiteName(diySite.getTitle());
				order_lyz.setDiySiteName(diySite.getTitle());
			}
		}

		order_hr.setDeliveryDate(deliveryDate);
		order_lyz.setDeliveryDate(deliveryDate);

		order_hr.setDeliveryDetail(deliveryDetailId + ":30-" + (deliveryDetailId + 1) + ":30");
		order_lyz.setDeliveryDetail(deliveryDetailId + ":30-" + (deliveryDetailId + 1) + ":30");

		// 获取所有的已选商品，整合后
		List<TdCartGoods> selected_all = tdCommonService.getAllContainsColorPackage(req);

		List<TdOrderGoods> hr_order_goods_list = new ArrayList<>();
		List<TdOrderGoods> lyz_order_goods_list = new ArrayList<>();

		// 开始拆单
		if (null != selected_all) {
			for (TdCartGoods cartGoods : selected_all) {
				if (null != cartGoods) {
					TdGoods goods = tdGoodsService.findOne(cartGoods.getGoodsId());
					if (null != goods) {
						TdOrderGoods orderGoods = new TdOrderGoods();
						orderGoods.setGoodsId(cartGoods.getGoodsId());
						orderGoods.setGoodsTitle(goods.getTitle());
						orderGoods.setGoodsSubTitle(goods.getSubTitle());
						orderGoods.setGoodsCoverImageUri(goods.getCoverImageUri());
						orderGoods.setPrice(cartGoods.getPrice());
						orderGoods.setQuantity(cartGoods.getQuantity());
						orderGoods = tdOrderGoodsService.save(orderGoods);
						if (null != goods.getBelongTo() && 1L == goods.getBelongTo()) {
							hr_order_goods_list.add(orderGoods);
							hr_total += (cartGoods.getPrice() * cartGoods.getQuantity());
						}
						if (null != goods.getBelongTo() && 2L == goods.getBelongTo()) {
							lyz_order_goods_list.add(orderGoods);
							lyz_total += (cartGoods.getPrice() * cartGoods.getQuantity());
						}
						total_price += (cartGoods.getPrice() * cartGoods.getQuantity());
						max_privilege += ((cartGoods.getPrice() - cartGoods.getRealPrice()) * cartGoods.getQuantity());
					}
				}
			}
			hr_order_goods_list = tdOrderGoodsService.save(hr_order_goods_list);
			lyz_order_goods_list = tdOrderGoodsService.save(lyz_order_goods_list);
			order_hr.setOrderGoodsList(hr_order_goods_list);
			order_lyz.setOrderGoodsList(lyz_order_goods_list);
		}

		List<TdCartGoods> presented = new ArrayList<>();
		// 获取当前所有的已选
		List<TdCartGoods> selected = tdCartGoodsService.findByUsername(username);

		List<TdOrderGoods> hr_present = new ArrayList<>();
		List<TdOrderGoods> lyz_present = new ArrayList<>();

		// 获取所有的赠品
		presented = tdCommonService.getPresent(req, selected, presented);
		// 进行赠品拆单
		for (TdCartGoods present : presented) {
			if (null != present) {
				Long goodsId = present.getGoodsId();
				TdGoods goods = tdGoodsService.findOne(goodsId);
				if (null != goods) {
					TdOrderGoods orderGoods = new TdOrderGoods();
					orderGoods.setGoodsId(present.getGoodsId());
					orderGoods.setGoodsTitle(goods.getTitle());
					orderGoods.setGoodsSubTitle(goods.getSubTitle());
					orderGoods.setGoodsCoverImageUri(goods.getCoverImageUri());
					orderGoods.setPrice(0.0);
					orderGoods.setQuantity(present.getQuantity());
					orderGoods = tdOrderGoodsService.save(orderGoods);
					if (null != goods.getBelongTo() && 1L == goods.getBelongTo()) {
						hr_present.add(orderGoods);
					}
					if (null != goods.getBelongTo() && 2L == goods.getBelongTo()) {
						lyz_present.add(orderGoods);
					}
				}
			}
		}
		hr_present = tdOrderGoodsService.save(hr_present);
		lyz_present = tdOrderGoodsService.save(lyz_present);
		order_hr.setPresentedList(hr_present);
		if (hr_present.size() > 0) {
			order_hr.setIsPromotion(true);
		}
		order_lyz.setPresentedList(lyz_present);
		if (lyz_present.size() > 0) {
			order_lyz.setIsPromotion(true);
		}

		// 获取所有的小辅料
		List<TdActivityGiftList> sendGift = new ArrayList<>();
		Map<Long, Long> group = tdCommonService.getGroup(req);
		sendGift = tdCommonService.getGift(req, group, sendGift);

		List<TdOrderGoods> hr_gift = new ArrayList<>();
		List<TdOrderGoods> lyz_gift = new ArrayList<>();

		// 进行小辅料拆单
		for (TdActivityGiftList gift : sendGift) {
			Long goodsId = gift.getGoodsId();
			TdGoods goods = tdGoodsService.findOne(goodsId);
			if (null != goods) {
				TdOrderGoods orderGoods = new TdOrderGoods();
				orderGoods.setGoodsId(gift.getGoodsId());
				orderGoods.setGoodsTitle(goods.getTitle());
				orderGoods.setGoodsSubTitle(goods.getSubTitle());
				orderGoods.setGoodsCoverImageUri(goods.getCoverImageUri());
				orderGoods.setPrice(0.0);
				orderGoods.setQuantity(gift.getNumber());
				orderGoods.setSku(goods.getCode());
				orderGoods = tdOrderGoodsService.save(orderGoods);
				if (null != goods.getBelongTo() && 1L == goods.getBelongTo()) {
					hr_gift.add(orderGoods);
				}
				if (null != goods.getBelongTo() && 2L == goods.getBelongTo()) {
					lyz_gift.add(orderGoods);
				}
			}
		}
		hr_gift = tdOrderGoodsService.save(hr_gift);
		lyz_gift = tdOrderGoodsService.save(lyz_gift);
		order_hr.setGiftGoodsList(hr_gift);
		order_lyz.setGiftGoodsList(lyz_gift);

		order_hr.setProductCoupon("");
		order_lyz.setProductCoupon("");

		// 获取确定使用的产品券
		@SuppressWarnings("unchecked")
		List<TdCoupon> product_used = (List<TdCoupon>) req.getSession().getAttribute("order_productCouponUsed");

		if (null != product_used) {
			for (TdCoupon tdCoupon : product_used) {
				if (null != tdCoupon) {
					Long goodsId = tdCoupon.getGoodsId();
					TdGoods goods = tdGoodsService.findOne(goodsId);
					TdPriceListItem priceListItem = tdPriceListItemService
							.findByPriceListIdAndGoodsId(site.getPriceListId(), goodsId);
					if (null != goods) {
						if (null != goods.getBelongTo() && 1L == goods.getBelongTo()) {
							order_hr.setProductCoupon(order_hr.getProductCoupon() + "," + goods.getCode() + "* 1");
							hr_total -= priceListItem.getSalePrice();
						}
						if (null != goods.getBelongTo() && 2L == goods.getBelongTo()) {
							order_lyz.setProductCoupon(order_lyz.getProductCoupon() + "," + goods.getCode() + "* 1");
							lyz_total -= priceListItem.getSalePrice();
						}
					}
					total_price -= priceListItem.getSalePrice();
					max_privilege -= (priceListItem.getSalePrice() - priceListItem.getRealSalePrice());
				}
			}
		}

		// 获取确定使用的现金券
		@SuppressWarnings("unchecked")
		List<TdCoupon> no_product_used = (List<TdCoupon>) req.getSession().getAttribute("order_noProductCouponUsed");
		// 统计现金券使用总额
		if (null != no_product_used) {
			for (TdCoupon coupon : no_product_used) {
				if (null != coupon) {
					Double price = coupon.getPrice();
					total_privilege += price;
				}
			}
		}

		if (total_privilege > max_privilege) {
			total_privilege = max_privilege;
		}

		// 计算华润商品所占比例
		Double hr_ponit = hr_total / total_price;
		Double lyz_point = lyz_total / total_price;

		order_hr.setTotalPrice(hr_total);
		order_lyz.setTotalPrice(lyz_total);

		// 获取华润订单所使用现金券额度
		order_hr.setCashCoupon(total_privilege * hr_ponit);
		order_lyz.setCashCoupon(total_privilege * lyz_point);

		// 判断当前支付方式是否是预存款支付
		String title = payType.getTitle();
		if (title.equals("预存款")) {
			TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
			Double balance = user.getBalance();
			Double cashBalance = user.getCashBalance();
			Double unCashBalance = user.getUnCashBalance();
			if (null == balance) {
				balance = 0.0;
			}
			if (null == cashBalance) {
				cashBalance = 0.0;
			}
			if (null == unCashBalance) {
				unCashBalance = 0.0;
			}
			if (total_price > balance) {
				order_hr.setStatusId(2L);
				order_lyz.setStatusId(2L);
				if (order_hr.getOrderGoodsList().size() > 0) {
					tdOrderService.save(order_hr);
				}
				if (order_lyz.getOrderGoodsList().size() > 0) {
					tdOrderService.save(order_lyz);
				}

				tdCommonService.clear(req);
				res.put("status", -2);
				res.put("message", "您的余额不足");
				return res;
			} else {
				order_hr.setStatusId(3L);
				order_lyz.setStatusId(3L);
				if (unCashBalance > total_price) {
					user.setUnCashBalance(unCashBalance - total_price);

				} else {
					user.setUnCashBalance(0.0);
					user.setCashBalance(cashBalance + unCashBalance - total_price);
				}
				user.setBalance(balance - total_price);
				tdUserService.save(user);
			}
		} else {
			order_hr.setStatusId(2L);
			order_lyz.setStatusId(2L);

		}
		if (order_hr.getOrderGoodsList().size() > 0) {
			order_hr.setTotalPrice(hr_total);
			tdOrderService.save(order_hr);
		}
		if (order_lyz.getOrderGoodsList().size() > 0) {
			order_lyz.setTotalPrice(lyz_total);
			tdOrderService.save(order_lyz);
		}
		res.put("message", "操作成功");
		// 清空购物信息
		tdCommonService.clear(req);
		return res;
	}

}
