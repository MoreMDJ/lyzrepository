package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdCoupon;

/**
 * TdCoupon 实体数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdCouponRepo extends PagingAndSortingRepository<TdCoupon, Long>, JpaSpecificationExecutor<TdCoupon> {
	List<TdCoupon> findByUsernameAndExpireTimeAfterAndIsDistributtedTrueAndIsUsedFalse(String username, Date current);

	List<TdCoupon> findByMobileAndExpireTimeAfterAndIsDistributtedTrueAndIsUsedFalse(String mobile, Date current);

	List<TdCoupon> findByUsernameAndIsDistributtedTrue(String username);

	List<TdCoupon> findByMobileAndIsDistributtedTrue(String mobile);

	List<TdCoupon> findByTypeIdAndIsDistributtedFalse(Long typeId);

	List<TdCoupon> findByIsDistributtedFalseOrderBySortIdAsc();

	List<TdCoupon> findByTypeIdAndIsDistributtedTrueOrderByIdDesc(Long typeId);

	TdCoupon findTopByTypeIdAndMobileAndIsDistributtedTrue(Long typeId, String mobile);

	TdCoupon findTopByTypeIdAndUsernameAndIsDistributtedTrue(Long typeId, String username); // topby啥意思？？？zhangji

	Page<TdCoupon> findByIsDistributtedFalseOrderBySortIdAsc(Pageable page);

	Page<TdCoupon> findByIsDistributtedTrueOrderByIdDesc(Pageable page);

	Page<TdCoupon> findByTypeIdAndIsDistributtedTrueOrderByIdDesc(Long typeId, Pageable page);//

	Page<TdCoupon> findByIsDistributtedTrueAndIsUsedTrueOrderByIdDesc(Pageable page);

	Page<TdCoupon> findByIsDistributtedTrueAndIsUsedFalseOrderByIdDesc(Pageable page);

	List<TdCoupon> findByIsDistributtedTrueOrderByIdDesc();

	List<TdCoupon> findTypeIdDistinctByIsDistributtedFalse();

	TdCoupon findByTypeId(Long typeId);

	TdCoupon findTopByTypeIdAndIsDistributtedFalse(Long typeId);

	/**
	 * 类型筛选
	 * 
	 * @author libiao
	 * 
	 * @param typeId
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByTypeIdAndIsDistributtedTrueAndIsUsedTrueOrderByIdDesc(Long typeId, Pageable page);//

	Page<TdCoupon> findByTypeIdAndIsDistributtedTrueAndIsUsedFalseOrderByIdDesc(Long typeId, Pageable page);//

	/**
	 * 获取指定用户所有未使用且未过期的优惠券，按照获取时间倒序排序
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateFalseOrderByGetTimeDesc(String username);

	/**
	 * 获取用户未使用且未过期的现金券，按照获取时间倒序排序
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdNotOrderByGetTimeDesc(String username,
			Long categoryId);

	/**
	 * 获取用户未使用且未过期的产品券，按照获取时间倒序排序
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdOrderByGetTimeDesc(String username,
			Long categoryId);

	/**
	 * 查找指定用户未使用但已过期的现金券，按照过期时间倒序排序
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotOrderByExpireTimeDesc(
			String username, Long categoryId);

	/**
	 * 获取最近一个月内用户已经过期的现金券，按照过期时间倒序排序
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotAndExpireTimeBetweenOrderByExpireTimeDesc(
			String username, Long categoryId, Date begin, Date finish);

	/**
	 * 获取最近一个月内已使用的现金券
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedTrueAndTypeCategoryIdNotAndUseTimeBetweenOrderByUseTimeDesc(String username,
			Long categoryId, Date begin, Date finish);

	/**
	 * 获取最近一个月内已过期的产品券
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdAndExpireTimeBetweenOrderByExpireTimeDesc(
			String username, Long categoryId, Date begin, Date finish);

	/**
	 * 获取最近一个月内已经使用的产品券
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedTrueAndTypeCategoryIdAndUseTimeBetweenOrderByUseTimeDesc(String username,
			Long categoryId, Date begin, Date finish);

	/**
	 * 获取用户未过期的通用现金券
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseOrderByGetTimeDesc(String username,
			Long typeCategoryId);

	/**
	 * 根据用户名，商品id获取指定用户未过期且未使用的产品券
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndGoodsIdAndCityNameOrderByGetTimeDesc(
			String username, Long typeCategoryId, Long goodsId,String cityName);

	/**
	 * 根据用户名查找指定品牌下未过期未使用的通用现金券
	 * 
	 * @author dengxiao
	 */
	List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndBrandIdAndCityNameOrderByGetTimeDesc(
			String username, Long typeCategoryId, Long brandId,String cityName);
	
	// Max 根据类别和发放类型查找未过期 优惠券
	List<TdCoupon> findByTypeIdAndTypeCategoryIdAndIsDistributtedFalseAndExpireTimeAfter(Long typeId,Long cateId,Date date);
	
	/**
	 * 查询已领取的优惠券,根据领取时间排序
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByIsDistributtedTrueOrderByGetTimeDesc(Pageable page);
	
	/**
	 * 模糊查询优惠券名称,已领取,根据领取时间排序
	 * @param keywords
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByTypeTitleContainingAndIsDistributtedTrueOrderByGetTimeDesc(String keywords,Pageable page);
	
	/**
	 * 查询已领取,已使用或未使用,根据领取时间排序
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByIsDistributtedTrueAndIsUsedOrderByGetTimeDesc(Boolean isUsed,Pageable page);
	/**
	 * 模糊查询优惠券名称,已领取,已使用或未使用,根据领取时间排序
	 * @param keywords
	 * @param isUsed
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByTypeTitleContainingAndIsDistributtedTrueAndIsUsedOrderByGetTimeDesc(String keywords,Boolean isUsed,Pageable page);
	/**
	 * 查询已领取,类型筛选,根据领取时间排序
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByIsDistributtedTrueAndTypeCategoryIdOrderByGetTimeDesc(Long typeCategoryId,Pageable page);
	
	/**
	 * 模糊查询优惠券名称,已领取,类型筛选,根据领取时间排序
	 * @param keywords
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByTypeTitleContainingAndIsDistributtedTrueAndTypeCategoryIdOrderByGetTimeDesc(String keywords,Long typeCategoryId,Pageable page);
	
	/**
	 * 查询已领取,已使用或未使用,类型筛选,根据领取时间排序
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrderByGetTimeDesc(Boolean isUsed,Long typeCategoryId,Pageable page);
	/**
	 * 模糊查询优惠券名称,已领取,已使用或未使用,类型筛选,根据领取时间排序
	 * @param keywords
	 * @param isUsed
	 * @param page
	 * @return
	 */
	Page<TdCoupon> findByTypeTitleContainingAndIsDistributtedTrueAndIsUsedAndTypeCategoryIdOrderByGetTimeDesc(String keywords,Boolean isUsed,Long typeCategoryId,Pageable page);
}
