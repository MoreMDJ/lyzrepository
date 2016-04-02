package com.ynyes.lyz.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdSalesDetail;

/**
 * TdSalesDetail 虚拟数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdSalesDetailRepo extends PagingAndSortingRepository<TdSalesDetail, Long>, JpaSpecificationExecutor<TdSalesDetail> {
	
	/**
	 * 调用存储过程
	 * @return
	 */
	@Query(value = "{call insertSalesDetail_initial(?1,?2)}",nativeQuery = true)
	void callInsertSalesDetail(Date start,Date end);
	
}
