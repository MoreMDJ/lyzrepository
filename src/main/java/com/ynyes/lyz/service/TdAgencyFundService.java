package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdAgencyFund;
import com.ynyes.lyz.repository.TdAgencyFundRepo;
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
public class TdAgencyFundService {
	@Autowired
	TdAgencyFundRepo repository;

	/**
	 * 根据订单时间查询代收款报表数据
	 * @return
	 */
	public List<TdAgencyFund> searchAllByTime(Date start,Date end){
		if(null == start || null == end){
			return null;
		}
		return repository.searchAllByTime(start, end);
	}
	
	/**
	 * 根据订单时间,门店查询代收款报表数据
	 * @return
	 */
	public List<TdAgencyFund> searchAllbyDiyCodeAndTime(String diyCode,Date start,Date end){
		if(null == start || null == end){
			return null;
		}
		return repository.searchAllbyDiyCodeAndTime(diyCode,start, end);
	}
	
	/**
	 * 根据订单时间,城市查询代收款报表数据
	 * @return
	 */
	public List<TdAgencyFund> searchAllbyCityAndTime(String city,Date start,Date end){
		if(null == start || null == end){
			return null;
		}
		return repository.searchAllbyCityAndTime(city,start, end);
	}
	
	/**
	 * 根据时间,城市,门店查询代销售报表
	 * @return
	 */
	public List<TdAgencyFund> searchAgencyFund(Date begin,Date end,String cityName,String diySiteCode){
		Criteria<TdAgencyFund> c = new Criteria<TdAgencyFund>();
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
	public void callInsertAgencyFund(Date start,Date end){
		repository.callInsertAgencyFund(start, end);
	}
}
