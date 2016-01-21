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

import com.ynyes.lyz.entity.TdReturnNote;
import com.ynyes.lyz.repository.TdReturnNoteRepo;

@Service
@Transactional
public class TdReturnNoteService {
	
	
	@Autowired
	TdReturnNoteRepo repository;

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
	public void delete(TdReturnNote e) {
		if (null != e) {
			repository.delete(e);
		}
	}

	public void delete(List<TdReturnNote> entities) {
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
	public TdReturnNote findOne(Long id) {
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
	public List<TdReturnNote> findAll(Iterable<Long> ids) {
		return (List<TdReturnNote>) repository.findAll(ids);
	}

	// zhangji 修改用户名所用
	public List<TdReturnNote> findByUsername(String username) {
		return (List<TdReturnNote>) repository.findByUsername(username);
	}

	public List<TdReturnNote> findAll() {
		return (List<TdReturnNote>) repository.findAll();
	}
	
	public List<TdReturnNote> findByStatusIdAndOrderTimeBetween(Long statusId, Date start, Date end) {

		if (null == statusId || null == start || null == end) {
			return null;
		}

		return repository.findByStatusIdAndOrderTimeBetweenOrderByIdDesc(statusId, start, end);
	}
	
	public Integer countByStatusIdAndOrderTimeBetween(Long statusId, Date start, Date end) {

		if (null == statusId || null == start || null == end) {
			return null;
		}

		return repository.countByStatusIdAndOrderTimeBetweenOrderByIdDesc(statusId, start, end);
	}
	
	public List<TdReturnNote> findByStatusIdAndOrderTimeAfter(Long statusId, Date time) {

		if (null == statusId || null == time) {
			return null;
		}

		return repository.findByStatusIdAndOrderTimeAfterOrderByIdDesc(statusId, time);
	}
	
	public Integer countByStatusIdAndOrderTimeAfter(Long statusId, Date time) {

		if (null == statusId || null == time) {
			return null;
		}

		return repository.countByStatusIdAndOrderTimeAfterOrderByIdDesc(statusId, time);
	}
	

	public Page<TdReturnNote> findAll(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));

		return repository.findAll(pageRequest);
	}

	public Page<TdReturnNote> searchAll(String keywords, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));

		return repository.findByDiySiteTitleOrOrderNumberOrReturnNumberOrUsername(keywords, keywords, keywords,
				keywords, pageRequest);
	}

	/**
	 * 保存
	 * 
	 * @param e
	 * @return
	 */
	public TdReturnNote save(TdReturnNote e) {
		if (null == e) {
			return null;
		}
		return repository.save(e);
	}
	
	public void testSave(TdReturnNote note)
	{
		if(note == null)
		{
			return ;
		}
		repository.save(note);
	}

	public List<TdReturnNote> save(List<TdReturnNote> entities) {

		return (List<TdReturnNote>) repository.save(entities);
	}

	public TdReturnNote findByReturnNumber(String returnNumber) {
		if (null == returnNumber) {
			return null;
		}
		return repository.findByReturnNumber(returnNumber);

	}

}
