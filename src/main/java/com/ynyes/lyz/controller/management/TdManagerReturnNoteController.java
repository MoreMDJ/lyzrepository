package com.ynyes.lyz.controller.management;

import static org.apache.commons.lang3.StringUtils.leftPad;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdUserTurnRecord;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdOrderGoodsService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdPayTypeService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.service.TdUserTurnRecordService;
import com.ynyes.lyz.util.SiteMagConstant;

@Controller
@RequestMapping(value = "/Verwalter/returnNote")
public class TdManagerReturnNoteController {

	@Autowired
	TdReturnNoteService tdReturnNoteService;

	@Autowired
	TdManagerLogService tdManagerLogService;

	@Autowired
	private TdOrderService tdOrderService;

	@Autowired
	private TdOrderGoodsService tdOrderGoodsService;

	@Autowired
	private TdUserService tdUserSerrvice;

	@Autowired
	private TdDiySiteService tdDisSiteService;

	@Autowired
	private TdPayTypeService tdPayTypeService;

	@Autowired
	private TdUserTurnRecordService tdUserTurnRecordService;

	@Autowired
	private TdCommonService tdCommonService;

	// 列表
	@RequestMapping(value = "/{type}/list")
	public String list(@PathVariable String type, Integer page, Integer size, String keywords, String __EVENTTARGET,
			String __EVENTARGUMENT, String __VIEWSTATE, Long[] listId, Integer[] listChkId, Double[] listSortId,
			ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != __EVENTTARGET) {
			if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
			{
				btnDelete(type, listId, listChkId);

				if (type.equalsIgnoreCase("returnNote")) 
				{
					tdManagerLogService.addLog("delete", "删除退货单", req);
				}

			}
			if (__EVENTTARGET.equalsIgnoreCase("btnPage"))
			{
				if (__EVENTARGUMENT != null)
				{
					try
					{
						page = Integer.parseInt(__EVENTARGUMENT);
					}
					catch (Exception e) 
					{
						// TODO: handle exception
						page = 0;
					}
				}
			}

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
		map.addAttribute("keywords", keywords);
		map.addAttribute("__EVENTTARGET", __EVENTTARGET);
		map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null != type) {
			if (type.equalsIgnoreCase("returnNote")) //
			{
				if (null == keywords || keywords.equalsIgnoreCase("")) 
				{
					map.addAttribute("returnNote_page", tdReturnNoteService.findAll(page, size));
				}
				else
				{
					map.addAttribute("returnNote_page", tdReturnNoteService.searchAll(keywords, page, size));
				}

				return "/site_mag/returnNote_list";
			}

		}
		return "/site_mag/returnNote_list";
	}

	@RequestMapping(value = "/{type}/edit")
	public String edit(@PathVariable String type, Long id, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null != type) {
			if (type.equalsIgnoreCase("returnNote")) // 支付方式
			{
				if (null != id) {
					map.addAttribute("returnNote", tdReturnNoteService.findOne(id));
				}

				return "/site_mag/returnNote_edit";
			}
		}

		return "/site_mag/returnNote_edit";
	}

	/**
	 * 修改退货单状态
	 * 
	 * @author Max
	 * 
	 */
	@RequestMapping(value = "/param/edit", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> returnNoteParam(String returnNumber, String type, String data, HttpServletRequest req) {
		Map<String, Object> res = new HashMap<>();

		res.put("code", 1);

		String username = (String) req.getSession().getAttribute("manager");

		if (null == username) {
			res.put("message", "请重新登录");
			return res;
		}

		if (null != returnNumber && !returnNumber.isEmpty() && null != type && !type.isEmpty()) {
			TdReturnNote returnNote = tdReturnNoteService.findByReturnNumber(returnNumber);

			// 修改备注
			if ("editMark".equalsIgnoreCase(type)) {
				returnNote.setManagerRemarkInfo(data);
			}
			// 确认退货单
			else if ("confirm".equalsIgnoreCase(type)) {
				if (returnNote.getStatusId() == 1) {
					returnNote.setStatusId(2L);
				}
			}
			// 通知物流
			else if ("informDiy".equalsIgnoreCase(type)) {
				// 配送单——到店退
				if (returnNote.getTurnType() == 2) {

					// 生成收货通知
					tdCommonService.sendBackMsgToWMS(returnNote);

				}

				if (returnNote.getStatusId().equals(2L)) {
					returnNote.setStatusId(3L);
				}
			}
			// 确认验货
			else if ("examineReturn".equalsIgnoreCase(type)) {
				if (returnNote.getStatusId().equals(3L)) {
					returnNote.setStatusId(4L);
					returnNote.setCheckTime(new Date());
				}
			}
			// 确认退款
			else if ("refund".equals(type)) {
				if (returnNote.getStatusId().equals(4L)) {
					// 查找订单
					TdOrder order = tdOrderService.findByOrderNumber(returnNote.getOrderNumber());

					// 查找会员
					TdUser user = tdUserSerrvice.findByUsernameAndIsEnableTrue(returnNote.getUsername());

					// 查找门店
					TdDiySite diySite = tdDisSiteService.findOne(returnNote.getDiySiteId());

					// 查看支付方式
					TdPayType payType = tdPayTypeService.findOne(order.getPayTypeId());

					Date current = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
					String curStr = sdf.format(current);
					Random random = new Random();

					TdUserTurnRecord record = new TdUserTurnRecord();
					record.setOrderNumber(order.getOrderNumber());
					record.setTurnNumber(returnNote.getReturnNumber());
					record.setUsername(username);
					record.setRecordTime(new Date());
					record.setCashBalance(order.getCashBalanceUsed());
					record.setUnCashBalance(order.getUnCashBalanceUsed());
					record.setTurnPrice(order.getTotalPrice());

					record.setRecordNumber("J" + curStr + leftPad(Integer.toString(random.nextInt(999)), 3, "0"));
					// 保存退款记录
					tdUserTurnRecordService.save(record);

					// 自提单
					if ("门店自提".equals(order.getDeliverTypeTitle())) {
						// 直营
						if (diySite.getStatus() == 0L) {
							// 线上支付
							if (payType.getIsOnlinePay()) {
								// 线上退款 生成退款记录
								// 退回用户可提现金额
								if (null != order.getCashBalanceUsed()) {
									user.setCashBalance(user.getCashBalance() + order.getCashBalanceUsed());
								}

								// 退回用户不可提现金额
								if (null != order.getUnCashBalanceUsed()) {
									user.setUnCashBalance(user.getUnCashBalance() + order.getUnCashBalanceUsed());
								}
							}
							// 退货单退款信息EBS

						}
						// 加盟
						else if (diySite.getStatus() == 1L) {
							// 线上支付
							if (payType.getIsOnlinePay()) {
								// 线上退款 生成退款记录
								// 退回用户可提现金额
								if (null != order.getCashBalanceUsed()) {
									user.setCashBalance(user.getCashBalance() + order.getCashBalanceUsed());
								}

								// 退回用户不可提现金额
								if (null != order.getUnCashBalanceUsed()) {
									user.setUnCashBalance(user.getUnCashBalance() + order.getUnCashBalanceUsed());
								}

								// 款项传入EBS

							}
						}
					}
					// 配送单
					else if ("送货上门".equals(order.getDeliverTypeTitle())) {
						// 线上退款 生成退款记录
						// 线上支付 退回提现和不可提现金额
						if (payType.getIsOnlinePay()) {
							// 退回用户可提现金额
							if (null != order.getCashBalanceUsed()) {
								user.setCashBalance(user.getCashBalance() + order.getCashBalanceUsed());
							}

							// 退回用户不可提现金额
							if (null != order.getUnCashBalanceUsed()) {
								user.setUnCashBalance(user.getUnCashBalance() + order.getUnCashBalanceUsed());
							}
						} else {
							// 货到付款退总额
							user.setCashBalance(user.getCashBalance() + order.getTotalPrice());
						}

						// 款项传入EBS

					}

					// 更新用户金额
					tdUserSerrvice.save(user);

					returnNote.setStatusId(5L);// 退货单设置已完成
				}
			}

			tdReturnNoteService.save(returnNote);
			res.put("code", 0);
			res.put("message", "修改成功");
			return res;

		}

		res.put("message", "参数错误");
		return res;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(TdReturnNote tdReturnNote, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null == tdReturnNote.getId()) {
			tdManagerLogService.addLog("add", "新增退货单", req);
		} else {
			tdManagerLogService.addLog("edit", "修改退货单", req);
		}
		tdReturnNoteService.save(tdReturnNote);

		return "redirect:/Verwalter/returnNote/returnNote/list";
	}

	@ModelAttribute
	public void getModel(@RequestParam(value = "returnNoteId", required = false) Long returnNoteId, Model model) {
		if (null != returnNoteId) {
			model.addAttribute("tdReturnNote", tdReturnNoteService.findOne(returnNoteId));
		}

	}

	private void btnDelete(String type, Long[] ids, Integer[] chkIds) {
		if (null == type || type.isEmpty()) {
			return;
		}

		if (null == ids || null == chkIds || ids.length < 1 || chkIds.length < 1) {
			return;
		}

		for (int chkId : chkIds) {
			if (chkId >= 0 && ids.length > chkId) {
				Long id = ids[chkId];

				if (type.equalsIgnoreCase("returnNote")) {
					tdReturnNoteService.delete(id);
				}

			}
		}
	}

	private void btnSave(String type, Long[] ids, Double[] sortIds) {
		if (null == type || type.isEmpty()) {
			return;
		}

		if (null == ids || null == sortIds || ids.length < 1 || sortIds.length < 1) {
			return;
		}

		for (int i = 0; i < ids.length; i++) {
			Long id = ids[i];

			if (type.equalsIgnoreCase("returnNote")) {
				TdReturnNote e = tdReturnNoteService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdReturnNoteService.save(e);
					}
				}
			}

		}
	}
}
