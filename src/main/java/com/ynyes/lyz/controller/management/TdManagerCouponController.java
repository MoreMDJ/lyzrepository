package com.ynyes.lyz.controller.management;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdCouponType;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdManager;
import com.ynyes.lyz.entity.TdManagerRole;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.service.TdBrandService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdCouponTypeService;
import com.ynyes.lyz.service.TdDiySiteService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdManagerRoleService;
import com.ynyes.lyz.service.TdManagerService;
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
public class TdManagerCouponController {
    
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
    TdManagerService tdManagerService;
    
    // Max
    @Autowired
    private TdUserService tdUseService;
    
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
        
        if (null == username) {
            return "redirect:/Verwalter/login";
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
            else if (__EVENTTARGET.equalsIgnoreCase("changeDiysite")) {
		   
			}else if(__EVENTTARGET.equalsIgnoreCase("changeType")){
				
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
    	
    	if(null == keywords)
    	{
    		map.addAttribute("user_page", tdUseService.findAllOrderByIdDesc(page, size));
    	}else{
    		map.addAttribute("user_page", tdUseService.searchAndOrderByIdDesc(keywords, page, size));
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
    	if (null == ids || null == chkIds || null == numbers
                || ids.length < 1 || chkIds.length < 1 || numbers.length < 1)
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
}
