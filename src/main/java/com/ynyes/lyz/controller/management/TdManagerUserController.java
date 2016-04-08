package com.ynyes.lyz.controller.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdBalanceLog;
import com.ynyes.lyz.entity.TdCartColorPackage;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdManager;
import com.ynyes.lyz.entity.TdMessage;
import com.ynyes.lyz.entity.TdMessageType;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdUserCollect;
import com.ynyes.lyz.entity.TdUserComment;
import com.ynyes.lyz.entity.TdUserLevel;
import com.ynyes.lyz.entity.TdUserRecentVisit;
import com.ynyes.lyz.entity.TdUserSuggestion;
import com.ynyes.lyz.service.TdBalanceLogService;
import com.ynyes.lyz.service.TdCartColorPackageService;
import com.ynyes.lyz.service.TdCartGoodsService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdManagerService;
import com.ynyes.lyz.service.TdMessageService;
import com.ynyes.lyz.service.TdMessageTypeService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.service.TdUserCollectService;
import com.ynyes.lyz.service.TdUserCommentService;
import com.ynyes.lyz.service.TdUserLevelService;
import com.ynyes.lyz.service.TdUserRecentVisitService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.service.TdUserSuggestionCategoryService;
import com.ynyes.lyz.service.TdUserSuggestionService;
import com.ynyes.lyz.util.MD5;
import com.ynyes.lyz.util.SiteMagConstant;

/**
 * 后台用户管理控制器
 * 
 * @author Sharon
 */

@Controller
@RequestMapping(value = "/Verwalter/user")
public class TdManagerUserController {

	@Autowired
	TdUserService tdUserService;

	@Autowired
	TdUserLevelService tdUserLevelService;

	@Autowired
	TdUserCommentService tdUserCommentService;

	@Autowired
	TdUserCollectService tdUserCollectService;

	@Autowired
	TdUserRecentVisitService tdUserRecentVisitService;

	@Autowired
	TdManagerLogService tdManagerLogService;

	@Autowired
	TdUserSuggestionService tdUserSuggestionService; // zhangji 2016-1-3
														// 12:59:58

	@Autowired
	TdMessageService tdMessageService; // zhangji 2016-1-3 15:29:21

	@Autowired
	TdMessageTypeService tdMessageTypeService; // zhangji 2016-1-3 15:40:32

	@Autowired
	private TdDiySiteService tdDiySiteService;
	
	@Autowired
	private TdBalanceLogService tdBalanceLogService;
	
	@Autowired
	private TdManagerService tdManagerService;

	/**
	 * 修改账户名所用 2016-1-8 10:34:46
	 * 
	 * @author Zhangji
	 */
	@Autowired
	TdCartColorPackageService TdCartColorPackageService;
	@Autowired
	TdCartGoodsService tdCartGoodsService;
	@Autowired
	TdCouponService tdCouponService;
	@Autowired
	TdReturnNoteService tdReturnNoteService;

	@Autowired
	TdUserSuggestionCategoryService tdUserSuggestionCategoryService;

	@Autowired
	TdGoodsService tdGoodsService;

	@Autowired
	TdOrderService tdOrderService;

	@Autowired
	private TdCityService tdCityService;

	@RequestMapping(value = "/check", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> validateForm(String param, Long id) {
		Map<String, String> res = new HashMap<String, String>();

		res.put("status", "n");

		if (null == param || param.isEmpty()) {
			res.put("info", "该字段不能为空");
			return res;
		}

		if (null == id) {
			if (null != tdUserService.findByUsername(param)) {
				res.put("info", "已存在同名用户");
				return res;
			}
		} else {
			if (null != tdUserService.findByUsernameAndIdNot(param, id)) {
				res.put("info", "已存在同名用户");
				return res;
			}
		}

		res.put("status", "y");

		return res;
	}

	@RequestMapping(value = "/list")
	public String setting(Integer page, Integer size, String keywords, Long roleId, Long userType,
			String __EVENTTARGET, String __EVENTARGUMENT, String __VIEWSTATE, Long[] listId, Integer[] listChkId,
			ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		if (null != __EVENTTARGET) {
			if (__EVENTTARGET.equalsIgnoreCase("btnPage")) {
				if (null != __EVENTARGUMENT) {
					page = Integer.parseInt(__EVENTARGUMENT);
				}
			} else if (__EVENTTARGET.equalsIgnoreCase("btnDelete")) {
				btnDelete("user", listId, listChkId);
				tdManagerLogService.addLog("delete", "删除用户", req);
			}
		}

		if (null == page || page < 0) {
			page = 0;
		}

		if (null == size || size <= 0) {
			size = SiteMagConstant.pageSize;
			;
		}

		if (null != keywords) {
			keywords = keywords.trim();
		}

		map.addAttribute("page", page);
		map.addAttribute("size", size);
		map.addAttribute("keywords", keywords);
		map.addAttribute("roleId", roleId);
		map.addAttribute("userType", userType);
		map.addAttribute("__EVENTTARGET", __EVENTTARGET);
		map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		// 等级list
		// map.addAttribute("userType_list",
		// tdUserLevelService.findIsEnableTrue());

		Page<TdUser> userPage = null;

		if (null == roleId) {
			if (null == keywords || "".equalsIgnoreCase(keywords)) 
			{
				if (null == userType) 
				{
					userPage = tdUserService.findAllOrderByIdDesc(page, size);
				} 
				else 
				{
					userPage = tdUserService.findByUserTypeOrderByIdDesc(userType, page, size);
				}
			} 
			else 
			{
				if (null == userType) 
				{
					userPage = tdUserService.searchAndOrderByIdDesc(keywords, page, size);
				}
				else
				{
					userPage = tdUserService.searchAndfindByUserTypeOrderByIdDesc(keywords, userType, page, size);
				}
			}
		}

		map.addAttribute("user_page", userPage);

		return "/site_mag/user_list";
	}

	@RequestMapping(value = "/change_city", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> changeCity(Long cid){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code", 0);
		List<TdDiySite> site_list = tdDiySiteService.findByRegionIdOrderBySortIdAsc(cid);
		map.put("site_list", site_list);
		return map;
	}
	
	@RequestMapping(value = "/edit")
	public String userEdit(Long id, Long roleId, String action, String __VIEWSTATE, ModelMap map,
			HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("roleId", roleId);

		TdUser user = tdUserService.findOne(id);

		if (null != user) {
			map.addAttribute("user", user);
			List<TdCity> cities = tdCityService.findAll();
			map.addAttribute("city_list",cities);
			// 获取用户所在城市
			Long cityId = user.getCityId();

			if (null != cityId) {
				TdCity city = tdCityService.findBySobIdCity(cityId);

				if (null != city) {
					// 获取指定id城市下的所有门店
					List<TdDiySite> site_list = tdDiySiteService.findByRegionIdOrderBySortIdAsc(city.getSobIdCity());

					map.addAttribute("site_list", site_list);
				}
			}
		} else {
			List<TdCity> cities = tdCityService.findAll();
			map.addAttribute("city_list",cities);
			List<TdDiySite> site_list = tdDiySiteService.findAll();
			// 获取所有的门店
			map.addAttribute("site_list", site_list);
		}
		return "/site_mag/user_edit";
	}

	@RequestMapping(value = "/save")
	public String orderEdit(TdUser tdUser,Double obalance,Double ocashBalance,Double ounCashBalance, String oldPassword, String __VIEWSTATE, ModelMap map, String birthdate, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		TdManager manager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		if (manager == null)
		{
			return "redirect:/Verwalter/login";
		}
		
		// 修改门店的时候要修改用户的customerId和diyName
		Long diySiteId = tdUser.getUpperDiySiteId();
		if (null != diySiteId) {
			TdDiySite site = tdDiySiteService.findOne(diySiteId);
			if (null != site) {
				tdUser.setCustomerId(site.getCustomerId());
				tdUser.setCityId(site.getRegionId());
				tdUser.setDiyName(site.getTitle());
			}
		}

		if (null != birthdate) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if (StringUtils.isNotBlank(birthdate)) {
					Date brithday = sdf.parse(birthdate);
					tdUser.setBirthday(brithday);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Boolean isVipUser0 = true;
		Boolean isVipUser1 = true;
		Boolean isVipUser2 = true;
		if (obalance == null) 
		{
			obalance = 0.00;
			isVipUser0 = false;
		}
		if (ocashBalance == null) 
		{
			ocashBalance = 0.00;
			isVipUser1 = false;
		}
		if (ounCashBalance == null) 
		{
			ounCashBalance = 0.00;
			isVipUser2 = false;
		}
		
		// 设置新密码 zhangji 2016-1-7 22:01:45
		if (null != tdUser.getId()) //老用户
		{
			if (StringUtils.isNotBlank(oldPassword)) 
			{
				tdUser.setPassword(MD5.md5(oldPassword, 32));
			}
			
			// add MDJ
			if (!obalance.equals(tdUser.getBalance()) && isVipUser0)
			{
				this.setAndSaveBalanceLog(obalance - tdUser.getBalance(),0L, tdUser, manager);
				tdUser.setBalance(obalance);
			}
			if (!ocashBalance.equals(tdUser.getCashBalance()) && isVipUser1)
			{
				this.setAndSaveBalanceLog(ocashBalance - tdUser.getCashBalance(),1L, tdUser, manager);
				tdUser.setCashBalance(ocashBalance);
			}
			if (!ounCashBalance.equals(tdUser.getUnCashBalance()) && isVipUser2)
			{
				this.setAndSaveBalanceLog(ounCashBalance - tdUser.getUnCashBalance(),2L, tdUser, manager);
				tdUser.setUnCashBalance(ounCashBalance);
			}
		}
		else//新用户
		{
			TdCity city = tdCityService.findBySobIdCity(tdUser.getCityId());
			if (StringUtils.isNotBlank(oldPassword)) 
			{
				tdUser.setPassword(MD5.md5(oldPassword, 32));
			}
			else
			{
				tdUser.setPassword(MD5.md5("123456", 32));
			}
			if (null != city)
			{
				tdUser.setCityName(city.getCityName());
				tdUser.setCityId(city.getSobIdCity());
			}
			tdUser.setRegisterTime(new Date());
			tdUser.setAllPayed(0.00);
			tdUser.setNickname(tdUser.getUsername());
			tdUser.setFirstOrder(true);
			tdUser.setIsOld(false);
			
			// add MDJ
			tdUser.setBalance(0d);
			tdUser.setCashBalance(0d);
			tdUser.setUnCashBalance(0d);
			if (obalance != 0)
			{
				this.setAndSaveBalanceLog(obalance,0L, tdUser, manager);
			}
			if (ocashBalance != 0)
			{
				this.setAndSaveBalanceLog(ocashBalance,1L, tdUser, manager);
			}
			if (ounCashBalance != 0)
			{
				this.setAndSaveBalanceLog(ounCashBalance,2L, tdUser, manager);
			}
			tdUser.setBalance(obalance);
			tdUser.setCashBalance(ocashBalance);
			tdUser.setUnCashBalance(ounCashBalance);
		}
		
		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null == tdUser.getId()) 
		{
			tdManagerLogService.addLog("add", "修改用户", req);
		}
		else
		{
			tdManagerLogService.addLog("edit", "修改用户", req);
		}
		tdUserService.save(tdUser);

		return "redirect:/Verwalter/user/list/";
	}
	
	private void setAndSaveBalanceLog( Double changeBalance,Long balancelType,TdUser newUser,TdManager operator)
	{
		TdBalanceLog balanceLog = new TdBalanceLog();
		balanceLog.setUsername(newUser.getUsername());
		if (changeBalance != null)
		{
			balanceLog.setMoney(changeBalance);
		}
		else
		{
			balanceLog.setMoney(newUser.getBalance());
		}
		balanceLog.setType(2L);
		balanceLog.setCreateTime(new Date());
		balanceLog.setFinishTime(new Date());
		balanceLog.setIsSuccess(true);
		balanceLog.setBalanceType(balancelType);
		if (balancelType == 0)
		{
			balanceLog.setBalance(newUser.getBalance() + changeBalance);
		}
		else if (balancelType == 1)
		{
			balanceLog.setBalance(newUser.getCashBalance() + changeBalance);
		}
		else if (balancelType == 2)
		{
			balanceLog.setBalance(newUser.getUnCashBalance() + changeBalance);
		}
		balanceLog.setOperator(operator.getUsername());
		balanceLog.setOperatorIp(operator.getLastLoginIp());
		balanceLog.setReason("后台管理员修改");
		if (newUser.getId() != null) 
		{
			balanceLog.setUserId(newUser.getId());
		}
		else
		{
			balanceLog.setReason("后台新增会员设置预存款");
		}
		tdBalanceLogService.save(balanceLog);
	}

	/**
	 * 修改用户名
	 * 
	 * @author Zhangji
	 */
	@RequestMapping(value = "/setUsername")
	public String setUsername(Long id, Long roleId, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("roleId", roleId);
		if (null != id) {
			map.addAttribute("user", tdUserService.findOne(id));
		}
		// map.addAttribute("user_level_list",
		// tdUserLevelService.findIsEnableTrue());
		return "/site_mag/user_username_edit";
	}

	@RequestMapping(value = "/username", method = RequestMethod.POST)
	public String username(Long id, String oldUsername, String newUsername, String __VIEWSTATE, ModelMap map,
			String birthdate, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		TdUser tdUser = tdUserService.findOne(id);
		// 修改用户名 zhangji 2016-1-8 10:30:38
		if (null != oldUsername && !oldUsername.equals(newUsername)) {
			List<TdCartColorPackage> tdCartColorPackage = TdCartColorPackageService.findByUsername(oldUsername);
			for (TdCartColorPackage item : tdCartColorPackage) {
				item.setUsername(newUsername);
				TdCartColorPackageService.save(item);
			}

			List<TdCartGoods> tdCartGoods = tdCartGoodsService.findByUsername(oldUsername);
			for (TdCartGoods item : tdCartGoods) {
				item.setUsername(newUsername);
				tdCartGoodsService.save(item);
			}

			List<TdCoupon> tdCoupon = tdCouponService.findByUsername(oldUsername);
			for (TdCoupon item : tdCoupon) {
				item.setUsername(newUsername);
				tdCouponService.save(item);
			}

			List<TdReturnNote> tdReturnNote = tdReturnNoteService.findByUsername(oldUsername);
			for (TdReturnNote item : tdReturnNote) {
				item.setUsername(newUsername);
				tdReturnNoteService.save(item);
			}

			List<TdOrder> tdOrder = tdOrderService.findByUsername(oldUsername);
			for (TdOrder item : tdOrder) {
				item.setUsername(newUsername);
				tdOrderService.save(item);
			}

			List<TdUserCollect> tdUserCollect = tdUserCollectService.findByUsername(oldUsername);
			for (TdUserCollect item : tdUserCollect) {
				item.setUsername(newUsername);
				tdUserCollectService.save(item);
			}

			List<TdUserComment> tdUserComment = tdUserCommentService.findByUsername(oldUsername);
			for (TdUserComment item : tdUserComment) {
				item.setUsername(newUsername);
				tdUserCommentService.save(item);
			}

			List<TdUserRecentVisit> tdUserRecentVisit = tdUserRecentVisitService.findByUsername(oldUsername);
			for (TdUserRecentVisit item : tdUserRecentVisit) {
				item.setUsername(newUsername);
				tdUserRecentVisitService.save(item);
			}

		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null == tdUser.getId()) {
			tdManagerLogService.addLog("add", "修改用户", req);
		} else {
			tdManagerLogService.addLog("edit", "修改用户", req);
		}

		tdUser.setUsername(newUsername);
		tdUserService.save(tdUser);

		return "redirect:/Verwalter/user/list/";
	}

	@RequestMapping(value = "/level/edit")
	public String edit(Long id, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != id) {
			map.addAttribute("userLevelId", id);
			map.addAttribute("user_level", tdUserLevelService.findOne(id));
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		return "/site_mag/user_level_edit";
	}

	@RequestMapping(value = "/level/save")
	public String levelSave(TdUserLevel tdUserLevel, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		if (null == tdUserLevel.getId()) {
			tdManagerLogService.addLog("add", "添加用户等级", req);
		} else {
			tdManagerLogService.addLog("edit", "修改用户等级", req);
		}

		tdUserLevelService.save(tdUserLevel);

		return "redirect:/Verwalter/user/level/list";
	}

	@RequestMapping(value = "/level/check/{type}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> validateForm(@PathVariable String type, String param, Long id) {
		Map<String, String> res = new HashMap<String, String>();

		res.put("status", "n");
		res.put("info", "通过");

		if (null != type) {
			if (type.equalsIgnoreCase("levelId")) {
				if (null == param || param.isEmpty()) {
					res.put("info", "该字段不能为空");
					return res;
				}

				if (null == id) {
					if (null != tdUserLevelService.findByLevelId(Long.parseLong(param))) {
						res.put("info", "该用户等级已存在");
						return res;
					}
				} else {
					if (null != tdUserLevelService.findByLevelIdAndIdNot(Long.parseLong(param), id)) {
						res.put("info", "该用户等级已存在");
						return res;
					}
				}

				res.put("status", "y");
			} else if (type.equalsIgnoreCase("title")) {
				if (null == param || param.isEmpty()) {
					res.put("info", "该字段不能为空");
					return res;
				}

				if (null == id) {
					if (null != tdUserLevelService.findByTitle(param)) {
						res.put("info", "该等级用户名称已存在");
						return res;
					}
				} else {
					if (null != tdUserLevelService.findByTitleAndIdNot(param, id)) {
						res.put("info", "该等级用户名称已存在");
						return res;
					}
				}

				res.put("status", "y");
			}
		}

		return res;
	}

	@RequestMapping(value = "/comment/edit")
	public String commentEdit(Long id, Long statusId, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != id) {
			map.addAttribute("userCommentId", id);
			map.addAttribute("user_comment", tdUserCommentService.findOne(id));
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("statusId", statusId);

		return "/site_mag/user_comment_edit";
	}

	@RequestMapping(value = "/comment/save")
	public String commentSave(TdUserComment tdUserComment, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null == tdUserComment.getIsReplied() || !tdUserComment.getIsReplied()) {
			tdUserComment.setIsReplied(true);
			tdUserComment.setReplyTime(new Date());
		}

		if (null == tdUserComment.getId()) {
			tdManagerLogService.addLog("add", "修改用户评论", req);
		} else {
			tdManagerLogService.addLog("edit", "修改用户评论", req);
		}

		tdUserCommentService.save(tdUserComment);

		return "redirect:/Verwalter/user/comment/list?statusId=" + __VIEWSTATE;
	}

	// zhangji
	@RequestMapping(value = "/cancel/edit")
	public String cancelEdit(Long id, Long statusId, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != id) {
			map.addAttribute("id", id);
			map.addAttribute("user_cancel", tdOrderService.findOne(id));
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("statusId", statusId);

		return "/site_mag/user_cancel_edit";
	}

	@RequestMapping(value = "/cancel/save")
	public String cancelSave(Long id, Boolean isRefund, Double refund, String __VIEWSTATE, ModelMap map,
			HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		TdOrder order = tdOrderService.findOne(id);

		if (null == order.getIsRefund()) {
			tdManagerLogService.addLog("add", "修改用户取消订单", req);
		} else {
			tdManagerLogService.addLog("edit", "修改用户取消订单", req);
		}

		order.setRefundTime(new Date());
		order.setRefund(refund);
		order.setIsRefund(isRefund);
		tdOrderService.save(order);

		return "redirect:/Verwalter/user/cancel/list?statusId=" + __VIEWSTATE;
	}

	/*----------------用户投诉咨询 begin ------------------*/
	@RequestMapping(value = "/{type}/list")
	public String list(@PathVariable String type, Integer page, Integer size, String __EVENTTARGET, String date_1,
			String date_2, String keywords, Long categoryId, String __EVENTARGUMENT, String __VIEWSTATE, Long[] listId,
			Integer[] listChkId, Long[] listSortId, ModelMap map, HttpServletRequest req) throws ParseException {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		if (null != __EVENTTARGET) {
			if (__EVENTTARGET.equalsIgnoreCase("btnPage")) {
				if (null != __EVENTARGUMENT) {
					page = Integer.parseInt(__EVENTARGUMENT);
				}
			} else if (__EVENTTARGET.equalsIgnoreCase("btnDelete")) {
				btnDelete(type, listId, listChkId);
			}
			// else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
			// {
			// btnSave(type, listId, listSortId);
			// }
			// else if (__EVENTTARGET.equalsIgnoreCase("btnVerify"))
			// {
			// btnVerify(type, listId, listChkId);
			// }
		}

		if (null == page || page < 0) {
			page = 0;
		}

		if (null == size || size <= 0) {
			size = SiteMagConstant.pageSize;
			;
		}

		map.addAttribute("page", page);
		map.addAttribute("size", size);
		map.addAttribute("__EVENTTARGET", __EVENTTARGET);
		map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null != type) {
			if (type.equalsIgnoreCase("suggestion")) // 投诉咨询
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date1 = null;
				Date date2 = null;
				if (null != date_1 && !date_1.equals("")) {
					date1 = sdf.parse(date_1);
				}
				if (null != date_2 && !date_2.equals("")) {
					date2 = sdf.parse(date_2);
				}
				Page<TdUserSuggestion> suggestionPage = null;

				// 开始筛选 zhangji
				if (null == keywords || "".equals(keywords)) {
					if (null == date1) {
						if (null == date2) {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findAll(page, size);
							} else {
								suggestionPage = tdUserSuggestionService.findByCategoryId(categoryId, page, size);
							}
						} else {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findByCreateTimeBefore(date2, page, size);
							} else {
								suggestionPage = tdUserSuggestionService.findByCreateTimeBeforeAndCategoryId(date2,
										categoryId, page, size);
							}
						}

					} else {
						if (null == date2) {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findByCreateTimeAfter(date1, page, size);
							} else {
								suggestionPage = tdUserSuggestionService.findByCreateTimeAfterAndCategoryId(date1,
										categoryId, page, size);
							}
						} else {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findByCreateTimeAfterAndCreateTimeBefore(date1,
										date2, page, size);
							} else {
								suggestionPage = tdUserSuggestionService
										.findByCreateTimeAfterAndCreateTimeBeforeAndCategoryId(date1, date2, categoryId,
												page, size);
							}
						}
					}
				} else {
					if (null == date1) {
						if (null == date2) {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findBySearch(keywords, page, size);
							} else {
								suggestionPage = tdUserSuggestionService.findByCategoryIdAndSearch(categoryId, keywords,
										page, size);
							}
						} else {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findByCreateTimeBeforeAndSearch(date2,
										keywords, page, size);
							} else {
								suggestionPage = tdUserSuggestionService.findByCreateTimeBeforeAndCategoryIdAndSearch(
										date2, categoryId, keywords, page, size);
							}
						}
					} else {
						if (null == date2) {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService.findByCreateTimeAfterAndSearch(date1, keywords,
										page, size);
							} else {
								suggestionPage = tdUserSuggestionService.findByCreateTimeAfterAndCategoryIdAndSearch(
										date1, categoryId, keywords, page, size);
							}
						} else {
							if (null == categoryId) {
								suggestionPage = tdUserSuggestionService
										.findByCreateTimeAfterAndCreateTimeBeforeAndSearch(date1, date2, keywords, page,
												size);
							} else {
								suggestionPage = tdUserSuggestionService
										.findByCreateTimeAfterAndCreateTimeBeforeAndCategoryIdAndSearch(date1, date2,
												categoryId, keywords, page, size);
							}
						}
					}
				}

				map.addAttribute("category_list", tdUserSuggestionCategoryService.findByIsEnableTrueOrderBySortIdAsc());
				map.addAttribute("date_1", date_1);
				map.addAttribute("date_2", date_2);
				map.addAttribute("keywords", keywords);
				map.addAttribute("categoryId", categoryId);

				map.addAttribute("user_suggestion_page", suggestionPage);
				for (TdUserSuggestion item : suggestionPage.getContent()) {
					TdUser user = tdUserService.findOne(item.getUserId());
					if (null != user) {
						map.addAttribute("username_" + user.getId(), user.getUsername());
					}
				}
				return "/site_mag/user_suggestion_list";
			} else if (type.equalsIgnoreCase("message")) // 信息
			{
				Page<TdMessage> messagePage = tdMessageService.findAll(page, size);
				map.addAttribute("message_page", messagePage);
				for (TdMessage item : messagePage.getContent()) {
					TdUser user = tdUserService.findOne(item.getUserId());
					if (null != user) {
						map.addAttribute("username_" + user.getId(), user.getUsername());
					}
					TdMessageType messageType = tdMessageTypeService.findOne(item.getTypeId());
					if (null != messageType) {
						map.addAttribute("messageType_" + messageType.getId(), messageType.getName());
					}
				}
				return "/site_mag/message_list";
			}
		}

		return "/site_mag/error_404";
	}

	@RequestMapping(value = "/suggestion/edit")
	public String suggestionEdit(Long id, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != id) {
			TdUserSuggestion suggestion = tdUserSuggestionService.findOne(id);
			map.addAttribute("userSuggestionId", id);
			map.addAttribute("user_suggestion", suggestion);
			map.addAttribute("user", tdUserService.findOne(suggestion.getUserId()));
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		return "/site_mag/user_suggestion_edit";
	}

	@RequestMapping(value = "/message/detail")
	public String messageDetail(Long id, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != id) {
			TdMessage message = tdMessageService.findOne(id);
			if (null != message.getIsRead() && !message.getIsRead() || null == message.getIsRead()) {
				message.setIsRead(true);
				message.setReadTime(new Date());
				tdMessageService.save(message);
			}

			map.addAttribute("messageId", message.getId());
			map.addAttribute("message", message);
			map.addAttribute("user", tdUserService.findOne(message.getUserId()));
			map.addAttribute("messageType", tdMessageTypeService.findOne(message.getTypeId()).getName());
			map.addAttribute("imgUri", tdMessageTypeService.findOne(message.getTypeId()).getImgUrl());
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		return "/site_mag/message_detail";
	}

	@RequestMapping(value = "/suggestion/save", method = RequestMethod.POST)
	public String suggestionSave(Long userSuggestionId, String answerContent, String __VIEWSTATE, ModelMap map,
			HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		TdUserSuggestion suggestion = tdUserSuggestionService.findOne(userSuggestionId);

		if (null == suggestion.getIsAnswered() || !suggestion.getIsAnswered()) {
			suggestion.setIsAnswered(true);
			suggestion.setAnswerTime(new Date());
			suggestion.setAnswerContent(answerContent);
		}

		tdManagerLogService.addLog("add", "回复用户投诉咨询", req);

		tdUserSuggestionService.save(suggestion);

		return "redirect:/Verwalter/user/suggestion/list";
	}

	/*----------------用户投诉咨询 end  ---------------------*/

	// @RequestMapping(value="/{type}/list")
	// public String list(@PathVariable String type,
	// Integer page,
	// Integer size,
	// Long userId,
	// Long statusId,
	// String keywords,
	// Boolean isRefund, //zhangji
	// String __EVENTTARGET,
	// String __EVENTARGUMENT,
	// String __VIEWSTATE,
	// Long[] listId,
	// Integer[] listChkId,
	// Long[] listSortId,
	// ModelMap map,
	// HttpServletRequest req){
	// String username = (String) req.getSession().getAttribute("manager");
	// if (null == username)
	// {
	// return "redirect:/Verwalter/login";
	// }
	// if (null != __EVENTTARGET)
	// {
	// if (__EVENTTARGET.equalsIgnoreCase("btnPage"))
	// {
	// if (null != __EVENTARGUMENT)
	// {
	// page = Integer.parseInt(__EVENTARGUMENT);
	// }
	// }
	// else if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
	// {
	// btnDelete(type, listId, listChkId);
	// }
	// else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
	// {
	// btnSave(type, listId, listSortId);
	// }
	// else if (__EVENTTARGET.equalsIgnoreCase("btnVerify"))
	// {
	// btnVerify(type, listId, listChkId);
	// }
	// }
	//
	// if (null == page || page < 0)
	// {
	// page = 0;
	// }
	//
	// if (null == size || size <= 0)
	// {
	// size = SiteMagConstant.pageSize;;
	// }
	//
	// if (null != keywords)
	// {
	// keywords = keywords.trim();
	// }
	//
	// map.addAttribute("isRefund", isRefund); //zhangji
	// map.addAttribute("page", page);
	// map.addAttribute("size", size);
	// map.addAttribute("userId", userId);
	// map.addAttribute("statusId", statusId);
	// map.addAttribute("keywords", keywords);
	// map.addAttribute("__EVENTTARGET", __EVENTTARGET);
	// map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
	// map.addAttribute("__VIEWSTATE", __VIEWSTATE);
	//
	// if (null != type)
	// {
	// if (type.equalsIgnoreCase("point")) // 积分
	// {
	// if (null == userId)
	// {
	// return "/site_mag/error_404";
	// }
	//
	// TdUser user = tdUserService.findOne(userId);
	//
	// if (null == user || null == user.getUsername())
	// {
	// return "/site_mag/error_404";
	// }
	//
	// map.addAttribute("user_point_page",
	// tdUserPointService.findByUsername(user.getUsername(), page, size));
	// return "/site_mag/user_point_list";
	// }
	// else if (type.equalsIgnoreCase("collect")) // 关注
	// {
	// if (null == userId)
	// {
	// return "/site_mag/error_404";
	// }
	//
	// TdUser user = tdUserService.findOne(userId);
	//
	// if (null == user || null == user.getUsername())
	// {
	// return "/site_mag/error_404";
	// }
	//
	// map.addAttribute("user_collect_page",
	// tdUserCollectService.findByUsername(user.getUsername(), page, size));
	// return "/site_mag/user_collect_list";
	// }
	// else if (type.equalsIgnoreCase("recent")) // 最近浏览
	// {
	// if (null == userId)
	// {
	// return "/site_mag/error_404";
	// }
	//
	// TdUser user = tdUserService.findOne(userId);
	//
	// if (null == user || null == user.getUsername())
	// {
	// return "/site_mag/error_404";
	// }
	//
	// map.addAttribute("user_recent_page",
	// tdUserRecentVisitService.findByUsernameOrderByVisitTimeDesc(user.getUsername(),
	// page, size));
	// return "/site_mag/user_recent_list";
	// }
	// else if (type.equalsIgnoreCase("reward")) // 返现
	// {
	// if (null == userId)
	// {
	// return "/site_mag/error_404";
	// }
	//
	// TdUser user = tdUserService.findOne(userId);
	//
	// if (null == user || null == user.getUsername())
	// {
	// return "/site_mag/error_404";
	// }
	//
	// map.addAttribute("user_reward_page",
	// tdUserCashRewardService.findByUsernameOrderByIdDesc(user.getUsername(),
	// page, size));
	// return "/site_mag/user_reward_list";
	// }
	// else if (type.equalsIgnoreCase("level")) // 用户等级
	// {
	// map.addAttribute("user_level_page",
	// tdUserLevelService.findAllOrderBySortIdAsc(page, size));
	// return "/site_mag/user_level_list";
	// }
	// else if (type.equalsIgnoreCase("consult")) // 用户咨询
	// {
	// map.addAttribute("user_consult_page", findTdUserConsult(statusId,
	// keywords, page, size));
	// return "/site_mag/user_consult_list";
	// }
	// else if (type.equalsIgnoreCase("comment")) // 用户评论
	// {
	// map.addAttribute("user_comment_page", findTdUserComment(statusId,
	// keywords, page, size));
	// return "/site_mag/user_comment_list";
	// }
	// else if (type.equalsIgnoreCase("return")) // 退换货
	// {
	// map.addAttribute("user_return_page", findTdUserReturn(statusId, keywords,
	// page, size));
	// return "/site_mag/user_return_list";
	// }
	// else if (type.equalsIgnoreCase("complain")) // 投诉
	// {
	// map.addAttribute("user_complain_page", findTdUserComplain(statusId,
	// keywords, page, size));
	// return "/site_mag/user_complain_list";
	// }
	// else if (type.equalsIgnoreCase("cancel")) // 取消订单 zhangji
	// {
	// map.addAttribute("user_cancel_page", findTdUserCancel( isRefund,page,
	// size));
	// return "/site_mag/user_cancel_list";
	// }
	// }
	//
	// return "/site_mag/error_404";
	// }

	@ModelAttribute
	public void getModel(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "userLevelId", required = false) Long userLevelId,
			@RequestParam(value = "userConsultId", required = false) Long userConsultId,
			@RequestParam(value = "userCommentId", required = false) Long userCommentId,
			@RequestParam(value = "userReturnId", required = false) Long userReturnId,
			@RequestParam(value = "userComplainId", required = false) Long userComplainId,
			@RequestParam(value = "userWithdrawId", required = false) Long userWithdrawId, Model model) {
		if (null != userId) {
			TdUser tdUser = tdUserService.findOne(userId);
			model.addAttribute("tdUser", tdUser);
		}

		if (null != userLevelId) {
			model.addAttribute("tdUserLevel", tdUserLevelService.findOne(userLevelId));
		}

		// if (null != userConsultId) {
		// model.addAttribute("tdUserConsult",
		// tdUserConsultService.findOne(userConsultId));
		// }

		if (null != userCommentId) {
			model.addAttribute("tdUserComment", tdUserCommentService.findOne(userCommentId));
		}

		// if (null != userReturnId) {
		// model.addAttribute("tdUserReturn",
		// tdUserReturnService.findOne(userReturnId));
		// }
		//
		// if (null != userComplainId) {
		// model.addAttribute("tdUserComplain",
		// tdUserComplainService.findOne(userComplainId));
		// }
		// if (null != userWithdrawId) {
		// model.addAttribute("tdUserWithdraw",
		// tdUserWithdrawService.findOne(userWithdrawId));
		// }
	}

	// private Page<TdUserConsult> findTdUserConsult(Long statusId, String
	// keywords, int page, int size)
	// {
	// Page<TdUserConsult> dataPage = null;
	//
	// if (null == statusId)
	// {
	// if (null == keywords || "".equalsIgnoreCase(keywords))
	// {
	// dataPage = tdUserConsultService.findAllOrderByIdDesc(page, size);
	// }
	// else
	// {
	// dataPage = tdUserConsultService.searchAndOrderByIdDesc(keywords, page,
	// size);
	// }
	// }
	// else
	// {
	// if (null == keywords || "".equalsIgnoreCase(keywords))
	// {
	// dataPage = tdUserConsultService.findByStatusIdOrderByIdDesc(statusId,
	// page, size);
	// }
	// else
	// {
	// dataPage =
	// tdUserConsultService.searchAndFindByStatusIdOrderByIdDesc(keywords,
	// statusId, page, size);
	// }
	// }
	//
	// return dataPage;
	// }

	private Page<TdUserComment> findTdUserComment(Long statusId, String keywords, int page, int size) {
		Page<TdUserComment> dataPage = null;

		if (null == statusId) {
			if (null == keywords || "".equalsIgnoreCase(keywords)) {
				dataPage = tdUserCommentService.findAllOrderByIdDesc(page, size);
			} else {
				dataPage = tdUserCommentService.searchAndOrderByIdDesc(keywords, page, size);
			}
		} else {
			if (null == keywords || "".equalsIgnoreCase(keywords)) {
				dataPage = tdUserCommentService.findByStatusIdOrderByIdDesc(statusId, page, size);
			} else {
				dataPage = tdUserCommentService.searchAndFindByStatusIdOrderByIdDesc(keywords, statusId, page, size);
			}
		}

		return dataPage;
	}

	// private Page<TdUserComplain> findTdUserComplain(Long statusId, String
	// keywords, int page, int size)
	// {
	// Page<TdUserComplain> dataPage = null;
	//
	// if (null == statusId)
	// {
	// if (null == keywords || "".equalsIgnoreCase(keywords))
	// {
	// dataPage = tdUserComplainService.findAllOrderByIdDesc(page, size);
	// }
	// else
	// {
	// dataPage = tdUserComplainService.searchAndOrderByIdDesc(keywords, page,
	// size);
	// }
	// }
	// else
	// {
	// if (null == keywords || "".equalsIgnoreCase(keywords))
	// {
	// dataPage = tdUserComplainService.findByStatusIdOrderByIdDesc(statusId,
	// page, size);
	// }
	// else
	// {
	// dataPage =
	// tdUserComplainService.searchAndFindByStatusIdOrderByIdDesc(keywords,
	// statusId, page, size);
	// }
	// }
	//
	// return dataPage;
	// }

	// private Page<TdUserReturn> findTdUserReturn(Long statusId, String
	// keywords, int page, int size)
	// {
	// Page<TdUserReturn> dataPage = null;
	//
	// if (null == statusId)
	// {
	// if (null == keywords || "".equalsIgnoreCase(keywords))
	// {
	// dataPage = tdUserReturnService.findAllOrderBySortIdAsc(page, size);
	// }
	// else
	// {
	// dataPage = tdUserReturnService.searchAndOrderBySortIdAsc(keywords, page,
	// size);
	// }
	// }
	// else
	// {
	// if (null == keywords || "".equalsIgnoreCase(keywords))
	// {
	// dataPage = tdUserReturnService.findByStatusIdOrderBySortIdAsc(statusId,
	// page, size);
	// }
	// else
	// {
	// dataPage =
	// tdUserReturnService.searchAndFindByStatusIdOrderBySortIdAsc(keywords,
	// statusId, page, size);
	// }
	// }
	//
	// return dataPage;
	// }
	/**
	 * @author Zhangji
	 * @param isRefund
	 * @param page
	 * @param size
	 * @return
	 */
	private Page<TdOrder> findTdUserCancel(Boolean isRefund, int page, int size) {
		Page<TdOrder> dataPage = null;

		if (null == isRefund) {
			dataPage = tdOrderService.findByIsCancelTrue(page, size);
		} else if (true == isRefund) {
			dataPage = tdOrderService.findByIsCancelTrueAndIsRefundTrue(page, size);
		} else if (false == isRefund) {
			dataPage = tdOrderService.findByIsCancelTrueAndIsRefundFalse(page, size);
		}

		return dataPage;
	}

	private void btnSave(String type, Long[] ids, Double[] sortIds) {
		if (null == ids || null == sortIds || ids.length < 1 || sortIds.length < 1 || null == type || "".equals(type)) {
			return;
		}

		for (int i = 0; i < ids.length; i++) {
			Long id = ids[i];

			if (type.equalsIgnoreCase("user")) // 用户
			{
				TdUser e = tdUserService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdUserService.save(e);
					}
				}
			} else if (type.equalsIgnoreCase("level")) // 用户等级
			{
				TdUserLevel e = tdUserLevelService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdUserLevelService.save(e);
					}
				}
			}
			// else if (type.equalsIgnoreCase("consult")) // 咨询
			// {
			// TdUserConsult e = tdUserConsultService.findOne(id);
			//
			// if (null != e)
			// {
			// if (sortIds.length > i)
			// {
			// e.setSortId(sortIds[i]);
			// tdUserConsultService.save(e);
			// }
			// }
			// }
			else if (type.equalsIgnoreCase("comment")) // 评论
			{
				TdUserComment e = tdUserCommentService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdUserCommentService.save(e);
					}
				}
			}
			// else if (type.equalsIgnoreCase("complain")) // 投诉
			// {
			// TdUserComplain e = tdUserComplainService.findOne(id);
			//
			// if (null != e)
			// {
			// if (sortIds.length > i)
			// {
			// e.setSortId(sortIds[i]);
			// tdUserComplainService.save(e);
			// }
			// }
			// }
			// else if (type.equalsIgnoreCase("return")) // 退换货
			// {
			// TdUserReturn e = tdUserReturnService.findOne(id);
			//
			// if (null != e)
			// {
			// if (sortIds.length > i)
			// {
			// e.setSortId(sortIds[i]);
			// tdUserReturnService.save(e);
			// }
			// }
			// }
		}
	}

	private void btnDelete(String type, Long[] ids, Integer[] chkIds) {
		if (null == ids || null == chkIds || ids.length < 1 || chkIds.length < 1 || null == type || "".equals(type)) {
			return;
		}

		for (int chkId : chkIds) {
			if (chkId >= 0 && ids.length > chkId) {
				Long id = ids[chkId];

				if (type.equalsIgnoreCase("user")) // 用户
				{
					tdUserService.delete(id);
				} else if (type.equalsIgnoreCase("level")) // 用户等级
				{
					tdUserLevelService.delete(id);
				} else if (type.equalsIgnoreCase("suggestion")) // 投诉咨询 zhangji
				{
					tdUserSuggestionService.delete(id);
				} else if (type.equalsIgnoreCase("message")) // 信息 zhangji
				{
					tdMessageService.delete(id);
				}
				// else if (type.equalsIgnoreCase("consult")) // 咨询
				// {
				// tdUserConsultService.delete(id);
				// }
				// else if (type.equalsIgnoreCase("comment")) // 评论
				// {
				// tdUserCommentService.delete(id);
				// }
				// else if (type.equalsIgnoreCase("complain")) // 投诉
				// {
				// tdUserComplainService.delete(id);
				// }
				// else if (type.equalsIgnoreCase("return")) // 退换货
				// {
				// tdUserReturnService.delete(id);
				// }
				// else if (type.equalsIgnoreCase("withdraw")) // 退换货
				// {
				// tdUserWithdrawService.delete(id);
				// }
			}
		}
	}

	private void btnVerify(String type, Long[] ids, Integer[] chkIds) {
		if (null == ids || null == chkIds || ids.length < 1 || chkIds.length < 1 || null == type || "".equals(type)) {
			return;
		}

		for (int chkId : chkIds) {
			if (chkId >= 0 && ids.length > chkId) {
				Long id = ids[chkId];

				// if (type.equalsIgnoreCase("consult")) // 咨询
				// {
				// TdUserConsult e = tdUserConsultService.findOne(id);
				//
				// if (null != e)
				// {
				// e.setStatusId(1L);
				// tdUserConsultService.save(e);
				// }
				// }
				if (type.equalsIgnoreCase("comment")) // 评论
				{
					TdUserComment e = tdUserCommentService.findOne(id);

					if (null != e) {
						e.setStatusId(1L);
						tdUserCommentService.save(e);
					}
				}
				// else if (type.equalsIgnoreCase("complain")) // 投诉
				// {
				// TdUserComplain e = tdUserComplainService.findOne(id);
				//
				// if (null != e)
				// {
				// e.setStatusId(1L);
				// tdUserComplainService.save(e);
				// }
				// }
				// else if (type.equalsIgnoreCase("return")) // 退换货
				// {
				// TdUserReturn e = tdUserReturnService.findOne(id);
				//
				// if (null != e)
				// {
				// e.setStatusId(1L);
				// tdUserReturnService.save(e);
				// }
				// }
			}
		}
	}
}
