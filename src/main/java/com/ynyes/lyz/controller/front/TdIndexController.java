package com.ynyes.lyz.controller.front;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdNaviBarItem;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdRequisition;
import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.entity.TdSmsAccount;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdActivityService;
import com.ynyes.lyz.service.TdAdService;
import com.ynyes.lyz.service.TdAdTypeService;
import com.ynyes.lyz.service.TdArticleCategoryService;
import com.ynyes.lyz.service.TdArticleService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdNaviBarItemService;
import com.ynyes.lyz.service.TdPriceListItemService;
import com.ynyes.lyz.service.TdRequisitionService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.service.TdSmsAccountService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.util.ClientConstant;
import com.ynyes.lyz.util.MD5;

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

	@Autowired
	private TdReturnNoteService tdReturnNoteService;

	@Autowired
	private TdCityService tdCityService;

	@Autowired
	private TdSmsAccountService tdSmsAccountService;

	@RequestMapping("/sendRequisition")
	@ResponseBody
	public Map<String, String> testmathod(String aString) {
		TdRequisition requisition = tdRequisitionService.findByOrderNumber(aString);
		Map<String, String> map = new HashMap<>();
		map.put("结果", "要货单不存在");
		if (requisition != null) {
			map.put("结果", "要货单发送成功");
			map = tdCommonService.sendWmsMst(requisition);
		}

		return map;
	}

	@RequestMapping("/tempBack")
	@ResponseBody
	public Map<String, String> tempBack(String number) {
		Map<String, String> map = new HashMap<>();
		List<TdReturnNote> returnNotes = tdReturnNoteService.findByOrderNumberContaining(number);
		if (returnNotes != null && returnNotes.size() >= 1) {
			for (TdReturnNote tdReturnNote : returnNotes) {
				map = tdCommonService.testSendBackMsgToWMS(tdReturnNote);
			}
		}
		return map;
	}

	@RequestMapping
	public String index(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		if (null != user.getUserType()
				&& (user.getUserType().equals(0L) || user.getUserType().equals(1L) || user.getUserType().equals(2L))) {
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

	@RequestMapping(value = "/return/password")
	public String returnPassword(HttpServletRequest req, ModelMap map) {
		return "/client/return_password";
	}

	@RequestMapping(value = "/password/code")
	@ResponseBody
	public Map<String, Object> sendCode(HttpServletRequest req, String phone) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);

		// 判断用户是否存在
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(phone);
		if (null == user) {
			res.put("message", "该手机号码未注册");
			return res;
		}

		// 获取指定用户的所属城市
		Long cityId = user.getCityId();
		TdCity city = tdCityService.findBySobIdCity(cityId);
		if (null == city) {
			res.put("message", "获取用户归属城市失败");
			return res;
		}

		// 获取城市所使用的短信账户
		TdSmsAccount account = tdSmsAccountService.findOne(city.getSmsAccountId());
		if (null == account) {
			res.put("message", "获取短信账户信息失败");
			return res;
		}

		Random random = new Random();
		String smscode = random.nextInt(9000) + 1000 + "";
		HttpSession session = req.getSession();
		session.setAttribute("SMS" + phone, smscode);
		String info = "您正在执行【找回密码】操作，验证码为" + smscode + "，请在页面中输入以完成验证。";
		String content = null;
		try {
			content = URLEncoder.encode(info, "GB2312");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("message", "验证码生成失败");
			return res;
		}

		String url = "http://www.mob800.com/interface/Send.aspx?enCode=" + account.getEncode() + "&enPass="
				+ account.getEnpass() + "&userName=" + account.getUserName() + "&mob=" + phone + "&msg=" + content;
		StringBuffer return_code = null;
		try {
			URL u = new URL(url);
			URLConnection connection = u.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			httpConn.setRequestProperty("Content-type", "text/html");
			httpConn.setRequestProperty("Accept-Charset", "utf-8");
			httpConn.setRequestProperty("contentType", "utf-8");
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader reader = null;
			StringBuffer resultBuffer = new StringBuffer();
			String tempLine = null;

			if (httpConn.getResponseCode() >= 300) {
				res.put("message", "HTTP Request is not success, Response code is " + httpConn.getResponseCode());
				return res;
			}

			try {
				inputStream = httpConn.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);

				while ((tempLine = reader.readLine()) != null) {
					resultBuffer.append(tempLine);
				}
				return_code = resultBuffer;

			} finally {
				if (reader != null) {
					reader.close();
				}
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.put("message", "验证码生成失败");
			return res;
		}
		res.put("status", 0);
		res.put("code", return_code);
		return res;
	}

	@RequestMapping(value = "/sms/check")
	@ResponseBody
	public Map<String, Object> smsCheck(String phone, String code, HttpServletRequest req) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		if (null == phone || null == code) {
			res.put("message", "获取数据失败");
			return res;
		}

		// 获取session中的验证码
		String sms_session = (String) req.getSession().getAttribute("SMS" + phone);
		if (!(code.equalsIgnoreCase(sms_session))) {
			res.put("message", "验证码输入错误");
			return res;
		}

		res.put("phone", phone);
		res.put("status", 0);
		return res;
	}

	@RequestMapping(value = "/change/password")
	public String changePassword(HttpServletRequest req, ModelMap map, String phone) {
		if (null == phone) {
			return "redirect:/return/password";
		}
		map.addAttribute("phone", phone);
		return "/client/change_password";
	}

	@RequestMapping(value = "/password/save")
	@ResponseBody
	public Map<String, Object> passwordSave(String password, String username) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		if (null == password || null == username) {
			res.put("message", "数据获取失败");
			return res;
		}
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		user.setPassword(MD5.md5(password, 32));
		tdUserService.save(user);
		res.put("status", 0);
		return res;
	}
}
