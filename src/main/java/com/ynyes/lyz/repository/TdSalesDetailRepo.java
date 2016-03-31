package com.ynyes.lyz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdSalesDetail;

/**
 * TdSalesDetail 虚拟数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdSalesDetailRepo extends PagingAndSortingRepository<TdSalesDetail, Long>, JpaSpecificationExecutor<TdSalesDetail> {
	
}
