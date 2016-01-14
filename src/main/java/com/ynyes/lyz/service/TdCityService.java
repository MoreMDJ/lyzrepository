package com.ynyes.lyz.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.ynyes.lyz.entity.TdCity;
import com.ynyes.lyz.repository.TdCityRepo;

@Service
@Transactional
public class TdCityService {

	@Autowired
	private TdCityRepo repository;

	public TdCity save(TdCity entity) {
		if (null == entity) {
			return null;
		}
		return repository.save(entity);
	}

	public void delete(Long id) {
		if (null != id) {
			repository.delete(id);
		}
	}

	public TdCity findOne(Long id) {
		if (null == id) {
			return null;
		}
		return repository.findOne(id);
	}

	public List<TdCity> findAll() {
		return (List<TdCity>) repository.findAll();
	}

	/**
	 * 根据城市名称查询到地区实体
	 * 
	 * @author dengxiao
	 */
	public TdCity findByCityName(String cityName) {
		if (null == cityName) {
			return null;
		}
		return repository.findByCityName(cityName);
	}

	public Page<TdCity> findAll(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "sortId"));

		return repository.findAll(pageRequest);
	}

	public TdCity findBySobIdCity(Long sobIdCity) {
		if (null == sobIdCity) {
			return null;
		}
		return repository.findBySobIdCity(sobIdCity);
	}
}
