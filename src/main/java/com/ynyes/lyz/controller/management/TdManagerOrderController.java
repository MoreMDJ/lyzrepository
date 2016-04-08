package com.ynyes.lyz.controller.management;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import com.ibm.icu.util.Calendar;
import com.ynyes.lyz.entity.TdAgencyFund;
import com.ynyes.lyz.entity.TdDeliveryInfo;
import com.ynyes.lyz.entity.TdDeliveryType;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGathering;
import com.ynyes.lyz.entity.TdManager;
import com.ynyes.lyz.entity.TdManagerRole;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOwnMoneyRecord;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdPriceList;
import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.entity.TdSalesDetail;
import com.ynyes.lyz.entity.TdShippingAddress;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdWareHouse;
import com.ynyes.lyz.service.TdAgencyFundService;
import com.ynyes.lyz.service.TdArticleService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdDeliveryInfoDetailService;
import com.ynyes.lyz.service.TdDeliveryInfoService;
import com.ynyes.lyz.service.TdDeliveryTypeService;
import com.ynyes.lyz.service.TdDistrictService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGatheringService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdManagerRoleService;
import com.ynyes.lyz.service.TdManagerService;
import com.ynyes.lyz.service.TdOrderGoodsService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdOwnMoneyRecordService;
import com.ynyes.lyz.service.TdPayTypeService;
import com.ynyes.lyz.service.TdPriceListService;
import com.ynyes.lyz.service.TdProductCategoryService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.service.TdSalesDetailService;
import com.ynyes.lyz.service.TdSettingService;
import com.ynyes.lyz.service.TdShippingAddressService;
import com.ynyes.lyz.service.TdSubdistrictService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.service.TdWareHouseService;
import com.ynyes.lyz.util.SiteMagConstant;

/**
 * 后台首页控制器
 * 
 * @author Sharon
 */

@Controller
@RequestMapping(value = "/Verwalter/order")
public class TdManagerOrderController {

	@Autowired
	TdProductCategoryService tdProductCategoryService;

	@Autowired
	TdArticleService tdArticleService;

	@Autowired
	TdGoodsService tdGoodsService;

	@Autowired
	TdPayTypeService tdPayTypeService;

	@Autowired
	TdDeliveryTypeService tdDeliveryTypeService;

	@Autowired
	TdDiySiteService tdDiySiteService;

	@Autowired
	TdOrderService tdOrderService;

	@Autowired
	TdUserService tdUserService;

	@Autowired
	TdManagerLogService tdManagerLogService;

	@Autowired
	TdSettingService tdSettingService;

	@Autowired
	TdShippingAddressService tdShippingAddressService;

	@Autowired
	TdCityService tdCityService;

	@Autowired
	private TdDistrictService tdDistrictService;

	@Autowired
	private TdSubdistrictService tdSubdistrictService;

	@Autowired
	private TdPriceListService tdPriceListService;
	
	@Autowired
	private TdManagerService tdManagerService;
	
	@Autowired
	private TdManagerRoleService tdManagerRoleService;
	
	@Autowired
	private TdOwnMoneyRecordService tdOwnMoneyRecordService;
	
	@Autowired
	private TdDeliveryInfoService tdDeliveryInfoService;
	
	@Autowired
	private TdDeliveryInfoDetailService tdDeliveryInfoDetailService;
	
	@Autowired
	private TdOrderGoodsService tdOrderGoodsService;
	
	@Autowired
	private TdCommonService tdCommonService;
	
	@Autowired
	private TdReturnNoteService tdReturnNoteService;
	
	@Autowired
	private TdWareHouseService tdWareHouseService;
	
	@Autowired
	private TdAgencyFundService tdAgencyFundService;
	
	@Autowired
	private TdSalesDetailService tdSalesDetailService;
	
	@Autowired
	private TdGatheringService tdGatheringService;
	
	@RequestMapping(value = "/downdatagoods")
	@ResponseBody
	public String downdatagoods(HttpServletRequest req,ModelMap map,String begindata,String enddata,HttpServletResponse response,String diyCode,String city)
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = null;
		Date date2 = null;
		if(null !=begindata && !begindata.equals(""))
		{
			try {
				date1 = sdf.parse(begindata);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(null !=enddata && !enddata.equals(""))
		{
			try {
				date2 = sdf.parse(enddata);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (date2 == null)
		{
			date2 = new Date();
		}
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		
		HSSFSheet sheet = workbook.createSheet("销售明细表");
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("门店名称");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("主单号");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("分单号");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("下单时间");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("订单状态");
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("会员电话");
		cell.setCellStyle(style);
		cell = row.createCell(6);
		cell.setCellValue("客户名称");
		cell.setCellStyle(style);
		cell = row.createCell(7);
		cell.setCellValue("产品编号");
		cell.setCellStyle(style);
		cell = row.createCell(8);
		cell.setCellValue("产品名称");
		cell.setCellStyle(style);
		cell = row.createCell(9);
		cell.setCellValue("数量");
		cell.setCellStyle(style);
		cell = row.createCell(10);
		cell.setCellValue("单价");
		cell.setCellStyle(style);
		cell = row.createCell(11);
		cell.setCellValue("总价");
		cell.setCellStyle(style);
		cell = row.createCell(12);
		cell.setCellValue("使用可提现金额");
		cell.setCellStyle(style);
		cell = row.createCell(13);
		cell.setCellValue("使用不可提现金额");
		cell.setCellStyle(style);
		cell = row.createCell(14);
		cell.setCellValue("备注");
		cell.setCellStyle(style);
		cell = row.createCell(15);
		cell.setCellValue("中转仓");
		cell.setCellStyle(style);
		cell = row.createCell(16);
		cell.setCellValue("配送人员");
		cell.setCellStyle(style);
		cell = row.createCell(17);
		cell.setCellValue("配送人员电话");
		cell.setCellStyle(style);
		cell = row.createCell(18);
		cell.setCellValue("导购姓名");
		cell.setCellStyle(style);
		cell = row.createCell(19);
		cell.setCellValue("商品类型");
		cell.setCellStyle(style);
		cell = row.createCell(20);
		cell.setCellValue("配送方式");
		cell.setCellStyle(style);
		cell = row.createCell(21);
		cell.setCellValue("收货人地址");
		cell.setCellStyle(style);
		
		/*List<TdOrder> orderList = null;
		if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
		{
			orderList = tdOrderService.findByDiySiteCodeAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(tdManager.getDiyCode(),date1,date2);
		}
        else
        {
        	if(tdManagerRole.getTitle().equalsIgnoreCase("超级管理组") && (StringUtils.isNotBlank(diyCode)||StringUtils.isNotBlank(city))){
        		if(StringUtils.isNotBlank(diyCode)){
        			orderList = tdOrderService.findByDiySiteCodeAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(diyCode,date1,date2);
        		}else{
        			orderList=tdOrderService.findByCityAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(city, date1, date2);
        		}
        	}else{
        		orderList = tdOrderService.findByBeginAndEndOrderByOrderTimeDesc(date1, date2);
        	}
        }
		
		if (orderList != null)
		{
			Integer i = 1;
			for (TdOrder tdOrder : orderList) {
				if(null != tdOrder.getOrderGoodsList())
				{
					for (TdOrderGoods og : tdOrder.getOrderGoodsList())
					{
						row = sheet.createRow(i);
						if (tdOrder.getDiySiteName() != null)
						{
							row.createCell(0).setCellValue(tdOrder.getDiySiteName());
						}
						//代付款订单没有主单号  分单号显示到主单号位置
						if(tdOrder.getStatusId() != null && tdOrder.getStatusId().equals(2L)){
							if (tdOrder.getOrderNumber() != null){
								row.createCell(1).setCellValue(tdOrder.getOrderNumber());
							}
						}else{
							if (tdOrder.getMainOrderNumber() != null)
							{
								row.createCell(1).setCellValue(tdOrder.getMainOrderNumber());
							}
							if (tdOrder.getOrderNumber() != null)
							{
								row.createCell(2).setCellValue(tdOrder.getOrderNumber());
							}
						}
						
						if (tdOrder.getOrderTime() != null)
						{
							row.createCell(3).setCellValue(tdOrder.getOrderTime().toString());
						}
						if (tdOrder.getStatusId() != null)
						{
							row.createCell(4).setCellValue(orderStatus(tdOrder.getStatusId()));
						}
						if (tdOrder.getUsername() != null)
						{
							row.createCell(5).setCellValue(tdOrder.getUsername());
						}
						if (tdOrder.getShippingName() != null)
						{
							row.createCell(6).setCellValue(tdOrder.getShippingName());
						}
						if (og.getSku() != null)
						{
							row.createCell(7).setCellValue(og.getSku());
						}
						if (og.getGoodsTitle() != null)
						{
							row.createCell(8).setCellValue(og.getGoodsTitle());
						}
						if (og.getQuantity() != null)
						{
							row.createCell(9).setCellValue(og.getQuantity());
						}
						if (og.getPrice() != null)
						{
							row.createCell(10).setCellValue(og.getPrice());
						}
						if(og.getQuantity() != null && og.getPrice() != null){
							row.createCell(11).setCellValue(og.getPrice()*og.getQuantity());
						}
						if (null != tdOrder.getCashBalanceUsed())
			        	{
			            	row.createCell(12).setCellValue(tdOrder.getCashBalanceUsed());
			    		}
			        	if (null != tdOrder.getUnCashBalanceUsed())
			        	{
			            	row.createCell(13).setCellValue(tdOrder.getUnCashBalanceUsed());
			    		}
						if (tdOrder.getRemark() != null)
						{
							row.createCell(14).setCellValue(tdOrder.getRemark());
						}
						
						List<TdDeliveryInfo> deliveryInfo = null;
			         	List<TdDeliveryInfoDetail> infoDetails = tdDeliveryInfoDetailService.findBySubOrderNumber(tdOrder.getOrderNumber());
			        	if (infoDetails != null && infoDetails.size() > 0) 
			        	{
			        	 	deliveryInfo = tdDeliveryInfoService.findDistinctTaskNoByTaskNo(infoDetails.get(0).getTaskNo());
						}
						TdUser user = null;
			        	if (null != deliveryInfo && deliveryInfo.size() > 0)
			        	{
			        		String driver = deliveryInfo.get(0).getDriver();
			        		if (driver != null)
			        		{
								user = tdUserService.findByOpUser(driver);
							}
			    		}
			        	if (deliveryInfo != null && deliveryInfo.size() > 0)
			        	{
			        		row.createCell(15).setCellValue(changeName(deliveryInfo.get(0).getWhNo()));
						}
			        	if (user != null)
						{
			        		row.createCell(16).setCellValue(user.getRealName());
						}
			        	if (null != user)
			        	{
			            	row.createCell(17).setCellValue(user.getUsername());
			    		}
			        	if(tdOrder.getSellerRealName() != null){
			        		row.createCell(18).setCellValue(tdOrder.getSellerRealName());
			        	}
			        	if(og.getGoodsId() != null){
			        		TdGoods goods= tdGoodsService.findOne(og.getGoodsId());
			        		if(null != goods && goods.getCategoryId() != null){
			        			TdProductCategory productCategory= tdProductCategoryService.findOne(goods.getCategoryId());
			        			if( null != productCategory && productCategory.getTitle()!=null){
			        				row.createCell(19).setCellValue(productCategory.getTitle());
			        			}
			        		}
			        	}
			        	if(tdOrder.getDeliverTypeTitle()!=null){
			        		row.createCell(20).setCellValue(tdOrder.getDeliverTypeTitle());
			        	}
			        	if(tdOrder.getShippingAddress()!=null && !"门店自提".equals(tdOrder.getDeliverTypeTitle())){
			        		row.createCell(21).setCellValue(tdOrder.getShippingAddress());
			        	}
						
						i++;
					}
				}
			}
		}*/
		
		
        try {//调用存储过程 报错
        	tdSalesDetailService.callInsertSalesDetail(getStartTime(), getEndTime());
    	} catch (Exception e) {
    		System.out.println(e);
    	}
        
		
		if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
		{
			diyCode= tdManager.getDiyCode();
			city=null;
		}
		List<TdSalesDetail> salesDetailList = tdSalesDetailService.searchSalesDetail(date1, date2, city, diyCode);
		
		if (salesDetailList != null)
		{
			Integer i = 1;
			for (TdSalesDetail salesDetail : salesDetailList) {
				
						row = sheet.createRow(i);
						if (salesDetail.getDiySiteName() != null)
						{
							row.createCell(0).setCellValue(salesDetail.getDiySiteName());
						}
						//代付款订单没有主单号  分单号显示到主单号位置
						if(salesDetail.getStatusId() != null && salesDetail.getStatusId().equals(2L)){
							if (salesDetail.getOrderNumber() != null){
								row.createCell(1).setCellValue(salesDetail.getOrderNumber());
							}
						}else{
							if (salesDetail.getMainOrderNumber() != null)
							{
								row.createCell(1).setCellValue(salesDetail.getMainOrderNumber());
							}
							if (salesDetail.getOrderNumber() != null)
							{
								row.createCell(2).setCellValue(salesDetail.getOrderNumber());
							}
						}
						
						if (salesDetail.getOrderTime() != null)
						{
							row.createCell(3).setCellValue(salesDetail.getOrderTime().toString());
						}
						if (salesDetail.getStatusId() != null)
						{
							row.createCell(4).setCellValue(orderStatus(salesDetail.getStatusId()));
						}
						if (salesDetail.getUsername() != null)
						{
							row.createCell(5).setCellValue(salesDetail.getUsername());
						}
						if (salesDetail.getShippingName() != null)
						{
							row.createCell(6).setCellValue(salesDetail.getShippingName());
						}
						if (salesDetail.getSku() != null)
						{
							row.createCell(7).setCellValue(salesDetail.getSku());
						}
						if (salesDetail.getGoodsTitle() != null)
						{
							row.createCell(8).setCellValue(salesDetail.getGoodsTitle());
						}
						if (salesDetail.getQuantity() != null)
						{
							row.createCell(9).setCellValue(salesDetail.getQuantity());
						}
						if (salesDetail.getPrice() != null)
						{
							row.createCell(10).setCellValue(salesDetail.getPrice());
						}
						if(salesDetail.getQuantity() != null && salesDetail.getPrice() != null){
							row.createCell(11).setCellValue(salesDetail.getPrice()*salesDetail.getQuantity());
						}
						if (null != salesDetail.getCashBalanceUsed())
			        	{
			            	row.createCell(12).setCellValue(salesDetail.getCashBalanceUsed());
			    		}
			        	if (null != salesDetail.getUnCashBalanceUsed())
			        	{
			            	row.createCell(13).setCellValue(salesDetail.getUnCashBalanceUsed());
			    		}
						if (salesDetail.getRemark() != null)
						{
							row.createCell(14).setCellValue(salesDetail.getRemark());
						}
						
						
			        	if (salesDetail.getWhNo() != null )
			        	{
			        		row.createCell(15).setCellValue(changeName(salesDetail.getWhNo()));
						}
			        	if (null != salesDetail.getDeliverRealName())
						{
			        		row.createCell(16).setCellValue(salesDetail.getDeliverRealName());
						}
			        	if (null != salesDetail.getDeliverUsername())
			        	{
			            	row.createCell(17).setCellValue(salesDetail.getDeliverUsername());
			    		}
			        	if(salesDetail.getSellerRealName() != null){
			        		row.createCell(18).setCellValue(salesDetail.getSellerRealName());
			        	}
			        	if(salesDetail.getTitle() != null){
			        		row.createCell(19).setCellValue(salesDetail.getTitle());
			        	}
			        	if(salesDetail.getDeliverTypeTitle()!=null){
			        		row.createCell(20).setCellValue(salesDetail.getDeliverTypeTitle());
			        	}
			        	if(salesDetail.getShippingAddress()!=null && !"门店自提".equals(salesDetail.getDeliverTypeTitle())){
			        		row.createCell(21).setCellValue(salesDetail.getShippingAddress());
			        	}
						
						i++;
					}
		}
		
		download(workbook, "1", response,"销售明细报表");
		return "";
	}
	
	/*
	 * 代收款报表
	 */
	@RequestMapping(value = "/downdata",method = RequestMethod.GET)
	@ResponseBody
	public String dowmData(HttpServletRequest req,ModelMap map,String begindata,String enddata,HttpServletResponse response,String diyCode,String city)
	{
		
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		
		TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		TdManagerRole tdManagerRole = null;
		if (null != tdManager && null != tdManager.getRoleId())
		{
			tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
		}
		if (tdManagerRole == null)
		{
			return "redirect:/Verwalter/login";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = null;
		Date date2 = null;
		if(null !=begindata && !begindata.equals(""))
		{
			try {
				date1 = sdf.parse(begindata);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(null !=enddata && !enddata.equals(""))
		{
			try {
				date2 = sdf.parse(enddata);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (date2 == null)
		{
			date2 = new Date();
		}
		
		// 第一步，创建一个webbook，对应一个Excel文件 
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("代收款报表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        //列宽
        sheet.setColumnWidth(0 , 8*256);
        sheet.setColumnWidth(1 , 13*256);
        sheet.setColumnWidth(2 , 25*256);
//      sheet.setColumnWidth(3 , 25*256);
        sheet.setColumnWidth(3 , 18*256);
        sheet.setColumnWidth(4 , 11*256);
        sheet.setColumnWidth(5 , 13*256);
        sheet.setColumnWidth(6 , 11*256);
        sheet.setColumnWidth(7 , 19*256);
        sheet.setColumnWidth(8 , 12*256);
        sheet.setColumnWidth(9 , 9*256);
        sheet.setColumnWidth(10 , 13*256);
        sheet.setColumnWidth(11 , 13*256);
        sheet.setColumnWidth(12 , 13*256);
        sheet.setColumnWidth(13 , 40*256);
        sheet.setColumnWidth(14 , 40*256);
        
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        style.setWrapText(true);
    	//门店、门店电话、单据、日期、预存款使用金额、代收款金额、实际代收款金额、欠款、配送人员、配送人电话、收货人、收货人电话、备注信息
        HSSFRow row = sheet.createRow((int) 0); 
        HSSFCell cell = row.createCell(0);  
        cell.setCellValue("门店名称");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue("门店电话");  
        cell.setCellStyle(style);  
        cell = row.createCell(2);  
        cell.setCellValue("主单号");  
        cell.setCellStyle(style);
//        cell = row.createCell(3);  
//        cell.setCellValue("分单号");  
//        cell.setCellStyle(style);
        cell = row.createCell(3);  
        cell.setCellValue("订单日期");
        cell.setCellStyle(style);
        cell = row.createCell(4);  
        cell.setCellValue("使用可提现金额");
        cell.setCellStyle(style);
        cell = row.createCell(5);  
        cell.setCellValue("使用不可提现金额");
        cell.setCellStyle(style);
        cell = row.createCell(6);  
        cell.setCellValue("代收款金额");
        cell.setCellStyle(style);
        cell = row.createCell(7);  
        cell.setCellValue("实际代收款金额");
        cell.setCellStyle(style);
        cell = row.createCell(8);  
        cell.setCellValue("欠款");
        cell.setCellStyle(style);
        cell = row.createCell(9);  
        cell.setCellValue("配送人员");
        cell.setCellStyle(style);
        cell = row.createCell(10);  
        cell.setCellValue("配送人电话");
        cell.setCellStyle(style);
        cell = row.createCell(11);  
        cell.setCellValue("收货人");
        cell.setCellStyle(style);
        cell = row.createCell(12);  
        cell.setCellValue("收货人电话");
        cell.setCellStyle(style);
        cell = row.createCell(13);  
        cell.setCellValue("收货人地址");
        cell.setCellStyle(style);
        cell = row.createCell(14);  
        cell.setCellValue("备注信息");
        cell.setCellStyle(style);
        cell = row.createCell(15);
        cell.setCellValue("现金券额度");
        cell.setCellStyle(style);
        cell = row.createCell(16);
        cell.setCellValue("订单状态");
        cell.setCellStyle(style);
        cell = row.createCell(17);
        cell.setCellValue("仓库名称");
        cell.setCellStyle(style);
        cell = row.createCell(18);
        cell.setCellValue("订单总金额");
        cell.setCellStyle(style);
        cell = row.createCell(19);
        cell.setCellValue("预约配送时间");
        cell.setCellStyle(style);
        cell = row.createCell(20);
        cell.setCellValue("实际配送时间");
        cell.setCellStyle(style);
        
        // 第五步，设置值  
        /*List<TdOrder> orders = null;
        if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
		{
        	orders = tdOrderService.searchMainOrderNumberByTimeAndDiySiteCode(tdManager.getDiyCode(),date1,date2);
		}
        else
        {
        	orders = tdOrderService.searchMainOrderNumberByTime(date1, date2);
        }
        Integer i = 0;
        for (TdOrder tdOrder : orders)
        {
        	row = sheet.createRow((int) i + 1);
        	if (null != tdOrder.getDiySiteName())
        	{
            	row.createCell(0).setCellValue(tdOrder.getDiySiteName());
    		}
        	if (null != tdOrder.getDiySitePhone())
        	{
            	row.createCell(1).setCellValue(tdOrder.getDiySitePhone());
    		}
        	if (null != tdOrder.getMainOrderNumber())
        	{
            	row.createCell(2).setCellValue(tdOrder.getMainOrderNumber());
    		}
//        	if (null != tdOrder.getOrderNumber())
//        	{
//            	row.createCell(3).setCellValue(tdOrder.getOrderNumber());
//    		}
        	if (null != tdOrder.getOrderTime())
        	{
        		Date orderTime = tdOrder.getOrderTime();
        		String orderTimeStr = orderTime.toString();
            	row.createCell(3).setCellValue(orderTimeStr);
    		}
        	if (null != tdOrder.getCashBalanceUsed())
        	{
            	row.createCell(4).setCellValue(tdOrder.getCashBalanceUsed());
    		}
        	if (null != tdOrder.getUnCashBalanceUsed())
        	{
            	row.createCell(5).setCellValue(tdOrder.getUnCashBalanceUsed());
    		}
        	List<TdOwnMoneyRecord> records = tdOwnMoneyRecordService.findByOrderNumberIgnoreCase(tdOrder.getOrderNumber());
        	
        	if (null != records && records.size() > 0)
        	{
            	row.createCell(6).setCellValue((records.get(0).getPayed() == null ? 0 : records.get(0).getPayed()) + (records.get(0).getOwned()== null ? 0 : records.get(0).getOwned()));
    		}else{
    			if(tdOrder.getTotalPrice() != null){
    				row.createCell(6).setCellValue(tdOrder.getTotalPrice());
    			}
    		}
        	if (null != records && records.size() > 0 && records.get(0).getPayed() != null)
        	{
            	row.createCell(7).setCellValue(records.get(0).getPayed());
    		}
        	if (null != records && records.size() > 0 && records.get(0).getOwned() != null)
        	{
            	row.createCell(8).setCellValue(records.get(0).getOwned());
    		}
        	
        	List<TdDeliveryInfo> deliveryInfo = null;
         	List<TdDeliveryInfoDetail> infoDetails = tdDeliveryInfoDetailService.findBySubOrderNumber(tdOrder.getOrderNumber());
        	if (infoDetails != null && infoDetails.size() > 0) 
        	{
        	 	deliveryInfo = tdDeliveryInfoService.findDistinctTaskNoByTaskNo(infoDetails.get(0).getTaskNo());
			}
         	TdUser user = null;
        	if (null != deliveryInfo && deliveryInfo.size() > 0)
        	{
        		String driver = deliveryInfo.get(0).getDriver();
        		if (driver != null)
        		{
					user = tdUserService.findByOpUser(driver);
				}
            	
    		}
        	if (deliveryInfo != null && deliveryInfo.size() > 0)
        	{
        		row.createCell(17).setCellValue(changeName(deliveryInfo.get(0).getWhNo()));
			}
        	if (user != null)
			{
        		row.createCell(9).setCellValue(user.getRealName());
			}
        	if (null != user)
        	{
            	row.createCell(10).setCellValue(user.getUsername());
    		}
        	if (null != tdOrder.getShippingName())
        	{
            	row.createCell(11).setCellValue(tdOrder.getShippingName());
    		}
        	if (null != tdOrder.getShippingPhone())
        	{
            	row.createCell(12).setCellValue(tdOrder.getShippingPhone());
    		}
        	if (null != tdOrder.getShippingAddress())
        	{
            	row.createCell(13).setCellValue(tdOrder.getShippingAddress());
    		}
        	if (null != tdOrder.getRemark())
        	{
            	row.createCell(14).setCellValue(tdOrder.getRemark());
    		}
        	if (null != tdOrder.getCashCoupon())
        	{
				row.createCell(15).setCellValue(tdOrder.getCashCoupon());
			}
        	if (null != tdOrder.getStatusId())
        	{
        		String statusStr = orderStatus(tdOrder.getStatusId());
				row.createCell(16).setCellValue(statusStr);
			}
        	if (null != tdOrder.getTotalPrice())
        	{
				row.createCell(18).setCellValue(tdOrder.getTotalPrice());
			}
        	if (null != tdOrder.getDeliveryDate()) 
        	{
        		String dayTime = tdOrder.getDeliveryDate();
    			dayTime = dayTime + " " + tdOrder.getDeliveryDetailId() + ":30";
				row.createCell(19).setCellValue(dayTime);
			}
        	if (null != tdOrder.getDeliveryTime()) 
        	{
				row.createCell(20).setCellValue(tdOrder.getDeliveryTime().toString());
			}
        	
        	i++;
		}*/
        
        try {//调用存储过程 报错
            tdAgencyFundService.callInsertAgencyFund(getStartTime(), getEndTime());
    	} catch (Exception e) {
    		System.out.println(e);
    	}
        
        if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
		{
        	diyCode=tdManager.getDiyCode();
        	city=null;
		}
        List<TdAgencyFund> agencyFundList = tdAgencyFundService.searchAgencyFund(date1, date2, city, diyCode);
        
        Integer i = 0;
        for (TdAgencyFund agencyFund : agencyFundList)
        {
        	row = sheet.createRow((int) i + 1);
        	if (null != agencyFund.getDiySiteName())
        	{
            	row.createCell(0).setCellValue(agencyFund.getDiySiteName());
    		}
        	if (null != agencyFund.getDiySitePhone())
        	{
            	row.createCell(1).setCellValue(agencyFund.getDiySitePhone());
    		}
        	if (null != agencyFund.getMainOrderNumber())
        	{
            	row.createCell(2).setCellValue(agencyFund.getMainOrderNumber());
    		}
//        	if (null != tdOrder.getOrderNumber())
//        	{
//            	row.createCell(3).setCellValue(tdOrder.getOrderNumber());
//    		}
        	if (null != agencyFund.getOrderTime())
        	{
        		Date orderTime = agencyFund.getOrderTime();
        		String orderTimeStr = orderTime.toString();
            	row.createCell(3).setCellValue(orderTimeStr);
    		}
        	if (null != agencyFund.getCashBalanceUsed())
        	{
            	row.createCell(4).setCellValue(agencyFund.getCashBalanceUsed());
    		}
        	if (null != agencyFund.getUnCashBalanceUsed())
        	{
            	row.createCell(5).setCellValue(agencyFund.getUnCashBalanceUsed());
    		}
        	
        	if (null != agencyFund.getPayPrice())
        	{
    			row.createCell(6).setCellValue(agencyFund.getPayPrice());
    		}
        	if (null != agencyFund.getPayed())
        	{
            	row.createCell(7).setCellValue(agencyFund.getPayed());
    		}
        	if (null != agencyFund.getOwned())
        	{
            	row.createCell(8).setCellValue(agencyFund.getOwned());
    		}
        	
        	if (null != agencyFund.getWhNo())
        	{
        		row.createCell(17).setCellValue(changeName(agencyFund.getWhNo()));
			}
        	if (null != agencyFund.getRealName())
			{
        		row.createCell(9).setCellValue(agencyFund.getRealName());
			}
        	if (null != agencyFund.getUsername())
        	{
            	row.createCell(10).setCellValue(agencyFund.getUsername());
    		}
        	if (null != agencyFund.getShippingName())
        	{
            	row.createCell(11).setCellValue(agencyFund.getShippingName());
    		}
        	if (null != agencyFund.getShippingPhone())
        	{
            	row.createCell(12).setCellValue(agencyFund.getShippingPhone());
    		}
        	if (null != agencyFund.getShippingAddress())
        	{
            	row.createCell(13).setCellValue(agencyFund.getShippingAddress());
    		}
        	if (null != agencyFund.getRemark())
        	{
            	row.createCell(14).setCellValue(agencyFund.getRemark());
    		}
        	if (null != agencyFund.getCashCoupon())
        	{
				row.createCell(15).setCellValue(agencyFund.getCashCoupon());
			}
        	if (null != agencyFund.getStatusId())
        	{
        		String statusStr = orderStatus(agencyFund.getStatusId());
				row.createCell(16).setCellValue(statusStr);
			}
        	if (null != agencyFund.getTotalPrice())
        	{
				row.createCell(18).setCellValue(agencyFund.getTotalPrice());
			}
        	if (null != agencyFund.getDeliveryDate()) 
        	{
        		String dayTime = agencyFund.getDeliveryDate();
    			dayTime = dayTime + " " + agencyFund.getDeliveryDetailId() + ":30";
				row.createCell(19).setCellValue(dayTime);
			}
        	if (null != agencyFund.getDeliveryTime()) 
        	{
				row.createCell(20).setCellValue(agencyFund.getDeliveryTime().toString());
			}
        	
        	i++;
		}
        
        String exportAllUrl = SiteMagConstant.backupPath;
        download(wb, exportAllUrl, response,"代收款报表");
        return "";
	}
	private String changeName(String name)
	{
//		郑州公司	11	总仓
//		天荣中转仓	1101	分仓
//		五龙口中转仓	1102	分仓
//		东大中转仓	1103	分仓
//		百姓中转仓	1104	分仓
//		主仓库	1105	分仓
		
		List<TdWareHouse> wareHouses = tdWareHouseService.findBywhNumberOrderBySortIdAsc(name);
		if (wareHouses != null && wareHouses.size() > 0)
		{
			return wareHouses.get(0).getWhName();
		}
		else 
		{
			return "未知编号：" + name;
		}
		
//		if (name == null || name.equalsIgnoreCase(""))
//		{
//			return "未知";
//		}
//		if (name.equalsIgnoreCase("11"))
//		{
//			return "郑州公司";
//		}
//		else if (name.equalsIgnoreCase("1101"))
//		{
//			return "天荣中转仓";
//		}
//		else if (name.equalsIgnoreCase("1102"))
//		{
//			return "五龙口中转仓";
//		}
//		else if (name.equalsIgnoreCase("1103"))
//		{
//			return "东大中转仓";
//		}
//		else if (name.equalsIgnoreCase("1104"))
//		{
//			return "百姓中转仓";
//		}
//		else if (name.equalsIgnoreCase("1105"))
//		{
//			return "主仓库";
//		}
//		else
//		{
//			return "未知编号：" + name;
//		}
	}
	private String orderStatus(Long status)
	{
		// 订单状态 1:待审核 2:待付款 3:待出库 4:待签收 5: 待评价 6: 已完成 7: 已取消 8:用户删除 9:退货中 10：退货确认
		// 11：退货取消 12 : 退货完成
		Integer integerStatus = status.intValue();
		switch (integerStatus) {
			case 1:
				return "待审核";
			case 2:
				return "待付款";
			case 3:
				return "待出库";
			case 4:
				return "待签收";
			case 5:
				return "待评价";
			case 6:
				return "已完成";
			case 7:
				return "已取消";
			case 8:
				return "用户删除";
			case 9:
				return "退货中";
			case 10:
				return "退货确认";
			case 11:
				return "退货取消";
			case 12:
				return "退货完成";
				
			default:
				return "未知";
		}
	}
	 /**
	 * @author lc
	 * @注释：下载
	 */
	public Boolean download(HSSFWorkbook wb, String exportUrl, HttpServletResponse resp,String fileName){
		String filename="table";
		try {
			filename = new String(fileName.getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("下载文件名格式转换错误！");
		}
		try
		{
			OutputStream os;
			try {
				os = resp.getOutputStream();
				try {
					
					resp.reset();
					resp.setHeader("Content-Disposition", "attachment; filename=" + filename +".xls");
					resp.setContentType("application/octet-stream; charset=utf-8");
					wb.write(os);
					os.flush();
				}
				finally {
					if (os != null) {
						os.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch (Exception e)  
		{  
			e.printStackTrace();  
		} 
		return true;	
	}

	// 城市选择
	@RequestMapping(value = "/city", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> citySelect() {
		Map<String, Object> map = new ModelMap();
		map.put("code", 0);
		map.put("city", tdCityService.findAll());
		map.put("code", 1);
		return map;
	}

	// 行政区划选择
	@RequestMapping(value = "/district", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> districtSelect(Long cityId) {
		Map<String, Object> map = new ModelMap();
		map.put("code", 0);
		map.put("district", tdDistrictService.findByCityIdOrderBySortIdAsc(cityId));
		map.put("code", 1);
		return map;
	}

	// 行政街道选择
	@RequestMapping(value = "/subdistrict", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> subdistrictSelect(Long districtId) {
		Map<String, Object> map = new ModelMap();
		map.put("code", 0);
		map.put("subdistrict", tdSubdistrictService.findByDistrictIdOrderBySortIdAsc(districtId));
		map.put("code", 1);
		return map;
	}

	// 订单设置
	@RequestMapping(value = "/setting/{type}/list")
	public String setting(@PathVariable String type, Integer page, Integer size, String keywords, String __EVENTTARGET,
			String __EVENTARGUMENT, String __VIEWSTATE, Long[] listId, Integer[] listChkId, Double[] listSortId,
			ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != __EVENTTARGET) {
			if (__EVENTTARGET.equalsIgnoreCase("btnDelete")) {
				btnDelete(type, listId, listChkId);

				if (type.equalsIgnoreCase("pay")) {
					tdManagerLogService.addLog("delete", "删除支付方式", req);
				} else if (type.equalsIgnoreCase("delivery")) {
					tdManagerLogService.addLog("delete", "删除配送方式", req);
				} else if (type.equalsIgnoreCase("diysite")) {
					tdManagerLogService.addLog("delete", "删除门店", req);
				} else if (type.equalsIgnoreCase("codDistrict")) {
					tdManagerLogService.addLog("delete", "删除货到付款地区", req);
				}
			} else if (__EVENTTARGET.equalsIgnoreCase("btnSave")) {
				btnSave(type, listId, listSortId);

				if (type.equalsIgnoreCase("pay")) {
					tdManagerLogService.addLog("edit", "修改支付方式", req);
				} else if (type.equalsIgnoreCase("delivery")) {
					tdManagerLogService.addLog("edit", "修改配送方式", req);
				} else if (type.equalsIgnoreCase("diysite")) {
					tdManagerLogService.addLog("edit", "修改门店", req);
				} else if (type.equalsIgnoreCase("codDistrict")) {
					tdManagerLogService.addLog("edit", "修改货到付款地区", req);
				}
			} else if (__EVENTTARGET.equalsIgnoreCase("btnPage")) {
				if (null != __EVENTARGUMENT) {
					page = Integer.parseInt(__EVENTARGUMENT);
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
			if (type.equalsIgnoreCase("pay")) // 支付方式
			{
				if (null == keywords) {
					map.addAttribute("pay_type_page", tdPayTypeService.findAllOrderBySortIdAsc(page, size));
				} 
				else
				{
					map.addAttribute("pay_type_page", tdPayTypeService.searchAllOrderBySortIdAsc(keywords, page, size));
				}

				return "/site_mag/pay_type_list";
			} else if (type.equalsIgnoreCase("delivery")) // 配送方式
			{
				if (null == keywords) {
					map.addAttribute("delivery_type_page", tdDeliveryTypeService.findAllOrderBySortIdAsc(page, size));
				} else {
					map.addAttribute("delivery_type_page",
							tdDeliveryTypeService.searchAllOrderBySortIdAsc(keywords, page, size));
				}

				return "/site_mag/delivery_type_list";
			} else if (type.equalsIgnoreCase("diysite")) // 门店
			{
				if (null == keywords) {
					map.addAttribute("diy_site_page", tdDiySiteService.findAllOrderBySortIdAsc(page, size));
				} else {
					map.addAttribute("diy_site_page", tdDiySiteService.searchAllOrderBySortIdAsc(keywords, page, size));
				}

				return "/site_mag/diy_site_list";
			}
			// else if (type.equalsIgnoreCase("codDistrict")) // 货到付款地区
			// {
			// if (null == keywords)
			// {
			// map.addAttribute("cod_district_page",
			// tdShippingAddressService.findByIsCod(page, size));
			// }
			// else
			// {
			// map.addAttribute("cod_district_page",
			// tdShippingAddressService.searchBykeywords(keywords, page, size));
			// }
			//
			// return "/site_mag/cod_district_list";
			// }
		}

		return "/site_mag/pay_type_list";
	}
	
	
	// 欠款审核
	@RequestMapping(value = "/own/list")
	public String ownList(HttpServletRequest req, Integer page, Long statusId, Integer size,ModelMap map, String __EVENTTARGET,
			String __EVENTARGUMENT, String __VIEWSTATE,Long[] listChkId,Long payLeft)
	{
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) 
		{
			return "redirect:/Verwalter/login";
		}
		TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		TdManagerRole tdManagerRole = null;
		String diyCode = null;
		if (null != tdManager && null != tdManager.getRoleId())
		{
			tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
			if (tdManagerRole == null)
			{
				return "redirect:/Verwalter/login";
			}
			if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
			{
				diyCode = tdManagerRole.getTitle();
			}
		}
		
		if (null == page || page < 0) 
		{
			page = 0;
		}

		if (null == size || size <= 0) 
		{
			size = SiteMagConstant.pageSize;
		}
		
		if (tdManagerRole.getTitle().equalsIgnoreCase("门店"))
		{
			map.addAttribute("own_page",tdOwnMoneyRecordService.findByDiyCodeOrderByIdDesc(tdManager.getDiyCode(), page, size));
		}
		else
		{
			map.addAttribute("own_page",tdOwnMoneyRecordService.findAllOrderBySortIdAsc(page, size));
		}
		if (null != __EVENTTARGET)
		{
			if (__EVENTTARGET.equalsIgnoreCase("btnPage")) 
			{
				if (null != __EVENTARGUMENT)
				{
					page = Integer.parseInt(__EVENTARGUMENT);
				}
			}
			else if (__EVENTTARGET.equalsIgnoreCase("statusId")) 
			{
				if (null != statusId)
				{
					if (statusId == 0)
					{
						map.remove("own_page");
						map.addAttribute("own_page", tdOwnMoneyRecordService.findByDiyCodeAndIsEnableOrderByIdDesc(diyCode, false, page, size));
					}
					else if (statusId == 1)
					{
						map.remove("own_page");
						map.addAttribute("own_page", tdOwnMoneyRecordService.findByDiyCodeAndIsEnableOrderByIdDesc(diyCode, true, page, size));
					}
				}
			}
			else if (__EVENTTARGET.equalsIgnoreCase("btnEnable")) 
			{
				if (null != listChkId)
				{
					btnEnale(listChkId);
				}
			}
			else if (__EVENTTARGET.equalsIgnoreCase("btnNotEnable")) 
			{
				if (null != listChkId)
				{
					btnNotEnale(listChkId);
				}
			}
			else if (__EVENTTARGET.equalsIgnoreCase("payLeft")) 
			{
				if (null != payLeft)
				{
					if (payLeft == 0)
					{
						map.remove("own_page");
						map.addAttribute("own_page", tdOwnMoneyRecordService.findByDiyCodeAndIsPayedOrderByIdDesc(diyCode, false, page, size));
					}
					else if (payLeft == 1)
					{
						map.remove("own_page");
						map.addAttribute("own_page", tdOwnMoneyRecordService.findByDiyCodeAndIsPayedOrderByIdDesc(diyCode, true, page, size));
					}
				}
			}
			
		}
		map.addAttribute("page", page);
		map.addAttribute("size", size);
		map.addAttribute("__EVENTTARGET", __EVENTTARGET);
		map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("statusId",statusId);
		map.addAttribute("payLeft",payLeft);
		return "/site_mag/own_list";
	}

	// 订单设置编辑
	@RequestMapping(value = "/setting/{type}/edit")
	public String edit(@PathVariable String type, Long id, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		if (null != type) {
			if (type.equalsIgnoreCase("pay")) // 支付方式
			{
				if (null != id) {
					map.addAttribute("pay_type", tdPayTypeService.findOne(id));
				}

				return "/site_mag/pay_type_edit";
			} else if (type.equalsIgnoreCase("delivery")) // 配送方式
			{
				if (null != id) {
					map.addAttribute("delivery_type", tdDeliveryTypeService.findOne(id));
				}

				return "/site_mag/delivery_type_edit";
			} else if (type.equalsIgnoreCase("diysite")) // 门店
			{
				map.addAttribute("price_list", tdPriceListService.findAll());
				if (null != id) {
					TdDiySite tdDiySite = tdDiySiteService.findOne(id);
					List<TdPriceList> price_list = tdPriceListService.findBySobId(tdDiySite.getRegionId());
					if (null != price_list) {
						for (int i = 0; i < price_list.size(); i++) {
							TdPriceList priceList = price_list.get(i);
							Date nowTime = new Date();
							if (priceList.getActiveFlag().equalsIgnoreCase("N")) {
								price_list.remove(priceList);
							}
							if (priceList.getStartDateActive() != null) {
								if (priceList.getStartDateActive().getTime() > nowTime.getTime()) {
									price_list.remove(priceList);
								}
							}
							if (priceList.getEndDateActive() != null) {
								if (priceList.getEndDateActive().getTime() < nowTime.getTime()) {
									price_list.remove(priceList);
								}
							}
						}
					}
					map.addAttribute("choose_price_list", tdPriceListService.findBySobId(tdDiySite.getRegionId()));

					map.addAttribute("diy_site", tdDiySiteService.findOne(id));
				}

				return "/site_mag/diy_site_edit";
			} else if (type.equalsIgnoreCase("codDistrict")) // 货到付款地区
			{
				if (null != id) {
					map.addAttribute("cod_district", tdShippingAddressService.findOne(id));
				}

				return "/site_mag/cod_district_edit";
			}
		}

		return "/site_mag/pay_type_edit";
	}

	@RequestMapping(value = "/edit")
	public String orderEdit(Long id, Long statusId, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("statusId", statusId);
		if (null != id) {
			TdOrder order=tdOrderService.findOne(id);
			map.addAttribute("order", tdOrderService.findOne(id));
			//仓库
			if(null != order){
				List<TdDeliveryInfo> deliveryList=tdDeliveryInfoService.findByOrderNumberOrderByBeginDtDesc(order.getMainOrderNumber());
				if(null!=deliveryList && deliveryList.size()>0){
					List<TdWareHouse> wareHouseList= tdWareHouseService.findBywhNumberOrderBySortIdAsc(deliveryList.get(0).getWhNo());
					if(null != wareHouseList && wareHouseList.size()>0){
						map.addAttribute("tdWareHouse", wareHouseList.get(0));
					}
				}
			}
		}
		return "/site_mag/order_edit";
	}

	@RequestMapping(value = "/save")
	public String orderEdit(TdOrder tdOrder, Long statusId, String __VIEWSTATE, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username)
		{
			return "redirect:/Verwalter/login";
		}

		map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		map.addAttribute("statusId", statusId);

		tdOrderService.save(tdOrder);

		tdManagerLogService.addLog("edit", "修改订单", req);

		return "redirect:/Verwalter/order/list/" + statusId;
	}

	// 订单列表
	@RequestMapping(value = "/list/{statusId}")
	public String goodsListDialog(String keywords, @PathVariable Long statusId, Integer page, Integer size,
			String __EVENTTARGET, String __EVENTARGUMENT, String __VIEWSTATE, Long[] listId, Integer[] listChkId,
			ModelMap map, HttpServletRequest req,String orderStartTime,String orderEndTime,String realName,String sellerRealName,String shippingAddress,String shippingPhone,
			String deliveryTime,String userPhone,Long orderStatusId,String shippingName,String sendTime,String diyCode,String city) {
		String username = (String) req.getSession().getAttribute("manager");

		if (null == username)
		{
			return "redirect:/Verwalter/login";
		}

		TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		TdManagerRole tdManagerRole = null;
		if (null != tdManager && null != tdManager.getRoleId())
		{
			tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
		}
		if (tdManagerRole == null)
		{
			return "redirect:/Verwalter/login";
		}
		
		if (null == page || page < 0) {
			page = 0;
		}

		if (null == size || size <= 0) {
			size = SiteMagConstant.pageSize;
		}
		
		if (null != __EVENTTARGET) {
			if (__EVENTTARGET.equalsIgnoreCase("btnCancel"))
			{
				btnCancel(listId, listChkId);
				tdManagerLogService.addLog("cancel", "取消订单", req);
			} 
			else if (__EVENTTARGET.equalsIgnoreCase("btnConfirm"))
			{
				btnConfirm(listId, listChkId);
				tdManagerLogService.addLog("confirm", "确认订单", req);
			}
			else if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
			{
				btnDelete(listId, listChkId);
				tdManagerLogService.addLog("delete", "删除订单", req);
			}
			else if (__EVENTTARGET.equalsIgnoreCase("btnPage")) 
			{
				if (null != __EVENTARGUMENT) 
				{
					page = Integer.parseInt(__EVENTARGUMENT);
				}
			}else if(__EVENTTARGET.equals("btnSearch")){
				page=0;
			}
		}

//		if (tdManagerRole.getTitle().equalsIgnoreCase("门店"))
//		{
//			if (null != statusId) 
//			{
//				if (null != keywords && !keywords.equalsIgnoreCase("")) 
//				{
//					map.addAttribute("order_page",tdOrderService.findByDiySiteCodeAndOrderNumberContainingOrDiySiteCodeAndUsernameContainingOrderByIdDesc(tdManager.getDiyCode(), keywords, keywords, 0, 0));
//				}
//				else
//				{
//					if (statusId.equals(0L)) // 全部订单
//					{
//						map.addAttribute("order_page", tdOrderService.findByDiyCode(tdManager.getDiyCode(), page, size));
//					}
//					else
//					{
//						map.addAttribute("order_page", tdOrderService.findByDiyCodeAndStatusIdOrderByIdDesc(tdManager.getDiyCode(), statusId, page, size));
//					}
//				}
//			}
//		}
//		else 
//		{
			if (null != statusId)
			{
				//判断用户是否输入了查询条件
//				Boolean searchCondition= judgeSearchCondition(keywords,orderStartTime,orderEndTime, realName, sellerRealName, shippingAddress, shippingPhone,
//						 deliveryTime, userPhone, shippingName, sendTime);
//				if (searchCondition) 
//				{
				String diySiteCode="";
				if (tdManagerRole.getTitle().equalsIgnoreCase("门店")){
					diySiteCode=tdManager.getDiyCode();
				}else if(tdManagerRole.getIsSys()){
					diySiteCode=diyCode;
				}
				String userName="";
				Boolean isNotFindUser=false;
				if(StringUtils.isNotBlank(realName)){ //根据会员真实姓名查询用户名
					TdUser user= tdUserService.findByRealName(realName);
					if(null != user){
						userName=user.getUsername();
					}else{
						isNotFindUser=true; 
					}
				}
				if(!isNotFindUser){
						map.addAttribute("order_page", tdOrderService.findAll(keywords,orderStartTime,orderEndTime, userName, sellerRealName, shippingAddress, shippingPhone,
					 deliveryTime, userPhone, shippingName, sendTime,statusId,diySiteCode,city, size, page));
				}
//				}
//				else
//				{
//					if (statusId.equals(0L)) // 全部订单
//					{
//						map.addAttribute("order_page", tdOrderService.findAllOrderByIdDesc(page, size));
//					} 
//					else 
//					{
//						map.addAttribute("order_page", tdOrderService.findByStatusIdOrderByIdDesc(statusId, page, size));
//					}
//				}
			}
//		}
		
		@SuppressWarnings("unchecked")
		Page<TdOrder> orders1 = (Page<TdOrder>)map.get("order_page");
		if (orders1 != null)
		{
			List<TdOrder> orderlist = orders1.getContent();
			Map<String,String> nameMap = getUserRealNameFormTdOder(orderlist);
			if (nameMap != null)
			{
				map.addAttribute("name_map",nameMap);
			}
		}
		//城市和门店信息
		if (tdManagerRole.getIsSys()){
			map.addAttribute("diySiteList",tdDiySiteService.findAll());
			map.addAttribute("cityList", tdCityService.findAll());
		}
		// 参数注回
		map.addAttribute("orderNumber", keywords);
		map.addAttribute("orderStartTime", orderStartTime);
		map.addAttribute("orderEndTime", orderEndTime);
		map.addAttribute("realName", realName);
		map.addAttribute("sellerRealName", sellerRealName);
		map.addAttribute("shippingAddress", shippingAddress);
		map.addAttribute("shippingPhone", shippingPhone);
		map.addAttribute("deliveryTime", deliveryTime);
		map.addAttribute("userPhone", userPhone);
		map.addAttribute("shippingName", shippingName);
		map.addAttribute("sendTime", sendTime);
		map.addAttribute("statusId", statusId);
		map.addAttribute("diyCode", diyCode);
		map.addAttribute("cityname", city);
		
		map.addAttribute("keywords", keywords);
		map.addAttribute("statusId", statusId);
		map.addAttribute("__EVENTTARGET", __EVENTTARGET);
		map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
		map.addAttribute("__VIEWSTATE", __VIEWSTATE);

		return "/site_mag/order_list";
	}

	@RequestMapping(value = "/setting/pay/save", method = RequestMethod.POST)
	public String save(TdPayType tdPayType, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null == tdPayType.getId()) {
			tdManagerLogService.addLog("add", "新增支付方式", req);
		} else {
			tdManagerLogService.addLog("edit", "修改支付方式", req);
		}
		tdPayTypeService.save(tdPayType);

		return "redirect:/Verwalter/order/setting/pay/list";
	}

	@RequestMapping(value = "/setting/codDistrict/save", method = RequestMethod.POST)
	public String codDistrictsave(String province, String city, String disctrict, Long codDistrictId, ModelMap map,
			HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null == codDistrictId) {
			tdManagerLogService.addLog("add", "新增货到付款地区", req);
			TdShippingAddress tdShippingAddress = new TdShippingAddress();
			if (null != province) {
				tdShippingAddress.setProvince(province);
			}
			if (null != city) {
				tdShippingAddress.setCity(city);
			}
			if (null != disctrict) {
				tdShippingAddress.setDisctrict(disctrict);
			}
			// tdShippingAddress.setIsCod(true);
			tdShippingAddressService.save(tdShippingAddress);
		} else {
			tdManagerLogService.addLog("edit", "修改货到付款地区", req);

			TdShippingAddress tdShippingAddress = tdShippingAddressService.findOne(codDistrictId);
			if (null != province) {
				tdShippingAddress.setProvince(province);
			}
			if (null != city) {
				tdShippingAddress.setCity(city);
			}
			if (null != disctrict) {
				tdShippingAddress.setDisctrict(disctrict);
			}

			tdShippingAddressService.save(tdShippingAddress);
		}

		return "redirect:/Verwalter/order/setting/codDistrict/list";
	}

	@RequestMapping(value = "/setting/delivery/save", method = RequestMethod.POST)
	public String save(TdDeliveryType tdDeliveryType, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null == tdDeliveryType.getId()) {
			tdManagerLogService.addLog("add", "新增配送方式", req);
		} else {
			tdManagerLogService.addLog("edit", "修改配送方式", req);
		}

		tdDeliveryTypeService.save(tdDeliveryType);

		return "redirect:/Verwalter/order/setting/delivery/list";
	}

	@RequestMapping(value = "/setting/diysite/save", method = RequestMethod.POST)
	public String save(TdDiySite tdDiySite, String[] hid_photo_name_show360, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

//		if (tdDiySite.getCityId() != null)
//		{
//			TdCity tdCity = tdCityService.findOne(tdDiySite.getCityId());
//			tdDiySite.setRegionId(tdCity.getSobIdCity());
//		}
		
		if (null == tdDiySite.getId()) {
			tdManagerLogService.addLog("add", "新增门店", req);
		} else {
			tdManagerLogService.addLog("edit", "修改门店", req);
		}

		tdDiySiteService.save(tdDiySite);

		return "redirect:/Verwalter/order/setting/diysite/list";
	}

	@RequestMapping(value = "/dialog/contact")
	public String addressDialog(ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		return "/site_mag/dialog_contact";
	}

	@RequestMapping(value = "/dialog/delivery")
	public String sendDialog(String orderNumber, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != orderNumber && !orderNumber.isEmpty()) {
			map.addAttribute("order", tdOrderService.findByOrderNumber(orderNumber));
		}

		map.addAttribute("delivery_type_list", tdDeliveryTypeService.findByIsEnableTrue());

		return "/site_mag/dialog_delivery";
	}

	@RequestMapping(value = "/dialog/print")
	public String printDialog(String orderNumber, ModelMap map, HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}

		if (null != orderNumber && !orderNumber.isEmpty()) {
			TdOrder order = tdOrderService.findByOrderNumber(orderNumber);
			map.addAttribute("order", order);
			map.addAttribute("now", new Date());
			map.addAttribute("manager", req.getSession().getAttribute("manager"));

			if (null != order) {
				// map.addAttribute("user",
				// tdUserService.findByUsernameAndIsEnabled(order.getUsername()));
			}
		}

		return "/site_mag/dialog_order_print";
	}

	@RequestMapping(value = "/param/edit", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> paramEdit(String orderNumber, String type, String data, String name, String address,
			String postal, String mobile, String expressNumber, Long deliverTypeId, ModelMap map,
			HttpServletRequest req) {

		Map<String, Object> res = new HashMap<String, Object>();

		res.put("code", 1);

		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			res.put("message", "请重新登录");
			return res;
		}

		if (null != orderNumber && !orderNumber.isEmpty() && null != type && !type.isEmpty()) {
			TdOrder order = tdOrderService.findByOrderNumber(orderNumber);

			// 修改备注
			if (type.equalsIgnoreCase("editMark")) {
				order.setRemarkInfo(data);
			}
			// 修改商品总金额
			else if (type.equalsIgnoreCase("editTotalGoodsPrice")) {
				double goodsPrice = Double.parseDouble(data);
				order.setTotalGoodsPrice(goodsPrice);
				order.setTotalPrice(goodsPrice + order.getPayTypeFee() + order.getDeliverFee());
			}
			// 修改配送费用
			else if (type.equalsIgnoreCase("editDeliveryPrice")) {
				double deliveryPrice = Double.parseDouble(data);
				order.setDeliverFee(deliveryPrice);
				order.setTotalPrice(deliveryPrice + order.getPayTypeFee() + order.getTotalGoodsPrice());
			}
			// 修改支付手续费
			else if (type.equalsIgnoreCase("editPayPrice")) {
				double payPrice = Double.parseDouble(data);
				order.setPayTypeFee(payPrice);
				order.setTotalPrice(payPrice + order.getTotalGoodsPrice() + order.getDeliverFee());
			}
			// 修改联系方式
			else if (type.equalsIgnoreCase("editContact")) {
				order.setShippingName(name);
				order.setShippingAddress(address);
				order.setShippingPhone(mobile);
				order.setPostalCode(postal);
			}
			// 确认订单
			else if (type.equalsIgnoreCase("orderConfirm")) {
				if (order.getStatusId().equals(1L)) {
					order.setStatusId(2L);
					order.setCheckTime(new Date());
				}
			}
			// 确认付款
			else if (type.equalsIgnoreCase("orderPay")) {
				if (order.getStatusId().equals(2L)) {
					order.setStatusId(3L);
					order.setPayTime(new Date());

				}
			}
			// 货到付款确认付款
			else if (type.equalsIgnoreCase("orderPayOffline")) {
				if (order.getStatusId().equals(2L) && !order.getIsOnlinePay()) {
					order.setStatusId(5L);
					order.setPayTime(new Date());
				}
			}
			// 确认发货
			else if (type.equalsIgnoreCase("orderDelivery")) {
				if (order.getStatusId().equals(3L)) {
					order.setStatusId(4L);
					order.setSendTime(new Date());
				}
			}
			// 确认收货
			else if (type.equalsIgnoreCase("orderReceive")) {
				if (order.getStatusId().equals(4L)) {
					order.setStatusId(5L);
					order.setReceiveTime(new Date());
				}
			}
			// 确认完成
			else if (type.equalsIgnoreCase("orderFinish")) {
				if (order.getStatusId().equals(5L)) {
					order.setStatusId(6L);
					order.setFinishTime(new Date());

					// tdUserService.addTotalSpend(order.getUsername(),
					// order.getTotalPrice());
				}
			}
			// 确认取消
			else if (type.equalsIgnoreCase("orderCancel")) {
				if (order.getStatusId().equals(1L) || order.getStatusId().equals(2L) || order.getStatusId().equals(3L)) // zhangji
				{
					if (StringUtils.isNotBlank(order.getRemarkInfo()))
					{
						order.setRemarkInfo(order.getRemarkInfo() + "管理员取消订单：" + username);
					}
					else
					{
						order.setRemarkInfo("管理员取消订单：" + username);
					}
					if (null != order && order.getStatusId().equals(3L)) 
					{
						TdReturnNote returnNote = tdCommonService.MakeReturnNote(order,1L,username);
						tdCommonService.sendBackMsgToWMS(returnNote);
					}
					order.setStatusId(7L);
					order.setCancelTime(new Date());
				}
			}
			tdOrderService.save(order);
			tdManagerLogService.addLog("edit", "修改订单", req);

			res.put("code", 0);
			res.put("message", "修改成功!");
			return res;
		}

		res.put("message", "参数错误!");
		return res;
	}

	/*---------------------------------------导入表格 begin -------------------------------*/

	/**************
	 * -----------------------------导入表格 end-----------------------------------
	 */

	@ModelAttribute
	public void getModel(@RequestParam(value = "payTypeId", required = false) Long payTypeId,
			@RequestParam(value = "deliveryTypeId", required = false) Long deliveryTypeId,
			@RequestParam(value = "diySiteId", required = false) Long diySiteId, Model model) {
		if (null != payTypeId) {
			model.addAttribute("tdPayType", tdPayTypeService.findOne(payTypeId));
		}

		if (null != deliveryTypeId) {
			model.addAttribute("tdDeliveryType", tdDeliveryTypeService.findOne(deliveryTypeId));
		}

		if (null != diySiteId) {
			model.addAttribute("tdDiySite", tdDiySiteService.findOne(diySiteId));
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

			if (type.equalsIgnoreCase("pay")) {
				TdPayType e = tdPayTypeService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdPayTypeService.save(e);
					}
				}
			} else if (type.equalsIgnoreCase("delivery")) {
				TdDeliveryType e = tdDeliveryTypeService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdDeliveryTypeService.save(e);
					}
				}
			} else if (type.equalsIgnoreCase("diysite")) {
				TdDiySite e = tdDiySiteService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdDiySiteService.save(e);
					}
				}
			} else if (type.equalsIgnoreCase("codDistrict")) {
				TdShippingAddress e = tdShippingAddressService.findOne(id);

				if (null != e) {
					if (sortIds.length > i) {
						e.setSortId(new Double(sortIds[i]));
						tdShippingAddressService.save(e);
					}
				}
			}
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

				if (type.equalsIgnoreCase("pay")) {
					tdPayTypeService.delete(id);
				} else if (type.equalsIgnoreCase("delivery")) {
					tdDeliveryTypeService.delete(id);
				} else if (type.equalsIgnoreCase("diysite")) {
					tdDiySiteService.delete(id);
				} else if (type.equalsIgnoreCase("codDistrict")) {
					tdShippingAddressService.delete(id);
				}
			}
		}
	}

	private void btnConfirm(Long[] ids, Integer[] chkIds) {
		if (null == ids || null == chkIds || ids.length < 1 || chkIds.length < 1) {
			return;
		}

		for (int chkId : chkIds) {
			if (chkId >= 0 && ids.length > chkId) {
				Long id = ids[chkId];

				TdOrder tdOrder = tdOrderService.findOne(id);

				// 只有待确认(1L)订单能进行确认，确认后状态为待发货(3L)
				if (tdOrder.getStatusId().equals(1L)) {
					tdOrder.setStatusId(3L);
					tdOrder.setCheckTime(new Date()); // 确认时间
					tdOrderService.save(tdOrder);
				}
			}
		}
	}

	private void btnCancel(Long[] ids, Integer[] chkIds) {
		if (null == ids || null == chkIds || ids.length < 1 || chkIds.length < 1) {
			return;
		}

		for (int chkId : chkIds) {
			if (chkId >= 0 && ids.length > chkId) {
				Long id = ids[chkId];

				TdOrder tdOrder = tdOrderService.findOne(id);

				// 只有待确认(1L)、待付款(2L)订单能进行删除，确认后状态为已取消(7L)
				if (tdOrder.getStatusId().equals(1L) || tdOrder.getStatusId().equals(2L)) {
					tdOrder.setStatusId(7L);
					tdOrder.setCancelTime(new Date()); // 取消时间
					tdOrderService.save(tdOrder);
				}
			}
		}
	}

	private void btnDelete(Long[] ids, Integer[] chkIds) {
		if (null == ids || null == chkIds || ids.length < 1 || chkIds.length < 1) {
			return;
		}

		for (int chkId : chkIds) {
			if (chkId >= 0 && ids.length > chkId) {
				Long id = ids[chkId];

				TdOrder tdOrder = tdOrderService.findOne(id);

				// 只有已取消(7L)订单能进行删除
				if (tdOrder.getStatusId().equals(7L)) {
					tdOrderService.delete(tdOrder);
				}
			}
		}
	}
	private void btnEnale(Long[] chkIds) {
		if ( null == chkIds || chkIds.length < 1) {
			return;
		}

		for (Long chkId : chkIds) {
			if (chkId >= 0) {
				TdOwnMoneyRecord ownMoneyRecord = tdOwnMoneyRecordService.findOne(chkId);
				if (ownMoneyRecord != null)
				{
					ownMoneyRecord.setIsEnable(true);
					ownMoneyRecord.setIspassed(true);
					tdOwnMoneyRecordService.save(ownMoneyRecord);
				}
			}
		}
	}
	private void btnNotEnale(Long[] chkIds) {
		if ( null == chkIds || chkIds.length < 1) {
			return;
		}

		for (Long chkId : chkIds) {
			if (chkId >= 0) {
				TdOwnMoneyRecord ownMoneyRecord = tdOwnMoneyRecordService.findOne(chkId);
				if (ownMoneyRecord != null)
				{
					ownMoneyRecord.setIsEnable(true);
					ownMoneyRecord.setIspassed(false);
					tdOwnMoneyRecordService.save(ownMoneyRecord);
				}
			}
		}
	}
	
	private Map<String,String> getUserRealNameFormTdOder(List<TdOrder> orders)
	{
		
		Map<String, String> map = new HashMap<>();
		for (TdOrder tdOrder : orders)
		{
			String username = tdOrder.getUsername();
			if(map.containsKey(username))
			{
				continue;
			}
			else
			{
				TdUser tdUser = tdUserService.findByUsername(username);
				if(null != tdUser && StringUtils.isNotBlank(username)){
					map.put(username, tdUser.getRealName());
				}
			}
		}

		return map;
	}
	/**
	 * 判断是否按条件查询
	 * @return 
	 */
	/*private Boolean judgeSearchCondition(String keywords,String orderStartTime,String orderEndTime,String realName,String sellerRealName,String shippingAddress,String shippingPhone,
			String deliveryTime,String userPhone,String shippingName,String sendTime){
		Boolean searchCondition=false;
		if(null != keywords && !keywords.equalsIgnoreCase("")){
			searchCondition=true;
		}
		if(null !=orderStartTime && !orderStartTime.equals("")){
			searchCondition=true;
			
		}
		if(null !=orderEndTime && !orderEndTime.equals("")){
			searchCondition=true;
		}
		
		if(null !=userPhone && !"".equals(userPhone)){
				searchCondition=true;
		}
		if(null !=shippingName && !"".equals(shippingName)){
			searchCondition=true;
		}
		if(null !=shippingPhone && !"".equals(shippingPhone)){
			searchCondition=true;
		}
		if(null !=shippingAddress && !"".equals(shippingAddress)){
			searchCondition=true;
		}
		
		if(null !=realName && !"".equals(realName)){	
				searchCondition=true;
		}
		
//		if(null !=orderStatusId && orderStatusId !=0 ){
//			if(searchCondition==2){
//				searchCondition=4;
//			}else{
//				searchCondition=3;
//			}
//		}
		
		if(null !=deliveryTime && !deliveryTime.equals("")){
			searchCondition=true;
			
		}
		if(null !=sendTime && !sendTime.equals("")){
				searchCondition=true;
		}
		if(null !=sellerRealName  && !"".equals(sellerRealName )){
			searchCondition=true;
		}
		return searchCondition;
	}*/
	
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
    
    /*
	 * 收款报表
	 */
	@RequestMapping(value = "/downdatapay",method = RequestMethod.GET)
	@ResponseBody
	public String dowmDataPay(HttpServletRequest req,ModelMap map,String begindata,String enddata,HttpServletResponse response,String diyCode,String city)
	{
		//检查登录
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		//检查权限
		TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
		TdManagerRole tdManagerRole = null;
		if (null != tdManager && null != tdManager.getRoleId())
		{
			tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
		}
		if (tdManagerRole == null)
		{
			return "redirect:/Verwalter/login";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = null;
		Date date2 = null;
		if(null !=begindata && !begindata.equals(""))
		{
			try {
				date1 = sdf.parse(begindata);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(null !=enddata && !enddata.equals(""))
		{
			try {
				date2 = sdf.parse(enddata);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (date2 == null)
		{
			date2 = new Date();
		}
		
		// 第一步，创建一个webbook，对应一个Excel文件 
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("收款报表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        //列宽
        sheet.setColumnWidth(0 , 8*256);
        sheet.setColumnWidth(1 , 13*256);
        sheet.setColumnWidth(2 , 25*256);
        sheet.setColumnWidth(3 , 25*256);
        sheet.setColumnWidth(4 , 13*256);
        sheet.setColumnWidth(5 , 13*256);
        sheet.setColumnWidth(6 , 13*256);
        sheet.setColumnWidth(7 , 13*256);
        sheet.setColumnWidth(8 , 13*256);
        sheet.setColumnWidth(9 , 13*256);
        sheet.setColumnWidth(10 , 13*256);
        sheet.setColumnWidth(11 , 13*256);
        sheet.setColumnWidth(12 , 13*256);
        sheet.setColumnWidth(13 , 13*256);
        sheet.setColumnWidth(14 , 13*256);
        
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        style.setWrapText(true);
    	//门店、门店电话、单据、日期、预存款使用金额、代收款金额、实际代收款金额、欠款、配送人员、配送人电话、收货人、收货人电话、备注信息
        HSSFRow row = sheet.createRow((int) 0); 
        HSSFCell cell = row.createCell(0);  
        cell.setCellValue("门店名称");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue("门店电话");  
        cell.setCellStyle(style);  
        cell = row.createCell(2);  
        cell.setCellValue("主单号");  
        cell.setCellStyle(style);
        cell = row.createCell(3);  
        cell.setCellValue("分单号");  
        cell.setCellStyle(style);
        cell = row.createCell(4);  
        cell.setCellValue("品牌");  
        cell.setCellStyle(style);
        cell = row.createCell(5);
        cell.setCellValue("导购");  
        cell.setCellStyle(style);
        cell = row.createCell(6);  
        cell.setCellValue("订单日期");
        cell.setCellStyle(style);
        cell = row.createCell(7);  
        cell.setCellValue("配送方式");
        cell.setCellStyle(style);
        cell = row.createCell(8);  
        cell.setCellValue("使用可提现金额");
        cell.setCellStyle(style);
        cell = row.createCell(9);  
        cell.setCellValue("使用不可提现金额");
        cell.setCellStyle(style);
        cell = row.createCell(10);
        cell.setCellValue("使用现金劵额度");  
        cell.setCellStyle(style);
        cell = row.createCell(11); 
        cell.setCellValue("使用产品券情况");  
        cell.setCellStyle(style);
        cell = row.createCell(12); 
        cell.setCellValue("代收款金额");
        cell.setCellStyle(style);
        cell = row.createCell(13);  
        cell.setCellValue("实际代收款金额");
        cell.setCellStyle(style);
        cell = row.createCell(14);  
        cell.setCellValue("支付方式");
        cell.setCellStyle(style);
        cell = row.createCell(15);
        cell.setCellValue("第三方支付金额");
        cell.setCellStyle(style);
        cell = row.createCell(16);
        cell.setCellValue("欠款");
        cell.setCellStyle(style);
        cell = row.createCell(17);  
        cell.setCellValue("配送人员");
        cell.setCellStyle(style);
        cell = row.createCell(18);  
        cell.setCellValue("配送人电话");
        cell.setCellStyle(style);
        cell = row.createCell(19);  
        cell.setCellValue("收货人");
        cell.setCellStyle(style);
        cell = row.createCell(20);  
        cell.setCellValue("收货人电话");
        cell.setCellStyle(style);
        cell = row.createCell(21);  
        cell.setCellValue("收货人地址");
        cell.setCellStyle(style);
        cell = row.createCell(22);  
        cell.setCellValue("备注信息");
        cell.setCellStyle(style);
        cell = row.createCell(23);
        cell.setCellValue("订单状态");
        cell.setCellStyle(style);
        cell = row.createCell(24);
        cell.setCellValue("仓库名称");
        cell.setCellStyle(style);
        cell = row.createCell(25);
        cell.setCellValue("订单总金额");
        cell.setCellStyle(style);
        cell = row.createCell(26);
        cell.setCellValue("预约配送时间");
        cell.setCellStyle(style);
        cell = row.createCell(27);
        cell.setCellValue("实际配送时间");
        cell.setCellStyle(style);
        
        // 第五步，设置值  
       
        try {//调用存储过程 报错
            tdGatheringService.callInsertGathering(getStartTime(), getEndTime());
    	} catch (Exception e) {
    		System.out.println(e);
    	}
        
        if (tdManagerRole.getTitle().equalsIgnoreCase("门店")) 
		{
        	diyCode=tdManager.getDiyCode();
        	city=null;
		}
        List<TdGathering> gatheringList = tdGatheringService.searchGathering(date1, date2, city, diyCode);
        
        Integer i = 0;
        for (TdGathering gathering : gatheringList)
        {
        	row = sheet.createRow((int) i + 1);
        	if (null != gathering.getDiySiteName())
        	{//门店名称
            	row.createCell(0).setCellValue(gathering.getDiySiteName());
    		}
        	if (null != gathering.getDiySitePhone())
        	{//门店电话
            	row.createCell(1).setCellValue(gathering.getDiySitePhone());
    		}
        	if (null != gathering.getMainOrderNumber())
        	{//主单号
            	row.createCell(2).setCellValue(gathering.getMainOrderNumber());
    		}
        	if (null != gathering.getOrderNumber())
        	{//分单号
            	row.createCell(3).setCellValue(gathering.getOrderNumber());
    		}
        	if (null != gathering.getBrandTitle())
        	{//品牌
            	row.createCell(4).setCellValue(gathering.getBrandTitle());
    		}else{
    			String brand= gathering.getOrderNumber().substring(0, 2);
    			if(brand.equals("HR")){
    				row.createCell(4).setCellValue("华润");
    			}else if(brand.equals("LY")){
    				row.createCell(4).setCellValue("乐易装");
    			}else if(brand.equals("YR")){
    				row.createCell(4).setCellValue("莹润");
    			}else{
    				row.createCell(4).setCellValue("其他");
    			}
    		}
        	if (null != gathering.getSellerRealName())
        	{//导购
            	row.createCell(5).setCellValue(gathering.getSellerRealName());
    		}
        	if (null != gathering.getOrderTime())
        	{//订单日期
        		Date orderTime = gathering.getOrderTime();
        		String orderTimeStr = orderTime.toString();
            	row.createCell(6).setCellValue(orderTimeStr);
    		}
        	if (null != gathering.getDeliverTypeTitle())
        	{//配送方式
            	row.createCell(7).setCellValue(gathering.getDeliverTypeTitle());
    		}
        	if (null != gathering.getCashBalanceUsed())
        	{//使用可提现金额
            	row.createCell(8).setCellValue(gathering.getCashBalanceUsed());
    		}
        	if (null != gathering.getUnCashBalanceUsed())
        	{//使用不可提现金额
            	row.createCell(9).setCellValue(gathering.getUnCashBalanceUsed());
    		}
        	if (null != gathering.getCashCoupon())
        	{//使用现金劵金额
            	row.createCell(10).setCellValue(gathering.getCashCoupon());
    		}
        	if (null != gathering.getProductCoupon())
        	{//使用产品劵情况
            	row.createCell(11).setCellValue(gathering.getProductCoupon());
    		}
        	if (null != gathering.getTotalPrice())
        	{//代收款金额
            	row.createCell(12).setCellValue(gathering.getTotalPrice()-(gathering.getUnCashBalanceUsed()==null?0:gathering.getUnCashBalanceUsed())-(gathering.getCashBalanceUsed()==null?0:gathering.getCashBalanceUsed()));
    		}
        	if(null!= gathering.getPayed()){//实际代收款金额 
        			row.createCell(13).setCellValue(gathering.getPayed());
        			
        	}
        	if(null!= gathering.getOwned()){//欠款
        		row.createCell(16).setCellValue(gathering.getOwned());
        	}
        	
        	if (null != gathering.getPayTypeTitle())
        	{//支付方式
            	row.createCell(14).setCellValue(gathering.getPayTypeTitle());
    		}
        	if (null != gathering.getOtherPay())
        	{//第三方支付金额
            	row.createCell(15).setCellValue(gathering.getOtherPay());
    		}
        	if(null!= gathering.getWhNo()){//配送仓库
        		row.createCell(24).setCellValue(changeName(gathering.getWhNo()));
        	}
        	if(gathering.getRealName() != null){ //配送人员姓名 和电话
        		row.createCell(17).setCellValue(gathering.getRealName());
        	}
        	if(gathering.getUsername() != null){ //配送人员姓名 和电话
        		row.createCell(18).setCellValue(gathering.getUsername());
        	}
        	if (null != gathering.getShippingName())
        	{//收货人姓名
            	row.createCell(19).setCellValue(gathering.getShippingName());
    		}
        	if (null != gathering.getShippingPhone())
        	{//收获人电话
            	row.createCell(20).setCellValue(gathering.getShippingPhone());
    		}
        	if (null != gathering.getShippingAddress())
        	{//收货人地址
            	row.createCell(21).setCellValue(gathering.getShippingAddress());
    		}
        	if (null != gathering.getRemark())
        	{//备注信息
            	row.createCell(22).setCellValue(gathering.getRemark());
    		}
        	if (null != gathering.getStatusId())
        	{//订单状态
        		String statusStr = orderStatus(gathering.getStatusId());
				row.createCell(23).setCellValue(statusStr);
			}
        	if (null != gathering.getTotalPrice())
        	{//订单总金额
				row.createCell(25).setCellValue(gathering.getTotalGoodsPrice());
			}
        	if (null != gathering.getDeliveryDate()) 
        	{//预约配送时间
        		String dayTime = gathering.getDeliveryDate();
    			dayTime = dayTime + " " + gathering.getDeliveryDetailId() + ":30";
				row.createCell(26).setCellValue(dayTime);
			}
        	if (null != gathering.getDeliveryTime()) 
        	{//实际配送时间
				row.createCell(27).setCellValue(gathering.getDeliveryTime().toString());
			}
        	
        	i++;
		}
        
        String exportAllUrl = SiteMagConstant.backupPath;
        download(wb, exportAllUrl, response,"收款报表");
        return "";
	}
}
