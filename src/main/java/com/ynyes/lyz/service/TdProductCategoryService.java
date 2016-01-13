package com.ynyes.lyz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdProductCategory;
import com.ynyes.lyz.repository.TdProductCategoryRepo;

/**
 * TdProductCategory 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdProductCategoryService {
    @Autowired
    TdProductCategoryRepo repository;
    
    
    public TdProductCategory findByTitle(String title)
    {
        if (null == title)
        {
            return null;
        }
        
        return repository.findByTitle(title);
    }
    
    public TdProductCategory findByTitleContaining(String title)
    {
        if (null == title)
        {
            return null;
        }
        
        return repository.findTopByTitleContaining(title);
    }
    
    public TdProductCategory findByTitleAndIdNot(String title, Long id)
    {
        if (null == title || id == null)
        {
            return null;
        }
        
        return repository.findByTitleAndIdNot(title, id);
    }
    
    public List<TdProductCategory> findByParentIdOrderBySortIdAsc(Long parentId)
    {
        if (null == parentId)
        {
            return null;
        }
        
        return repository.findByParentIdOrderBySortIdAsc(parentId);
    }
    
    public List<TdProductCategory> findByParentIdIsNullOrderBySortIdAsc()
    {
        return repository.findByParentIdIsNullOrderBySortIdAsc();
    }
    public List<TdProductCategory> findByParentIdNotNullOrderBySortIdAsc()
    {
        return repository.findByParentIdNotNullOrderBySortIdAsc();
    }
    
    
    /**
     * 查找商品分类，只支持三级菜单
     * 
     * @param menuId 菜单ID
     * @return
     */
    public List<TdProductCategory> findAll()
    {
        List<TdProductCategory> resList = new ArrayList<TdProductCategory>();
        List<TdProductCategory> topList = repository.findByParentIdIsNullOrderBySortIdAsc();
        
        for (TdProductCategory top : topList)
        {
            resList.add(top);
            
            List<TdProductCategory> childList = repository.findByParentIdOrderBySortIdAsc(top.getId());
            
            if (null != childList && childList.size() > 0)
            {
                for (TdProductCategory child : childList)
                {
                    resList.add(child);
                    
                    List<TdProductCategory> grandChildList = repository.findByParentIdOrderBySortIdAsc(child.getId());
                    
                    if (null != grandChildList && grandChildList.size() > 0)
                    {
                        resList.addAll(grandChildList);
                    }
                }
            }
        }
        
        return resList;
    }
    
    /**
     * 删除
     * 
     * @param id 菜单项ID
     */
    public void delete(Long id)
    {
        if (null != id)
        {
            repository.delete(id);
        }
    }
    
    /**
     * 删除
     * 
     * @param e 菜单项
     */
    public void delete(TdProductCategory e)
    {
        if (null != e)
        {
            repository.delete(e);
        }
    }
    
    /**
     * 查找
     * 
     * @param id ID
     * @return
     */
    public TdProductCategory findOne(Long id)
    {
        if (null == id)
        {
            return null;
        }
        
        return repository.findOne(id);
    }
    
    /**
     * 保存类型
     * 
     * @param e
     * @return
     */
    public TdProductCategory save(TdProductCategory e)
    {
        if (null == e)
        {
            return null;
        }
        
        // 设置layerCount和parentTree
        e = repository.save(e); // 保存后才会有ID值
        
        if (null == e.getParentId() || e.getParentId().equals(0L))
        {
            e.setLayerCount(1L);
            e.setParentTree("[" + e.getId() + "]");
        }
        else
        {
            TdProductCategory parent = repository.findOne(e.getParentId());
//            e.setLayerCount(parent.getLayerCount() + 1L);
            
            //zhangji 设置最大3层
			Long layerCount = parent.getLayerCount() + 1L;
			if(layerCount > 3L)
			{
				layerCount = 3L;
			}
			e.setLayerCount(layerCount);
            
            e.setParentTree(parent.getParentTree() + ",[" + e.getId() + "]");
        }
        
        return repository.save(e);
    }
    
}
