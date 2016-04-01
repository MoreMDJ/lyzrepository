package com.ynyes.lyz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdReturnReport;


/**
 * TdReturnReport 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdReturnReportRepo extends PagingAndSortingRepository<TdReturnReport, Long>, JpaSpecificationExecutor<TdReturnReport> {
	
}
