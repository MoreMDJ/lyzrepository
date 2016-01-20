package com.ynyes.lyz.controller.front;

import static org.apache.commons.lang3.StringUtils.leftPad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdArticle;
import com.ynyes.lyz.entity.TdArticleCategory;
import com.ynyes.lyz.entity.TdBalanceLog;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDeliveryInfoDetail;
import com.ynyes.lyz.entity.TdDistrict;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGeoInfo;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.entity.TdSetting;
import com.ynyes.lyz.entity.TdShippingAddress;
import com.ynyes.lyz.entity.TdSubdistrict;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdUserCollect;
import com.ynyes.lyz.entity.TdUserLevel;
import com.ynyes.lyz.entity.TdUserRecentVisit;
import com.ynyes.lyz.entity.TdUserSuggestion;
import com.ynyes.lyz.entity.TdUserSuggestionCategory;
import com.ynyes.lyz.service.TdArticleCategoryService;
import com.ynyes.lyz.service.TdArticleService;
import com.ynyes.lyz.service.TdBalanceLogService;
import com.ynyes.lyz.service.TdCartGoodsService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdDeliveryInfoDetailService;
import com.ynyes.lyz.service.TdDistrictService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGeoInfoService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdOrderGoodsService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdPriceListItemService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.service.TdSettingService;
import com.ynyes.lyz.service.TdShippingAddressService;
import com.ynyes.lyz.service.TdSubdistrictService;
import com.ynyes.lyz.service.TdUserCollectService;
import com.ynyes.lyz.service.TdUserLevelService;
import com.ynyes.lyz.service.TdUserRecentVisitService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.service.TdUserSuggestionCategoryService;
import com.ynyes.lyz.service.TdUserSuggestionService;
import com.ynyes.lyz.util.ClientConstant;
import com.ynyes.lyz.util.MD5;

@Controller
@RequestMapping(value = "/user")
public class TdUserController {

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdUserRecentVisitService tdUserRecentVisitService;

	@Autowired
	private TdUserCollectService tdUserCollectService;

	@Autowired
	private TdUserLevelService tdUserLevelService;

	@Autowired
	private TdOrderService tdOrderService;

	@Autowired
	private TdCommonService tdCommonService;

	@Autowired
	private TdUserSuggestionCategoryService tdUserSuggestionCategoryService;

	@Autowired
	private TdPriceListItemService tdPriceListItemService;

	@Autowired
	private TdUserSuggestionService tdUserSuggestionService;

	@Autowired
	private TdDistrictService tdDistrictService;

	@Autowired
	private TdSubdistrictService tdSubdistrictService;

	@Autowired
	private TdCityService tdCityService;

	@Autowired
	private TdShippingAddressService tdShippingAddressService;

	@Autowired
	private TdDiySiteService tdDiySiteService;

	@Autowired
	private TdBalanceLogService tdBalanceLogService;

	@Autowired
	private TdGoodsService tdGoodsService;

	@Autowired
	private TdCouponService tdCouponService;

	@Autowired
	private TdSettingService tdSettingService;

	@Autowired
	private TdArticleCategoryService tdArticleCategoryService;

	@Autowired
	private TdArticleService tdArticleService;

	@Autowired
	private TdCartGoodsService tdCartGoodsService;

	// 退货单Service Max
	@Autowired
	private TdReturnNoteService tdReturnNoteService;

	@Autowired
	private TdOrderGoodsService tdOrderGoodsService;

	@Autowired
	private TdDeliveryInfoDetailService tdDeliveryInfoDetailService;

	@Autowired
	private TdGeoInfoService tdGeoInfoService;

	/**
	 * 跳转到个人中心的方法（后期会进行修改，根据不同的角色，跳转的页面不同）
	 * 
	 * @author dengxiao
	 */
	@RequestMapping
	public String userCenter(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		map.addAttribute("user", user);

		// 获取浏览记录
		List<TdUserRecentVisit> recent_list = tdUserRecentVisitService.findByUsername(username);
		map.addAttribute("recent_list", recent_list);

		// 获取收藏记录
		List<TdUserCollect> collect_list = tdUserCollectService.findByUsername(username);
		map.addAttribute("collect_list", collect_list);

		// 获取已选
		Long number = tdCartGoodsService.countByUserId(user.getId());
		map.addAttribute("number", number);

		// 获取用户的等级
		TdUserLevel level = tdUserLevelService.findOne(user.getUserLevelId());
		map.addAttribute("level", level);

		// 获取客服电话
		List<TdSetting> all = tdSettingService.findAll();
		if (null != all && all.size() >= 1) {
			map.addAttribute("phone", all.get(0).getTelephone());
		}

		return "/client/user_center";
	}

	/**
	 * 查看我的订单的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/order/{typeId}")
	public String orderList(HttpServletRequest req, ModelMap map, @PathVariable Long typeId) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 查找所有的订单
		List<TdOrder> all_order_list = tdOrderService.findByUsernameAndStatusIdNotOrderByOrderTimeDesc(username);
		map.addAttribute("all_order_list", all_order_list);

		// 查找所有待支付的订单
		List<TdOrder> unpayed_order_list = tdOrderService.findByUsernameAndStatusId(username, 2L);
		map.addAttribute("unpayed_order_list", unpayed_order_list);

		// 查找所有待发货的订单
		List<TdOrder> undeliver_order_list = tdOrderService.findByUsernameAndStatusId(username, 3L);
		map.addAttribute("undeliver_order_list", undeliver_order_list);

		// 查找所有待收货的订单
		List<TdOrder> unsignin_order_list = tdOrderService.findByUsernameAndStatusId(username, 4L);
		map.addAttribute("unsignin_order_list", unsignin_order_list);

		// 查找所有待评价的订单
		List<TdOrder> uncomment_order_list = tdOrderService.findByUsernameAndStatusId(username, 5L);
		map.addAttribute("uncomment_order_list", uncomment_order_list);

		map.addAttribute("typeId", typeId);
		return "/client/user_order_list";
	}

	/**
	 * 跳转到我的收藏页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/collect")
	public String userCollect(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		List<TdUserCollect> collect_list = tdUserCollectService.findByUsernameOrderByCollectTimeDesc(username);
		map.addAttribute("collect_list", collect_list);
		TdDiySite diySite = tdCommonService.getDiySite(req);
		// 查找收藏的商品当前的价格
		if (null != collect_list && null != diySite) {
			for (int i = 0; i < collect_list.size(); i++) {
				TdUserCollect userCollect = collect_list.get(i);
				if (null != userCollect) {
					TdPriceListItem priceListItem = tdPriceListItemService
							.findByPriceListIdAndGoodsId(diySite.getPriceListId(), userCollect.getGoodsId());
					map.addAttribute("priceListItem" + i, priceListItem);
				}
			}
		}
		return "/client/user_collect_list";
	}

	/**
	 * 删除收藏商品的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/delete/collect")
	@ResponseBody
	public Map<String, Object> deleteCollect(HttpServletRequest req, Long id) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		// 查找到指定id的用户收藏
		TdUserCollect collect = tdUserCollectService.findOne(id);
		if (null == collect) {
			res.put("messag", "为查找到指定id的收藏");
			return res;
		}
		tdUserCollectService.delete(collect);
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到浏览记录的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/recent")
	// param为【排序字段】-【规则1】-【规则2】-【规则3】
	public String userRecent(HttpServletRequest req, ModelMap map, String param) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		if (null == param) {
			param = "0-0-0-0";
		}

		// 拆分排序字段
		String[] strs = param.split("-");
		String sortFiled = strs[0];
		String rule1 = strs[1];
		String rule2 = strs[2];
		String rule3 = strs[3];

		// 新建一个集合用于接收用户的浏览记录
		List<TdUserRecentVisit> recent_list = new ArrayList<>();

		// 获取用户归属门店的信息
		TdDiySite diySite = tdCommonService.getDiySite(req);

		if ("0".equals(sortFiled)) {
			if ("0".equals(rule1)) {
				recent_list = tdUserRecentVisitService.findByUsernameOrderByVisitTimeDesc(username);
				rule1 = "1";
			} else if ("1".equals(rule1)) {
				recent_list = tdUserRecentVisitService.findByUsernameOrderByVisitTimeAsc(username);
				rule1 = "0";
			}
		} else if ("1".equals(sortFiled)) {
			if ("0".equals(rule2)) {
				recent_list = tdUserRecentVisitService.findByUsernameOrderBySalePriceDesc(username,
						diySite.getPriceListId());
				rule2 = "1";
			} else if ("1".equals(rule2)) {
				recent_list = tdUserRecentVisitService.findByUsernameOrderBySalePriceAsc(username,
						diySite.getPriceListId());
				rule2 = "0";
			}
		} else if ("2".equals(sortFiled)) {
			if ("0".equals(rule3)) {
				recent_list = tdUserRecentVisitService.findByUsernameOrderBySoldNumberDesc(username);
				rule3 = "1";
			} else if ("1".equals(rule3)) {
				recent_list = tdUserRecentVisitService.findByUsernameOrderBySoldNumberAsc(username);
				rule3 = "0";
			}
		}

		map.addAttribute("selected_rule", Long.parseLong(sortFiled));
		map.addAttribute("rule1", rule1);
		map.addAttribute("rule2", rule2);
		map.addAttribute("rule3", rule3);

		// 遍历所有的浏览记录，获取所有浏览记录的价格
		for (int i = 0; i < recent_list.size(); i++) {
			TdUserRecentVisit recentVisit = recent_list.get(i);
			if (null != recentVisit) {
				// 获取指定商品的价目表项
				TdPriceListItem priceListItem = tdPriceListItemService
						.findByPriceListIdAndGoodsId(diySite.getPriceListId(), recentVisit.getGoodsId());
				map.addAttribute("priceListItem" + i, priceListItem);
			}
		}
		map.addAttribute("recent_list", recent_list);
		return "/client/user_recent_list";
	}

	/**
	 * 删除我的浏览记录的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/delete/recent")
	@ResponseBody
	public Map<String, Object> deleteRecent(HttpServletRequest req, Long id) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		// 查找到指定id的浏览记录
		TdUserRecentVisit recentVisit = tdUserRecentVisitService.findOne(id);
		if (null == recentVisit) {
			res.put("messag", "为查找到指定id的收藏");
			return res;
		}
		tdUserRecentVisitService.delete(recentVisit);
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到我的已选页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/selected")
	public String mySelected(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		Double total_price = 0.0;

		// 获取所有已选的商品
		List<TdCartGoods> all_selected = tdCartGoodsService.findByUserId(user.getId());
		for (int i = 0; i < all_selected.size(); i++) {
			TdCartGoods cartGoods = all_selected.get(i);
			// 获取已选商品的库存
			if (null != cartGoods) {
				TdGoods goods = tdGoodsService.findOne(cartGoods.getGoodsId());
				if (null != goods) {
					map.addAttribute("goods" + i, goods.getLeftNumber());
					// 如果已选数量大于了最大库存，则消减已选数量
					if (null != cartGoods.getQuantity() && cartGoods.getQuantity() > goods.getLeftNumber()) {
						cartGoods.setQuantity(goods.getLeftNumber());
						cartGoods.setTotalPrice(cartGoods.getPrice() * cartGoods.getQuantity());
						tdCartGoodsService.save(cartGoods);
					}
					total_price += cartGoods.getTotalPrice();
				}
			}
		}
		map.addAttribute("all_selected", all_selected);
		map.addAttribute("selected_number", tdCartGoodsService.countByUserId(user.getId()));
		map.addAttribute("totalPrice", total_price);

		return "/client/user_selected";
	}

	/**
	 * 更改已选数量的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/selected/change/quantity")
	public String selectedChangeQuantity(HttpServletRequest req, ModelMap map, Long operation, Long type, Long id) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		// 避免“空指针异常”
		if (null == user) {
			user = new TdUser();
		}
		List<TdCartGoods> selected_goods = tdCartGoodsService.findByUserId(user.getId());
		Double total_price = 0.0;
		// 操作已选商品的情况
		if (0 == type) {
			for (int i = 0; i < selected_goods.size(); i++) {
				TdCartGoods cartGoods = selected_goods.get(i);
				if (null != cartGoods && null != cartGoods.getGoodsId()
						&& cartGoods.getGoodsId().longValue() == id.longValue()) {
					if (0L == operation) {
						cartGoods.setQuantity(cartGoods.getQuantity() - 1);
					}
					if (1L == operation) {
						cartGoods.setQuantity(cartGoods.getQuantity() + 1);
					}
					tdCartGoodsService.save(cartGoods);
				}
			}
		}

		// 获取所有已选的商品
		for (int i = 0; i < selected_goods.size(); i++) {
			TdCartGoods cartGoods = selected_goods.get(i);
			// 获取已选商品的库存
			if (null != cartGoods) {
				TdGoods goods = tdGoodsService.findOne(cartGoods.getGoodsId());
				if (null != goods) {
					map.addAttribute("goods" + i, goods.getLeftNumber());
					// 如果已选数量大于了最大库存，则消减已选数量
					if (null != cartGoods.getQuantity() && cartGoods.getQuantity() > goods.getLeftNumber()) {
						cartGoods.setQuantity(goods.getLeftNumber());
					}
					cartGoods.setTotalPrice(cartGoods.getQuantity() * cartGoods.getPrice());
					cartGoods.setRealTotalPrice(cartGoods.getRealPrice() * cartGoods.getQuantity());
					cartGoods = tdCartGoodsService.save(cartGoods);
					total_price += cartGoods.getTotalPrice();
				}
			}
		}
		map.addAttribute("selected_number", tdCartGoodsService.countByUserId(user.getId()));
		map.addAttribute("all_selected", selected_goods);
		map.addAttribute("totalPrice", total_price);
		return "/client/selected_goods_and_color";
	}

	/**
	 * 跳转到咨询投诉页面的方法
	 * 
	 * @auhor dengxiao
	 */
	@RequestMapping(value = "/suggestion")
	public String userSuggestion(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 查询得到所有能用的咨询分类
		List<TdUserSuggestionCategory> category_list = tdUserSuggestionCategoryService.findAll();

		map.addAttribute("category_list", category_list);
		map.addAttribute("username", username);
		return "/client/user_suggestion";
	}

	/**
	 * 保存咨询投诉的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/suggestion/save")
	@ResponseBody
	public Map<String, Object> userSuggestionSave(HttpServletRequest req, String phone, String suggestion,
			Long categoryId) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断用户是否登陆
		String username = (String) req.getSession().getAttribute("username");
		// 获取登陆用户的信息
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("status", -2);
			return res;
		}

		// 创建一个实体用于存储咨询/投诉的信息
		TdUserSuggestion userSuggestion = new TdUserSuggestion();
		userSuggestion.setCategoryId(categoryId);
		userSuggestion.setContent(suggestion);
		userSuggestion.setUserId(user.getId());
		userSuggestion.setIsAnswered(false);
		userSuggestion.setCreateTime(new Date());
		userSuggestion.setParentId(0L);
		userSuggestion.setIsDelete(false);
		tdUserSuggestionService.save(userSuggestion);

		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到用户信息界面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/info")
	public String userInfo(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		map.addAttribute("user", user);
		return "/client/user_info";
	}

	/**
	 * 修改登陆用户生日或性别的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/change")
	@ResponseBody
	public Map<String, Object> userChangeInfo(HttpServletRequest req, Long type, String param) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断用户是否登陆
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("status", -2);
			return res;
		}

		// 如果是修改性别
		if (0L == type) {
			user.setSex(param);
		}

		// 如果是修改生日
		if (1L == type) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date birthday = null;
			try {
				birthday = sdf.parse(param + " 00:00:00");
			} catch (ParseException e) {
				e.printStackTrace();
				return res;
			}
			user.setBirthday(birthday);
		}

		tdUserService.save(user);
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到修改密码界面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/edit/password")
	public String userEditPassword(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		return "/client/edit_password";
	}

	/**
	 * 验证并保存修改密码的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/edit/password/save")
	@ResponseBody
	public Map<String, Object> userEditPasswordSave(HttpServletRequest req, String oldPassword, String newPassword) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断是否登陆
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("status", -2);
			return res;
		}

		if (null == oldPassword || !MD5.md5(oldPassword, 32).equals(user.getPassword())) {
			res.put("message", "亲，您输入的原密码不正确");
			return res;
		}

		if (MD5.md5(oldPassword, 32).equals(user.getPassword())) {
			user.setPassword(MD5.md5(newPassword, 32));
			tdUserService.save(user);
			res.put("status", 0);
		}

		return res;
	}

	/**
	 * 跳转到收货页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/address")
	public String userAddress(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		List<TdShippingAddress> address_list = user.getShippingAddressList();
		// 遍历集合获取默认收货地址
		if (null != address_list) {
			for (TdShippingAddress shippingAddress : address_list) {
				if (null != shippingAddress && null != shippingAddress.getIsDefaultAddress()
						&& shippingAddress.getIsDefaultAddress()) {
					map.addAttribute("default", shippingAddress);
				}
			}
		}
		map.addAttribute("address_list", address_list);
		return "/client/user_address";
	}

	/**
	 * 跳转到新增收货页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/address/{type}")
	public String userAddressAdd(HttpServletRequest req, ModelMap map, @PathVariable Long type, Long id,
			String receiver, String receiverMobile, String detailAddress) {
		// 判断用户是否登陆
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// type的值代表了当前进行的操作：1. type==0代表进行的是增加收货地址的操作；2. type==1代表进行的是修改收货地址的操作
		if (0L == type) {
			TdShippingAddress address = new TdShippingAddress();
			address.setCity(user.getCityName());
			// 默认联系人电话为会员的电话号码
			address.setReceiverMobile(username);
			map.addAttribute("address", address);
		}

		if (1L == type) {
			// 获取指定id的收货地址
			TdShippingAddress address = tdShippingAddressService.findOne(id);
			map.addAttribute("address", address);
			map.addAttribute("addressId", id);
		}
		map.addAttribute("city", user.getCityName());
		map.addAttribute("operation", type);
		return "/client/add_address_base";
	}

	/**
	 * 获取（指定城市/行政区划）下的所有（行政区划/行政街道）的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/address/get")
	public String addressGet(HttpServletRequest req, Long type, Long id, ModelMap map) {

		// type的值表示不同的操作：0. 获取指定id的城市的所有下属行政区划；1. 获取指定id的行政区划的所有下属行政街道
		// ;3.选择行政街道完毕，存储信息
		if (0 == type) {
			// 获取当前登陆的用户
			String username = (String) req.getSession().getAttribute("username");
			TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
			if (null != user) {
				// 获取用户城市下的所有行政区划
				TdCity city = tdCityService.findBySobIdCity(user.getCityId());
				List<TdDistrict> region_list = tdDistrictService.findByCityIdOrderBySortIdAsc(city.getId());
				map.addAttribute("region_list", region_list);
				// status的值为1表示下一步是选择行政区划】
				map.addAttribute("status", 1);
			}
		}

		if (1 == type) {
			// 获取指定的行政区划
			TdDistrict district = tdDistrictService.findOne(id);
			// 获取指定行政区划下的所有行政街道
			if (null != district) {
				List<TdSubdistrict> region_list = tdSubdistrictService
						.findByDistrictIdOrderBySortIdAsc(district.getId());
				map.addAttribute("region_list", region_list);
				// status的值为2表示下一步是选择行政街道
				map.addAttribute("status", 2);
				req.getSession().setAttribute("new_district", district.getName());
				req.getSession().setAttribute("new_district_id", id);
				// 删除session中的行政街道
				req.getSession().setAttribute("new_subdistrict", null);
				req.getSession().setAttribute("new_subdistrict_id", null);
			}
		}

		if (2 == type) {
			// 获取指定的行政街道
			TdSubdistrict subdistrict = tdSubdistrictService.findOne(id);
			// 存储行政街道
			if (null != subdistrict) {
				req.getSession().setAttribute("new_subdistrict", subdistrict.getName());
				req.getSession().setAttribute("new_subdistrict_id", id);
			}
		}

		return "/client/add_address_detail";
	}

	/**
	 * 保存新增的收货地址的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/address/add/save")
	@ResponseBody
	public Map<String, Object> userAddressAddSave(HttpServletRequest req, String receiver, String receiverMobile,
			String detailAddress, Long operation, Long addressId) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断用户是否登陆
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("status", -2);
			return res;
		}

		String district = (String) req.getSession().getAttribute("new_district");
		String subdistrict = (String) req.getSession().getAttribute("new_subdistrict");
		Long district_id = (Long) req.getSession().getAttribute("new_district_id");
		Long subdistrict_id = (Long) req.getSession().getAttribute("new_subdistrict_id");

		if (null == receiver || "".equals(receiver)) {
			res.put("message", "亲，请添加收货人姓名");
			return res;
		}

		if (null == receiverMobile || "".equals(receiverMobile)) {
			res.put("message", "亲，请添加收件人联系电话");
			return res;
		}

		if (null == district || "".equals(district)) {
			res.put("message", "亲，请选择收件地址的行政区划");
			return res;
		}

		if (null == subdistrict || "".equals(subdistrict)) {
			res.put("message", "亲，请选择收件地址的行政街道");
			return res;
		}

		if (null == detailAddress || "".equals(detailAddress)) {
			res.put("message", "亲，请添加详细地址");
			return res;
		}

		TdShippingAddress address = null;

		TdCity tdCity = tdCityService.findByCityName(user.getCityName());
		if (0L == operation) {
			address = new TdShippingAddress();
		} else if (1L == operation) {
			address = tdShippingAddressService.findOne(addressId);
		}
		address.setProvince(tdCity.getProvince());
		address.setCity(tdCity.getCityName());
		address.setCityId(tdCity.getId());
		address.setDisctrict(district);
		address.setSubdistrict(subdistrict);
		address.setDetailAddress(detailAddress);
		address.setReceiverName(receiver);
		address.setReceiverMobile(receiverMobile);
		address.setDistrictId(district_id);
		address.setSubdistrictId(subdistrict_id);
		// 获取登陆用户的收货地址
		List<TdShippingAddress> address_list = user.getShippingAddressList();
		if (null == address_list || 0 == address_list.size()) {
			address_list = new ArrayList<>();
			address.setIsDefaultAddress(true);
		}

		address = tdShippingAddressService.save(address);
		address_list.add(address);
		user.setShippingAddressList(address_list);
		tdUserService.save(user);

		// 清除session中的行政区划和行政街道
		req.getSession().setAttribute("new_district", null);
		req.getSession().setAttribute("new_district_id", null);
		req.getSession().setAttribute("new_subdistrict", null);
		req.getSession().setAttribute("new_subdistrict_id", null);

		res.put("status", 0);
		return res;
	}

	/**
	 * 操作用户收货地址删除/设为默认的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/address/operate")
	@ResponseBody
	public Map<String, Object> userAddressOperate(HttpServletRequest req, Long id, Long type) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断用户是否登陆
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("status", -2);
			return res;
		}

		// type的值为1的时候执行删除操作
		if (1L == type) {
			// 判断指定id的收货地址是否为默认地址
			TdShippingAddress address = tdShippingAddressService.findOne(id);
			// 创建一个布尔变量用于判断是否删除的是默认的
			Boolean isDefault = false;
			if (null != address && null != address.getIsDefaultAddress() && address.getIsDefaultAddress()) {
				isDefault = true;
			}

			// 直接进行删除操作
			tdShippingAddressService.delete(id);
			// 如果删除的是默认地址，则把剩余收货地址的第一个（默认）改变为默认收货地址
			if (isDefault) {
				// 因为之前做了删除操作，所以要重新获取用户的信息
				user = tdUserService.findByUsernameAndIsEnableTrue(username);
				List<TdShippingAddress> address_list = user.getShippingAddressList();
				if (null != address_list) {
					for (int i = 0; i < address_list.size(); i++) {
						if (0 == i) {
							address_list.get(i).setIsDefaultAddress(true);
							tdShippingAddressService.save(address_list.get(i));
						}
					}
				}
			}
		}

		// type的值为2的时候执行设为默认操作
		if (2L == type) {
			// 获取用户所有的收货地址
			List<TdShippingAddress> address_list = user.getShippingAddressList();
			if (null != address_list) {
				for (int i = 0; i < address_list.size(); i++) {
					if (null != address_list.get(i) && null != address_list.get(i).getIsDefaultAddress()
							&& address_list.get(i).getIsDefaultAddress()) {
						address_list.get(i).setIsDefaultAddress(false);
					}
					if (null != address_list.get(i) && null != address_list.get(i).getId()
							&& id == address_list.get(i).getId()) {
						address_list.get(i).setIsDefaultAddress(true);
					}
				}
			}
			user.setShippingAddressList(address_list);
			tdUserService.save(user);
		}

		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到用户门店归属的页面
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/diysite")
	public String userDiysite(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		map.addAttribute("user", user);

		// 虚拟门店的用户不能够更改门店
		Long customerId = user.getCustomerId();
		Long cityId = user.getCityId();

		TdDiySite site = tdDiySiteService.findByRegionIdAndCustomerId(cityId, customerId);
		if (null != site && (site.getStatus() == 2)) {
			map.addAttribute("isSelected", false);
		}
		// 获取用户所在城市的所有行政区划
		TdCity city = tdCityService.findBySobIdCity(user.getCityId());
		List<TdDistrict> district_list = tdDistrictService.findByCityIdOrderBySortIdAsc(city.getId());
		// 遍历集合，获取第一项行政区划下的所有行政街道
		if (null != district_list) {
			for (int i = 0; i < district_list.size(); i++) {
				if (0 == i) {
					TdDistrict district = district_list.get(i);
					// 获取指定行政区划下的所有门店
					if (null != district) {
						List<TdDiySite> all_site = tdDiySiteService.findByDisctrictIdOrderBySortIdAsc(district.getId());
						map.addAttribute("all_site", all_site);
					}
				}
			}
		}
		map.addAttribute("district_list", district_list);
		return "/client/user_diy_site";
	}

	/**
	 * 根据选择的行政区划获取其下属的所有门店信息
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/diysite/get")
	public String getDiySite(ModelMap map, Long districtId) {
		// 根据行政区划的id获取指定行政区划下的所有门店
		List<TdDiySite> all_site = tdDiySiteService.findByDisctrictIdOrderBySortIdAsc(districtId);
		map.addAttribute("all_site", all_site);
		return "/client/site_in_district";
	}

	/**
	 * 用户选择门店的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/select/diysite")
	@ResponseBody
	public Map<String, Object> selectDiySite(HttpServletRequest req, Long id) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断用户是否登陆
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("status", -2);
			return res;
		}

		// 获取指定id的门店信息
		TdDiySite diySite = tdDiySiteService.findOne(id);
		// 更改用户的归属门店
		user.setUpperDiySiteId(diySite.getId());
		user.setDiyName(diySite.getTitle());
		user.setCustomerId(diySite.getCustomerId());
		tdUserService.save(user);

		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到我的财富的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/fortune")
	public String userFortune(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 检验当前优惠券是否存在过期的
		tdCommonService.checkUserCoupon(req);

		List<TdCoupon> no_product_coupon_list = tdCouponService
				.findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdNotOrderByGetTimeDesc(username, 3L);
		List<TdCoupon> product_coupon_list = tdCouponService
				.findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdOrderByGetTimeDesc(username, 3L);

		Double no_product_total = 0.0;

		// 获取现金券的总额
		for (TdCoupon coupon : no_product_coupon_list) {
			if (null != coupon && null != coupon.getPrice()) {
				no_product_total += coupon.getPrice();
			}
		}

		// 产品全张数
		Long totalNummber = 0L;
		for (TdCoupon tdCoupon : product_coupon_list) {
			if (null != tdCoupon && null != tdCoupon.getGetNumber()) {
				totalNummber += tdCoupon.getGetNumber();
			}
		}

		map.addAttribute("user", user);
		map.addAttribute("no_product_total", no_product_total);
		map.addAttribute("product_coupon_list", product_coupon_list);
		map.addAttribute("totalNumber", totalNummber);

		return "/client/user_fortune";
	}

	/**
	 * 跳转到我的钱包的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/wallet")
	public String userWallet(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 查询到登陆用户所有的钱包操作记录
		Page<TdBalanceLog> balance_log_page = tdBalanceLogService
				.findByUserIdAndIsSuccessTrueOrderByCreateTimeDesc(user.getId(), 0, ClientConstant.MAXRECENTNUM);
		map.addAttribute("balance_log_page", balance_log_page);
		map.addAttribute("user", user);
		return "/client/user_wallet";
	}

	/**
	 * 跳转到充值页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/recharge")
	public String userRecharge(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		return "/client/user_recharge";
	}

	/**
	 * 跳转到用户指定优惠券的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/coupon/{type}")
	public String userProductCoupon(HttpServletRequest req, ModelMap map, @PathVariable Long type) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		if (0L == type) {
			// 获取所有未使用且未过期的现金券
			List<TdCoupon> no_product_unused_list = tdCouponService
					.findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdNotOrderByGetTimeDesc(username, 3L);
			map.addAttribute("no_product_unused_list", no_product_unused_list);
			// 获取最近一个月内已过期的现金券
			List<TdCoupon> no_product_out_date_list = tdCouponService
					.findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotAndExpireTimeBetweenOrderByExpireTimeDesc(
							username, 3L);
			map.addAttribute("no_product_out_date_list", no_product_out_date_list);
			// 获取最近一个月内已使用的现金券
			List<TdCoupon> no_product_used_list = tdCouponService
					.findByUsernameAndIsUsedTrueAndTypeCategoryIdNotAndUseTimeBetweenOrderByUseTimeDesc(username, 3L);
			map.addAttribute("no_product_used_list", no_product_used_list);
			return "/client/user_cash_coupon";
		} else {
			// 获取所有未使用且未过期的产品券
			List<TdCoupon> product_unused_list = tdCouponService
					.findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdOrderByGetTimeDesc(username, 3L);
			map.addAttribute("product_unused_list", product_unused_list);
			// 获取最近一个月内已过期的产品券
			List<TdCoupon> product_out_date_list = tdCouponService
					.findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdAndExpireTimeBetweenOrderByExpireTimeDesc(
							username, 3L);
			map.addAttribute("product_out_date_list", product_out_date_list);
			// 获取最近一个月内已经使用的产品券
			List<TdCoupon> product_used_list = tdCouponService
					.findByUsernameAndIsUsedTrueAndTypeCategoryIdAndUseTimeBetweenOrderByUseTimeDesc(username, 3L);
			map.addAttribute("product_used_list", product_used_list);
			return "/client/user_product_coupon";
		}
	}

	/**
	 * 取消订单的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/order/cancel")
	@ResponseBody
	public Map<String, Object> userOrderCancel(Long orderId) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		// 查询到指定的订单
		TdOrder order = tdOrderService.findOne(orderId);
		// 首先判断订单是不是运费单
		Double fee = order.getDeliverFee();
		if (null != fee && fee > 0) {
			// 此时是属于运费单的情况了，现在判断下其他单据有没有退还
			String orderNumber = order.getOrderNumber();
			if (null != orderNumber && !"".equals(orderNumber)) {
				Boolean isCancel = true;
				String otherOrderNumber = "";
				String newOrderNumber = "";
				for (int i = 0; i < orderNumber.length(); i++) {
					if (orderNumber.charAt(i) >= 48 && orderNumber.charAt(i) <= 57) {
						newOrderNumber += orderNumber.charAt(i);
					}
				}
				// 查询此单的其他单据有没有取消
				List<TdOrder> list = tdOrderService.findByOrderNumberContaining(newOrderNumber);
				if (null != list && list.size() > 0) {
					for (TdOrder associated_order : list) {
						if (null != associated_order && null != associated_order.getStatusId()) {
							// 如果是本单就不用判断了
							if (null != associated_order.getId() && associated_order.getId() != orderId) {
								Long statusId = associated_order.getStatusId();
								if (7L != statusId.longValue()) {
									otherOrderNumber += associated_order.getOrderNumber();
									isCancel = false;
									break;
								}
							}
						}

					}
				}
				if (!isCancel) {
					res.put("message", "亲，您需要先取消掉关联<br>订单（" + otherOrderNumber + "）才能够取消此单");
					return res;
				}
			}
		}

		
		if (null != order.getStatusId() && 3L == order.getStatusId()) {
			// 进行资金退还
			Double totalPrice = order.getTotalPrice();
			Double unCashBalanceUsed = order.getUnCashBalanceUsed();
			if (null == unCashBalanceUsed) {
				unCashBalanceUsed = 0.00;
			}
			Double cashBalanceUsed = order.getCashBalanceUsed();
			if (null == cashBalanceUsed) {
				cashBalanceUsed = 0.00;
			}
			String productCouponId = order.getProductCouponId();
			String cashCouponId = order.getCashCouponId();
			Long userId = order.getUserId();
			TdUser user = tdUserService.findOne(userId);
			if (null == user) {
				res.put("message", "未找到订单的归属用户");
				return res;
			}
			user.setBalance(user.getBalance() + unCashBalanceUsed + cashBalanceUsed);
			user.setUnCashBalance(user.getUnCashBalance() + unCashBalanceUsed);
			user.setCashBalance(user.getCashBalance() + cashBalanceUsed);
			tdUserService.save(user);

			// 拆分使用的现金券的id
			if (null != cashCouponId && !"".equals(cashCouponId)) {
				String[] cashs = cashCouponId.split(",");
				if (null != cashs) {
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
				if (null != products) {
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
			
			// 生成退货单
			if (null != order) {
				TdReturnNote returnNote = new TdReturnNote();

				// 退货单编号
				Date current = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				String curStr = sdf.format(current);
				Random random = new Random();

				returnNote.setReturnNumber("T" + curStr + leftPad(Integer.toString(random.nextInt(999)), 3, "0"));

				// 添加订单信息
				returnNote.setOrderNumber(order.getOrderNumber());
				// 支付方式
				returnNote.setPayTypeId(order.getPayTypeId());
				returnNote.setPayTypeTitle(order.getPayTypeTitle());
				// 门店信息
				if (null != order.getDiySiteId()) {
					TdDiySite diySite = tdDiySiteService.findOne(order.getDiySiteId());
					returnNote.setDiySiteId(order.getDiySiteId());
					returnNote.setDiySiteTel(diySite.getServiceTele());
					returnNote.setDiySiteTitle(diySite.getTitle());
					returnNote.setDiySiteAddress(diySite.getAddress());
				}

				// 退货信息
				returnNote.setUsername(order.getUsername());
				returnNote.setRemarkInfo("取消订单退货");

				Long turnType = 2L;
				// 退货方式
				returnNote.setTurnType(turnType);
				// 原订单配送方式
				if ("门店自提".equals(order.getDeliverTypeTitle())) {
					if (turnType.equals(1L)) {
						returnNote.setStatusId(3L); // 门店自提单-门店到店退货 待验货
					} else {
						returnNote.setStatusId(2L); // 门店自提单-物流取货 待取货
					}
				} else {
					if (turnType.equals(1L)) {
						returnNote.setStatusId(3L); // 送货上门单 门店到店退货 待验货
					} else {
						returnNote.setStatusId(2L); // 送货上门单 物流取货 待取货
					}
				}

				returnNote.setDeliverTypeTitle(order.getDeliverTypeTitle());
				returnNote.setOrderTime(new Date());

				returnNote.setTurnPrice(order.getTotalGoodsPrice());
				List<TdOrderGoods> orderGoodsList = new ArrayList<>();
				if (null != order.getOrderGoodsList()) {
					for (TdOrderGoods oGoods : order.getOrderGoodsList()) {
						TdOrderGoods orderGoods = new TdOrderGoods();

						orderGoods.setBrandId(oGoods.getBrandId());
						orderGoods.setBrandTitle(oGoods.getBrandTitle());
						orderGoods.setGoodsId(oGoods.getGoodsId());
						orderGoods.setGoodsSubTitle(oGoods.getGoodsSubTitle());
						orderGoods.setSku(oGoods.getSku());
						orderGoods.setGoodsCoverImageUri(oGoods.getGoodsCoverImageUri());
						orderGoods.setGoodsColor(oGoods.getGoodsColor());
						orderGoods.setGoodsCapacity(oGoods.getGoodsCapacity());
						orderGoods.setGoodsVersion(oGoods.getGoodsVersion());
						orderGoods.setGoodsSaleType(oGoods.getGoodsSaleType());
						orderGoods.setGoodsTitle(oGoods.getGoodsTitle());

						orderGoods.setPrice(oGoods.getPrice());
						orderGoods.setQuantity(oGoods.getQuantity());

						orderGoods.setDeliveredQuantity(oGoods.getDeliveredQuantity());
						orderGoods.setPoints(oGoods.getPoints());
						// tdOrderGoodsService.save(orderGoods);
						// 添加商品信息
						orderGoodsList.add(orderGoods);

						// 订单商品设置退货为True
						oGoods.setIsReturnApplied(true);
						// 更新订单商品信息是否退货状态
						tdOrderGoodsService.save(oGoods);
					}
				}

				returnNote.setReturnGoodsList(orderGoodsList);
				tdOrderGoodsService.save(orderGoodsList);
				// 保存退货单
				returnNote = tdReturnNoteService.save(returnNote);

				tdCommonService.sendBackMsgToWMS(returnNote);
				
				returnNote.setStatusId(3L);
				
				tdReturnNoteService.save(returnNote);
				
				order.setStatusId(7L);
				order.setIsRefund(true);
				tdOrderService.save(order);
			}
		}

		order.setStatusId(7L);
		tdOrderService.save(order);
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到订单详情的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/order/detail/{id}")
	public String userOrderDetail(HttpServletRequest req, ModelMap map, @PathVariable Long id) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		// 获取指定id的订单信息
		TdOrder order = tdOrderService.findOne(id);
		map.addAttribute("order", order);

		// 获取配送的信息
		if (null != order) {
			String orderNumber = order.getOrderNumber();
			List<TdDeliveryInfoDetail> delivery_list = tdDeliveryInfoDetailService.findBySubOrderNumber(orderNumber);
			if (null != delivery_list && delivery_list.size() > 0) {
				TdDeliveryInfoDetail detail = delivery_list.get(0);
				if (null != detail) {
					List<TdGeoInfo> geoInfo_list = tdGeoInfoService.findByOpUserOrderByTimeDesc(detail.getOpUser());
					TdUser tdUser = tdUserService.findByOpUser(detail.getOpUser());
					if (null != geoInfo_list && geoInfo_list.size() > 0) {
						TdGeoInfo geoInfo = geoInfo_list.get(0);
						map.addAttribute("geoInfo", geoInfo);
						map.addAttribute("tdUser", tdUser);
					}
				}
			}
		}

		return "/client/user_order_detail";
	}

	/**
	 * 用户修改归属门店并且保存的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/diy/save")
	@ResponseBody
	public Map<String, Object> userDiySave(HttpServletRequest req, Long id) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		// 获取登陆用户的信息
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			res.put("message", "未获取到登陆用户的信息，操作失败");
			return res;
		}

		// 获取指定id的门店信息
		TdDiySite site = tdDiySiteService.findOne(id);
		if (null == site) {
			res.put("message", "未获取到指定门店的信息，操作失败");
			return res;
		}

		user.setUpperDiySiteId(site.getId());
		user.setDiyName(site.getTitle());
		tdUserService.save(user);
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到提现页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/deposit")
	public String userDeposit(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		return "/client/user_deposit";
	}

	/**
	 * 跳转到优惠券使用说明的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/coupon/description")
	public String userCouponDescription(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 获取优惠券使用说明的文章分类
		TdArticleCategory category = tdArticleCategoryService.findByTitle("优惠券使用规则");
		// 获取指定的文章类别id获取所有的文章，按照排序号正序排序
		if (null != category) {
			List<TdArticle> article_list = tdArticleService.findByCategoryIdOrderBySortIdAsc(category.getId());
			if (null != article_list && article_list.size() > 0) {
				map.addAttribute("article", article_list.get(0));
			}
		}

		return "/client/coupon_description";
	}

	/**
	 * 删除订单的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/order/delete")
	@ResponseBody
	public Map<String, Object> orderDelete(Long orderId) {
		Map<String, Object> res = new HashMap<>();
		TdOrder order = tdOrderService.findOne(orderId);
		order.setStatusId(8L);
		tdOrderService.save(order);
		res.put("status", 0);
		return res;
	}

	/**
	 * 申请退货
	 * 
	 * @author Max
	 */
	@RequestMapping(value = "/order/return", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> orderReturn(Long id, String remark, Long turnType, HttpServletRequest req) {
		Map<String, Object> res = new HashMap<>();
		res.put("code", 1);

		String username = (String) req.getSession().getAttribute("username");

		if (null == username) {
			res.put("message", "请重新登录");
			return res;
		}

		if (null != id) {
			TdOrder order = tdOrderService.findOne(id);

			if (null != order) {
				TdReturnNote returnNote = new TdReturnNote();

				// 退货单编号
				Date current = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				String curStr = sdf.format(current);
				Random random = new Random();

				returnNote.setReturnNumber("T" + curStr + leftPad(Integer.toString(random.nextInt(999)), 3, "0"));

				// 添加订单信息
				returnNote.setOrderNumber(order.getOrderNumber());
				// 支付方式
				returnNote.setPayTypeId(order.getPayTypeId());
				returnNote.setPayTypeTitle(order.getPayTypeTitle());
				// 门店信息
				if (null != order.getDiySiteId()) {
					TdDiySite diySite = tdDiySiteService.findOne(order.getDiySiteId());
					returnNote.setDiySiteId(order.getDiySiteId());
					returnNote.setDiySiteTel(diySite.getServiceTele());
					returnNote.setDiySiteTitle(diySite.getTitle());
					returnNote.setDiySiteAddress(diySite.getAddress());
				}

				// 退货信息
				returnNote.setUsername(username);
				returnNote.setRemarkInfo(remark);

				// 退货方式
				returnNote.setTurnType(turnType);
				// 原订单配送方式
				if ("门店自提".equals(order.getDeliverTypeTitle())) {
					if (turnType == 1) {
						returnNote.setStatusId(3L); // 门店自提单-门店到店退货 待验货
					} else {
						returnNote.setStatusId(2L); // 门店自提单-物流取货 待取货
					}
				} else {
					if (turnType == 1) {
						returnNote.setStatusId(3L); // 送货上门单 门店到店退货 待验货
					} else {
						returnNote.setStatusId(2L); // 送货上门单 物流取货 待取货
					}
				}

				returnNote.setDeliverTypeTitle(order.getDeliverTypeTitle());
				returnNote.setOrderTime(new Date());

				returnNote.setTurnPrice(order.getTotalGoodsPrice());
				List<TdOrderGoods> orderGoodsList = new ArrayList<>();
				if (null != order.getOrderGoodsList()) {
					for (TdOrderGoods oGoods : order.getOrderGoodsList()) {
						TdOrderGoods orderGoods = new TdOrderGoods();

						orderGoods.setBrandId(oGoods.getBrandId());
						orderGoods.setBrandTitle(oGoods.getBrandTitle());
						orderGoods.setGoodsId(oGoods.getGoodsId());
						orderGoods.setGoodsSubTitle(oGoods.getGoodsSubTitle());
						orderGoods.setSku(oGoods.getSku());
						orderGoods.setGoodsCoverImageUri(oGoods.getGoodsCoverImageUri());
						orderGoods.setGoodsColor(oGoods.getGoodsColor());
						orderGoods.setGoodsCapacity(oGoods.getGoodsCapacity());
						orderGoods.setGoodsVersion(oGoods.getGoodsVersion());
						orderGoods.setGoodsSaleType(oGoods.getGoodsSaleType());
						orderGoods.setGoodsTitle(oGoods.getGoodsTitle());

						orderGoods.setPrice(oGoods.getPrice());
						orderGoods.setQuantity(oGoods.getQuantity());

						orderGoods.setDeliveredQuantity(oGoods.getDeliveredQuantity());
						orderGoods.setPoints(oGoods.getPoints());
						// tdOrderGoodsService.save(orderGoods);
						// 添加商品信息
						orderGoodsList.add(orderGoods);

						// 订单商品设置退货为True
						oGoods.setIsReturnApplied(true);
						// 更新订单商品信息是否退货状态
						tdOrderGoodsService.save(oGoods);
					}
				}

				returnNote.setReturnGoodsList(orderGoodsList);
				tdOrderGoodsService.save(orderGoodsList);
				// 保存退货单
				tdReturnNoteService.save(returnNote);

				order.setStatusId(7L);
				order.setIsRefund(true);
				tdOrderService.save(order);

				res.put("code", 0);
				res.put("message", "提交退货成功");
				return res;
			}
		}

		res.put("message", "参数错误");

		return res;

	}

}
