package com.ynyes.lyz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdDeliveryInfo;

public interface TdDeliveryInfoRepo
		extends PagingAndSortingRepository<TdDeliveryInfo, Long>, JpaSpecificationExecutor<TdDeliveryInfo> {
	
	TdDeliveryInfo findByTaskNo(String taskNo);
	
}