package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdOrder;

/**
 * TdOrder 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdOrderRepo extends PagingAndSortingRepository<TdOrder, Long>, JpaSpecificationExecutor<TdOrder> {
	
	//根据门店id
	Page<TdOrder> findByDiySiteCode(String diyCode,Pageable page);
	
	//根据门店id和订单状态
	Page<TdOrder> findByDiySiteCodeAndStatusIdOrderByIdDesc(String diyCode,Long statusId,Pageable page);
	
	
	Page<TdOrder> findByStatusIdOrderByIdDesc(Long statusId, Pageable page);

	Page<TdOrder> findByUsernameOrderByIdDesc(String username, Pageable page);
	
	
	
	List<TdOrder> findByStatusIdAndOrderTimeAfterAndOrderNumberIn(Long statusId, Date time, List<String> orderNumbers);
	
	List<TdOrder> findByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberIn(Long statusId, Date time, List<String> orderNumbers, Long statusId2, Date time2, List<String> orderNumbers2);
	
	List<TdOrder> findByStatusIdAndOrderTimeBetweenAndOrderNumberIn(Long statusId, List<String> orderNumbers, Date start, Date end);
	
	List<TdOrder> findByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberIn(Long statusId, Date start, Date end, List<String> orderNumbers, Long statusId2, Date start2, Date end2, List<String> orderNumbers2);

	
	Integer countByStatusIdAndOrderTimeAfterAndOrderNumberIn(Long statusId, Date time, List<String> orderNumbers);
	
	Integer countByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberIn(Long statusId, Date time, List<String> orderNumbers, Long statusId2, Date time2, List<String> orderNumbers2);
	
	Integer countByStatusIdAndOrderTimeBetweenAndOrderNumberIn(Long statusId, List<String> orderNumbers, Date start, Date end);
	
	Integer countByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberIn(Long statusId, Date start, Date end, List<String> orderNumbers, Long statusId2, Date start2, Date end2, List<String> orderNumbers2);

	
	
	
	Page<TdOrder> findByUsernameAndStatusIdNotOrderByIdDesc(String username, Long statusId, Pageable page);

	Page<TdOrder> findByUsernameAndStatusIdOrUsernameAndStatusIdOrUsernameAndStatusIdOrderByIdDesc(String username1,
			Long statusId1, String username2, Long statusId2, String username3, Long statusId3, Pageable page);

	Page<TdOrder> findByUsernameAndOrderTimeAfterOrderByIdDesc(String username, Date time, Pageable page);

	Page<TdOrder> findByUsernameAndOrderTimeAfterAndOrderNumberContainingOrderByIdDesc(String username, Date time,
			String keywords, Pageable page);

	Page<TdOrder> findByUsernameAndOrderNumberContainingOrderByIdDesc(String username, String keywords, Pageable page);

	Page<TdOrder> findByUsernameAndStatusIdNotAndOrderNumberContainingOrderByIdDesc(String username, Long statusId,
			String keywords, Pageable page);

	Page<TdOrder> findByIdInAndOrderNumberContainingOrderByIdDesc(List<Long> orderids, String keywords, Pageable page);

	Page<TdOrder> findByIdInOrderByIdDesc(List<Long> orderids, Pageable page);

	// zhangji
	Page<TdOrder> findByUsernameAndOrderNumberContainingAndStatusIdOrUsernameAndOrderNumberContainingAndStatusIdOrUsernameAndOrderNumberContainingAndStatusIdOrderByIdDesc(
			String username1, String keywords1, Long statusId1, String username2, String keywords2, Long statusId2,
			String username3, String keywords3, Long statusId3, Pageable page);

	Page<TdOrder> findByUsernameAndOrderNumberAndStatusIdOrUsernameAndOrderNumberAndStatusIdOrUsernameAndOrderNumberAndStatusIdOrderByIdDesc(
			String username1, String keywords1, Long statusId1, String username2, String keywords2, Long statusId2,
			String username3, String keywords3, Long statusId3, Pageable page);

	Page<TdOrder> findByUsernameAndStatusIdOrderByIdDesc(String username, Long statusId, Pageable page);

	List<TdOrder> findByUsernameAndStatusIdOrderByIdDesc(String username, Long statusId);

	Page<TdOrder> findByUsernameAndIsCancelTrue(String username, Pageable page); // 取消订单
																					// zhangji

	Page<TdOrder> findByIsCancelTrue(Pageable page); // 取消订单 zhangji

	Page<TdOrder> findByIsCancelTrueAndIsRefundFalse(Pageable page); // 取消订单
																		// zhangji

	Page<TdOrder> findByIsCancelTrueAndIsRefundTrue(Pageable page); // 取消订单
																	// zhangji

	Page<TdOrder> findByUsernameAndStatusIdAndOrderNumberContainingOrderByIdDesc(String username, Long statusId,
			String keywords, Pageable page);

	Page<TdOrder> findByUsernameAndStatusIdAndOrderTimeAfterOrderByIdDesc(String username, Long statusId, Date time,
			Pageable page);

	Page<TdOrder> findByUsernameAndStatusIdAndOrderTimeAfterAndOrderNumberContainingOrderByIdDesc(String username,
			Long statusId, Date time, String keywords, Pageable page);

	Page<TdOrder> findByUsernameAndStatusIdOrStatusIdOrStatusIdOrStatusId(String username, Long statusId1,
			Long statusId2, Long statusId3, Long statusId4, Pageable page); // zhangji

	Long countByUsernameAndStatusId(String username, Long statusId);

	List<TdOrder> findByStatusId(Long statusId);

	Long countByStatusId(Long statusId);

	TdOrder findByOrderNumber(String orderNumber);

	// 根据用户名查找所有的订单
	List<TdOrder> findByUsernameOrderByIdDesc(String username);

	// 查找用户所有非删除的订单
	List<TdOrder> findByUsernameAndStatusIdNotOrderByOrderTimeDesc(String username, Long status);
	
	//根据订单号查找订单
	List<TdOrder> findByOrderNumberContaining(String orderNumber);
}
