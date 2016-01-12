package com.ynyes.lyz.controller.delivery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOwnMoneyRecord;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdOwnMoneyRecordService;
import com.ynyes.lyz.service.TdUserService;

@Controller
@RequestMapping(value = "/delivery")
public class TdDeliveryIndexController {

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdOrderService tdOrderService;
	
	@Autowired
	private TdOwnMoneyRecordService tdOwnMoneyRecordService;

	/**
	 * 获取配送列表
	 * @param start 开始日期
	 * @param end 结束日期
	 * @param days 几天内
	 * @param type 类型
	 * @param req
	 * @param map
	 * @return
	 */
	@RequestMapping
	public String deliveryIndex(String start, String end, Integer days, Integer type,
			HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");

		if (null == username) {
			return "redirect:/login";
		}

		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);

		if (null == user) {
			return "redirect:/login";
		}
		
		if (null == type)
		{
			type = 1;
		}
		
		map.addAttribute("type", type);
		
		if (null == start && null == end && null == days) {
			days = 3;
		}
		
		Date startDate = null, endDate = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		
		if (null != start) {
			try {
				cal.setTime(sdf.parse(start));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			startDate = cal.getTime();
			map.addAttribute("startDate", startDate);
		}
		
		if (null != end) {
			try {
				cal.setTime(sdf.parse(end));
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			endDate = cal.getTime();
			map.addAttribute("endDate", endDate);
		}
		
		if (null == startDate && null == endDate){
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);

			// 7天内
			if (days.equals(7)) {
				cal.add(Calendar.DATE, -7);
				startDate = cal.getTime();
			} else {
				cal.add(Calendar.DATE, -3);
				startDate = cal.getTime();
			}

			map.addAttribute("days", days);
		}
		
		List<TdOrder> orderList = null;
		
		if (null != startDate)
		{
			if (null != endDate)
			{
				if (type.equals(1))
				{
					orderList = tdOrderService.findByStatusIdAndOrderTimeBetweenOrStatusIdAndOrderTimeBetween(5L, 6L, startDate, endDate);
				}
				else if (type.equals(2))
				{
					orderList = tdOrderService.findByStatusIdAndOrderTimeBetween(4L, startDate, endDate);
				}
				else if (type.equals(3))
				{
					orderList = tdOrderService.findByStatusIdAndOrderTimeBetween(3L, startDate, endDate);
				}
			}
			else
			{
				if (type.equals(1))
				{
					orderList = tdOrderService.findByStatusIdAndOrderTimeAfterOrStatusIdAndOrderTimeAfter(5L, 6L, startDate);
				}
				else if (type.equals(2))
				{
					orderList = tdOrderService.findByStatusIdAndOrderTimeAfter(4L, startDate);
				}
				else if (type.equals(3))
				{
					orderList = tdOrderService.findByStatusIdAndOrderTimeAfter(3L, startDate);
				}
			}
		}
		
		map.addAttribute("order_list", orderList);

		return "/client/delivery_list";
	}
	
	@RequestMapping(value="/detail/{id}", method=RequestMethod.GET)
	public String detail(@PathVariable Integer id,
			HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");

		if (null == username) {
			return "redirect:/login";
		}

		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);

		if (null == user) {
			return "redirect:/login";
		}
		
		return "/client/delivery_detail";
	}
	
	@RequestMapping(value="/submitDelivery", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> submit(Long id,
			HttpServletRequest req, ModelMap map) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("code", 1);
		
		if (null == id)
		{
			res.put("message", "ID不能为空");
			return res;
		}
		
		TdOrder order = tdOrderService.findOne(id);
		
		if (null == order)
		{
			res.put("message", "订单不存在");
			return res;
		}
		
		if (null != order.getStatusId() && !order.getStatusId().equals(4L))
		{
			res.put("message", "订单未出库");
			return res;
		}
		
		order.setStatusId(5L);
		order.setDeliveryTime(new Date());
		
		tdOrderService.save(order);
		
		res.put("code", 0);
		
		return res;
	}
	
	@RequestMapping(value="/submitOwnMoney/{id}", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> submitMoney(@PathVariable Long id, Double payed, Double owned,
			HttpServletRequest req, ModelMap map) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("code", 1);
		
		if (null == payed || null == owned)
		{
			res.put("message", "金额不能为空");
			return res;
		}
		
		TdOrder order = tdOrderService.findOne(id);
		
		if (null == order)
		{
			res.put("message", "订单不存在");
			return res;
		}
		
		List<TdOwnMoneyRecord> recList = tdOwnMoneyRecordService.findByOrderNumberIgnoreCase(order.getOrderNumber());
		
		if (null != recList && recList.size() > 0)
		{
			res.put("message", "该订单已申请了欠款");
			return res;
		}
		
		TdOwnMoneyRecord rec = new TdOwnMoneyRecord();
		rec.setCreateTime(new Date());
		rec.setOrderNumber(order.getOrderNumber());
		rec.setOwned(owned);
		rec.setPayed(payed);
		rec.setUsername(order.getUsername());
		rec.setIsEnable(false);
		rec.setSortId(99L);
		
		tdOwnMoneyRecordService.save(rec);
		
		res.put("code", 0);
		
		return res;
	}

}
