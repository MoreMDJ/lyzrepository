package com.ynyes.lyz.controller.front;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.webservice.impl.CallWMSImpl;

@Controller
@RequestMapping(value = "/diysite")
public class TdDiySiteController {

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdDiySiteService tdDiySiteService;

	/**
	 * 跳转到附近门店的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping
	public String diySiteList(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		// 获取用户的城市id
		Long cityId = user.getCityId();
		// 查找到附近所有的门店
		List<TdDiySite> diysite_list = tdDiySiteService.findByRegionIdAndIsEnableOrderBySortIdAsc(cityId);
		map.addAttribute("diysite_list", diysite_list);
		return "/client/diy_site_list";
	}
	@RequestMapping(value="testWMS")
	@ResponseBody
	public String testWMS()
	{
		CallWMSImpl iml = new CallWMSImpl();
//		String result = iml.sendMsgToWMS("test","order");
		return "";
	}
}
