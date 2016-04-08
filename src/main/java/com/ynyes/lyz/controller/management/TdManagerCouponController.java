package com.ynyes.lyz.controller.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.ynyes.lyz.entity.TdBrand;
import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdCouponType;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdManager;
import com.ynyes.lyz.entity.TdManagerRole;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdBrandService;
import com.ynyes.lyz.service.TdCityService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdCouponTypeService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdManagerRoleService;
import com.ynyes.lyz.service.TdManagerService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdProductCategoryService;
import com.ynyes.lyz.service.TdUserService;
import com.ynyes.lyz.util.ClientConstant;
import com.ynyes.lyz.util.SiteMagConstant;

/**
 * 优惠券管理
 * 
 * @author Sharon
 */

@Controller
@RequestMapping(value="/Verwalter/coupon")
public class TdManagerCouponController extends TdManagerBaseController{
    
    @Autowired
    private TdCouponTypeService tdCouponTypeService;
    
    @Autowired
    private TdCouponService tdCouponService;
    
    @Autowired
    private TdManagerLogService tdManagerLogService;
    
    @Autowired 
    private TdDiySiteService tdDiySiteService;
    
    @Autowired
    private TdProductCategoryService tdProductCategoryService;
    
    @Autowired
    private TdManagerRoleService tdManagerRoleService;
    
    @Autowired
    private TdBrandService tdBrandService;
    
    @Autowired
    private TdGoodsService tdGoodsService;
    
    @Autowired
    private TdManagerService tdManagerService;
    
    // Max
    @Autowired
    private TdUserService tdUseService;
    
    @Autowired
    private TdOrderService tdOrderService;
    
    @Autowired 
    private TdCityService tdCityService;
    
    @RequestMapping(value = "/down/order/use")
    public void couponUseDetail()
    {
    	HSSFWorkbook workbook = new HSSFWorkbook();
    	HSSFCellStyle cellStyle = workbook.createCellStyle();
    	cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    	cellStyle.setWrapText(true);
    	
    	HSSFSheet sheet = workbook.createSheet("券的使用");
    	HSSFRow row = sheet.createRow(0);
    	HSSFCell cell = row.createCell(0);
    	cell.setCellStyle(cellStyle);
    	cell.setCellValue("订单号");
    	cell = row.createCell(1);
    	cell.setCellStyle(cellStyle);
    	cell.setCellValue("使用时间");
    	cell = row.createCell(2);
    	cell.setCellStyle(cellStyle);
    	cell.setCellValue("门店名称");
    	cell = row.createCell(3);
    	cell.setCellStyle(cellStyle);
    	cell.setCellValue("券名称");
    	cell = row.createCell(4);
    	cell.setCellStyle(cellStyle);
    	cell.setCellValue("用户名");
    	cell = row.createCell(5);
    	cell.setCellStyle(cellStyle);
    	cell.setCellValue("产品分类");
    	List<TdOrder> orders = tdOrderService.findByCompleteOrder();
    	Integer rowNumber = 1;
    	for (TdOrder tdOrder : orders) 
    	{
			if (tdOrder.getCashCouponId() != null && !tdOrder.getCashCouponId().equalsIgnoreCase("") && tdOrder.getCashCoupon() != null)
			{
				row = sheet.createRow(rowNumber);
				cell = row.createCell(0);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("订单号");
		    	cell = row.createCell(1);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("使用时间");
		    	cell = row.createCell(2);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("门店名称");
		    	cell = row.createCell(3);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("券名称");
		    	cell = row.createCell(4);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("用户名");
		    	cell = row.createCell(5);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("产品分类");
		    	cell = row.createCell(6);
		    	cell.setCellStyle(cellStyle);
		    	cell.setCellValue("产品分类");
				rowNumber++;
			}
		}
    	
    }
    
    
    @RequestMapping(value="/type/list")
    public String couponType(String __EVENTTARGET,
                          String __EVENTARGUMENT,
                          String __VIEWSTATE,
                          Long[] listId,
                          Integer[] listChkId,
                          Long[] listSortId,
                          ModelMap map,
                          HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        
        if (null == username) {
            return "redirect:/Verwalter/login";
        }
        //管理员角色
        TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
        TdManagerRole tdManagerRole = null;
        
        if (null != tdManager && null != tdManager.getRoleId())
        {
            tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
        }
        
        if (null != tdManagerRole) 
        {
			map.addAttribute("tdManagerRole", tdManagerRole);
		}
        
        if (null != __EVENTTARGET)
        {
            if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
            {
                btnTypeDelete(listId, listChkId);
                tdManagerLogService.addLog("delete", "删除优惠券类型", req);
            }
            else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
            {
                btnTypeSave(listId, listSortId);
                tdManagerLogService.addLog("edit", "修改优惠券类型", req);
            }
        }
        
        map.addAttribute("__EVENTTARGET", __EVENTTARGET);
        map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);

        List<TdCouponType> couponTypeList = null;
        
        couponTypeList = tdCouponTypeService.findAllOrderBySortIdAsc();
        
        map.addAttribute("coupon_type_list", couponTypeList);
        
        return "/site_mag/coupon_type_list";
    }
    
    @RequestMapping(value = "/type/edit")
    public String typeEdit(Long id, String __VIEWSTATE, ModelMap map,
            HttpServletRequest req) {
        
        String username = (String) req.getSession().getAttribute("manager");
        
        if (null == username) {
            return "redirect:/Verwalter/login";
        }

        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        map.addAttribute("category_list", tdProductCategoryService.findAll());
       
        if (null != id) {
            map.addAttribute("coupon_type", tdCouponTypeService.findOne(id));
        }
        return "/site_mag/coupon_type_edit";
    }
    
    @RequestMapping(value = "/type/save")
    public String typeSave(TdCouponType tdCouponType, String __VIEWSTATE, ModelMap map,
            HttpServletRequest req) {
        String username = (String) req.getSession().getAttribute("manager");
        
        if (null == username) {
            return "redirect:/Verwalter/login";
        }

        map.addAttribute("__VIEWSTATE", __VIEWSTATE);

        if (null == tdCouponType.getId()) {
            tdManagerLogService.addLog("add", "用户修改优惠券类型", req);
        } else {
            tdManagerLogService.addLog("edit", "用户修改优惠券类型", req);
        }
        
//        if(null != tdCouponType.getCategoryId() && (tdCouponType.getCategoryId() == 1L || tdCouponType.getCategoryId() == 2L))
//        tdCouponType.setPicUri("unique");
        tdCouponTypeService.save(tdCouponType);       
        
        //同步优惠券数据
        List<TdCoupon> couponList = tdCouponService.findByTypeIdAndIsDistributtedFalse(tdCouponType.getId());
        List<TdCoupon> couponListTrue = tdCouponService.findByTypeIdAndIsDistributtedTrueOrderByIdDesc(tdCouponType.getId());  //zhangji
        for (TdCoupon item : couponList)
        {
//        	item.setCanUsePrice(tdCouponType.getCanUsePrice());
        	item.setTypeTitle(tdCouponType.getTitle());
        	tdCouponService.save(item);
        }
        //zhangji
        for (TdCoupon item : couponListTrue)
        {
//        	item.setCanUsePrice(tdCouponType.getCanUsePrice());
        	item.setTypeTitle(tdCouponType.getTitle());
        	tdCouponService.save(item);
        }
        return "redirect:/Verwalter/coupon/type/list";
    }
    
    
    @RequestMapping(value="/list")
    public String setting(Integer page,
                          Integer size,
                          String __EVENTTARGET,
                          String __EVENTARGUMENT,
                          String __VIEWSTATE,
                          Long[] listId,
                          Integer[] listChkId,
                          Double[] listSortId,
                          ModelMap map,
                          HttpServletRequest req){
        
        String username = (String) req.getSession().getAttribute("manager");
        
        if (null == username) {
            return "redirect:/Verwalter/login";
        }
        //管理员角色
        TdManager tdManager = tdManagerService.findByUsernameAndIsEnableTrue(username);
        TdManagerRole tdManagerRole = null;
        
        if (null != tdManager && null != tdManager.getRoleId())
        {
            tdManagerRole = tdManagerRoleService.findOne(tdManager.getRoleId());
        }
        
        if (null != tdManagerRole) {
			map.addAttribute("tdManagerRole", tdManagerRole);
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
            else if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
            {
                btnDelete(listId, listChkId);
                tdManagerLogService.addLog("delete", "删除优惠券", req);
            }
            else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
            {
                btnSave(listId, listSortId);
                tdManagerLogService.addLog("edit", "修改优惠券", req);
            }
        }
        
        if (null == page || page < 0)
        {
            page = 0;
        }
        
        if (null == size || size <= 0)
        {
            size = SiteMagConstant.pageSize;;
        }
        
        map.addAttribute("page", page);
        map.addAttribute("size", size);
        map.addAttribute("__EVENTTARGET", __EVENTTARGET);
        map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);

        Page<TdCoupon> couponPage = null;
        
        couponPage = tdCouponService.findByIsDistributtedFalseOrderBySortIdAsc(page, size);
        
        map.addAttribute("coupon_page", couponPage);
        
        return "/site_mag/coupon_list";
    }
    
    @RequestMapping(value="/edit")
    public String orderEdit(Long id,
                        String __VIEWSTATE,
                        ModelMap map,
                        HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username)
        {
            return "redirect:/Verwalter/login";
        }
        
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        
        map.addAttribute("brand_list", tdBrandService.findAll());
        map.addAttribute("city_list", tdCityService.findAll());

        if (null != id)
        {
        	TdCoupon coupon = tdCouponService.findOne(id);
            map.addAttribute("coupon", coupon);
            if(null != coupon.getGoodsId())
            {
            	map.addAttribute("cou_goods",tdGoodsService.findOne(coupon.getGoodsId()));
            }
//            return "/site_mag/coupon_edit_hasId";
        }
        return "/site_mag/coupon_edit";
    }
    
    @RequestMapping(value="/distributed/list")
    public String distributedList(Integer page,
                          Integer size,
                          String __EVENTTARGET,
                          String __EVENTARGUMENT,
                          String __VIEWSTATE,
                          Long[] listId,
                          Integer[] listChkId,
                          Double[] listSortId,
                          ModelMap map,
                          Long diysiteId,
                          String keywords,
                          Long isUsed,
                          Long typeId,
                          HttpServletRequest req){
        
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
        
        if (null != __EVENTTARGET)
        {
            if (__EVENTTARGET.equalsIgnoreCase("btnPage"))
            {
                if (null != __EVENTARGUMENT)
                {
                    page = Integer.parseInt(__EVENTARGUMENT);
                } 
            }
            else if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
            {
                btnDelete(listId, listChkId);
                tdManagerLogService.addLog("delete", "删除优惠券", req);
            }
            else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
            {
                btnSave(listId, listSortId);
                tdManagerLogService.addLog("edit", "修改优惠券", req);
            }
            else if(__EVENTTARGET.equalsIgnoreCase("btnFailure"))
            {
            	btnFailure(listId, listChkId);
            	tdManagerLogService.addLog("failure", "失效优惠卷", req);
            }
        }
        
        if (null == page || page < 0)
        {
            page = 0;
        }
        
        if (null == size || size <= 0)
        {
            size = SiteMagConstant.pageSize;;
        }
        
        if (null == diysiteId) 
        {
			diysiteId = 0L;
		}
        
        if (null == isUsed) 
        {
        	isUsed = 0L;
		}
        
        if(null == typeId)
        {
        	typeId =0L;
        }
        
       
        Page<TdCoupon> couponPage = null;
        
        if(typeId.equals(0L))
        {
        	if(isUsed.equals(0L))
        	{
        		if(StringUtils.isNotBlank(keywords))
        		{
        			//模糊查询优惠券名称,已领取的优惠券,根据领取时间排序
        			couponPage = tdCouponService.findByTypeTitleContainingAndIsDistributtedTrueOrUsernameContainingAndIsDistributtedTrueOrderByGetTimeDesc(keywords,page, size);
        		}
        		else
        		{
        			//查询已领取的优惠券,根据领取时间排序
        			couponPage = tdCouponService.findByIsDistributtedTrueOrderByGetTimeDesc(page, size);
        		}
        	}
        	else
        	{
        		if(StringUtils.isNotBlank(keywords))
        		{
        			if(isUsed.equals(1L))
        			{
        				//模糊查询优惠券名称,已领取,已使用的优惠券,根据领取时间排序
        				couponPage = tdCouponService.findByTypeTitleContainingAndIsDistributtedTrueAndIsUsedOrUsernameContainingAndIsDistributtedTrueAndIsUsedOrderByGetTimeDesc(keywords,true,page, size);
        			}
        			else
        			{
        				//模糊查询优惠券名称,已领取,未使用的优惠券,根据领取时间排序
        				couponPage = tdCouponService.findByTypeTitleContainingAndIsDistributtedTrueAndIsUsedOrUsernameContainingAndIsDistributtedTrueAndIsUsedOrderByGetTimeDesc(keywords,false,page, size);
        			}
        		}
        		else
        		{
        			if(isUsed.equals(1L))
        			{
        				//已领取,已使用的优惠券,根据领取时间排序
        				couponPage = tdCouponService.findByIsDistributtedTrueAndIsUsedOrderByGetTimeDesc(true,page, size);
        			}
        			else
        			{
        				//已领取,未使用的优惠券,根据领取时间排序
        				couponPage = tdCouponService.findByIsDistributtedTrueAndIsUsedOrderByGetTimeDesc(false,page, size);
        			}
        		}
        	}
        }
        else
        {
        	if(isUsed.equals(0L))
        	{
        		if(null != keywords && !keywords.equalsIgnoreCase(""))
        		{
        			//模糊查询优惠券名称,已领取的优惠券,类型筛选,根据领取时间排序
        			couponPage = tdCouponService.findByTypeTitleContainingAndIsDistributtedTrueAndTypeCategoryIdOrUsernameContainingAndIsDistributtedTrueAndTypeCategoryIdOrderByGetTimeDesc(keywords,typeId,page, size);
        		}
        		else
        		{
        			//查询领取的优惠券,类型筛选,根据领取时间排序
        			couponPage = tdCouponService.findByIsDistributtedTrueAndTypeCategoryIdOrderByGetTimeDesc(typeId,page, size);
        		}
        	}
        	else
        	{
        		if(null != keywords && !keywords.equalsIgnoreCase(""))
        		{
        			if(isUsed.equals(1L))
        			{
        				//模糊查询优惠券名称,已领取,已使用,类型筛选,根据领取时间排序
        				couponPage = tdCouponService.findByTypeTitleContainingAndIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrUsernameContainingAndIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrderByGetTimeDesc(keywords,true,typeId,page, size);
        			}
        			else
        			{
        				//模糊查询优惠券名称,已领取,未使用,类型筛选,根据领取时间排序
        				couponPage = tdCouponService.findByTypeTitleContainingAndIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrUsernameContainingAndIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrderByGetTimeDesc(keywords,false,typeId,page, size);
        			}
        		}
        		else
        		{
        			if(isUsed.equals(1L))
        			{
        				//查询已领取,已使用,类型筛选,根据领取时间排序
        				couponPage = tdCouponService.findByIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrderByGetTimeDesc(true,typeId,page, size);
        			}
        			else
        			{
        				//查询已领取,未使用,类型筛选,根据领取时间排序
        				couponPage = tdCouponService.findByIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrderByGetTimeDesc(false,typeId,page, size);
        			}
        		}
        	}
        	
        }
        
		
		map.addAttribute("coupon_page", couponPage);
        
        map.addAttribute("page", page);
        map.addAttribute("size", size);
        map.addAttribute("diysiteId", diysiteId);
        map.addAttribute("isUsed", isUsed);
        map.addAttribute("keywords", keywords);
        map.addAttribute("__EVENTTARGET", __EVENTTARGET);
        map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        //查询同盟店
        List<TdDiySite> tdDiySitelist = tdDiySiteService.findByIsEnableTrue();
        map.addAttribute("tdDiySite_list", tdDiySitelist);
        //查询优惠券类型
        map.addAttribute("couponType_list", tdCouponTypeService.findAllOrderBySortIdAsc());
        map.addAttribute("typeId", typeId);
        
      //城市和门店信息
		if (null != tdManagerRole && tdManagerRole.getIsSys()){
			map.addAttribute("diySiteList",tdDiySiteService.findAll());
			map.addAttribute("cityList", tdCityService.findAll());
		}
        
        return "/site_mag/coupon_distributed_list";
    }
    
    @RequestMapping(value="/add")
    public String couponAdd(
                        String __VIEWSTATE,
                        ModelMap map,
                        HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username)
        {
            return "redirect:/Verwalter/login";
        }
        
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        
        map.addAttribute("product_category_list", tdProductCategoryService.findAll());
        
        List<TdCouponType> couponTypeList = null;
        
        couponTypeList = tdCouponTypeService.findAllOrderBySortIdAsc();
        
        map.addAttribute("coupon_type_list", couponTypeList);

        return "/site_mag/coupon_add";
    }
    
    @RequestMapping(value="/add/submit")
    public String addSubmit(TdCoupon coupon,
                        String __VIEWSTATE,
//                        Long leftNumber,
                        Long typeId,
                        ModelMap map,
                        HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username)
        {
            return "redirect:/Verwalter/login";
        }
        
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);

            tdManagerLogService.addLog("add", "发放优惠券", req);
            
           
            
            if (  null != typeId)
            {
            	TdCouponType tdCouponType = tdCouponTypeService.findOne(typeId);

				TdCoupon tdCoupon = tdCouponService.findByTypeIdAndUsernameAndIsDistributtedTrue(typeId, coupon.getUsername());
                
                if (null == tdCoupon)
                {
//                    coupon = new TdCoupon();                        
//                    coupon.setLeftNumber(leftNumber);
                    coupon.setTypeId(typeId);
//                    coupon.setCanUsePrice(tdCouponType.getCanUsePrice());
                    coupon.setSortId(99.00);
                    coupon.setPrice(tdCouponType.getPrice());
                 
//                    coupon.setCanUsePrice(tdCouponType.getCanUsePrice());
                    coupon.setTypeCategoryId(tdCouponType.getCategoryId());
                    coupon.setGetNumber(1L);
                    coupon.setGetTime(new Date());
        		    
        		    
        		    if (null != tdCouponType && null != tdCouponType.getTotalDays())
        		    {
        	    	    Calendar ca = Calendar.getInstance();
        	    	    ca.add(Calendar.DATE, tdCouponType.getTotalDays().intValue());
        	    	    coupon.setExpireTime(ca.getTime());
        		    }
        		    coupon.setPrice(tdCouponType.getPrice());
        		    coupon.setIsDistributted(true);
        		    coupon.setIsUsed(false);
        		    coupon.setTypeDescription(tdCouponType.getDescription());
        		    coupon.setTypeId(tdCouponType.getId());
        		    coupon.setTypePicUri(tdCouponType.getPicUri());
        		    coupon.setTypeTitle(tdCouponType.getTitle());
        		    coupon.setMobile(coupon.getUsername());
//        		    coupon.setIsEnable(true); 	//账户存在，可用
        		    
        		    tdCouponService.save(coupon);
                }
                else
                {
                	return "redirect:/Verwalter/coupon/add";
                }
                
                
			
            }          
				
            
        
//        else
//        {
//            tdManagerLogService.addLog("edit", "用户修改优惠券", req);
//            tdCouponService.save(tdCoupon);
//        }
        
        return "redirect:/Verwalter/coupon/distributed/list";
    }
    
    @RequestMapping(value="/save")
    public String orderEdit(TdCoupon tdCoupon,
                        String __VIEWSTATE,
                        Long leftNumber,
                        Long typeId,
                        ModelMap map,
                        HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username)
        {
            return "redirect:/Verwalter/login";
        }
        
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        
        if (null == tdCoupon.getId())
        {
            tdManagerLogService.addLog("add", "用户添加优惠券", req);
            
            
//            if ( null != leftNumber 
//                    && null != typeId)
//            {
//            	TdCouponType tdCouponType = tdCouponTypeService.findOne(typeId);
//                         
//				TdCoupon coupon = tdCouponService.findTopByTypeIdAndIsDistributtedFalse(typeId);
//                
//                if (null == coupon)
//                {
//                    coupon = new TdCoupon();                        
//                    coupon.setLeftNumber(leftNumber);
//                    coupon.setTypeId(typeId);
//                    coupon.setCanUsePrice(tdCouponType.getCanUsePrice());
//                    coupon.setSortId(99.00);
//                    coupon.setPrice(tdCouponType.getPrice());
//                }
//                else
//                {
//                    coupon.setLeftNumber(coupon.getLeftNumber() + leftNumber);
//                }
//                
//                tdCouponService.save(coupon);
//			
//            }          
				
            
            tdCoupon.setIsDistributted(false);
        }
        else
        {
            tdManagerLogService.addLog("edit", "用户修改优惠券", req);
        }
        tdCoupon.setAddTime(new Date());
        
        if(null != tdCoupon.getGoodsId())
        {
        	TdGoods goods = tdGoodsService.findOne(tdCoupon.getGoodsId());
        	tdCoupon.setGoodsName(goods.getTitle());
        	tdCoupon.setPicUri(goods.getCoverImageUri());
        }
        if(null != tdCoupon.getBrandId())
        {
        	TdBrand brand = tdBrandService.findOne(tdCoupon.getBrandId());
        	if(null != brand)
        	{
        		tdCoupon.setBrandTitle(brand.getTitle());
        	}
        }
        if(null != tdCoupon.getCityId()){
        	TdCity city= tdCityService.findOne(tdCoupon.getCityId());
        	if(null != city){
        		tdCoupon.setCityName(city.getCityName());
        	}
        }
        tdCouponService.save(tdCoupon);
        
        return "redirect:/Verwalter/coupon/list";
    }
    /**
	 * @author lc
	 * @注释：通过id返回title
	 */
    @RequestMapping(value = "/getTitle", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> login(Long typeId, 
			 HttpServletRequest request) {
    	Map<String, Object> res = new HashMap<String, Object>();

		res.put("code", 1);
		if (null == typeId) {
			res.put("msg", "error");
			return res;
		}
		
		TdCouponType tdCouponType = tdCouponTypeService.findOne(typeId);
		res.put("typetitle", tdCouponType.getTitle());
		res.put("code", 0);
		return res;
    }
	
    
    @RequestMapping(value = "/typeCheck", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> validateForm( Long param , Long id) {
        Map<String, String> res = new HashMap<String, String>();

        res.put("status", "n");
        
		if (null == param) {
			res.put("info", "该字段不能为空");
			return res;
		}
		if (param != 0L)
		{
			if (null == id) {
				if (null != tdCouponTypeService.findByCategoryIdAndPicUriNotNull(param)) {
					res.put("info", "该类型优惠券已存在");
					return res;
				}
			} else {
				TdCouponType present = tdCouponTypeService.findOne(id);
				TdCouponType exist = tdCouponTypeService.findByCategoryIdAndPicUriNotNull(param);
				if (exist.getId() != present.getId()) {
					res.put("info", "该类型优惠券已存在");
					return res;
				}
			}
		}
		
        

        res.put("status", "y");

        return res;
    }
	
    @ModelAttribute
    public void getModel(@RequestParam(value = "couponTypeId", required = false) Long couponTypeId,
                        @RequestParam(value = "couponId", required = false) Long couponId,
                        Model model) {
        if (null != couponTypeId) {
            model.addAttribute("tdCouponType", tdCouponTypeService.findOne(couponTypeId));
        }
        
        if (null != couponId) {
            model.addAttribute("tdCoupon", tdCouponService.findOne(couponId));
        }
    }
   
    /**
     * 指定商品券和产品券 关键字收索商品
     * @author Max
     */
    @RequestMapping(value="/search",method=RequestMethod.POST)
    public String goodsSeach(String keywords,Integer page,HttpServletRequest req,ModelMap map)
    {
    	
    	map.addAttribute("goodsList", tdGoodsService.searchGoods(keywords));
    	return "/site_mag/coupon_goods";
    }
    
    /**
     * 优惠券发放
     * @author Max
     * 
     */
    @RequestMapping(value="/grant/{couponId}")
    public String couponGrant(@PathVariable Long couponId
    		,String keywords,
    		String __EVENTTARGET,
            String __EVENTARGUMENT,
            String __VIEWSTATE,
    		Integer page,
    		Integer size,
    		Integer[] listChkId,
    		Long[] listId,
    		Long[] quantity,
    		String cityName,
    		HttpServletRequest req,
    		ModelMap map)
    {
    	if (null != __EVENTTARGET)
        {
            if (__EVENTTARGET.equalsIgnoreCase("btnPage"))
            {
                if (null != __EVENTARGUMENT)
                {
                    page = Integer.parseInt(__EVENTARGUMENT);
                } 
            }
            else if (__EVENTTARGET.equalsIgnoreCase("grantMore"))
            {
                grantMoreCoupon(listId, listChkId, quantity,couponId);
//            	btnDelete(listId, listChkId);
              tdManagerLogService.addLog("add", "发放优惠券", req);
            }
            else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
            {
//                btnSave(listId, listSortId);
//                tdManagerLogService.addLog("edit", "修改优惠券", req);
            }
            else if (__EVENTTARGET.equalsIgnoreCase("changeDiysite")) {
		   
			}else if(__EVENTTARGET.equalsIgnoreCase("changeType")){
				
			}
        }
    	
    	if(null != couponId)
    	{
    		TdCoupon coupon = tdCouponService.findOne(couponId);
    		if(null != coupon){
    			cityName=coupon.getCityName();
    			System.out.println(cityName);
    			map.addAttribute("cityName",cityName);
    		}
    		map.addAttribute("coupon", coupon);
    	}
    	if(null == page)
    	{
    		page = 0;
    	}
    	if(null == size)
    	{
    		size=ClientConstant.pageSize;
    	}
    	map.addAttribute("couponId", couponId);
    	map.addAttribute("page", page);
    	map.addAttribute("size", size);
    	map.addAttribute("keywords", keywords);
    	
    	if(null == keywords || "".equals(keywords))
    	{
    		map.addAttribute("user_page", tdUseService.findByCityNameOrderByIdDesc(cityName,page, size));
    	}else{
    		map.addAttribute("user_page", tdUseService.searchcityNameAndOrderByIdDesc(keywords,cityName, page, size));
    	}
    	
    	
    	
    	map.addAttribute("__EVENTTARGET", __EVENTTARGET);
        map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        
    	return "/site_mag/coupon_grant";
    }
    
    /**
     * 优惠券单个会员发放
     * @author Max
     * 
     */
    @RequestMapping(value="grantOne",method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> grantOne(Long userId,Long number,
    			Long couponId,HttpServletRequest req){
    	Map<String,Object> res = new HashMap<>();
    	res.put("code", 1);
    	
    	if(null != userId && null != couponId && null != number)
    	{
    		
    		TdCoupon coupon = tdCouponService.findOne(couponId);
    		if(number > coupon.getLeftNumber())
    		{
    			res.put("message", "此优惠券剩余量不足"+number+"张");
    			return res;
    		}
    		for (int i = 0; i < number; i++) {
				
    			TdUser user = tdUseService.findOne(userId);
    			
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
    			TdBrand brand = tdBrandService.findOne(coupon.getBrandId());
    			if(null != brand)
    			{
    				tdCoupon.setBrandTitle(brand.getTitle());
    			}
    			tdCoupon.setTypeDescription(coupon.getTypeDescription());
    			tdCoupon.setGoodsId(coupon.getGoodsId());
    			tdCoupon.setGoodsName(coupon.getGoodsName());
    			tdCoupon.setPicUri(coupon.getPicUri());
    			tdCoupon.setTypeId(coupon.getTypeId());
    			tdCoupon.setTypeTitle(coupon.getTypeTitle());
    			tdCoupon.setTypeCategoryId(coupon.getTypeCategoryId());
    			tdCoupon.setCityId(coupon.getCityId());
    			tdCoupon.setCityName(coupon.getCityName());
    			TdGoods good= tdGoodsService.findOne(coupon.getGoodsId());
    			if(good!=null){
    				tdCoupon.setSku(good.getCode());
    			}
    			
    			
    			// 保存领取
    			tdCouponService.save(tdCoupon);
			}
    		
    		// 更新剩余量
    		coupon.setLeftNumber(coupon.getLeftNumber()-number);
    		tdCouponService.save(coupon);
    		
    		res.put("code", 0);
    		res.put("message", "发放成功");
    	}
    	
    	res.put("message", "参数错误");
    	return res;
    }
    
    private void grantMoreCoupon(Long[] ids,Integer[] chkIds,Long[] numbers,Long couponId)
    {
    	if (null == ids || null == chkIds || null == numbers || ids.length < 1 || chkIds.length < 1 || numbers.length < 1)
        {
            return;
        }
        
        for (int chkId : chkIds)
        {
            if (chkId >=0 && ids.length > chkId && numbers.length > chkId)
            {
                Long id = ids[chkId];
                Long number = numbers[chkId];
               
                TdCoupon coupon = tdCouponService.findOne(couponId);
        		if(number > coupon.getLeftNumber())
        		{
        			return ;
        		}
        		for (int i = 0; i < number; i++) {
					
        			TdUser user = tdUseService.findOne(id);
        			
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
        			TdBrand brand = tdBrandService.findOne(coupon.getBrandId());
        			if(null != brand)
        			{
        				tdCoupon.setBrandTitle(brand.getTitle());
        			}
        			tdCoupon.setTypeDescription(coupon.getTypeDescription());
        			tdCoupon.setGoodsId(coupon.getGoodsId());
        			tdCoupon.setGoodsName(coupon.getGoodsName());
        			tdCoupon.setPicUri(coupon.getPicUri());
        			tdCoupon.setTypeId(coupon.getTypeId());
        			tdCoupon.setTypeTitle(coupon.getTypeTitle());
        			tdCoupon.setTypeCategoryId(coupon.getTypeCategoryId());
        			tdCoupon.setCityId(coupon.getCityId());
        			tdCoupon.setCityName(coupon.getCityName());
        			TdGoods good= tdGoodsService.findOne(coupon.getGoodsId());
        			if(good!=null){
        				tdCoupon.setSku(good.getCode());
        			}
        			// 保存领取
        			tdCouponService.save(tdCoupon);
				}
        		
        		// 更新剩余量
        		coupon.setLeftNumber(coupon.getLeftNumber()-number);
        		tdCouponService.save(coupon);
                
                
//                tdCouponTypeService.delete(id);
            }
        }
    }
    
    
    private void btnTypeSave(Long[] ids, Long[] sortIds)
    {
        if (null == ids || null == sortIds
                || ids.length < 1 || sortIds.length < 1)
        {
            return;
        }
        
        for (int i = 0; i < ids.length; i++)
        {
            Long id = ids[i];
            
            TdCouponType e = tdCouponTypeService.findOne(id);
            
            if (null != e)
            {
                if (sortIds.length > i)
                {
                    e.setSortId(sortIds[i]);
                    tdCouponTypeService.save(e);
                }
            }
        }
    }
    
    private void btnTypeDelete(Long[] ids, Integer[] chkIds)
    {
        if (null == ids || null == chkIds
                || ids.length < 1 || chkIds.length < 1)
        {
            return;
        }
        
        for (int chkId : chkIds)
        {
            if (chkId >=0 && ids.length > chkId)
            {
                Long id = ids[chkId];
                
                tdCouponTypeService.delete(id);
            }
        }
    }
    
    private void btnSave(Long[] ids, Double[] sortIds)
    {
        if (null == ids || null == sortIds
                || ids.length < 1 || sortIds.length < 1)
        {
            return;
        }
        
        for (int i = 0; i < ids.length; i++)
        {
            Long id = ids[i];
            
            TdCoupon e = tdCouponService.findOne(id);
            
            if (null != e)
            {
                if (sortIds.length > i)
                {
                    e.setSortId(sortIds[i]);
                    tdCouponService.save(e);
                }
            }
        }
    }
    
    private void btnDelete(Long[] ids, Integer[] chkIds)
    {
        if (null == ids || null == chkIds
                || ids.length < 1 || chkIds.length < 1)
        {
            return;
        }
        
        for (int chkId : chkIds)
        {
            if (chkId >=0 && ids.length > chkId)
            {
                Long id = ids[chkId];
                
                tdCouponService.delete(id);
            }
        }
    }
    /*
	 * 领用记录报表
	 */
	@RequestMapping(value = "/downdata",method = RequestMethod.GET)
	@ResponseBody
	public String dowmData(HttpServletRequest req,ModelMap map,String begindata,String enddata,HttpServletResponse response,Long cityId,String diyCode)
	{
		
		String username = (String) req.getSession().getAttribute("manager");
		if (null == username) {
			return "redirect:/Verwalter/login";
		}
		TdUser user=tdUseService.findByUsername(username);
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
        HSSFSheet sheet = wb.createSheet("领用记录报表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        //列宽
        int[] widths={25,13,25,25,18,11,13,11,19,12,
        		9,13,13,13,13,40,40};
        sheetColumnWidth(sheet,widths);
        
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        style.setWrapText(true);
        
    	//优惠券名称、金额、领卷时间、领用用户、是否使用、使用的时间、使用订单号
        HSSFRow row = sheet.createRow((int) 0); 
        
        String[] cellValues={"优惠券类型","优惠券名称","金额","劵的来源","劵的归属","领卷时间","领用用户","领用人姓名","券状态","失效时间",
				"是否使用","使用时间","使用订单号","城市名称","门店名称"};
		cellDates(cellValues, style, row);
        
        // 第五步，设置值  
        List<TdCoupon> coupon = null;
//        coupon=tdCouponService.findByIsDistributtedTrueOrderByIdDesc();
        
        if(tdManagerRole.getIsSys() &&  null != cityId){
        	coupon= tdCouponService.findByGetTimeAndCityIdOrderByGetTimeDesc(date1, date2, cityId);
    	}else{
    		TdCity city= tdCityService.findByCityName(user.getCityName());
    		diyCode=user.getDiyCode();
    		if(null != city){
    			coupon= tdCouponService.findByGetTimeAndCityIdOrderByGetTimeDesc(date1, date2, city.getId());
    		}
    	}
        //获取所有的会员
        List<TdUser> userList=tdUseService.findByUserTypeOrderByIdDesc(0L);
        //存放会员信息的map
        Map<String,Object> userMap=new HashMap<String, Object>();
        if(userList != null && userList.size()>0){
        	for (TdUser tdUser : userList) {
    			userMap.put(tdUser.getUsername()+"name",tdUser.getRealName());
    			userMap.put(tdUser.getUsername()+"diyName",tdUser.getDiyName());
    			userMap.put(tdUser.getUsername()+"diyCode",tdUser.getDiyCode());
    		}
        }
        
        Integer i = 0;
        for (TdCoupon tdCoupon : coupon)
        {
        	if(StringUtils.isBlank(diyCode) || (null!=diyCode && diyCode.equals(userMap.get(tdCoupon.getUsername()+"diyName")))){
        		
        	row = sheet.createRow((int) i + 1);
        	if(null != tdCoupon.getTypeCategoryId()){//优惠券类型
        		if(tdCoupon.getTypeCategoryId().equals(1L)){
        			row.createCell(0).setCellValue("通用现金券");
        		}
        		if(tdCoupon.getTypeCategoryId().equals(2L)){
        			row.createCell(0).setCellValue("指定商品现金券");
        		}
        		if(tdCoupon.getTypeCategoryId().equals(3L)){
        			row.createCell(0).setCellValue("产品券");
        		}
        	}
        		
        	if (null != tdCoupon.getTypeTitle())
        	{//优惠券名称
            	row.createCell(1).setCellValue(tdCoupon.getTypeTitle());
    		}
        	if (null != tdCoupon.getPrice())
        	{//金额
            	row.createCell(2).setCellValue(tdCoupon.getPrice());
    		}
        	if(null != tdCoupon.getTypeId()){//劵的来源
        		if(tdCoupon.getCustomerId()!=null){
        			row.createCell(3).setCellValue("期初导入");
        		}else{
        			if(tdCoupon.getTypeId().equals(1L)){
            			row.createCell(3).setCellValue("促销发劵");
            		}
            		if(tdCoupon.getTypeId().equals(2L)){
            			row.createCell(3).setCellValue("抢劵");
            		}
        		}
        		
        	}
        	if(null != tdCoupon.getBrandTitle()){//劵的归属
        		row.createCell(4).setCellValue(tdCoupon.getBrandTitle());
        	}
        	if (null != tdCoupon.getGetTime())
        	{//领卷时间
        		Date getTime = tdCoupon.getGetTime();
        		String couponTimeStr = getTime.toString();
            	row.createCell(5).setCellValue(couponTimeStr);
    		}
        	if (null != tdCoupon.getUsername())
        	{//领用用户
            	row.createCell(6).setCellValue(tdCoupon.getUsername());
    		}
        	if(null!= userMap.get(tdCoupon.getUsername()+"name")){//领用人姓名
        		row.createCell(7).setCellValue(userMap.get(tdCoupon.getUsername()+"name").toString());
        	}
        	if(null != tdCoupon.getIsOutDate()){//券状态
        		if(tdCoupon.getIsOutDate()){
        			row.createCell(8).setCellValue("失效");
        		}else{
        			row.createCell(8).setCellValue("生效");
        		}
        	}
        	if(null != tdCoupon.getExpireTime()){//失效时间
        		row.createCell(9).setCellValue(tdCoupon.getExpireTime().toString());
        	}
        		
        	if (null != tdCoupon.getIsUsed())
        	{//是否使用
        		if(tdCoupon.getIsUsed()){
        			row.createCell(10).setCellValue("是");
        			String couponUserTimeStr="";
        			if (null != tdCoupon.getUseTime()){
        				Date userTime = tdCoupon.getUseTime();
        				couponUserTimeStr = userTime.toString();
        			}
        			//使用时间
        			row.createCell(11).setCellValue(couponUserTimeStr);
        			//使用单号
        			row.createCell(12).setCellValue(tdCoupon.getOrderNumber());
        		}else{
        			row.createCell(10).setCellValue("否");
        		}
            	
    		}
        	if( null != tdCoupon.getCityName()){
        		row.createCell(13).setCellValue(tdCoupon.getCityName());
        	}
        	if(null!= userMap.get(tdCoupon.getUsername()+"diyName") ){
        		row.createCell(14).setCellValue(userMap.get(tdCoupon.getUsername()+"diyName").toString());
        	}
        	
        	i++;
        	}
		}
        
        String exportAllUrl = SiteMagConstant.backupPath;
        download(wb, exportAllUrl, response,"领用记录");
        return "";
	}
	
	
	private void btnFailure(Long[] ids, Integer[] chkIds)
    {
        if (null == ids || null == chkIds
                || ids.length < 1 || chkIds.length < 1)
        {
            return;
        }
        
        for (int chkId : chkIds)
        {
            if (chkId >=0 && ids.length > chkId)
            {
                Long id = ids[chkId];
                TdCoupon e = tdCouponService.findOne(id);
                if(null != e){
                	 e.setIsOutDate(true);
                     tdCouponService.save(e);
                }
            }
        }
    }
	
}
