package com.ynyes.lyz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdBrand;

public interface TdBrandRepo extends PagingAndSortingRepository<TdBrand, Long>, JpaSpecificationExecutor<TdBrand> {
	
	TdBrand findByShortName(String shortName);
}
