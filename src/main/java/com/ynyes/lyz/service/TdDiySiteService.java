package com.ynyes.lyz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.repository.TdDiySiteRepo;

/**
 * TdDiySite 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdDiySiteService {
	@Autowired
	TdDiySiteRepo repository;

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
	public void delete(TdDiySite e) {
		if (null != e) {
			repository.delete(e);
		}
	}

	public void delete(List<TdDiySite> entities) {
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
	public TdDiySite findOne(Long id) {
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
	public List<TdDiySite> findAll(Iterable<Long> ids) {
		return (List<TdDiySite>) repository.findAll(ids);
	}

	public TdDiySite findByCustomerIdAndSobId(Long customerId, Long sobId) {
		if (customerId == null || sobId == null) {
			return null;
		}
		return repository.findByCustomerIdAndSobId(customerId, sobId);
	}

	public List<TdDiySite> findAll(Sort sort) {

		return (List<TdDiySite>) repository.findAll(sort);
	}

	/**
	 * 根据城市id 找下属门店
	 * 
	 * @param regionId
	 * @author Mdj
	 */
	public List<TdDiySite> findByRegionIdAndIsEnableOrderBySortIdAsc(Long regionId) {
		return repository.findByRegionIdAndIsEnableTrueOrderBySortIdAsc(regionId);
	}

	public Page<TdDiySite> findByRegionIdAndIsEnableTrueOrderBySortIdAsc(Long regionId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByRegionIdAndIsEnableTrueOrderBySortIdAsc(regionId, pageRequest);
	}

	public List<TdDiySite> findByIsEnableTrue() {
		return repository.findByIsEnableTrue();
	}

	public Page<TdDiySite> findAllOrderBySortIdAsc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.ASC, "sortId"));

		return repository.findAll(pageRequest);
	}

	public Page<TdDiySite> searchAllOrderBySortIdAsc(String keywords, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByTitleContainingOrderBySortIdAsc(keywords, pageRequest);
	}

	/**
	 * 保存
	 * 
	 * @param e
	 * @return
	 */
	public TdDiySite save(TdDiySite e) {

		return repository.save(e);
	}

	public List<TdDiySite> save(List<TdDiySite> entities) {

		return (List<TdDiySite>) repository.save(entities);
	}

	/**
	 * 通过行政区划id查找其下属的所有门店
	 * 
	 * @author dengxiao
	 */
	public List<TdDiySite> findByDisctrictIdOrderBySortIdAsc(Long districtId) {
		if (null == districtId) {
			return null;
		}
		return repository.findByDisctrictIdOrderBySortIdAsc(districtId);
	}

	public TdDiySite findByTitleAndIsEnableTrue(String title) {
		if (null == title) {
			return null;
		}
		return repository.findByTitleAndIsEnableTrue(title);
	}
}
