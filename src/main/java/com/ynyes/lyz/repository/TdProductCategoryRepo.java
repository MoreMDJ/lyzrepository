package com.ynyes.lyz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdProductCategory;



/**
 * TdProductCategory 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdProductCategoryRepo extends
		PagingAndSortingRepository<TdProductCategory, Long>,
		JpaSpecificationExecutor<TdProductCategory> 
{
    List<TdProductCategory> findByParentIdIsNullOrderBySortIdAsc();
    List<TdProductCategory> findByParentIdNotNullOrderBySortIdAsc();
    List<TdProductCategory> findByParentIdOrderBySortIdAsc(Long parentId);
    TdProductCategory findByTitle(String title);
    TdProductCategory findTopByTitleContaining(String title);
    TdProductCategory findByTitleAndIdNot(String title, Long id);
}
