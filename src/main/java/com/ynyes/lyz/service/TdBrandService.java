package com.ynyes.lyz.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ynyes.lyz.entity.TdBrand;
import com.ynyes.lyz.repository.TdBrandRepo;

@Service
@Transactional
public class TdBrandService {

	@Autowired
	private TdBrandRepo repository;

	public TdBrand save(TdBrand e) {
		if (null == e) {
			return e;
		}
		return repository.save(e);
	}

	public void delete(Long id) {
		if (null != id) {
			repository.delete(id);
		}
	}

	public TdBrand findOne(Long id) {
		if (null == id) {
			return null;
		}
		return repository.findOne(id);
	}
	
	public TdBrand findByShortName(String shortName)
	{
		if (shortName == null)
		{
			return null;
		}
		return repository.findByShortName(shortName);
	}

	public List<TdBrand> findAll() {
		return (List<TdBrand>) repository.findAll();
	}

}
