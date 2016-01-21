package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdAd;
import com.ynyes.lyz.entity.TdBackDetail;
import com.ynyes.lyz.entity.TdBackMain;

/**
 * TdBackMain  实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdBackDetailRepo extends PagingAndSortingRepository<TdBackDetail, Long>, JpaSpecificationExecutor<TdBackDetail> {
	
}
