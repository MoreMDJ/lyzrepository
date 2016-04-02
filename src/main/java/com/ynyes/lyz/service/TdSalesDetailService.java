package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdSalesDetail;
import com.ynyes.lyz.repository.TdSalesDetailRepo;
import com.ynyes.lyz.util.Criteria;
import com.ynyes.lyz.util.Restrictions;

/**
 * TdSalesDetail 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdSalesDetailService {
	@Autowired
	TdSalesDetailRepo repository;
	
	/**
	 * 根据时间,城市,门店查询销售明细报表
	 * @return
	 */
	public List<TdSalesDetail> searchSalesDetail(Date begin,Date end,String cityName,String diySiteCode){
		Criteria<TdSalesDetail> c = new Criteria<TdSalesDetail>();
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
	public void callInsertSalesDetail(Date start,Date end){
		repository.callInsertSalesDetail(start, end);
	}
	
}
