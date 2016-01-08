package com.ynyes.lyz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdUser;

public interface TdUserRepo extends PagingAndSortingRepository<TdUser, Long>, JpaSpecificationExecutor<TdUser> {

	TdUser findByUsernameAndPasswordAndIsEnableTrue(String username, String password);

	TdUser findByUsernameAndIsEnableTrue(String username);

	TdUser findByUsername(String username);

	TdUser findByUsernameAndCityNameAndIsEnableTrue(String username, String cityName);
	
	TdUser findByUsernameAndIdNot(String username, Long id);  //zhangji 2016-1-8 10:26:41

	Page<TdUser> findByUserLevelIdOrderByIdDesc(Long userlevelId, Pageable page);

	Page<TdUser> findByUsernameContainingOrEmailContainingOrderByIdDesc(String keywords1, String keywords2,
			Pageable page);

	Page<TdUser> findByUsernameContainingAndUserLevelIdOrEmailContainingAndUserLevelIdOrderByIdDesc(String keywords1,
			Long userLevelId, String keywords3, Long userLevelId2, Pageable page);

}
