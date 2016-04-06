package com.ynyes.lyz.controller.management;

import static org.apache.commons.lang3.StringUtils.leftPad;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import com.ibm.icu.util.Calendar;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdManager;
import com.ynyes.lyz.entity.TdManagerRole;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.entity.TdReturnReport;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdUserTurnRecord;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdDeliveryInfoDetailService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdManagerRoleService;
import com.ynyes.lyz.service.TdManagerService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdPayTypeService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.service.TdReturnReportService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.service.TdUserTurnRecordService;
import com.ynyes.lyz.util.SiteMagConstant;

@Controller
@RequestMapping(value = "/Verwalter/returnNote")
public class TdManagerReturnNoteController extends TdManagerBaseController{

	@Autowired
	TdReturnNoteService tdReturnNoteService;

	@Autowired
	TdManagerLogService tdManagerLogService;

	@Autowired
	private TdOrderService tdOrderService;

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
	
	@Autowired
	private TdManagerService tdManagerService;
	
	@Autowired
	private TdManagerRoleService tdManagerRoleService;
	
	@Autowired
	private TdGoodsService tdGoodsService;
	
	@Autowired
	private TdDeliveryInfoDetailService tdDeliveryInfoDetailService;
	
	@Autowired
	private TdCityService tdCityService;
	
	@Autowired
	private TdDiySiteService tdDiySiteService;
	
	@Autowired
	private TdReturnReportService tdReturnReportService;
	
	// 列表
	@RequestMapping(value = "/{type}/list")
	public String list(@PathVariable String type, Integer page, Integer size, String keywords, String __EVENTTARGET,
			String __EVENTARGUMENT, String __VIEWSTATE, Long[] listId, Integer[] listChkId, Double[] listSortId,
			ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		
		TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		TdManagerRole tdManagerRole = null;
		if (tdManager != null && tdManager.getRoleId() != null)
		{
			tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
		}
		if (tdManagerRole == null)
		{
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
//				if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
//				{
//					String diyCode = tdManager.getDiyCode();
//					TdDiySite tdDiySite = tdDisSiteService.findByStoreCode(diyCode);
//					map.addAttribute("returnNote_page",tdReturnNoteService.findBySiteIdAndKeywords(tdDiySite.getId(), keywords, page, size));
//				}
//				else
//				{
//					if (StringUtils.isNotBlank(keywords))
//					{
//						map.addAttribute("returnNote_page", tdReturnNoteService.searchAll(keywords, page, size));
//					}
//					else
//					{
//						map.addAttribute("returnNote_page", tdReturnNoteService.findAll(page, size));
//					}
//				}
				String siteName = tdReturnNoteService.findSiteTitleByUserName(username);
				siteName = StringUtils.isNotBlank(siteName) ? siteName : keywords;
				String keyword = StringUtils.isNotBlank(keywords) ? keywords : "";
				if (StringUtils.isNotBlank(siteName))
				{
					map.addAttribute("returnNote_page", tdReturnNoteService
							.findByDiySiteTitleAndOrderNumberOrReturnNumberOrUsername(siteName, keyword, page, size));
				}
				else if (StringUtils.isNotBlank(keyword))
				{
					map.addAttribute("returnNote_page", tdReturnNoteService.searchAll(keyword, page, size));
				}
				else
				{
					map.addAttribute("returnNote_page", tdReturnNoteService.findAll(page, size));
				}
				//城市和门店信息
				if (tdManagerRole.getIsSys()){
					map.addAttribute("diySiteList",tdDiySiteService.findAll());
					map.addAttribute("cityList", tdCityService.findAll());
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
	
	@RequestMapping(value = "/downdatareturnorder")
	@ResponseBody
	public String downdatareturnorder(HttpServletRequest req,ModelMap map,String begindata,String enddata,HttpServletResponse response,String diyCode,String city)
	{
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username)
		{
			return "redirect:/Verwalter/login";
		}
		TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		TdManagerRole tdManagerRole = null;
		if (tdManager != null && tdManager.getRoleId() != null)
		{
			tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
		}
		if (tdManagerRole == null)
		{
			return "redirect:/Verwalter/login";
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		
		HSSFSheet sheet = workbook.createSheet("退货报表");
		//列宽
		sheet.setColumnWidth(0 , 10*256);//退货门店
        sheet.setColumnWidth(1 , 25*256);//原订单号
        sheet.setColumnWidth(2 , 20*256);//退货单号
        sheet.setColumnWidth(3 , 13*256);//退货单状态
        sheet.setColumnWidth(4 , 13*256);//品牌
        sheet.setColumnWidth(5 , 13*256);//商品类别
        sheet.setColumnWidth(6 , 13*256);//导购
        sheet.setColumnWidth(7 , 13*256);//订单日期
        sheet.setColumnWidth(8 , 13*256);//退货日期
        sheet.setColumnWidth(9 , 13*256);//客户名称
        sheet.setColumnWidth(10 , 13*256);//客户电话
        sheet.setColumnWidth(11 , 13*256);//产品编号
        sheet.setColumnWidth(12 , 26*256);//产品名称
        sheet.setColumnWidth(13 , 13*256);//退货数量
        sheet.setColumnWidth(14 , 13*256);//退货单价
        sheet.setColumnWidth(15 , 13*256);//退货金额
        sheet.setColumnWidth(16 , 13*256);//退现金卷金额
        sheet.setColumnWidth(17 , 13*256);//退产品卷金额
        sheet.setColumnWidth(18 , 13*256);//客户备注
        sheet.setColumnWidth(19 , 13*256);//中转仓
        sheet.setColumnWidth(20 , 13*256);//配送人员
        sheet.setColumnWidth(21 , 13*256);//配送人电话
        sheet.setColumnWidth(22 , 13*256);//退货地址
        
		HSSFRow row = sheet.createRow(0);
		String[] cellValues={"退货门店","原订单号","退货单号","退货单状态","品牌","商品类别","导购","订单日期","退货日期","客户名称",
				"客户电话","产品编号","产品名称","退货数量","退货单价","退货金额","退现金卷金额","退产品卷金额","客户备注","中转仓",
				"配送人员","配送人电话","退货地址"};
		cellDates(cellValues, style, row);
		
		Date begin = stringToDate(begindata,null);
		Date end = stringToDate(enddata,null);
		
//		String siteName = tdReturnNoteService.findSiteTitleByUserName(username);
//		List<TdReturnNote> returnList = tdReturnNoteService.findByOrderTimeOrderByOrderTimeDesc(begin, end,siteName);
//		
		/*List<TdReturnNote> returnList = null;
       
        
        	if(tdManagerRole.getTitle().equalsIgnoreCase("超级管理组") &&  null != city && !city.equals(0L)){
        		List<TdDiySite> siteList =tdDiySiteService.findByCityId(city);
        		List<String> siteNamesList=new ArrayList<String>();
        		if(null != siteList){
        			for (TdDiySite site : siteList) {
            			siteNamesList.add(site.getTitle());
    				}
        		}
        		returnList =tdReturnNoteService.findByOrderTimeOrderByOrderTimeDesc(begin, end,null,siteNamesList);
        		
        	}else{
        		String siteName = tdReturnNoteService.findSiteTitleByUserName(username);
        		returnList = tdReturnNoteService.findByOrderTimeOrderByOrderTimeDesc(begin, end,siteName,null);
        	}
        
		
		
		if (returnList != null && returnList.size()>0)
		{
			Integer i = 1;
			for (TdReturnNote returnNote : returnList) {
				if(null!= returnNote.getReturnGoodsList() && returnNote.getReturnGoodsList().size()>0){
					for(TdOrderGoods good:returnNote.getReturnGoodsList()){
						row = sheet.createRow(i);
						if (returnNote.getDiySiteTitle() != null)
						{//退货门店
							row.createCell(0).setCellValue(returnNote.getDiySiteTitle());
						}
						if (returnNote.getOrderNumber() != null)
						{//原订单号
							row.createCell(1).setCellValue(returnNote.getOrderNumber());
							TdOrder order= tdOrderService.findByOrderNumber(returnNote.getOrderNumber());
							if(null != order){
								TdUser user= tdUserSerrvice.findByUsername(order.getUsername());
								if(user!=null){
									row.createCell(9).setCellValue(user.getRealName());//客户名称 
								}
								row.createCell(10).setCellValue(order.getUsername());// 客户电话
								row.createCell(6).setCellValue(order.getSellerRealName());//导购
					        	row.createCell(16).setCellValue(order.getCashCoupon());//退现金卷金额
					            row.createCell(17).setCellValue(order.getProductCoupon());//退产品卷金额
					        	row.createCell(22).setCellValue(order.getShippingAddress());//退货地址
							}
							if(StringUtils.isNotBlank(order.getMainOrderNumber())){
								TdUser user= tdUserSerrvice.searchDriverByMainOrderNumber(order.getMainOrderNumber());
								if(user!=null){
									row.createCell(20).setCellValue(user.getRealName());//配送人员
						        	row.createCell(21).setCellValue(user.getUsername());//配送人员电话
								}
							}
							
						}
						if (returnNote.getReturnNumber() != null)
						{//退货单号
							row.createCell(2).setCellValue(returnNote.getReturnNumber());
						}
						if (returnNote.getStatusId() != null)
						{//退货单状态
							if(returnNote.getStatusId().equals(1L)){
								row.createCell(3).setCellValue("确认退货单");
							}
							if(returnNote.getStatusId().equals(2L)){
								row.createCell(3).setCellValue("通知物流");
							}
							if(returnNote.getStatusId().equals(3L)){
								row.createCell(3).setCellValue("验货确认");
							}
							if(returnNote.getStatusId().equals(4L)){
								row.createCell(3).setCellValue("确认退款");
							}
							if(returnNote.getStatusId().equals(1L)){
								row.createCell(3).setCellValue("已完成");
							}
						}
						if (good.getBrandTitle() != null)
						{//品牌
							row.createCell(4).setCellValue(good.getBrandTitle());
						}
						if (good.getGoodsId() != null)
						{//商品类别
							TdGoods g= tdGoodsService.findOne(good.getGoodsId());
							if(null != g && null != g.getCategoryTitle())
							row.createCell(5).setCellValue(g.getCategoryTitle());
						}
						if (returnNote.getOrderTime() != null)
						{//订单日期
							row.createCell(7).setCellValue(returnNote.getOrderTime().toString());
						}
						if (returnNote.getCancelTime() != null)
						{//退货日期
							row.createCell(8).setCellValue(returnNote.getCancelTime().toString());
						}
						if (good.getSku() != null)
						{//产品编号
							row.createCell(11).setCellValue(good.getSku());
						}
						if (good.getGoodsTitle() != null)
			        	{//产品名称
			            	row.createCell(12).setCellValue(good.getGoodsTitle());
			    		}
			        	if (good.getQuantity() != null)
			        	{//退货数量
			            	row.createCell(13).setCellValue(good.getQuantity());
			    		}
						if (good.getPrice() != null)
						{//退货单价
							row.createCell(14).setCellValue(good.getPrice());
						}
						
			        	if (returnNote.getTurnPrice() != null)
			        	{//退货总价
			        		row.createCell(15).setCellValue(returnNote.getTurnPrice());
						}
			        	
			        	if(returnNote.getRemarkInfo() != null){//客户备注
			        		row.createCell(18).setCellValue(returnNote.getRemarkInfo());
			        	}
			        	if(returnNote.getRemarkInfo() != null){//中转仓
			        		row.createCell(19).setCellValue(returnNote.getRemarkInfo());
			        	}
			        	
						i++;
					}
				}
			}
		}*/
		if(null == end || end.after(getStartTime())){
        	try {//调用存储过程 报错
        		tdReturnReportService.callInsertReturnReport(getStartTime(), getEndTime());
    		} catch (Exception e) {
    			System.out.println(e);
    		}
        }
    	if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
		{
        	diyCode=tdManager.getDiyCode();
        	city=null;
		}
        List<TdReturnReport> returnReportList = tdReturnReportService.searchReturnReport(begin,end, city, diyCode);
	
		if (returnReportList != null && returnReportList.size()>0)
		{
			Integer i = 1;
			for (TdReturnReport returnReport : returnReportList) {
						row = sheet.createRow(i);
						if (returnReport.getDiySiteName() != null)
						{//退货门店
							row.createCell(0).setCellValue(returnReport.getDiySiteName());
						}
						if (returnReport.getOrderNumber() != null)
						{//原订单号
							row.createCell(1).setCellValue(returnReport.getOrderNumber());
									row.createCell(9).setCellValue(returnReport.getRealName());//客户名称 
								row.createCell(10).setCellValue(returnReport.getUsername());// 客户电话
								row.createCell(6).setCellValue(returnReport.getSellerRealName());//导购
					        	row.createCell(16).setCellValue(returnReport.getCashCoupon());//退现金卷金额
					            row.createCell(17).setCellValue(returnReport.getProductCoupon());//退产品卷金额
					        	row.createCell(22).setCellValue(returnReport.getShippingAddress());//退货地址
									row.createCell(20).setCellValue(returnReport.getDeliverRealName());//配送人员
						        	row.createCell(21).setCellValue(returnReport.getDeliverUsername());//配送人员电话
							
						}
						if (returnReport.getReturnNumber() != null)
						{//退货单号
							row.createCell(2).setCellValue(returnReport.getReturnNumber());
						}
						if (returnReport.getStatusId() != null)
						{//退货单状态
							if(returnReport.getStatusId().equals(1L)){
								row.createCell(3).setCellValue("确认退货单");
							}
							if(returnReport.getStatusId().equals(2L)){
								row.createCell(3).setCellValue("通知物流");
							}
							if(returnReport.getStatusId().equals(3L)){
								row.createCell(3).setCellValue("验货确认");
							}
							if(returnReport.getStatusId().equals(4L)){
								row.createCell(3).setCellValue("确认退款");
							}
							if(returnReport.getStatusId().equals(1L)){
								row.createCell(3).setCellValue("已完成");
							}
						}
						if (returnReport.getBrandTitle() != null)
						{//品牌
							row.createCell(4).setCellValue(returnReport.getBrandTitle());
						}
						if (returnReport.getCategoryTitle() != null)
						{//商品类别
							row.createCell(5).setCellValue(returnReport.getCategoryTitle());
						}
						if (returnReport.getOrderTime() != null)
						{//订单日期
							row.createCell(7).setCellValue(returnReport.getOrderTime().toString());
						}
						if (returnReport.getCancelTime() != null)
						{//退货日期
							row.createCell(8).setCellValue(returnReport.getCancelTime().toString());
						}
						if (returnReport.getSku() != null)
						{//产品编号
							row.createCell(11).setCellValue(returnReport.getSku());
						}
						if (returnReport.getGoodsTitle() != null)
			        	{//产品名称
			            	row.createCell(12).setCellValue(returnReport.getGoodsTitle());
			    		}
			        	if (returnReport.getQuantity() != null)
			        	{//退货数量
			            	row.createCell(13).setCellValue(returnReport.getQuantity());
			    		}
						if (returnReport.getPrice() != null)
						{//退货单价
							row.createCell(14).setCellValue(returnReport.getPrice());
						}
						
			        	if (returnReport.getTurnPrice() != null)
			        	{//退货总价
			        		row.createCell(15).setCellValue(returnReport.getTurnPrice());
						}
			        	
			        	if(returnReport.getRemarkInfo() != null){//客户备注
			        		row.createCell(18).setCellValue(returnReport.getRemarkInfo());
			        	}
			        	if(returnReport.getWhNo() != null){//中转仓
			        		row.createCell(19).setCellValue(tdCommonService.changeName(returnReport.getWhNo()));
			        	}
			        	
						i++;
					}
		}
		
		download(workbook, "1", response,"退货报表");
		return "";
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
	private Date getStartTime(){  
        Calendar todayStart = Calendar.getInstance();  
        todayStart.set(Calendar.HOUR, 0);  
        todayStart.set(Calendar.MINUTE, 0);  
        todayStart.set(Calendar.SECOND, 0);  
        todayStart.set(Calendar.MILLISECOND, 0);  
        return todayStart.getTime();  
    }  
      
    private Date getEndTime(){  
        Calendar todayEnd = Calendar.getInstance();  
        todayEnd.set(Calendar.HOUR, 23);  
        todayEnd.set(Calendar.MINUTE, 59);  
        todayEnd.set(Calendar.SECOND, 59);  
        todayEnd.set(Calendar.MILLISECOND, 999);  
        return todayEnd.getTime();  
    }
}
