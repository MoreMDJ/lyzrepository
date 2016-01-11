package com.ynyes.lyz.controller.delivery;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdUserService;

@Controller
@RequestMapping(value = "/delivery")
public class TdDeliveryIndexController {

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdDiySiteService tdDiySiteService;

	/**
	 * 跳转到附近门店的方法
	 * 
	 * @author Shawn
	 */
	@RequestMapping
	public String deliveryIndex(Date start, Date end, HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return "redirect:/login";
		}

		return "/client/delivery_list";
	}
	
}
