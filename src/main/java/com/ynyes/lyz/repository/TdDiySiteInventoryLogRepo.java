package com.ynyes.lyz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdDiySiteInventoryLog;

public interface TdDiySiteInventoryLogRepo extends PagingAndSortingRepository<TdDiySiteInventoryLog, Long>,
		JpaSpecificationExecutor<TdDiySiteInventoryLog> {

	
}
