package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdAgencyFund;
import com.ynyes.lyz.repository.TdAgencyFundRepo;

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
	
}
