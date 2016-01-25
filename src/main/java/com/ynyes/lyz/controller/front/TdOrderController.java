package com.ynyes.lyz.controller.front;

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

import com.ibm.icu.math.BigDecimal;
import com.ynyes.lyz.entity.TdBrand;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDistrict;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdShippingAddress;
import com.ynyes.lyz.entity.TdSubdistrict;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdBrandService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdDistrictService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdOrderGoodsService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdPayTypeService;
import com.ynyes.lyz.service.TdPriceCountService;
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
	private TdDistrictService tdDistrictService;

	@Autowired
	private TdOrderService tdOrderService;

	@Autowired
	private TdBrandService tdBrandService;

	@Autowired
	private TdOrderGoodsService tdOrderGoodsService;

	@Autowired
	private TdPriceCountService tdPriceCouintService;

	/**
	 * 清空部分信息的控制器
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/clear")
	public String clearInfomations(HttpServletRequest req) {
		req.getSession().setAttribute("order_temp", null);
		req.getSession().setAttribute("maxCash", null);
		req.getSession().setAttribute("maxCoupon", null);
		req.getSession().setAttribute("usedNow", null);
		return "redirect:/order";
	}

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

		TdOrder order_temp = new TdOrder();

		// 生成虚拟订单
		if (null != id) {
			order_temp = tdOrderService.findOne(id);
		} else {
			order_temp = (TdOrder) req.getSession().getAttribute("order_temp");
		}

		// 如果session中没有虚拟订单，则通过方法生成一个
		if (null == order_temp) {
			order_temp = tdCommonService.createVirtual(req);
		}

		order_temp = tdPriceCouintService.checkCouponIsUsed(order_temp);

		// 计算价格和最大优惠券使用金额
		Map<String, Object> results = tdPriceCouintService.countPrice(order_temp, user);

		// 如果计算的结果不为NULL，就获取一系列的值
		if (null != results) {
			TdOrder order_count = (TdOrder) results.get("result");
			Double max = (Double) results.get("max");
			Boolean isCoupon = (Boolean) results.get("isCoupon");

			// 得到订单的金额
			if (null != order_count) {
				order_temp = order_count;
				map.addAttribute("order", order_temp);
			}

			// 获取该单能够使用的最大预存款
			if (null != max) {
				map.addAttribute("max", max);
			}

			if (null != isCoupon) {
				map.addAttribute("isCoupon", isCoupon);
			}

		}

		// 获取已选的所有品牌的id
		List<Long> brandIds = tdCommonService.getBrandId(user.getId(), order_temp);

		// 创建一个集合存储用户所能够使用的现金券
		List<TdCoupon> no_product_coupon_list = new ArrayList<>();

		// 创建一个集合存储用户所能够使用的产品券
		List<TdCoupon> product_coupon_list = new ArrayList<>();

		// 遍历所有的品牌，查找用户对于当前订单可以使用的现金券
		for (Long brandId : brandIds) {
			List<TdCoupon> coupon_list = tdCouponService
					.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndBrandIdOrderByGetTimeDesc(
							username, 1L, brandId);
			no_product_coupon_list.addAll(coupon_list);
		}

		// 遍历所有已选，查找用户对于当前订单可以使用的指定商品现金券和产品券
		List<TdOrderGoods> selected = order_temp.getOrderGoodsList();
		if (null != selected) {
			for (TdOrderGoods goods : selected) {
				if (null != goods) {
					// 查找能使用的产品券
					List<TdCoupon> p_coupon_list = tdCouponService
							.findByUsernameAndIsUsedFalseAndTypeCategoryId3LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
									username, goods.getGoodsId());
					product_coupon_list.addAll(p_coupon_list);

					// 查找能使用的指定商品现金券
					List<TdCoupon> c_coupon_list = tdCouponService
							.findByUsernameAndIsUsedFalseAndTypeCategoryId2LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
									username, goods.getGoodsId());
					no_product_coupon_list.addAll(c_coupon_list);
				}
			}
		}

		String productCouponId = order_temp.getProductCouponId();
		String cashCouponId = order_temp.getCashCouponId();

		if (null != productCouponId && !"".equals(productCouponId)) {
			String[] strings = productCouponId.split(",");
			if (null != strings) {
				map.addAttribute("product_used", strings.length);
			}
		}

		if (null != cashCouponId && !"".equals(cashCouponId)) {
			String[] strings = cashCouponId.split(",");
			if (null != strings) {
				map.addAttribute("no_product_used", strings.length);
			}
		}

		// 将虚拟订单添加到session中
		req.getSession().setAttribute("order_temp", order_temp);

		// 清空已选
		if (null == id) {
			tdCommonService.clear(req);
		}

		map.addAttribute("order", order_temp);
		map.addAttribute("no_product_coupon_list", no_product_coupon_list);
		map.addAttribute("product_coupon_list", product_coupon_list);
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
		if (null != req.getSession().getAttribute("order_temp")) {
			TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
			order = tdOrderService.findOne(order.getId());
			if (null != order) {
				if (null == order.getPresentedList()) {
					order.setPresentedList(new ArrayList<TdOrderGoods>());
				}

				if (null == order.getGiftGoodsList()) {
					order.setGiftGoodsList(new ArrayList<TdOrderGoods>());
				}
			}

			map.addAttribute("order", order);
		}
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
		
		if (null != order)
		{
			order.setRemark(remark);
			tdOrderService.save(order);
		}
		
		req.getSession().setAttribute("order_temp", order);
		
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
		Long cityId = user.getCityId();
		TdCity city = tdCityService.findBySobIdCity(cityId);

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
		List<TdDiySite> diy_list = tdDiySiteService.findByRegionIdOrderBySortIdAsc(city.getSobIdCity());
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
			// 判断当前支付方式是否是“门店自提”
			String title = order.getPayTypeTitle();
			if (null != title && "到店支付".equals(title)) {
				// 此时将支付方式更改为到店支付
				TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("货到付款");
				if (null != payType) {
					order.setPayTypeId(payType.getId());
					order.setPayTypeTitle(payType.getTitle());
				}
			}
		}
		if (2L == type) {
			order.setDeliverTypeTitle("门店自提");
			// 判断当前支付方式是否是“货到付款”
			String title = order.getPayTypeTitle();
			if (null != title && "货到付款".equals(title)) {
				// 此时将支付方式更改为到店支付
				TdPayType payType = tdPayTypeService.findByTitleAndIsEnableTrue("到店支付");
				if (null != payType) {
					order.setPayTypeId(payType.getId());
					order.setPayTypeTitle(payType.getTitle());
				}
			}
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
		order.setDiySiteCode(tdDiySite.getStoreCode());

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

		// 获取所有的在线支付方式
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
			pay_type_list.add(payType);
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

		// 获取虚拟订单
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");

		// 获取所有已选的商品
		List<TdOrderGoods> selected_list = order.getOrderGoodsList();

		// 使用一个集合存储所有的品牌
		List<TdBrand> brand_list = new ArrayList<>();

		Map<Long, List<TdCoupon>> cash_coupon_map = new HashMap<>();

		// 获取所有的品牌
		List<Long> brandIds = tdCommonService.getBrandId(user.getId(), order);
		if (null != brandIds && brandIds.size() > 0) {
			for (int i = 0; i < brandIds.size(); i++) {
				Long brandId = brandIds.get(i);
				// 根据品牌id查找通用优惠券
				if (null != brandId) {
					List<TdCoupon> cash_coupon_brand = tdCouponService
							.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndBrandIdOrderByGetTimeDesc(
									username, 1L, brandId);
					if (null != cash_coupon_brand && cash_coupon_brand.size() > 0) {
						cash_coupon_map.put(brandId, cash_coupon_brand);
					}
					TdBrand brand = tdBrandService.findOne(brandId);
					if (null != brand) {
						brand_list.add(brand);
					}
				}
			}
		}

		// 创建一个集合用于存储当前用户能够在此单使用的产品券
		List<TdCoupon> product_coupon_list = new ArrayList<>();
		// 遍历已选，一个一个的查找用户能够使用的产品券或指定产品现金券
		for (TdOrderGoods cartGoods : selected_list) {
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
					Long brandId = cartGoods.getBrandId();
					List<TdCoupon> list = cash_coupon_map.get(brandId);
					if (null == list) {
						list = new ArrayList<>();
					}
					list.addAll(no_product_coupon_by_goodsId);
					cash_coupon_map.put(brandId, list);
				}
			}
		}

		// 使用的现金券的id
		String cashCouponId = order.getCashCouponId();
		// 拆分现金券id
		String[] cash_ids = cashCouponId.split(",");
		List<Long> no_product_used = new ArrayList<>();
		if (null != cash_ids) {
			for (String id : cash_ids) {
				if (null != id && !"".equals(id.trim())) {
					no_product_used.add(Long.parseLong(id));
				}
			}
		}

		// 使用产品券的id
		String productCouponId = order.getProductCouponId();
		// 拆分产品券id
		String[] product_ids = productCouponId.split(",");
		List<Long> product_used = new ArrayList<>();
		if (null != product_ids) {
			for (String id : product_ids) {
				if (null != id && !"".equals(id.trim())) {
					product_used.add(Long.parseLong(id));
				}
			}
		}

		// 跳转到选择现金券的页面
		if (0L == type) {
			for (Long brandId : cash_coupon_map.keySet()) {
				if (null != brandId) {
					List<TdCoupon> list = cash_coupon_map.get(brandId);
					if (null != list) {
						map.addAttribute("coupons" + brandId, list);
					}
				}
			}
			map.addAttribute("brand_list", brand_list);
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

		// 获取虚拟订单
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");

		String productCouponId = order.getProductCouponId();
		if (null == productCouponId) {
			productCouponId = "";
		}
		String cashCouponId = order.getCashCouponId();
		if (null == cashCouponId) {
			cashCouponId = "";
		}

		// 获取指定id的优惠券
		TdCoupon coupon = tdCouponService.findOne(id);
		if (null == coupon) {
			res.put("message", "未找到指定的优惠券");
			return res;
		}

		// 判断当前优惠券是否过期或是被使用
		if ((null != coupon.getIsOutDate() && coupon.getIsOutDate())
				|| (null != coupon.getIsUsed() && coupon.getIsUsed())) {
			res.put("message", "此优惠券已被使用或已过期");
			return res;
		}

		// 获取每个品牌的优惠券的最大使用额度和当前使用额度
		Map<Long, Double[]> permit = tdPriceCouintService.getPermit(order);
		if (null == permit) {
			res.put("message", "操作优惠券失败");
			return res;
		}

		Long brandId = coupon.getBrandId();
		TdBrand brand = tdBrandService.findOne(brandId);
		// 获取该品牌的优惠券【可使用限额，已使用限额】的队列
		Double[] permits = permit.get(brandId);
		if (null == permits) {
			permits = new Double[2];
			permits[0] = 0.00;
			permits[1] = 0.00;
		}
		if (null == permits[0]) {
			permits[0] = 0.00;
		}
		if (null == permits[1]) {
			permits[1] = 0.00;
		}

		if (0L == type) {
			if (0L == status) {
				if (null == brandId) {
					res.put("message", "未找到指定优惠券的信息");
					return res;
				}
				if ((permits[1] + coupon.getPrice()) > permits[0]) {
					if (0.00 == permits[0].doubleValue()) {
						res.put("message", "本单不能使用" + brand.getTitle() + "公司<br>的优惠券");
					} else {
						res.put("message", "您所能使用的" + brand.getTitle() + "公司<br>的优惠券最大限额为" + permits[1] + "元");
					}
					return res;
				} else {
					// 创建一个布尔变量用于判断该张券是否在当前订单使用过，以应对网络条件不好的情况下，同一张券在一张订单中多次使用的情况
					Boolean isHave = false;

					if (null != cashCouponId && !"".equals(cashCouponId)) {
						String[] strings = cashCouponId.split(",");
						if (null != strings && strings.length > 0) {
							for (String sId : strings) {
								if (null != sId) {
									Long theId = Long.parseLong(sId);
									if (null != theId && theId.longValue() == coupon.getId().longValue()) {
										isHave = true;
									}
								}
							}
						}
					}

					// 指定的券在本单中没有使用时，才能够添加成功
					if (!isHave) {
						cashCouponId += coupon.getId() + ",";
						order.setCashCouponId(cashCouponId);
						req.getSession().setAttribute("order_temp", order);
						tdOrderService.save(order);
					}
				}
			}
			if (1L == status) {
				if (!"".equals(cashCouponId)) {
					// 拆分当前使用的现金券
					String[] strings = cashCouponId.split(",");
					// 创建一个新的变量用于存储删减后的现金券使用情况
					String ids = "";
					// 遍历现金券，当id与当前获取的优惠券的id不相同，添加到新的使用记录中
					if (null != strings) {
						for (String sCouponId : strings) {
							if (null != sCouponId) {
								Long couponId = Long.valueOf(sCouponId);
								if (null != couponId && couponId.longValue() != coupon.getId().longValue()) {
									ids += (sCouponId + ",");
								}
							}
							cashCouponId = ids;
						}
						order.setCashCouponId(cashCouponId);
						req.getSession().setAttribute("order_temp", order);
						tdOrderService.save(order);
					}
				}
			}
		}

		if (1L == type) {
			// 获取订单的已选商品
			List<TdOrderGoods> goodsList = order.getOrderGoodsList();
			if (null == goodsList) {
				res.put("message", "未检索到订单的商品信息");
				return res;
			}
			if (0L == status) {
				// 遍历订单商品，查找到与产品券对应的
				for (TdOrderGoods orderGoods : goodsList) {
					if (null != orderGoods && null != orderGoods.getGoodsId()
							&& orderGoods.getGoodsId().longValue() == coupon.getGoodsId().longValue()) {
						// 获取此件商品的产品券使用数量
						Long couponNumber = orderGoods.getCouponNumber();

						if (null == couponNumber) {
							couponNumber = 0L;
						}

						// 如果使用的产品券已经等于商品的数量，那么就不能够再使用了
						if (couponNumber == orderGoods.getQuantity()) {
							res.put("message", "您不能使用更多对于<br>该件产品的优惠券了");
							return res;
						} else {
							// 创建一个布尔变量用于判断该张券是否在当前订单使用过，以应对网络条件不好的情况下，同一张券在一张订单中多次使用的情况
							Boolean isHave = false;
							if (null != productCouponId && !"".equals(productCouponId)) {
								String[] strings = productCouponId.split(",");
								if (null != strings && strings.length > 0) {
									for (String sId : strings) {
										if (null != sId) {
											Long theId = Long.parseLong(sId);
											if (null != theId && theId.longValue() == coupon.getId().longValue()) {
												isHave = true;
											}
										}
									}
								}
							}

							if (!isHave) {
								orderGoods.setCouponNumber(couponNumber + 1L);
								tdOrderGoodsService.save(orderGoods);
								productCouponId += coupon.getId() + ",";
								order.setProductCouponId(productCouponId);
								req.getSession().setAttribute("order_temp", order);
								tdOrderService.save(order);
							}
						}
					}
				}
			}
			if (1L == status) {
				// 遍历已选商品，查找到与指定产品券所对应的那一个
				for (TdOrderGoods orderGoods : goodsList) {
					if (null != orderGoods && null != orderGoods.getGoodsId()
							&& orderGoods.getGoodsId().longValue() == coupon.getGoodsId().longValue()) {
						// 获取该件商品使用产品券的数量
						Long couponNumber = orderGoods.getCouponNumber();
						if (null == couponNumber) {
							couponNumber = 0L;
						} else {
							orderGoods.setCouponNumber(orderGoods.getCouponNumber() - 1L);
						}
						tdOrderGoodsService.save(orderGoods);
						if (!"".equals(productCouponId)) {
							String[] strings = productCouponId.split(",");
							// 创建一个变量用于存储新的产品券使用情况
							String ids = "";
							for (String sCouponId : strings) {
								if (null != sCouponId) {
									Long couponId = Long.valueOf(sCouponId);
									if (null != couponId && couponId.longValue() != coupon.getId().longValue()) {
										ids += (sCouponId + ",");
									}
									productCouponId = ids;
								}
							}
						}
						order.setProductCouponId(productCouponId);
						req.getSession().setAttribute("order_temp", order);
						tdOrderService.save(order);
					}
				}

			}
		}

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
		TdCity city = tdCityService.findBySobIdCity(cityId);
		if (null != cityId) {
			// 查找指定城市下的所有行政区划
			List<TdDistrict> district_list = tdDistrictService.findByCityIdOrderBySortIdAsc(city.getId());
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

		// 将用户所有的收货地址都设置为非默认
		List<TdShippingAddress> shippingAddressList = user.getShippingAddressList();
		if (null != shippingAddressList) {
			for (TdShippingAddress address : shippingAddressList) {
				if (null != address) {
					address.setIsDefaultAddress(false);
					tdShippingAddressService.save(address);
				}
			}
		}

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

		// Add by Shawn
		order.setProvince(address.getProvince());
		order.setCity(address.getCity());
		order.setDisctrict(address.getDisctrict());
		order.setSubdistrict(address.getSubdistrict());
		order.setDetailAddress(address.getDetailAddress());

		order.setShippingAddress(
				address.getCity() + address.getDisctrict() + address.getSubdistrict() + address.getDetailAddress());
		order.setShippingName(address.getReceiverName());
		order.setShippingPhone(address.getReceiverMobile());
		order.setDeliverFee(tdSubdistrict.getDeliveryFee());
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

		// Add by Shawn
		order.setProvince(address.getProvince());
		order.setCity(address.getCity());
		order.setDisctrict(address.getDisctrict());
		order.setSubdistrict(address.getSubdistrict());
		order.setDetailAddress(address.getDetailAddress());

		order.setShippingAddress(
				address.getCity() + address.getDisctrict() + address.getSubdistrict() + address.getDetailAddress());
		order.setShippingName(address.getReceiverName());
		order.setShippingPhone(address.getReceiverMobile());
		tdOrderService.save(order);
		req.getSession().setAttribute("order_temp", order);
		return "redirect:/order";
	}

	/**
	 * 进行支付的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/check")
	@ResponseBody
	public Map<String, Object> checkOrder(HttpServletRequest req, Boolean userCash, Double userUsed, ModelMap map) {
		System.err.println("进入支付控制器");
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 获取登陆用户
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("message", "未找到指定的用户");
			return res;
		}

		// 获取虚拟订单
		System.err.println("开始获取虚拟订单");
		TdOrder order_temp = (TdOrder) req.getSession().getAttribute("order_temp");
		
		// add by Shawn
		// 解决内存溢出的bug
		if (null == order_temp)
		{
			res.put("message", "未找到虚拟订单");
			return res;
		}

		System.err.println("获取虚拟订单中的地址信息");
		String address = order_temp.getShippingAddress();
		String shippingName = order_temp.getShippingName();
		String shippingPhone = order_temp.getShippingPhone();

		System.err.println("判断是否填写收货地址");
		if ((null == address || null == shippingName || null == shippingPhone)
				&& !"门店自提".equals(order_temp.getDeliverTypeTitle())) {
			res.put("message", "请填写收货地址");
			return res;
		}

		System.err.println("开始判断用户是否属于线上支付");
		// 判断用户是否是线下付款
		Boolean isOnline = false;
		Long payTypeId = order_temp.getPayTypeId();
		TdPayType payType = tdPayTypeService.findOne(payTypeId);
		if (null != payType && payType.getIsOnlinePay()) {
			System.err.println("用户属于线上支付");
			isOnline = true;
		}

		System.err.println("开始获取该订单使用的优惠券id");
		String cashCouponId = order_temp.getCashCouponId();
		String productCouponId = order_temp.getProductCouponId();

		System.err.println("开始忽略小数点后2位之后的数字");
		BigDecimal b = new BigDecimal(order_temp.getTotalPrice());
		order_temp.setTotalPrice(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		order_temp.setActualPay(userUsed);

		if (isOnline) {
			// 判断是否还有未支付的金额
			if (userUsed < order_temp.getTotalPrice()) {
				// 跳转第三方
				// res.put("url", payType.get);
				res.put("message", "您的余额不足");
				return res;
			} else {
				// 将选择的现金券和产品券设置为已使用
				if (null != cashCouponId) {
					String[] cashs = cashCouponId.split(",");
					if (null != cashs) {
						for (String sId : cashs) {
							if (null != sId && !"".equals(sId)) {
								Long id = Long.valueOf(sId);
								TdCoupon coupon = tdCouponService.findOne(id);
								if (null != coupon) {
									coupon.setIsUsed(true);
									tdCouponService.save(coupon);
								}
							}
						}
					}
				}

				if (null != productCouponId) {
					String[] products = productCouponId.split(",");
					if (null != products) {
						for (String sId : products) {
							if (null != sId && !"".equals(sId)) {
								Long id = Long.valueOf(sId);
								TdCoupon coupon = tdCouponService.findOne(id);
								if (null != coupon) {
									coupon.setIsUsed(true);
									tdCouponService.save(coupon);
								}
							}
						}
					}
				}
				order_temp.setStatusId(3L);
			}
		} else {
			// 将选择的现金券和产品券设置为已使用
			if (null != cashCouponId && !"".equals(cashCouponId)) {
				String[] cashs = cashCouponId.split(",");
				if (null != cashs) {
					for (String sId : cashs) {
						if (null != sId) {
							Long id = Long.valueOf(sId);
							TdCoupon coupon = tdCouponService.findOne(id);
							if (null != coupon) {
								coupon.setIsUsed(true);
								tdCouponService.save(coupon);
							}
						}
					}
				}
			}

			if (null != productCouponId && !"".equals(productCouponId)) {
				String[] products = productCouponId.split(",");
				if (null != products) {
					for (String sId : products) {
						if (null != sId) {
							Long id = Long.valueOf(sId);
							TdCoupon coupon = tdCouponService.findOne(id);
							if (null != coupon) {
								coupon.setIsUsed(true);
								tdCouponService.save(coupon);
							}
						}
					}
				}
			}
			order_temp.setStatusId(3L);
		}
		// 获取用户的不可体现余额
		Double unCashBalance = user.getUnCashBalance();
		if (null == unCashBalance) {
			unCashBalance = 0.00;
		}

		// 获取用户的可提现余额
		Double cashBalance = user.getCashBalance();
		if (null == cashBalance) {
			cashBalance = 0.00;
		}

		Double balance = user.getBalance();
		if (null == balance) {
			balance = 0.00;
		}

		if (unCashBalance >= userUsed) {
			user.setUnCashBalance(user.getUnCashBalance() - userUsed);
			order_temp.setUnCashBalanceUsed(userUsed);
		} else {
			user.setUnCashBalance(0.0);
			user.setCashBalance(user.getCashBalance() + user.getUnCashBalance() - userUsed);
			order_temp.setUnCashBalanceUsed(user.getUnCashBalance());
			order_temp.setCashBalanceUsed(userUsed - user.getUnCashBalance());
		}
		user.setBalance(user.getBalance() - userUsed);
		tdUserService.save(user);

		tdOrderService.save(order_temp);

		res.put("status", 0);
		res.put("message", "支付成功");
		return res;
	}

	/**
	 * 确认下单并拆单的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/pay")
	public String orderPay(HttpServletRequest req) {
		System.err.println("进入确认下单的方法");
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		TdOrder order = (TdOrder) req.getSession().getAttribute("order_temp");
		if (null != order && null != order.getOrderNumber()) {
			if (order.getOrderNumber().contains("XN")) {
				tdCommonService.dismantleOrder(req, username);
			}
		}
		return "redirect:/user/order/0";
	}

}
