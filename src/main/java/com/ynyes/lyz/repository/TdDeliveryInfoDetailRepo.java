package com.ynyes.lyz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdDeliveryInfo;
import com.ynyes.lyz.entity.TdDeliveryInfoDetail;

public interface TdDeliveryInfoDetailRepo
		extends PagingAndSortingRepository<TdDeliveryInfoDetail, Long>, JpaSpecificationExecutor<TdDeliveryInfoDetail> {
	
	TdDeliveryInfo findByTaskNo(String taskNo);
	
}
