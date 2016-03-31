package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdOrder;

/**
 * TdOrder 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdOrderRepo extends PagingAndSortingRepository<TdOrder, Long>, JpaSpecificationExecutor<TdOrder> {

	// 根据订单状态
	List<TdOrder> findByStatusIdAndCashCouponIdNotNullOrStatusIdAndCashCouponIdNotNullOrStatusIdAndProductCouponIdNotNullOrStatusIdAndProductCouponIdNotNullOrderByOrderTimeDesc(
			Long statusId1, Long statusId2, Long statusId3, Long statusId4);

	// 根据门店id
	Page<TdOrder> findByDiySiteCode(String diyCode, Pageable page);

	// 根据门店id和订单状态
	Page<TdOrder> findByDiySiteCodeAndStatusIdOrderByIdDesc(String diyCode, Long statusId, Pageable page);

	// 根据门店id，订单号，用户账号
	Page<TdOrder> findByDiySiteCodeAndOrderNumberContainingOrDiySiteCodeAndUsernameContainingOrderByIdDesc(
			String diyCode, String orderNumbers, String diyCode1, String username, Pageable page);

	Page<TdOrder> findByOrderNumberContainingOrUsernameContainingOrderByIdDesc(String orderNumbers, String username,
			Pageable page);

	Page<TdOrder> findByStatusIdOrderByIdDesc(Long statusId, Pageable page);

	Page<TdOrder> findByUsernameOrderByIdDesc(String username, Pageable page);

	List<TdOrder> findByOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(Date begin, Date end);

	List<TdOrder> findByDiySiteCodeAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(String diyCode, Date begin,
			Date end);

	// @Query("select g from TdOrder o join o.orderGoodsList og where o.statusId
	// = ?1 order by orderTime desc")
	List<TdOrder> findByStatusIdOrderByOrderTimeDesc(Long statusid);

	@Query("select o from TdOrder o where o.statusId= ?1 and o.orderTime > ?2 and o.orderNumber in ?3 group by o.mainOrderNumber order by o.id desc")
	List<TdOrder> findDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(Long statusId,
			Date time, List<String> orderNumbers);

	@Query("select o from TdOrder o where o.statusId= ?1 and o.orderTime > ?2 and o.orderNumber in ?3 or o.statusId= ?4 and o.orderTime > ?5 and o.orderNumber in ?6 group by o.mainOrderNumber order by o.id desc")
	List<TdOrder> findDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(
			Long statusId, Date time, List<String> orderNumbers, Long statusId2, Date time2,
			List<String> orderNumbers2);

	@Query("select o from TdOrder o where o.statusId= ?1 and o.orderTime Between ?2 and ?3 and o.orderNumber in ?4 group by o.mainOrderNumber order by o.id desc")
	List<TdOrder> findDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(Long statusId,
			Date start, Date end, List<String> orderNumbers);

	@Query("select o from TdOrder o where o.statusId= ?1 and o.orderTime Between ?2 and ?3 and o.orderNumber in ?4 or o.statusId= ?5 and o.orderTime Between ?6 and ?7 and o.orderNumber in ?8 group by o.mainOrderNumber order by o.id desc")
	List<TdOrder> findDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(
			Long statusId, Date start, Date end, List<String> orderNumbers, Long statusId2, Date start2, Date end2,
			List<String> orderNumbers2);

	@Query("select count(Distinct o.mainOrderNumber) from TdOrder o where o.statusId= ?1 and o.orderTime > ?2 and o.orderNumber in ?3  order by o.id desc")
	Integer countDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(Long statusId,
			Date time, List<String> orderNumbers);

	@Query("select count(Distinct o.mainOrderNumber) from TdOrder o where o.statusId= ?1 and o.orderTime > ?2 and o.orderNumber in ?3 or o.statusId= ?4 and o.orderTime > ?5 and o.orderNumber in ?6  order by o.id desc")
	Integer countDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(
			Long statusId, Date time, List<String> orderNumbers, Long statusId2, Date time2,
			List<String> orderNumbers2);

	@Query("select count(Distinct o.mainOrderNumber) from TdOrder o where o.statusId= ?1 and o.orderTime Between ?2 and ?3 and o.orderNumber in ?4  order by o.id desc")
	Integer countDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(Long statusId,
			Date start, Date end, List<String> orderNumbers);

	@Query("select count(Distinct o.mainOrderNumber) from TdOrder o where o.statusId= ?1 and o.orderTime Between ?2 and ?3 and o.orderNumber in ?4 or o.statusId= ?5 and o.orderTime Between ?6 and ?7 and o.orderNumber in ?8 order by o.id desc")
	Integer countDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(
			Long statusId, Date start, Date end, List<String> orderNumbers, Long statusId2, Date start2, Date end2,
			List<String> orderNumbers2);

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

	// 查找用户所有非删除的订单
	Page<TdOrder> findByUsernameAndStatusIdNotOrderByOrderTimeDesc(String username, Long status, Pageable page);

	// 根据订单号查找订单
	List<TdOrder> findByOrderNumberContaining(String orderNumber);

	List<TdOrder> findByMainOrderNumberIgnoreCase(String mainOrderNumber);

	/**
	 * 根据时间 查询总单号
	 * 
	 * @return
	 */
	@Query("select o from TdOrder o where o.orderTime between ?1 and ?2 group by o.mainOrderNumber order by o.orderTime desc")
	List<TdOrder> searchOrderByTime(Date begin, Date end);

	/**
	 * 根据时间 配送门店 查询总单号
	 * 
	 * @return
	 */
	@Query("select o from TdOrder o where o.diySiteCode = ?1 and o.orderTime between ?2 and ?3 group by o.mainOrderNumber order by o.orderTime desc")
	List<TdOrder> searchMainOrderNumberByOrderTimeAndDiySiteCode(String diyCode, Date begin, Date end);

	/**
	 * 查询指定归属销顾的订单
	 * 
	 * @author DengXiao
	 */
	List<TdOrder> findBySellerIdAndStatusIdNotOrderByOrderTimeDesc(Long sellerId, Long statusId);

	/**
	 * 查询指定归属销顾的订单
	 * 
	 * @author DengXiao
	 */
	Page<TdOrder> findBySellerIdAndStatusIdNotOrderByOrderTimeDesc(Long sellerId, Long statusId, Pageable page);

	/**
	 * 查询指定归属销顾的指定状态的订单
	 * 
	 * @author DengXiao
	 */
	List<TdOrder> findBySellerIdAndStatusIdOrderByOrderTimeDesc(Long sellerId, Long statusId);

	/**
	 * 根据门店的id查询门店下所有的订单
	 * 
	 * @author DengXiao
	 */
	Page<TdOrder> findByDiySiteIdAndStatusIdNotOrderByOrderTimeDesc(Long diySiteId, Long statusId, Pageable page);

	/**
	 * 根据门店id查询门店下指定状态的订单
	 * 
	 * @author DengXiao
	 */
	List<TdOrder> findByDiySiteIdAndStatusIdOrderByOrderTimeDesc(Long diySiteId, Long statusId);

	/**
	 * 用户模糊查找订单，参与参数：username，orderNumber
	 * 
	 * @author DengXiao
	 */
	List<TdOrder> findByUsernameContainingAndUsernameOrOrderNumberContainingAndUsernameOrderByOrderTimeDesc(
			String keywords1, String username1, String keywords2, String username2);

	/**
	 * 销顾模糊查询订单，参与参数：username，orderNumber
	 * 
	 * @author DengXiao
	 */
	List<TdOrder> findByUsernameContainingAndSellerIdOrOrderNumberContainingAndSellerIdOrderByOrderTimeDesc(
			String keywords1, Long sellerId1, String keywords2, Long sellerId2);

	/**
	 * 店长模糊查询订单，参与参数：username,orderNumber
	 * 
	 * @author DengXiao
	 */
	List<TdOrder> findByUsernameContainingAndDiySiteIdOrOrderNumberContainingAndDiySiteIdOrderByOrderTimeDesc(
			String keywords1, Long diySiteId1, String keywords2, Long diySiteId2);

	/**
	 * 根据城市名称和订单时间查询订单
	 * @return
	 */
	List<TdOrder> findByCityAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(String city,
			Date begin, Date end);
}
