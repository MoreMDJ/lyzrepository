package com.ynyes.lyz.controller.front;

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

import com.ynyes.lyz.entity.TdActivity;
import com.ynyes.lyz.entity.TdCartColorPackage;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdSetting;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdUserCollect;
import com.ynyes.lyz.entity.TdUserComment;
import com.ynyes.lyz.service.TdActivityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdPriceListItemService;
import com.ynyes.lyz.service.TdSettingService;
import com.ynyes.lyz.service.TdUserCollectService;
import com.ynyes.lyz.service.TdUserCommentService;
import com.ynyes.lyz.service.TdUserService;

@Controller
@RequestMapping(value = "/goods")
public class TdGoodsController {

	@Autowired
	private TdCommonService tdCommonService;

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdGoodsService tdGoodsService;

	@Autowired
	private TdUserCommentService tdUserCommentService;

	@Autowired
	private TdPriceListItemService tdPriceListItemService;

	@Autowired
	private TdUserCollectService tdUserCollectService;

	@Autowired
	private TdActivityService tdActivityService;

	@Autowired
	private TdSettingService tdSettingService;

	/*
	 *********************************** 普通下单模式的控制器和方法********************************************
	 */

	/**
	 * 跳转到普通下单（一键下单）页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/normal/list")
	public String goodsListNormal(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		tdCommonService.setHeader(req, map);
		tdCommonService.getCategory(req, map);
		Long number = tdCommonService.getSelectedNumber(req);
		// 将已选商品的数量（包括调色包）添加到ModelMap中
		map.addAttribute("selected_number", number);
		return "/client/goods_list_normal";
	}
	/*
	 *********************************** 普通下单结束******************************************************
	 */

	/*
	 *********************************** 步骤下单模式的控制器和方法*******************************************
	 */

	/**
	 * 跳转到步骤下单页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/step/list")
	public String goodsListStep(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		tdCommonService.setHeader(req, map);
		tdCommonService.getCategory(req, map);
		Long number = tdCommonService.getSelectedNumber(req);
		// 将已选商品的数量（包括调色包）添加到ModelMap中
		map.addAttribute("selected_number", number);
		return "/client/goods_list_step";
	}

	/*
	 *********************************** 步骤下单结束******************************************************
	 */

	/*
	 *********************************** 公共************************************************************
	 */

	/**
	 * 获取调色包的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/get/color")
	public String getColor(HttpServletRequest req, Long goodsId, ModelMap map) {
		// 根据goodsId获取指定的商品
		TdGoods goods = tdGoodsService.findOne(goodsId);
		// 创建一个集合用于存储指定商品特调色的调色包商品
		List<TdGoods> color_package_list = new ArrayList<>();

		// 获取指定商品可选调色包
		if (null != goods && null != goods.getColorPackageSku()) {
			String[] all_color_code = goods.getColorPackageSku().split(",");
			for (int i = 0; i < all_color_code.length; i++) {
				// 获取指定sku的调色包商品
				TdGoods colorGoods = tdGoodsService.findByCode(all_color_code[i]);
				if (null != colorGoods) {
					color_package_list.add(colorGoods);
				}
			}
		}

		// 获取用户的门店信息
		TdDiySite diySite = tdCommonService.getDiySite(req);
		Long priceListId = null;
		if (null != diySite) {
			priceListId = diySite.getPriceListId();
		}

		// 获取指定调色包对于登陆用户的价格
		for (int i = 0; i < color_package_list.size(); i++) {
			// 获取指定调色包对于用户的价格
			TdPriceListItem priceListItem = tdPriceListItemService.findByPriceListIdAndGoodsId(priceListId,
					color_package_list.get(i).getId());
			// 添加默认单价：第一个调色包的商品
			if (0 == i && null != priceListItem) {
				map.addAttribute("unit_price", priceListItem.getSalePrice());
			}
			// 添加默认库存：第一个调色包的库存
			if (0 == i) {
				map.addAttribute("inventory", color_package_list.get(0).getLeftNumber());
			}
			map.addAttribute("colorPackagePriceListItem" + i, priceListItem);
		}

		List<TdCartColorPackage> colors = tdCommonService.getSelectedColorPackage(req);
		map.addAttribute("select_colors", colors);

		map.addAttribute("goodsId", goodsId);
		map.addAttribute("color_package_list", color_package_list);
		return "/client/color_package";
	}

	/**
	 * 添加已选调色包的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/color/add")
	public String colorAdd(String colorName, Long quantity, ModelMap map, HttpServletRequest req) {
		colorName = colorName.trim();
		// 获取指定调色包商品
		TdGoods goods = tdGoodsService.findByCode(colorName);
		// 获取该调色包的价格
		TdDiySite diySite = tdCommonService.getDiySite(req);
		Long priceListId = null;
		if (null != diySite) {
			priceListId = diySite.getPriceListId();
		}
		// 根据价目表id和调色包商品的id查找到指定的调色包价目表项
		TdPriceListItem priceListItem = tdPriceListItemService.findByPriceListIdAndGoodsId(priceListId, goods.getId());
		// 创建一个已选调色包实体，用于存储各项已选数据
		TdCartColorPackage tdCartColorPackage = new TdCartColorPackage();
		// ********************************开始设置属性*******************************************
		tdCartColorPackage.setQuantity(quantity);
		tdCartColorPackage.setSalePrice(priceListItem.getSalePrice());
		tdCartColorPackage.setRealPrice(priceListItem.getRealSalePrice());
		tdCartColorPackage.setTotalPrice(tdCartColorPackage.getQuantity() * tdCartColorPackage.getSalePrice());
		tdCartColorPackage.setGoodsId(goods.getId());
		tdCartColorPackage.setImageUri(goods.getCoverImageUri());
		tdCartColorPackage.setNumber(goods.getCode());
		tdCartColorPackage.setGoodsTitle(goods.getTitle());
		// ********************************设置属性结束*******************************************

		// 获取所有已经选择的调色包
		List<TdCartColorPackage> all_color = tdCommonService.getSelectedColorPackage(req);

		// 创建一个布尔对象用于表示当前添加的调色包是不是已选中已经有了的，其初始值为false，表示没有
		Boolean isHave = false;
		for (int i = 0; i < all_color.size(); i++) {
			TdCartColorPackage cartColorPackage = all_color.get(i);
			if (null != cartColorPackage && null != cartColorPackage.getGoodsId()
					&& goods.getId() == cartColorPackage.getColorPackageId()) {
				isHave = true;
				cartColorPackage.setQuantity(tdCartColorPackage.getQuantity() + quantity);
				cartColorPackage.setSalePrice(tdCartColorPackage.getSalePrice());
			}
		}
		// 如果没有包含，则将新选择的调色包添加到已选中
		if (!isHave) {
			all_color.add(tdCartColorPackage);
		}

		req.getSession().setAttribute("all_color", all_color);

		map.addAttribute("unit_price", priceListItem.getSalePrice());
		map.addAttribute("select_colors", tdCommonService.getSelectedColorPackage(req));
		// 获取所有已选商品的数量
		map.addAttribute("selected_number", tdCommonService.getSelectedNumber(req));
		return "/client/selected_color_package";
	}

	/**
	 * 删除已选调色包的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/delete/color")
	@ResponseBody
	public Map<String, Object> deleteColor(HttpServletRequest req, Long colorPackageGoodsId) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 获取所有已经选择的调色包
		List<TdCartColorPackage> all_color = tdCommonService.getSelectedColorPackage(req);
		// 创建一个新的集合用于存储删除已选调色包后的数据
		List<TdCartColorPackage> new_color = new ArrayList<>();
		// 遍历已选调色包，找到指定id的调色包
		for (int i = 0; i < all_color.size(); i++) {
			TdCartColorPackage cartColorPackage = all_color.get(i);
			if (null != cartColorPackage && null != cartColorPackage.getGoodsId()
					&& cartColorPackage.getGoodsId() != colorPackageGoodsId) {
				new_color.add(cartColorPackage);
			}
		}
		// 操作完成之后让原集合失效
		all_color = null;
		req.getSession().setAttribute("all_color", new_color);
		res.put("selected_number", tdCommonService.getSelectedNumber(req));
		res.put("status", 0);
		return res;
	}

	/**
	 * 将商品加入已选的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/add/cart")
	@ResponseBody
	// params的格式为：goodsId + quantity - goodsId +quantity - ......
	public Map<String, Object> addCart(HttpServletRequest req, String params) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		// 获取登陆用户的门店信息
		TdDiySite diySite = tdCommonService.getDiySite(req);
		List<TdCartGoods> selected_goods = tdCommonService.getSelectedGoods(req);
		// param的格式为：goodsId+quantity
		String[] param = params.split("\\-");
		for (int i = 0; i < param.length; i++) {
			String item = param[i];
			String[] goodsId_quantity = item.split("\\+");
			// 获取指定商品
			TdGoods goods = tdGoodsService.findOne(Long.parseLong(goodsId_quantity[0]));
			// 获取指定商品对于用户的价格
			TdPriceListItem priceListItem = tdPriceListItemService.findByPriceListIdAndGoodsId(diySite.getPriceListId(),
					goods.getId());
			// 创建一个实体用于存储拆分好的goodsId和quantity
			TdCartGoods cartGoods = new TdCartGoods(Long.parseLong(goodsId_quantity[0]),
					Long.parseLong(goodsId_quantity[1]));
			cartGoods.setGoodsTitle(goods.getTitle());
			cartGoods.setGoodsCoverImageUri(goods.getCoverImageUri());
			if (null != priceListItem) {
				cartGoods.setPrice(priceListItem.getSalePrice());
				cartGoods.setRealPrice(priceListItem.getRealSalePrice());
				cartGoods.setSku(goods.getCode());
			}
			Boolean isHave = false;
			// 遍历已选商品的集合，判断新的商品是否已选
			for (int j = 0; j < selected_goods.size(); j++) {
				TdCartGoods tdCartGoods = selected_goods.get(j);
				if (null != tdCartGoods && null != tdCartGoods.getGoodsId()
						&& tdCartGoods.getGoodsId() == Long.parseLong(goodsId_quantity[0])) {
					// 在goodsId相同的情况下就代表已经被选择了，修改被选数量即可
					tdCartGoods.setPrice(tdCartGoods.getPrice());
					tdCartGoods.setQuantity(tdCartGoods.getQuantity() + Long.parseLong(goodsId_quantity[1]));
					isHave = true;
				}
			}
			// 新的商品没有在已选中找到，则将其添加进入已选
			if (!isHave) {
				selected_goods.add(cartGoods);
			}
		}
		req.getSession().setAttribute("all_selected", selected_goods);
		res.put("selected_number", tdCommonService.getSelectedNumber(req));
		res.put("stauts", 0);
		return res;
	}

	/**
	 * 查看商品详情的方法（跳转到详情页）
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/detail/{goodsId}")
	public String goodsDetail(HttpServletRequest req, ModelMap map, @PathVariable Long goodsId) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		tdCommonService.addUserRecentVisit(req, map, goodsId);

		// 获取用户的门店
		TdDiySite diySite = tdCommonService.getDiySite(req);
		// 根据门店信息获取用户的价目表
		TdPriceListItem priceListItem = tdPriceListItemService.findByPriceListIdAndGoodsId(diySite.getPriceListId(),
				goodsId);
		map.addAttribute("priceListItem", priceListItem);

		// 获取评论
		List<TdUserComment> user_comment_list = tdUserCommentService.findByGoodsIdAndIsShowable(goodsId);
		map.addAttribute("user_comment_list", user_comment_list);

		// 查询是否收藏该商品
		Boolean isCollect = false;
		TdUserCollect collect = tdUserCollectService.findByUsernameAndGoodsId(username, goodsId);
		if (null != collect) {
			isCollect = true;
		}

		// 创建一个集合用于存储该商品参加的活动
		List<TdActivity> activity_list = new ArrayList<>();

		// 获取该商品参加的所有活动
		if (null == priceListItem.getActivities()) {
			String activities = priceListItem.getActivities();
			if (null != activities) {
				String[] all_activity = activities.split(",");
				if (null != all_activity) {
					for (String sId : all_activity) {
						if (null != sId) {
							TdActivity activity = tdActivityService.findOne(Long.parseLong(sId));
							activity_list.add(activity);
						}
					}
				}
			}
		}

		// 获取客服电话
		List<TdSetting> all = tdSettingService.findAll();
		if (null != all && all.size() >= 1) {
			map.addAttribute("phone", all.get(0).getTelephone());
		}

		map.addAttribute("activity_list", activity_list);
		map.addAttribute("isCollect", isCollect);
		return "/client/goods_detail";
	}

	/**
	 * 添加收藏的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/operate/collection")
	@ResponseBody
	public Map<String, Object> addCollection(HttpServletRequest req, Long goodsId) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		// 获取登陆用户名
		String username = (String) req.getSession().getAttribute("username");
		// 查找指定商品是否收藏
		TdUserCollect userCollect = tdUserCollectService.findByUsernameAndGoodsId(username, goodsId);
		if (null == userCollect) {
			// 如果没有收藏则添加收藏
			userCollect = new TdUserCollect();
			TdGoods goods = tdGoodsService.findOne(goodsId);
			userCollect.setUsername(username);
			userCollect.setGoodsId(goods.getId());
			userCollect.setGoodsTitle(goods.getTitle());
			userCollect.setGoodsCoverImageUri(goods.getCoverImageUri());
			userCollect.setCollectTime(new Date());
			tdUserCollectService.save(userCollect);
		} else {
			// 如果已经收藏则取消收藏
			tdUserCollectService.delete(userCollect);
		}
		res.put("status", 0);
		return res;
	}

	/**
	 * 跳转到商品搜索页面的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/search")
	// param为【排序字段】-【规则1】-【规则2】-【规则3】
	public String goodsSearch(HttpServletRequest req, ModelMap map, String keywords, String param) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		map.addAttribute("user", user);

		// 获取用户的门店
		TdDiySite diySite = tdCommonService.getDiySite(req);

		if (null == param) {
			param = "0-0-0-0";
		}

		// 拆分排序字段
		String[] strs = param.split("-");
		String sortFiled = strs[0];
		String rule1 = strs[1];
		String rule2 = strs[2];
		String rule3 = strs[3];

		// 新建一个集合用于存储用户的搜索结果
		List<TdGoods> goods_list = new ArrayList<>();

		if ("0".equals(sortFiled)) {
			if ("0".equals(rule1)) {
				goods_list = tdGoodsService.searchGoodsOrderBySortIdAsc(keywords);
				rule1 = "1";
			} else if ("1".equals(rule1)) {
				goods_list = tdGoodsService.searchGoodsOrderBySortIdDesc(keywords);
				rule1 = "0";
			}
		} else if ("1".equals(sortFiled)) {
			if ("0".equals(rule2)) {
				goods_list = tdGoodsService.searchGoodsOrderBySalePriceAsc(keywords, diySite.getPriceListId());
				rule2 = "1";
			} else if ("1".equals(rule2)) {
				goods_list = tdGoodsService.searchGoodsOrderBySalePriceDesc(keywords, diySite.getPriceListId());
				rule2 = "0";
			}
		} else if ("2".equals(sortFiled)) {
			if ("0".equals(rule3)) {
				goods_list = tdGoodsService.searchGoodsOrderBySoldNumberAsc(keywords);
				rule3 = "1";
			} else if ("1".equals(rule3)) {
				goods_list = tdGoodsService.searchGoodsOrderBySoldNumberDesc(keywords);
				rule3 = "0";
			}
		}

		// 遍历集合，获取指定商品的价目表项
		if (null != goods_list) {
			for (int i = 0; i < goods_list.size(); i++) {
				TdGoods goods = goods_list.get(i);
				if (null != goods) {
					// 根据商品的id和价目表id获取指定商品的价目表项
					TdPriceListItem priceListItem = tdPriceListItemService
							.findByPriceListIdAndGoodsId(diySite.getPriceListId(), goods.getId());
					if (null != priceListItem) {
						map.addAttribute("priceListItem" + i, priceListItem);
					}
				}
			}
		}
		map.addAttribute("selected_rule", Long.parseLong(sortFiled));
		map.addAttribute("rule1", rule1);
		map.addAttribute("rule2", rule2);
		map.addAttribute("rule3", rule3);
		map.addAttribute("keywords", keywords);
		map.addAttribute("goods_list", goods_list);

		Long number = tdCommonService.getSelectedNumber(req);
		// 将已选商品的数量（包括调色包）添加到ModelMap中
		map.addAttribute("selected_number", number);

		return "/client/goods_search";
	}

	/**
	 * 在已选页面删除已选商品/调色包的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/delete/selected")
	@ResponseBody
	public Map<String, Object> deleteSelected(HttpServletRequest req, Long goodsId) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 获取所有的已选商品
		List<TdCartGoods> selected_goods = tdCommonService.getSelectedGoods(req);
		// 获取所有已选的调色包
		List<TdCartColorPackage> selected_colors = tdCommonService.getSelectedColorPackage(req);

		// 创建一个布尔变量表示是否在已选商品中查找指定的商品
		Boolean isFind = false;
		// 遍历所有的已选商品，查找出指定id的已选
		if (null != selected_goods) {
			for (int i = 0; i < selected_goods.size(); i++) {
				TdCartGoods cartGoods = selected_goods.get(i);
				if (null != cartGoods) {
					if (null != cartGoods.getGoodsId() && cartGoods.getGoodsId() == goodsId) {
						isFind = true;
						selected_goods.remove(i);
						req.getSession().setAttribute("all_selected", selected_goods);
						res.put("status", 0);
					}
				}
			}
		}

		// 在已选商品中没有找到指定goodsId的商品时，遍历已选调色包
		if (!isFind) {
			if (null != selected_colors) {
				for (int i = 0; i < selected_colors.size(); i++) {
					TdCartColorPackage colorPackage = selected_colors.get(i);
					if (null != colorPackage) {
						if (null != colorPackage.getGoodsId() && colorPackage.getGoodsId() == goodsId) {
							selected_colors.remove(i);
							req.getSession().setAttribute("all_color", selected_colors);
							res.put("status", 0);
						}
					}
				}
			}
		}

		// 获取所有的已选商品（整合后）
		List<TdCartGoods> all = tdCommonService.getAllContainsColorPackage(req);
		if (null != all) {
			res.put("number", all.size());
		} else {
			res.put("number", 0);
		}

		Double price = 0.0;
		for (TdCartGoods cartGoods : all) {
			if (null != cartGoods && null != cartGoods.getPrice() && null != cartGoods.getQuantity()) {
				price += (cartGoods.getPrice() * cartGoods.getQuantity());
			}
		}
		res.put("all_price", price);

		return res;
	}

	/**
	 * 立即购买某件商品的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/buy/now")
	public String buyNow(HttpServletRequest req, ModelMap map, Long goodsId) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}
		// 查找到指定的商品
		TdGoods goods = tdGoodsService.findOne(goodsId);

		// 获取用于的指定门店
		TdDiySite diySite = tdCommonService.getDiySite(req);

		// 获取指定商品的价目表项
		TdPriceListItem priceListItem = tdPriceListItemService.findByPriceListIdAndGoodsId(diySite.getPriceListId(),
				goodsId);

		// 判断将其加入到已选商品中还是已选调色包中
		if (null != goods && null != goods.getIsColorPackage() && goods.getIsColorPackage()) {
			TdCartColorPackage colorPackage = new TdCartColorPackage();
			colorPackage.setGoodsId(goodsId);
			colorPackage.setNumber(goods.getCode());
			colorPackage.setImageUri(goods.getCoverImageUri());
			colorPackage.setUsername(username);
			colorPackage.setSalePrice(priceListItem.getSalePrice());
			colorPackage.setRealPrice(priceListItem.getRealSalePrice());
			colorPackage.setNumber(goods.getCode());
			// 获取已选调色包
			List<TdCartColorPackage> selected_color = tdCommonService.getSelectedColorPackage(req);
			selected_color.add(colorPackage);
			req.getSession().setAttribute("all_color", selected_color);
		}

		else {
			TdCartGoods cartGoods = new TdCartGoods();
			cartGoods.setGoodsId(goodsId);
			cartGoods.setGoodsTitle(goods.getTitle());
			cartGoods.setGoodsCoverImageUri(goods.getCoverImageUri());
			cartGoods.setPrice(priceListItem.getSalePrice());
			cartGoods.setRealPrice(priceListItem.getRealSalePrice());
			cartGoods.setQuantity(1L);
			cartGoods.setSku(goods.getCode());
			// 获取已选商品
			List<TdCartGoods> selected_goods = tdCommonService.getSelectedGoods(req);
			selected_goods.add(cartGoods);
			req.getSession().setAttribute("all_selected", selected_goods);
		}

		return "redirect:/user/selected";
	}

	/*
	 *********************************** 公共结束************************************************************
	 */

}
