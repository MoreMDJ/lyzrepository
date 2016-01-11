package com.ynyes.lyz.controller.front;

import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdBrand;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDistrict;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdShippingAddress;
import com.ynyes.lyz.entity.TdSubdistrict;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdBrandService;
import com.ynyes.lyz.service.TdCartGoodsService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdCouponService;
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
	private TdOrderService tdOrderService;

	@Autowired
	private TdBrandService tdBrandService;

	/**
	 * 跳转到填写订单的页面
	 * 
	 * @author dengxiao
	 */
	@RequestMapping
	public String writeOrderInfo(HttpServletRequest req, ModelMap map, Long id) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		map.addAttribute("user", user);

		// 创建一个布尔值用于判断能否使用优惠券
		Boolean isCoupon = true;

		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		if (null == order) {
			order = tdCommonService.createVirtual(req);
			// 将其放入session中
			req.getSession().setAttribute("order_temp", order);
		}

		// 清空已选
		tdCommonService.clear(req);

		String deliverTypeTitle = order.getDeliverTypeTitle();
		// 如果配送方式是到店自提，則不能使用任何优惠券
		if ("门店自提".equals(deliverTypeTitle) && "到店支付".equals(order.getPayTypeTitle())) {
			isCoupon = false;
		}

		map.addAttribute("order", order);
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
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		if (null != order) {
			if (null == order.getPresentedList()) {
				order.setPresentedList(new ArrayList<TdOrderGoods>());
			}

			if (null == order.getGiftGoodsList()) {
				order.setGiftGoodsList(new ArrayList<TdOrderGoods>());
			}
		}
		map.addAttribute("order", order);
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
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		order.setRemark(remark);
		req.getSession().setAttribute("order_temp", order);
		tdOrderService.save(order);
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

		// 获取虚拟订单的信息
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");

		// 获取当前选择的门店
		Long diySiteId = order.getDiySiteId();
		// 获取当前选择的配送方式（0. 送货上门；1. 门店自提）
		String deliverTypeTitle = order.getDeliverTypeTitle();
		Long deliveryId = null;
		if ("送货上门".equals(deliverTypeTitle)) {
			deliveryId = 1L;
		}
		if ("门店自提".equals(deliverTypeTitle)) {
			deliveryId = 2L;
		}
		// 获取当前选择的配送时间
		String deliveryDate = order.getDeliveryDate();
		// 获取当前选择的配送时间段的id
		Long deliveryDetailId = order.getDeliveryDetailId();

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

		// 获取虚拟订单的信息
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		if (1L == type) {
			order.setDeliverTypeTitle("送货上门");
		}
		if (2L == type) {
			order.setDeliverTypeTitle("门店自提");
		}

		order.setDeliveryDate(date);
		order.setDeliveryDetailId(detailTime);

		// 获取指定的门店
		TdDiySite tdDiySite = tdDiySiteService.findOne(diySite);
		if (null == tdDiySite) {
			tdDiySite = new TdDiySite();
		}
		order.setDiySiteId(tdDiySite.getId());
		order.setDiySiteName(tdDiySite.getTitle());
		order.setDiySitePhone(tdDiySite.getServiceTele());

		req.getSession().setAttribute("order_temp", order);
		tdOrderService.save(order);

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

		// 从session获取临时订单
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		if (null == order) {
			order = new TdOrder();
		}

		map.addAttribute("payTypeId", order.getPayTypeId());

		// 获取所有的支付方式
		List<TdPayType> pay_type_list = tdPayTypeService.findByIsOnlinePayTrueAndIsEnableTrueOrderBySortIdAsc();

		// 获取配送方式
		String deliveryType = order.getDeliverTypeTitle();

		if ("送货上门".equals(deliveryType)) {
			// 查询是否具有货到付款的支付方式
			TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("货到付款");
			map.addAttribute("cashOndelivery", payType);
		}

		if ("门店自提".equals(deliveryType)) {
			// 查找是否具有到店支付的支付方式
			TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("到店支付");
			map.addAttribute("cashOndelivery", payType);
		}

		map.addAttribute("pay_type_list", pay_type_list);
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
		List<TdCartGoods> selected_list = tdCartGoodsService.findByUsername(username);

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
			TdPayType payType = tdPayTypeService.findOne(id);
			TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
			order.setPayTypeTitle(payType.getTitle());
			order.setPayTypeId(payType.getId());
			req.getSession().setAttribute("order_temp", order);
			tdOrderService.save(order);
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

		// 从session获取临时订单
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		order.setShippingAddress(
				address.getCity() + address.getDisctrict() + address.getSubdistrict() + address.getDetailAddress());
		order.setShippingName(address.getReceiverName());
		order.setShippingPhone(address.getReceiverMobile());
		req.getSession().setAttribute("order_temp", order);
		tdOrderService.save(order);

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
		TdShippingAddress address = tdShippingAddressService.findOne(id);
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		order.setShippingAddress(
				address.getCity() + address.getDisctrict() + address.getSubdistrict() + address.getDetailAddress());
		order.setShippingName(address.getReceiverName());
		order.setShippingPhone(address.getReceiverMobile());
		tdOrderService.save(order);
		req.getSession().setAttribute("order_temp", order);
		return "redirect:/order";
	}

	/**
	 * 确认下单的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/pay")
	public String orderPay(HttpServletRequest req) {

		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 获取虚拟订单
		TdOrder order_temp = (TdOrder) req.getSession().getAttribute("order_temp");
		if (null == order_temp) {
			order_temp = new TdOrder();
		}

		// 创建一个map用于存储拆单后的所有订单
		Map<Long, TdOrder> order_map = new HashMap<>();

		// 获取所有的品牌
		List<TdBrand> brand_list = tdBrandService.findAll();
		if (null != brand_list) {
			for (TdBrand brand : brand_list) {
				TdOrder order = new TdOrder();
				order.setOrderNumber(order_temp.getOrderNumber().replace("XN", brand.getShortName()));
				order.setShippingAddress(order_temp.getShippingAddress());
				order.setShippingName(order_temp.getShippingName());
				order.setShippingPhone(order_temp.getShippingPhone());
				order.setDeliverFee(0.00);
				order.setDeliverTypeTitle(order_temp.getDeliverTypeTitle());
				order.setDeliveryDate(order_temp.getDeliveryDate());
				order.setDeliveryDetailId(order_temp.getDeliveryDetailId());
				order.setOrderGoodsList(new ArrayList<TdOrderGoods>());
				order.setTotalGoodsPrice(0.00);
				order.setTotalPrice(0.00);
				order.setLimitCash(0.00);
				order.setCashCoupon(0.00);
				order.setLimitCash(0.00);
				order.setProductCoupon("");
				order.setCashCouponId("");
				order.setUsername(username);
			}
		}

		List<TdOrderGoods> goodsList = order_temp.getOrderGoodsList();
		// 对已选商品进行拆单
		for (TdOrderGoods orderGoods : goodsList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				TdOrder order = order_map.get(brandId);
				List<TdOrderGoods> orderGoodsList = order.getOrderGoodsList();
				if (null == orderGoodsList) {
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
				order.setTotalGoodsPrice(
						order.getTotalGoodsPrice() + (orderGoods.getPrice() * orderGoods.getQuantity()));
				order.setTotalPrice(order.getTotalGoodsPrice() + (orderGoods.getPrice() * orderGoods.getQuantity()));
			}
		}

		List<TdOrderGoods> presentedList = order_temp.getPresentedList();
		// 对赠品进行拆单
		for (TdOrderGoods orderGoods : presentedList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				TdOrder order = order_map.get(brandId);
				List<TdOrderGoods> orderGoodsList = order.getPresentedList();
				if (null == orderGoodsList) {
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
			}
		}

		List<TdOrderGoods> giftGoodsList = order_temp.getGiftGoodsList();
		// 对赠送的小辅料进行拆单
		for (TdOrderGoods orderGoods : giftGoodsList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				TdOrder order = order_map.get(brandId);
				List<TdOrderGoods> orderGoodsList = order.getGiftGoodsList();
				if (null == orderGoodsList) {
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
			}
		}

		// 获取使用现金券的金额
		Double cashCoupon = order_temp.getCashCoupon();
		if (null == cashCoupon) {
			cashCoupon = 0.00;
		}
		// 拆分已使用的现金券
		String cashCouponId = order_temp.getCashCouponId();
		// 分解cashCouponId
		if (null != cashCouponId) {
			String[] cashIds = cashCouponId.split(",");
			for (String id : cashIds) {
				if (null != id) {
					Long couponId = Long.parseLong(id);
					// 根据优惠券的id查找优惠券
					TdCoupon coupon = tdCouponService.findOne(couponId);
					if (null != coupon) {
						Long goodsId = coupon.getGoodsId();
						// 如果goodsId存在，则表示这张优惠券是指定产品现金券
						if (null != goodsId) {
							TdGoods goods = tdGoodsService.findOne(goodsId);
							Long brandId = goods.getBrandId();
							TdOrder order = order_map.get(brandId);
							order.setCashCoupon(order.getCashBalanceUsed() + coupon.getPrice());
							// 余下的金额暂不统计，在后面按照比例拆分
							cashCoupon -= coupon.getPrice();
							order.setTotalPrice(order.getTotalPrice() - coupon.getPrice());
						}
					}
				}
			}
		}

		// 拆分使用的产品券
		String productCouponId = order_temp.getProductCouponId();
		// 分解
		String[] productIds = productCouponId.split(",");
		for (String id : productIds) {
			if (null != id) {
				Long couponId = Long.parseLong(id);
				TdCoupon coupon = tdCouponService.findOne(couponId);
				if (null != coupon) {
					Long goodsId = coupon.getGoodsId();
					if (null != goodsId) {
						TdGoods goods = tdGoodsService.findOne(goodsId);
						Long brandId = goods.getBrandId();
						TdOrder order = order_map.get(brandId);
						order.setProductCouponId(coupon.getId() + ",");
						order.setProductCoupon(goods.getTitle() + "【" + goods.getCode() + "】*1,");
						List<TdOrderGoods> list = order.getOrderGoodsList();
						for (TdOrderGoods orderGoods : list) {
							if (null != orderGoods && null != orderGoods.getGoodsId()
									&& coupon.getGoodsId() == orderGoods.getGoodsId()) {
								order.setTotalPrice(order.getTotalPrice() - orderGoods.getPrice());
							}
						}
					}
				}
			}
		}

		// 开始进行剩余优惠券（即是通用现金券）的拆分，同时也可以进行可提现余额，不可提现余额的拆分

		Double total = 0.00;
		Double cashBalanceUsed = order_temp.getCashBalanceUsed();
		if (null == cashBalanceUsed) {
			cashBalanceUsed = 0.00;
		}
		Double unCashBalanceUsed = order_temp.getUnCashBalanceUsed();
		if (null == unCashBalanceUsed) {
			unCashBalanceUsed = 0.00;
		}

		// 获取目前的总金额
		for (TdOrder order : order_map.values()) {
			if (null != order && null != order.getTotalPrice()) {
				total += order.getTotalPrice();
			}
		}

		// 在此循环，拆分通用现金券额度，可提现余额，不可提现余额
		for (TdOrder order : order_map.values()) {
			if (null != order && null != order.getTotalPrice()) {
				if (total != 0) {
					Double point = order.getTotalPrice() / total;
					order.setCashCoupon(order.getCashCoupon() + (cashCoupon * point));
					order.setCashBalanceUsed(cashBalanceUsed * point);
					order.setUnCashBalanceUsed(unCashBalanceUsed * point);
				}
			}
		}

		// 查询是否存在乐易装的品牌
		TdBrand brand = tdBrandService.findByTitle("华润");
		if (null != brand) {
			Long brandId = brand.getId();
			TdOrder order = order_map.get(brandId);
			// 运费放置在乐易装的订单上
			order.setDeliverFee(order_temp.getDeliverFee());
			order.setTotalPrice(order.getTotalPrice() + order.getDeliverFee());
			order.setTotalGoodsPrice(order.getTotalGoodsPrice() + order.getDeliverFee());
		}

		// 遍历存储
		for (TdOrder order : order_map.values()) {
			if (null != order && null != order.getTotalGoodsPrice() && order.getTotalGoodsPrice() > 0) {
				tdOrderService.save(order);
			}
		}

		// 删除虚拟订单
		tdOrderService.delete(order_temp);

		return "redirect:/order";
	}

}
