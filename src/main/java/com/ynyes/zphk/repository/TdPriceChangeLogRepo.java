package com.ynyes.zphk.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.zphk.entity.TdPriceChangeLog;

/**
 * TdPriceChangeLog 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdPriceChangeLogRepo extends
		PagingAndSortingRepository<TdPriceChangeLog, Long>,
		JpaSpecificationExecutor<TdPriceChangeLog> 
{
    List<TdPriceChangeLog> findByGoodsIdOrderByIdDesc(Long goodsId);
    Page<TdPriceChangeLog> findByGoodsIdOrderByIdDesc(Long goodsId, Pageable page);
}