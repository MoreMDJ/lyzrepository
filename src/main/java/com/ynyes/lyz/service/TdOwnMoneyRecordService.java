package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdOwnMoneyRecord;
import com.ynyes.lyz.repository.TdOwnMoneyRecordRepo;


/**
 * TdOwnMoneyRecord 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdOwnMoneyRecordService {
    
    @Autowired
    TdOwnMoneyRecordRepo repository;
    
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
    public void delete(TdOwnMoneyRecord e)
    {
        if (null != e)
        {
            repository.delete(e);
        }
    }
    
    public void delete(List<TdOwnMoneyRecord> entities)
    {
        if (null != entities)
        {
            repository.delete(entities);
        }
    }
    
    /**
     * 查找
     * 
     * @param id ID
     * @return
     */
    public TdOwnMoneyRecord findOne(Long id)
    {
        if (null == id)
        {
            return null;
        }
        
        return repository.findOne(id);
    }
    
    /**
     * 查找
     * 
     * @param ids
     * @return
     */
    public List<TdOwnMoneyRecord> findAll(Iterable<Long> ids)
    {
        return (List<TdOwnMoneyRecord>) repository.findAll(ids);
    }
    
    public List<TdOwnMoneyRecord> findByOrderNumberIgnoreCase(String orderNumber)
    {
    	if (null == orderNumber)
    	{
    		return null;
    	}
    	
        return repository.findByOrderNumberIgnoreCase(orderNumber);
    }
    
    public List<TdOwnMoneyRecord> findAll()
    {
        return (List<TdOwnMoneyRecord>) repository.findAll();
    }
    
    public Page<TdOwnMoneyRecord> findAllOrderBySortIdAsc(int page, int size)
    {
        PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "createTime"));
        
        return repository.findAll(pageRequest);
    }
    
    public Page<TdOwnMoneyRecord> findByDiyCodeAndIsEnableOrderByIdDesc(String diyCode,Boolean isEnable ,int page, int size)
    {
    	PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "createTime"));
    	if (diyCode == null || diyCode.equalsIgnoreCase(""))
    	{
			return repository.findByIsEnableOrderByIdDesc(isEnable, pageRequest);
		}
    	return repository.findByDiyCodeAndIsEnableOrderByIdDesc(diyCode, isEnable, pageRequest);
    }
    public Page<TdOwnMoneyRecord> findByDiyCodeAndIsPayedOrderByIdDesc(String diyCode,Boolean isPayed ,int page, int size)
	{
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "createTime"));
		
		if (diyCode == null || diyCode.equalsIgnoreCase(""))
    	{
			return repository.findByIsPayedOrderByIdDesc(isPayed, pageRequest);
		}
		return repository.findByDiyCodeAndIsPayedOrderByIdDesc(diyCode, isPayed, pageRequest);
	}
    
    public Page<TdOwnMoneyRecord> findByIsEnableOrderByIdDesc(Boolean isEnale ,int page, int size)
    {
    	PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "createTime"));
    	return repository.findByIsEnableOrderByIdDesc(isEnale, pageRequest);
    }
    
    public Page<TdOwnMoneyRecord> findByIsPayedOrderByIdDesc(Boolean isPayed,int page, int size)
    {
    	PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "createTime"));
    	return repository.findByIsPayedOrderByIdDesc(isPayed, pageRequest);
    }
    
    
    public Page<TdOwnMoneyRecord> findByDiyCodeOrderByIdDesc(String diyCode ,int page, int size)
    {
        PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "createTime"));
        
        return repository.findByDiyCodeOrderByIdDesc(diyCode, pageRequest);
    }
    
    
    
    public List<TdOwnMoneyRecord> findAllOrderBySortIdAsc()
    {
        return (List<TdOwnMoneyRecord>) repository.findAll(new Sort(Direction.ASC, "sortId"));
    }
    
    /**
     * 保存
     * 
     * @param e
     * @return
     */
    public TdOwnMoneyRecord save(TdOwnMoneyRecord e)
    {
        if (null == e.getCreateTime())
        {
            e.setCreateTime(new Date());
        }
        
        return repository.save(e);
    }
    
    public List<TdOwnMoneyRecord> save(List<TdOwnMoneyRecord> entities)
    {
        
        return (List<TdOwnMoneyRecord>) repository.save(entities);
    }
}
