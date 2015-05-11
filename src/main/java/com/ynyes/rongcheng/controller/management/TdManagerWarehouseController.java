package com.ynyes.rongcheng.controller.management;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ynyes.rongcheng.entity.TdWarehouse;
import com.ynyes.rongcheng.service.TdManagerLogService;
import com.ynyes.rongcheng.service.TdWarehouseService;
import com.ynyes.rongcheng.util.SiteMagConstant;

/**
 * 后台用户管理控制器
 * 
 * @author Sharon
 */

@Controller
@RequestMapping(value="/admin/warehouse")
public class TdManagerWarehouseController {
    
    @Autowired
    TdWarehouseService tdWarehouseService;
    
    @Autowired
    TdManagerLogService tdManagerLogService;
    
    
    @RequestMapping(value="/check", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> validateForm(String param, Long id) {
        Map<String, String> res = new HashMap<String, String>();
        
        res.put("status", "n");
        
        if (null == param || param.isEmpty())
        {
            res.put("info", "该字段不能为空");
            return res;
        }
        
        if (null == id)
        {
            if (null != tdWarehouseService.findByTitle(param))
            {
                res.put("info", "已存在同名仓库");
                return res;
            }
        }
        else
        {
            if (null != tdWarehouseService.findByTitleAndIdNot(param, id))
            {
                res.put("info", "已存在同名仓库");
                return res;
            }
        }
        
        res.put("status", "y");
        
        return res;
    }
    
    @RequestMapping(value="/list")
    public String setting(Integer page,
                          Integer size,
                          String keywords,
                          String __EVENTTARGET,
                          String __EVENTARGUMENT,
                          String __VIEWSTATE,
                          Long[] listId,
                          Integer[] listChkId,
                          Long[] listSortId,
                          ModelMap map,
                          HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username) {
            return "redirect:/admin/login";
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
                tdManagerLogService.addLog("delete", "删除仓库", req);
            }
            else if (__EVENTTARGET.equalsIgnoreCase("btnSave"))
            {
                btnSave(listId, listSortId);
                tdManagerLogService.addLog("edit", "修改仓库", req);
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
        
        if (null != keywords)
        {
            keywords = keywords.trim();
        }
        
        map.addAttribute("page", page);
        map.addAttribute("size", size);
        map.addAttribute("keywords", keywords);
        map.addAttribute("__EVENTTARGET", __EVENTTARGET);
        map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);

        Page<TdWarehouse> warehousePage = null;
        
        if (null == keywords || "".equalsIgnoreCase(keywords))
        {
            warehousePage = tdWarehouseService.findAllOrderBySortIdAsc(page, size);
        }
        else
        {
            warehousePage = tdWarehouseService.searchAndOrderBySortIdAsc(keywords, page, size);
        }
        
        map.addAttribute("warehouse_page", warehousePage);
        
        return "/site_mag/warehouse_list";
    }
    
    @RequestMapping(value="/edit")
    public String orderEdit(Long id,
                        String __VIEWSTATE,
                        ModelMap map,
                        HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username)
        {
            return "redirect:/admin/login";
        }
        
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);

        if (null != id)
        {
            map.addAttribute("warehouse", tdWarehouseService.findOne(id));
        }
        return "/site_mag/warehouse_edit";
    }
    
    @RequestMapping(value="/save")
    public String orderEdit(TdWarehouse tdWarehouse,
                        String __VIEWSTATE,
                        ModelMap map,
                        HttpServletRequest req){
        String username = (String) req.getSession().getAttribute("manager");
        if (null == username)
        {
            return "redirect:/admin/login";
        }
        
        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
        
        if (null == tdWarehouse.getId())
        {
            tdManagerLogService.addLog("add", "用户修改仓库", req);
        }
        else
        {
            tdManagerLogService.addLog("edit", "用户修改仓库", req);
        }
        
        tdWarehouseService.save(tdWarehouse);
        
        return "redirect:/admin/warehouse/list";
    }

    @ModelAttribute
    public void getModel(@RequestParam(value = "id", required = false) Long id,
                        Model model) {
        if (null != id) {
            model.addAttribute("tdWarehouse", tdWarehouseService.findOne(id));
        }
    }
    
    private void btnSave(Long[] ids, Long[] sortIds)
    {
        if (null == ids || null == sortIds
                || ids.length < 1 || sortIds.length < 1)
        {
            return;
        }
        
        for (int i = 0; i < ids.length; i++)
        {
            Long id = ids[i];
            
            TdWarehouse e = tdWarehouseService.findOne(id);
            
            if (null != e)
            {
                if (sortIds.length > i)
                {
                    e.setSortId(sortIds[i]);
                    tdWarehouseService.save(e);
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
                
                tdWarehouseService.delete(id);
            }
        }
    }
}