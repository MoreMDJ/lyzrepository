package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdGathering;
import com.ynyes.lyz.repository.TdGatheringRepo;
import com.ynyes.lyz.util.Criteria;
import com.ynyes.lyz.util.Restrictions;

/**
 * TdOrder 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdGatheringService {
	@Autowired
	TdGatheringRepo repository;

	
	/**
	 * 根据时间,城市,门店查询收款报表
	 * @return
	 */
	public List<TdGathering> searchGathering(Date begin,Date end,String cityName,String diySiteCode){
		Criteria<TdGathering> c = new Criteria<TdGathering>();
		if(null!=begin){
			c.add(Restrictions.gte("orderTime", begin, true));
		}if(null!=end){
			c.add(Restrictions.lte("orderTime", end, true));
		}
		if(StringUtils.isNotBlank(cityName)){
			c.add( Restrictions.eq("city", cityName, true));
		}
		if(StringUtils.isNotBlank(diySiteCode)){
			c.add( Restrictions.eq("diySiteCode", diySiteCode, true));
		}
		c.setOrderByDesc("orderTime");
		return repository.findAll(c);
	}
	/**
	 * 调用存储过程
	 * @return 
	 * @return
	 */
	public void callInsertGathering(Date start,Date end){
		repository.callInsertGathering(start, end);
	}
}
