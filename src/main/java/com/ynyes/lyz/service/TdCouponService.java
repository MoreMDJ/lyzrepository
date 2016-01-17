package com.ynyes.lyz.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.repository.TdCouponRepo;

/**
 * TdCoupon 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdCouponService {
	@Autowired
	TdCouponRepo repository;

	@Autowired
	TdCouponTypeService tdCouponTypeService;

	@Autowired
	TdDiySiteService tdDiySiteService;

	/**
	 * 删除
	 * 
	 * @param id
	 *            菜单项ID
	 */
	public void delete(Long id) {
		if (null != id) {
			repository.delete(id);
		}
	}

	/**
	 * 删除
	 * 
	 * @param e
	 *            菜单项
	 */
	public void delete(TdCoupon e) {
		if (null != e) {
			repository.delete(e);
		}
	}

	public void delete(List<TdCoupon> entities) {
		if (null != entities) {
			repository.delete(entities);
		}
	}

	/**
	 * 查找
	 * 
	 * @param id
	 *            ID
	 * @return
	 */
	public TdCoupon findOne(Long id) {
		if (null == id) {
			return null;
		}

		return repository.findOne(id);
	}

	/**
	 * 查找
	 * 
	 * @param ids
	 * @return
	 */
	public List<TdCoupon> findAll(Iterable<Long> ids) {
		return (List<TdCoupon>) repository.findAll(ids);
	}

	public List<TdCoupon> findByUsernameAndIsUseable(String username) {
		if (null == username) {
			return null;
		}
		return repository.findByUsernameAndExpireTimeAfterAndIsDistributtedTrueAndIsUsedFalse(username, new Date());
	}

	public List<TdCoupon> findByMobileAndIsUseable(String mobile) {
		if (null == mobile) {
			return null;
		}
		return repository.findByMobileAndExpireTimeAfterAndIsDistributtedTrueAndIsUsedFalse(mobile, new Date());
	}

	public List<TdCoupon> findByUsername(String username) {
		if (null == username) {
			return null;
		}
		return repository.findByUsernameAndIsDistributtedTrue(username);
	}

	public List<TdCoupon> findByMoblie(String mobile) {
		if (null == mobile) {
			return null;
		}
		return repository.findByMobileAndIsDistributtedTrue(mobile);
	}

	public Page<TdCoupon> findAllOrderBySortIdAsc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.ASC, "sortId"));

		return repository.findAll(pageRequest);
	}

	public List<TdCoupon> findAllOrderBySortIdAsc() {
		return (List<TdCoupon>) repository.findAll();
	}

	public List<TdCoupon> findDistinctTypeIdByIsDistributtedFalse() {
		return repository.findTypeIdDistinctByIsDistributtedFalse();
	}

	// 列表查找，可领取优惠券 zhangji
	public List<TdCoupon> findByIsDistributtedFalseOrderBySortIdAsc() {
		return repository.findByIsDistributtedFalseOrderBySortIdAsc();
	}

	public List<TdCoupon> findByTypeIdAndIsDistributtedFalse(Long typeId) {
		return repository.findByTypeIdAndIsDistributtedFalse(typeId);
	}

	public List<TdCoupon> findByTypeIdAndIsDistributtedTrueOrderByIdDesc(Long typeId) {
		return repository.findByTypeIdAndIsDistributtedTrueOrderByIdDesc(typeId);
	}

	public Page<TdCoupon> findByIsDistributtedFalseOrderBySortIdAsc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIsDistributtedFalseOrderBySortIdAsc(pageRequest);
	}

	public Page<TdCoupon> findByIsDistributtedTrueOrderByIdDesc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIsDistributtedTrueOrderByIdDesc(pageRequest);
	}

	public Page<TdCoupon> findByTypeIdAndIsDistributtedTrueOrderByIdDesc(Long typeId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByTypeIdAndIsDistributtedTrueOrderByIdDesc(typeId, pageRequest);
	}

	// 根据核销状态查找
	public Page<TdCoupon> findByIsDistributtedTrueAndIsUsedTrueOrderByIdDesc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByIsDistributtedTrueAndIsUsedTrueOrderByIdDesc(pageRequest);
	}

	public Page<TdCoupon> findByIsDistributtedTrueAndIsUsedFalseOrderByIdDesc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByIsDistributtedTrueAndIsUsedFalseOrderByIdDesc(pageRequest);
	}

	public TdCoupon findByTypeIdAndMobileAndIsDistributtedTrue(Long typeId, String mobile) {
		if (null == typeId || null == mobile) {
			return null;
		}

		return repository.findTopByTypeIdAndMobileAndIsDistributtedTrue(typeId, mobile);
	}

	// zhangji
	public TdCoupon findByTypeIdAndUsernameAndIsDistributtedTrue(Long typeId, String username) {
		if (null == typeId || null == username) {
			return null;
		}

		return repository.findTopByTypeIdAndUsernameAndIsDistributtedTrue(typeId, username);
	}

	/**
	 * @author lc
	 * @注释：根据类型查找优惠券
	 */
	public TdCoupon findTopByTypeIdAndIsDistributtedFalse(Long typeId) {
		if (null == typeId) {
			return null;
		}
		return repository.findTopByTypeIdAndIsDistributtedFalse(typeId);
	}

	public List<TdCoupon> findByIsDistributtedTrueOrderByIdDesc() {
		return repository.findByIsDistributtedTrueOrderByIdDesc();
	}

	public TdCoupon save(TdCoupon e) {
		if (null == e) {
			return null;
		}
		return repository.save(e);
	}

	public List<TdCoupon> save(List<TdCoupon> entities) {

		return (List<TdCoupon>) repository.save(entities);
	}

	// ====================================================================================
	// ↓↓↓↓↓↓↓↓↓↓↓↓↓ 分 类 型 筛 选 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// ====================================================================================

	public Page<TdCoupon> findByTypeIdAndIsDistributtedTrueAndIsUsedTrueOrderByIdDesc(Long typeId, int page, int size) {
		if (null == typeId) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByTypeIdAndIsDistributtedTrueAndIsUsedTrueOrderByIdDesc(typeId, pageRequest);
	}

	public Page<TdCoupon> findByTypeIdAndIsDistributtedTrueAndIsUsedFalseOrderByIdDesc(Long typeId, int page,
			int size) {
		if (null == typeId) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByTypeIdAndIsDistributtedTrueAndIsUsedFalseOrderByIdDesc(typeId, pageRequest);
	}

	/**
	 * 获取指定用户所有未使用且未过期的优惠券，按照获取时间倒序排序
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateFalseOrderByGetTimeDesc(String username) {
		if (null == username) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndIsOutDateFalseOrderByGetTimeDesc(username);
	}

	/**
	 * 获取用户未使用且未过期的现金券，按照获取时间倒序排序
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdNotOrderByGetTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdNotOrderByGetTimeDesc(username,
				categoryId);
	}

	/**
	 * 获取用户未使用且未过期的产品券，按照获取时间倒序排序
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdOrderByGetTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndIsOutDateFalseAndTypeCategoryIdOrderByGetTimeDesc(username,
				categoryId);
	}

	/**
	 * 查找指定用户未使用但已过期的现金券，按照过期时间倒序排序
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotOrderByExpireTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotOrderByExpireTimeDesc(
				username, categoryId);
	}

	/**
	 * 获取最近一个月内用户已经过期的商品，按照过期时间倒序排序
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotAndExpireTimeBetweenOrderByExpireTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		// 获取结束时间
		Date finish = cal.getTime();
		// 获取起始时间
		cal.add(Calendar.MONTH, -1);
		Date begin = cal.getTime();

		if (null == begin || null == finish) {
			return null;
		}

		return repository
				.findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdNotAndExpireTimeBetweenOrderByExpireTimeDesc(
						username, categoryId, begin, finish);
	}

	/**
	 * 获取最近一个月内已使用的现金券
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedTrueAndTypeCategoryIdNotAndUseTimeBetweenOrderByUseTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		// 获取结束时间
		Date finish = cal.getTime();
		// 获取起始时间
		cal.add(Calendar.MONTH, -1);
		Date begin = cal.getTime();

		if (null == begin || null == finish) {
			return null;
		}

		return repository.findByUsernameAndIsUsedTrueAndTypeCategoryIdNotAndUseTimeBetweenOrderByUseTimeDesc(username,
				categoryId, begin, finish);
	}

	/**
	 * 获取最近一个月内用户已经过期的商品，按照过期时间倒序排序
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdAndExpireTimeBetweenOrderByExpireTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		// 获取结束时间
		Date finish = cal.getTime();
		// 获取起始时间
		cal.add(Calendar.MONTH, -1);
		Date begin = cal.getTime();

		if (null == begin || null == finish) {
			return null;
		}

		return repository
				.findByUsernameAndIsUsedFalseAndIsOutDateTrueAndTypeCategoryIdAndExpireTimeBetweenOrderByExpireTimeDesc(
						username, categoryId, begin, finish);
	}

	/**
	 * 获取最近一个月内已使用的现金券
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedTrueAndTypeCategoryIdAndUseTimeBetweenOrderByUseTimeDesc(
			String username, Long categoryId) {
		if (null == username || null == categoryId) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		// 获取结束时间
		Date finish = cal.getTime();
		// 获取起始时间
		cal.add(Calendar.MONTH, -1);
		Date begin = cal.getTime();

		if (null == begin || null == finish) {
			return null;
		}

		return repository.findByUsernameAndIsUsedTrueAndTypeCategoryIdAndUseTimeBetweenOrderByUseTimeDesc(username,
				categoryId, begin, finish);
	}

	/**
	 * 获取用户未过期的通用现金券
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseOrderByGetTimeDesc(
			String username, Long typeCategoryId) {
		if (null == username || null == typeCategoryId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseOrderByGetTimeDesc(username,
				typeCategoryId);
	}

	/**
	 * 根据用户名，商品id获取指定用户未过期且未使用的产品券
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryId3LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
			String username, Long goodsId) {
		if (null == username || null == goodsId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
				username, 3L, goodsId);
	}

	/**
	 * 根据用户名，商品id获取指定用户未过期且未使用的产品券
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryId2LAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
			String username, Long goodsId) {
		if (null == username || null == goodsId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndGoodsIdOrderByGetTimeDesc(
				username, 2L, goodsId);
	}

	/**
	 * 根据用户名查找指定品牌下未过期未使用的通用现金券
	 * 
	 * @author dengxiao
	 */
	public List<TdCoupon> findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndBrandIdOrderByGetTimeDesc(
			String username, Long typeCategoryId, Long brandId) {
		if (null == username || null == typeCategoryId || null == brandId) {
			return null;
		}
		return repository.findByUsernameAndIsUsedFalseAndTypeCategoryIdAndIsOutDateFalseAndBrandIdOrderByGetTimeDesc(
				username, typeCategoryId, brandId);
	}
	
	/**
	 * 查看可领取现金券
	 * @author Max
	 */
	
	public List<TdCoupon> findByTypeIdAndTypeCategoryId(Long typeId,Long cateId,Date date)
	{
		if(null == typeId || null == cateId)
		{
			return null;
		}
		return repository.findByTypeIdAndTypeCategoryIdAndIsDistributtedFalseAndExpireTimeAfter(typeId, cateId, date);
	}
}
