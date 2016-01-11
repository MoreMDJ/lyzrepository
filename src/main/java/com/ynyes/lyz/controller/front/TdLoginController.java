package com.ynyes.lyz.controller.front;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.util.MD5;

@Controller
@RequestMapping(value = "/login")
public class TdLoginController {

	@Autowired
	private TdUserService tdUserService;
	
	@Autowired
	private TdCommonService tdCommonService;

	@RequestMapping
	public String index() {
		return "/client/login";
	};

	@RequestMapping(value = "/check")
	@ResponseBody
	public Map<String, Object> loginCheck(HttpServletRequest req, String username, String password) {
		Map<String, Object> res = new HashMap<>();
		res.put("status", -1);
		TdUser user = tdUserService.findByUsernameAndPasswordAndIsEnableTrue(username, MD5.md5(password, 32));
		if (null != user) {
			res.put("status", 0);
			user.setLastLoginTime(new Date());
			// 设置session失效时间为一天
			req.getSession().setMaxInactiveInterval((60 * 60 * 24));
			req.getSession().setAttribute("username", username);

			tdCommonService.clear(req);
		}

		if (null == user) {
			TdUser user_by_username_is_enable = tdUserService.findByUsernameAndIsEnableTrue(username);
			if (null == user_by_username_is_enable) {
				TdUser user_by_username = tdUserService.findByUsername(username);
				if (null == user_by_username) {
					res.put("message", "该手机号码未注册");
				} else {
					res.put("message", "该账号已被冻结");
				}
			} else {
				res.put("message", "您输入的密码有误");
			}
		}
		return res;
	}

	/**
	 * 退出登陆的方法
	 * 
	 * @author dengxiao
	 */
	@RequestMapping(value = "/out")
	public String loginout(HttpServletRequest req, ModelMap map) {
		// 清空session中的用户信息
		req.getSession().setAttribute("username", null);
		return "redirect:/login";
	}
}
