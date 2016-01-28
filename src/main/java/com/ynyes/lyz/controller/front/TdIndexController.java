package com.ynyes.lyz.controller.front;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdActivity;
import com.ynyes.lyz.entity.TdAd;
import com.ynyes.lyz.entity.TdAdType;
import com.ynyes.lyz.entity.TdArticle;
import com.ynyes.lyz.entity.TdArticleCategory;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdNaviBarItem;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdRequisition;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdActivityService;
import com.ynyes.lyz.service.TdAdService;
import com.ynyes.lyz.service.TdAdTypeService;
import com.ynyes.lyz.service.TdArticleCategoryService;
import com.ynyes.lyz.service.TdArticleService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdNaviBarItemService;
import com.ynyes.lyz.service.TdPriceListItemService;
import com.ynyes.lyz.service.TdRequisitionService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.util.ClientConstant;

@Controller
@RequestMapping(value = "/")
public class TdIndexController {

	@Autowired
	private TdAdTypeService tdAdTypeService;

	@Autowired
	private TdAdService tdAdService;

	@Autowired
	private TdActivityService tdActivityService;

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdArticleCategoryService tdArticleCategoryService;

	@Autowired
	private TdArticleService tdArticleService;

	@Autowired
	private TdPriceListItemService tdPriceListService;

	@Autowired
	private TdCommonService tdCommonService;

	@Autowired
	private TdNaviBarItemService tdNaviBarItemService;

	@Autowired
	private TdGoodsService tdGoodsService;
	
	@Autowired
	private TdRequisitionService tdRequisitionService;

	
	@RequestMapping("/sendRequisition")
	@ResponseBody
	public String testmathod(String aString)
	{
		String astring= "要货单不存在";
		TdRequisition requisition = tdRequisitionService.findByOrderNumber(aString);
		if (requisition != null)
		{
			astring = "要货单发送成功";
			tdCommonService.sendWmsMst(requisition);
		}
		
		return astring;
	}
	
	@RequestMapping
	public String index(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		if (null != user.getUserType() && user.getUserType().equals(0L)) {
			tdCommonService.setHeader(req, map);

			// 查找指定用户所属的门店
			TdDiySite diySite = tdCommonService.getDiySite(req);

			// 查找首页轮播广告
			TdAdType adType = tdAdTypeService.findByTitle("首页轮播广告");
			if (null != adType) {
				List<TdAd> circle_ad_list = tdAdService.findByTypeId(adType.getId());
				map.addAttribute("circle_ad_list", circle_ad_list);
			}

			// 查找首页中部广告
			TdAdType index_center_adType = tdAdTypeService.findByTitle("首页中部广告");
			if (null != index_center_adType) {
				List<TdAd> index_center_list = tdAdService.findByTypeId(index_center_adType.getId());
				if (null != index_center_adType && index_center_list.size() > 0) {
					map.addAttribute("index_center", index_center_list.get(0));
				}
			}

			// 查找头条信息
			TdArticleCategory articleCategory = tdArticleCategoryService.findByTitle("头条消息");
			if (null != articleCategory) {
				List<TdArticle> headline_list = tdArticleService.findByCategoryId(articleCategory.getId());
				map.addAttribute("headline_list", headline_list);
			}

			// 查找导航栏
			List<TdNaviBarItem> navi_bar_list = tdNaviBarItemService.findByIsEnableTrueOrderBySortIdAsc();
			map.addAttribute("navi_bar_list", navi_bar_list);

			// 查找首页推荐商品
			if (null != diySite) {
				Page<TdPriceListItem> commend_page = tdPriceListService
						.findByPriceListIdAndIsCommendIndexTrueOrderBySortIdAsc(diySite.getPriceListId(),
								ClientConstant.pageSize, 0);
				map.addAttribute("commend_page", commend_page);
				if (null != commend_page) {
					List<TdPriceListItem> content = commend_page.getContent();
					if (null != content) {
						for (int i = 0; i < content.size(); i++) {
							TdPriceListItem priceListItem = content.get(i);
							if (null != priceListItem) {
								TdGoods goods = tdGoodsService.findOne(priceListItem.getGoodsId());
								if (null != goods) {
									map.addAttribute("goods" + i, goods);
								}
							}
						}
					}
				}
			}

			// 查找所有首页推荐的未过期的活动
			List<TdActivity> index_activities = tdActivityService
					.findByDiySiteIdsContainingAndBeginDateBeforeAndFinishDateAfterAndIsCommendIndexTrueOrderBySortIdAsc(
							diySite.getId() + "", new Date());

			List<Map<TdGoods, Double>> promotion_list = tdCommonService.getPromotionGoodsAndPrice(req,
					index_activities);
					// 将存储促销信息的集合放入到ModelMap中

			// 清楚session中的订单信息
			req.getSession().setAttribute("order_temp", null);
			map.addAttribute("promotion_list", promotion_list);
		}

		if (null != user.getUserType() && user.getUserType().equals(5L)) {
			return "redirect:/delivery";
		}

		return "/client/index";
	}
}
