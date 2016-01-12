package com.ynyes.lyz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdOwnMoneyRecord;

/**
 * TdOwnMoneyRecord 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdOwnMoneyRecordRepo extends
		PagingAndSortingRepository<TdOwnMoneyRecord, Long>,
		JpaSpecificationExecutor<TdOwnMoneyRecord> 
{
	List<TdOwnMoneyRecord> findByOrderNumberIgnoreCase(String orderNumber);
}
