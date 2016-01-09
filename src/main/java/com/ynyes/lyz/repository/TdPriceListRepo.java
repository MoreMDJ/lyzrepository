package com.ynyes.lyz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdPriceList;

public interface TdPriceListRepo
		extends PagingAndSortingRepository<TdPriceList, Long>, JpaSpecificationExecutor<TdPriceList> {
	
	Page<TdPriceList>findByNameContaining(String keywords , Pageable page);
	
	TdPriceList findByListHeaderId(Long listHeaderId);
	TdPriceList findBysobIdAndPriceType(Long sobId,String priceType);
	List<TdPriceList> findBySobId(Long sobId);
}
