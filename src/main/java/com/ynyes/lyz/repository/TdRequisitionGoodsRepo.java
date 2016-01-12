package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdAd;
import com.ynyes.lyz.entity.TdRequisitionGoods;

/**
 * TdAd 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdRequisitionGoodsRepo extends PagingAndSortingRepository<TdRequisitionGoods, Long>, JpaSpecificationExecutor<TdRequisitionGoods> {
}
