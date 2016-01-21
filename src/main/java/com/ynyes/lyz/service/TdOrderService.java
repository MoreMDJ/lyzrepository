package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.repository.TdOrderRepo;

/**
 * TdOrder 服务类
 * 
 * @author Sharon
 *
 */

@Service
@Transactional
public class TdOrderService {
	@Autowired
	TdOrderRepo repository;

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
	public void delete(TdOrder e) {
		if (null != e) {
			repository.delete(e);
		}
	}

	public void delete(List<TdOrder> entities) {
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
	public TdOrder findOne(Long id) {
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
	public List<TdOrder> findAll(Iterable<Long> ids) {
		return (List<TdOrder>) repository.findAll(ids);
	}

	public List<TdOrder> findAll() {
		return (List<TdOrder>) repository.findAll();
	}

	public Page<TdOrder> findAllOrderBySortIdAsc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.ASC, "sortId"));

		return repository.findAll(pageRequest);
	}

	public Page<TdOrder> findAllOrderByIdDesc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));

		return repository.findAll(pageRequest);
	}
	
	/**
	 * 根据门店查询订单
	 * @param diyCode
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<TdOrder> findByDiyCode(String diyCode,int page,int size)
	{
		if (diyCode == null)
		{
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC,"id"));
		return repository.findByDiySiteCode(diyCode, pageRequest);
	}
	
	public Page<TdOrder> findByDiyCodeAndStatusIdOrderByIdDesc(String diyCode ,Long statusId,Integer page,Integer size)
	{
		if (diyCode == null || statusId == null || page == null || size == null)
		{
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByDiySiteCodeAndStatusIdOrderByIdDesc(diyCode, statusId, pageRequest);
	}

	public Page<TdOrder> findByStatusIdOrderByIdDesc(long statusId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByStatusIdOrderByIdDesc(statusId, pageRequest);
	}

	public Page<TdOrder> findByUsername(String username, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameOrderByIdDesc(username, pageRequest);
	}

	public Page<TdOrder> findByUsernameAndStatusIdNot(String username, Long StatusId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdNotOrderByIdDesc(username, StatusId, pageRequest);
		// return repository.findByUsernameOrderByIdDesc(username, pageRequest);
	}

	// zhangji
	public Page<TdOrder> findByUsernameAndStatusIdOrUsernameAndStatusIdOrUsernameAndStatusIdOrderByIdDesc(
			String username1, Long statusId1, String username2, Long statusId2, String username3, Long statusId3,
			int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdOrUsernameAndStatusIdOrUsernameAndStatusIdOrderByIdDesc(username1,
				4L, username2, 5L, username3, 6L, pageRequest);
	}

	// zhangji
	public Page<TdOrder> findByUsernameAndStatusIdOrStatusIdOrStatusIdOrStatusId(String username, Long statusId1,
			Long statusId2, Long statusId3, Long statusId4, Integer page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByUsernameAndStatusIdOrStatusIdOrStatusIdOrStatusId(username, 3L, 4L, 6L, 7L,
				pageRequest);
	}

	public TdOrder findByOrderNumber(String orderNumber) {
		return repository.findByOrderNumber(orderNumber);
	}

	public Page<TdOrder> findByUsernameAndTimeAfter(String username, Date time, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndOrderTimeAfterOrderByIdDesc(username, time, pageRequest);
	}

	public Page<TdOrder> findByUsernameAndTimeAfterAndSearch(String username, Date time, String keywords, int page,
			int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndOrderTimeAfterAndOrderNumberContainingOrderByIdDesc(username, time, keywords,
				pageRequest);
	}

	public Page<TdOrder> findByUsernameAndSearch(String username, String keywords, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndOrderNumberContainingOrderByIdDesc(username, keywords, pageRequest);
	}

	public Page<TdOrder> findByisComplainedByusernameAndSearch(List<Long> orderids, String keywords, int page,
			int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIdInAndOrderNumberContainingOrderByIdDesc(orderids, keywords, pageRequest);
	}

	public Page<TdOrder> findByisComplainedByusername(List<Long> orderids, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIdInOrderByIdDesc(orderids, pageRequest);
	}

	public Page<TdOrder> findByUsernameAndStatusIdNotAndSearch(String username, Long StatusId, String keywords,
			int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdNotAndOrderNumberContainingOrderByIdDesc(username, StatusId,
				keywords, pageRequest);
	}

	public Page<TdOrder> findByUsernameAndStatusId(String username, long statusId, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdOrderByIdDesc(username, statusId, pageRequest);
	}

	// 根据用户名和状态查找订单（不分页）
	public List<TdOrder> findByUsernameAndStatusId(String username, Long statusId) {
		if (null == username || null == statusId) {
			return null;
		}
		return repository.findByUsernameAndStatusIdOrderByIdDesc(username, statusId);
	}

	// 根据用户名查找所有的订单（不分页）
	public List<TdOrder> findByUsername(String username) {
		if (null == username) {
			return null;
		}
		return repository.findByUsernameOrderByIdDesc(username);
	};

	// zhangji
	public Page<TdOrder> findByUsernameAndSearchAndStatusIdOrUsernameAndSearchAndStatusIdOrUsernameAndSearchAndStatusId(
			String username1, String keywords1, Long statuisId1, String username2, String keywords2, Long statuisId2,
			String username3, String keywords3, Long statuisId3, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository
				.findByUsernameAndOrderNumberContainingAndStatusIdOrUsernameAndOrderNumberContainingAndStatusIdOrUsernameAndOrderNumberContainingAndStatusIdOrderByIdDesc(
						username1, keywords1, 4L, username2, keywords2, 5L, username3, keywords3, 6L, pageRequest);
	}

	public Page<TdOrder> findByUsernameAndOrderNumberAndStatusIdOrUsernameAndOrderNumberAndStatusIdOrUsernameAndOrderNumberAndStatusId(
			String username1, String keywords1, Long statuisId1, String username2, String keywords2, Long statuisId2,
			String username3, String keywords3, Long statuisId3, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository
				.findByUsernameAndOrderNumberAndStatusIdOrUsernameAndOrderNumberAndStatusIdOrUsernameAndOrderNumberAndStatusIdOrderByIdDesc(
						username1, keywords1, 4L, username2, keywords2, 5L, username3, keywords3, 6L, pageRequest);
	}

	// zhangji
	public Page<TdOrder> findByUsernameAndIsCancelTrue(String username, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndIsCancelTrue(username, pageRequest);
	}

	// zhangji
	public Page<TdOrder> findByIsCancelTrue(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIsCancelTrue(pageRequest);
	}

	// zhangji
	public Page<TdOrder> findByIsCancelTrueAndIsRefundFalse(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIsCancelTrueAndIsRefundFalse(pageRequest);
	}

	// zhangji
	public Page<TdOrder> findByIsCancelTrueAndIsRefundTrue(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByIsCancelTrueAndIsRefundTrue(pageRequest);
	}

	public Page<TdOrder> findByUsernameAndStatusIdAndSearch(String username, long statusId, String keywords, int page,
			int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdAndOrderNumberContainingOrderByIdDesc(username, statusId, keywords,
				pageRequest);
	}

	public Page<TdOrder> findByUsernameAndStatusIdAndTimeAfter(String username, long statusId, Date time, int page,
			int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdAndOrderTimeAfterOrderByIdDesc(username, statusId, time,
				pageRequest);
	}

	public Page<TdOrder> findByUsernameAndStatusIdAndTimeAfterAndSearch(String username, long statusId, Date time,
			String keywords, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameAndStatusIdAndOrderTimeAfterAndOrderNumberContainingOrderByIdDesc(username,
				statusId, time, keywords, pageRequest);
	}

	public Long countByUsernameAndStatusId(String username, long statusId) {
		return repository.countByUsernameAndStatusId(username, statusId);
	}

	public List<TdOrder> findByStatusId(Long statusId) {
		return repository.findByStatusId(statusId);
	}

	public Long countByStatusId(Long statusId) {
		return repository.countByStatusId(statusId);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeAfter(Long statusId, Date time, List<String> orderNumberList) {
		if (null == statusId || null == time || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.findByStatusIdAndOrderTimeAfterAndOrderNumberIn(statusId, time, orderNumberList);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeAfterOrStatusIdAndOrderTimeAfter(Long statusId, Long statusId2,
			List<String> orderNumberList, Date time) {
		if (null == statusId || null == statusId2 || null == time) {
			return null;
		}

		return repository.findByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberIn(statusId, time, orderNumberList, statusId2, time, orderNumberList);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeBetween(Long statusId, List<String> orderNumberList, Date start, Date end) {

		if (null == statusId || null == start || null == end || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.findByStatusIdAndOrderTimeBetweenAndOrderNumberIn(statusId, orderNumberList, start, end);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeBetweenOrStatusIdAndOrderTimeBetween(Long statusId, Long statusId2,
			List<String> orderNumberList, Date start, Date end) {

		if (null == statusId || null == statusId2 || null == start || null == end || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.findByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberIn(statusId, start, end,
				orderNumberList, statusId2, start, end, orderNumberList);
	}
	
	
	
	public Integer countByStatusIdAndOrderTimeAfter(Long statusId, Date time, List<String> orderNumberList) {
		if (null == statusId || null == time || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.countByStatusIdAndOrderTimeAfterAndOrderNumberIn(statusId, time, orderNumberList);
	}

	public Integer countByStatusIdAndOrderTimeAfterOrStatusIdAndOrderTimeAfter(Long statusId, Long statusId2,
			List<String> orderNumberList, Date time) {
		if (null == statusId || null == statusId2 || null == time) {
			return null;
		}

		return repository.countByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberIn(statusId, time, orderNumberList, statusId2, time, orderNumberList);
	}

	public Integer countByStatusIdAndOrderTimeBetween(Long statusId, List<String> orderNumberList, Date start, Date end) {

		if (null == statusId || null == start || null == end || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.countByStatusIdAndOrderTimeBetweenAndOrderNumberIn(statusId, orderNumberList, start, end);
	}

	public Integer countByStatusIdAndOrderTimeBetweenOrStatusIdAndOrderTimeBetween(Long statusId, Long statusId2,
			List<String> orderNumberList, Date start, Date end) {

		if (null == statusId || null == statusId2 || null == start || null == end || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.countByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberIn(statusId, start, end,
				orderNumberList, statusId2, start, end, orderNumberList);
	}
	
	

	/**
	 * 保存
	 * 
	 * @param e
	 * @return
	 */
	public TdOrder save(TdOrder e) {
		if (null == e) {
			return e;
		}
		return repository.save(e);
	}

	public List<TdOrder> save(List<TdOrder> entities) {

		return (List<TdOrder>) repository.save(entities);
	}

	public List<TdOrder> findByUsernameAndStatusIdNotOrderByOrderTimeDesc(String username) {
		if (null == username) {
			return null;
		}
		return repository.findByUsernameAndStatusIdNotOrderByOrderTimeDesc(username, 8L);
	}

	public List<TdOrder> findByOrderNumberContaining(String orderNumber) {
		if (null == orderNumber) {
			return null;
		}
		return repository.findByOrderNumberContaining(orderNumber);
	}
}
