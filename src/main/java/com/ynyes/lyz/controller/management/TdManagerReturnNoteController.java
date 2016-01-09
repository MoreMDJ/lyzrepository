package com.ynyes.lyz.controller.management;

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

import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.service.TdManagerLogService;
import com.ynyes.lyz.service.TdReturnNoteService;
import com.ynyes.lyz.util.SiteMagConstant;

@Controller
@RequestMapping(value="/Verwalter/returnNote")
public class TdManagerReturnNoteController {
	
	@Autowired
	TdReturnNoteService tdReturnNoteService;
	
	@Autowired
	TdManagerLogService tdManagerLogService;
	
	// 列表
		@RequestMapping(value="/{type}/list")
		public String list(@PathVariable String type,
							Integer page,
				            Integer size,
				            String keywords,
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
		     
		    if (null != __EVENTTARGET) {
		    	 if (__EVENTTARGET.equalsIgnoreCase("btnDelete"))
		            {
		                btnDelete(type, listId, listChkId);
		                
		                if (type.equalsIgnoreCase("returnNote"))
		                {
		                    tdManagerLogService.addLog("delete", "删除退货单", req);
		                }
		               
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
	        map.addAttribute("keywords", keywords);
	        map.addAttribute("__EVENTTARGET", __EVENTTARGET);
	        map.addAttribute("__EVENTARGUMENT", __EVENTARGUMENT);
	        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
	        
	        if (null != type)
	        {
	        	if (type.equalsIgnoreCase("returnNote")) // 
	            {
	                if (null == keywords)
	                {
	                    map.addAttribute("returnNote_page", 
	                            tdReturnNoteService.findAll(page, size));
	                }
	                else
	                {
	                    map.addAttribute("returnNote_page", 
	                    		tdReturnNoteService.searchAll(keywords, page, size));
	                }
	                
	                return "/site_mag/returnNote_list";
	            }
	        	
	        }
	        return "/site_mag/returnNote_list";
		}
		
		 @RequestMapping(value="/{type}/edit")
		 public String edit(@PathVariable String type, 
		                        Long id,
		                        String __VIEWSTATE,
		                        ModelMap map,
		                        HttpServletRequest req){
		        String username = (String) req.getSession().getAttribute("manager");
		        if (null == username)
		        {
		            return "redirect:/Verwalter/login";
		        }
		        
		        map.addAttribute("__VIEWSTATE", __VIEWSTATE);
		        
		        if (null != type)
		        {
		            if (type.equalsIgnoreCase("returnNote")) // 支付方式
		            {
		                if (null != id)
		                {
		                    map.addAttribute("returnNote", tdReturnNoteService.findOne(id));
		                }
		                
		                return "/site_mag/returnNote_edit";
		            }	          
		        }
		        
		        return "/site_mag/returnNote_edit";
		}
		
		 @RequestMapping(value="/save", method = RequestMethod.POST)
		 public String save(TdReturnNote tdReturnNote,
		                        ModelMap map,
		                        HttpServletRequest req){
		        String username = (String) req.getSession().getAttribute("manager");
		        if (null == username)
		        {
		            return "redirect:/Verwalter/login";
		        }
		        
		        if (null == tdReturnNote.getId())
		        {
		            tdManagerLogService.addLog("add", "新增退货单", req);
		        }
		        else
		        {
		            tdManagerLogService.addLog("edit", "修改退货单", req);
		        }
		        tdReturnNoteService.save(tdReturnNote);
		        
		        return "redirect:/Verwalter/returnNote/returnNote/list";
		    } 
		
		 @ModelAttribute
		    public void getModel(@RequestParam(value = "returnNoteId", required = false) Long returnNoteId,
		                        Model model) {
		        if (null != returnNoteId) {
		            model.addAttribute("tdReturnNote", tdReturnNoteService.findOne(returnNoteId));
		        }
		        	      
		    } 
		 
		private void btnDelete(String type, Long[] ids, Integer[] chkIds)
	    {
	        if (null == type || type.isEmpty())
	        {
	            return;
	        }
	        
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
	                
	                if (type.equalsIgnoreCase("returnNote"))
	                {
	                    tdReturnNoteService.delete(id);
	                }
	               
	            }
	        }
	    }
		
		 private void btnSave(String type, Long[] ids, Double[] sortIds)
		    {
		        if (null == type || type.isEmpty())
		        {
		            return;
		        }
		        
		        if (null == ids || null == sortIds
		                || ids.length < 1 || sortIds.length < 1)
		        {
		            return;
		        }
		        
		        for (int i = 0; i < ids.length; i++)
		        {
		            Long id = ids[i];
		            
		            if (type.equalsIgnoreCase("returnNote"))
		            {
		                TdReturnNote e = tdReturnNoteService.findOne(id);
		                
		                if (null != e)
		                {
		                    if (sortIds.length > i)
		                    {
		                        e.setSortId(new Double(sortIds[i]));
		                    	tdReturnNoteService.save(e);
		                    }
		                }
		            }
		          
		        }
		    }
}
