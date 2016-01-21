package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdReturnNote;

public interface TdReturnNoteRepo extends PagingAndSortingRepository<TdReturnNote, Long>, JpaSpecificationExecutor<TdReturnNote>{
	Page<TdReturnNote> findByDiySiteTitleOrOrderNumberOrReturnNumberOrUsername(String keywords, String keywords1, String keywords2, String keywords3, Pageable page);
	
	List <TdReturnNote> findByUsername(String username);
	
	TdReturnNote findByReturnNumber(String returnNumber);

}
