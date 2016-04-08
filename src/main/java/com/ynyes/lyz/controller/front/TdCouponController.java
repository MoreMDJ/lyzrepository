package com.ynyes.lyz.controller.front;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdUserService;
/**
 * 抢券
 * @author Max
 *
 */
@Controller
public class TdCouponController {

	@Autowired
	private TdUserService tdUserService;
	
	@Autowired
	private TdCouponService tdCouponService;
	
	@Autowired
	private TdGoodsService tdGoodsService;
	
	/**
	 * 抢券
	 * @author Max
	 * 
	 */
	@RequestMapping(value="/coupon/list")
	public String couponList(HttpServletRequest req,ModelMap map)
	{
		String username =(String)req.getSession().getAttribute("username");
		if(null == username)
		{
			return "redirect:/login";
		}
		TdUser user= tdUserService.findByUsername(username);
		if(null == user){
			return "redirect:/login";
		}
		// 查找可领取现金券
		map.addAttribute("couponList", tdCouponService.findByCityNameAndTypeIdAndTypeCategoryId(user.getCityName(),2L, 1L,new Date()));
		// 可领取产品券
		map.addAttribute("coupon_list", tdCouponService.findByCityNameAndTypeIdAndTypeCategoryId(user.getCityName(),2L, 3L,new Date()));
		
		return "/client/coupon_list";
	}
	
	/**
	 * 领券
	 * @author Max
	 */
	@RequestMapping(value="/coupon/grant",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> grantCoupon(Long id,HttpServletRequest req)
	{
		Map<String,Object> res = new HashMap<String, Object>();
		res.put("code","0");
		String username = (String)req.getSession().getAttribute("username");
		
		if(null == username)
		{
			res.put("msg", "请重新登录");
			return res;
		}
		
		TdUser user = tdUserService.findByUsername(username);
		
		if(null != id)
		{
			
			TdCoupon coupon = tdCouponService.findOne(id);
			
			if(null == coupon )
			{
				res.put("msg","参数错误");
				return res;
			}
			if(null == coupon.getLeftNumber() || coupon.getLeftNumber()==0)
			{
				res.put("msg", "次优惠券已领完");
				return res;
			}
			
			// 新创建会员领用券
			TdCoupon tdCoupon = new TdCoupon();
			// 会员领取信息
			tdCoupon.setUsername(user.getUsername());
			tdCoupon.setMobile(user.getNickname());
			tdCoupon.setGetNumber(1L);
			tdCoupon.setIsOutDate(false);
			tdCoupon.setIsUsed(false);
			tdCoupon.setGetTime(new Date());
			// 优惠券基本信息
			tdCoupon.setIsDistributted(true);
			tdCoupon.setPrice(coupon.getPrice());
			tdCoupon.setAddTime(coupon.getAddTime());
			tdCoupon.setTypePicUri(coupon.getTypePicUri());
			tdCoupon.setExpireTime(coupon.getExpireTime());
			
			tdCoupon.setBrandId(coupon.getBrandId());
			tdCoupon.setBrandTitle(coupon.getBrandTitle());
			tdCoupon.setTypeDescription(coupon.getTypeDescription());
			tdCoupon.setGoodsId(coupon.getGoodsId());
			tdCoupon.setGoodsName(coupon.getGoodsName());
			tdCoupon.setPicUri(coupon.getPicUri());
			tdCoupon.setTypeId(coupon.getTypeId());
			tdCoupon.setTypeTitle(coupon.getTypeTitle());
			tdCoupon.setTypeCategoryId(coupon.getTypeCategoryId());
			TdGoods good= tdGoodsService.findOne(coupon.getGoodsId());
			if(good!=null){
				tdCoupon.setSku(good.getCode());
			}
			// 保存领取
			tdCouponService.save(tdCoupon);
			
			// 更新剩余量
			coupon.setLeftNumber(coupon.getLeftNumber()-1);
			tdCouponService.save(coupon);
			
			res.put("code", 1);
			res.put("msg", "领券成功");
			return res;
		}
		res.put("msg","参数错误");
		
		return res;
	}
	
}
