package com.ynyes.lyz.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ynyes.lyz.entity.TdDiySiteInventoryLog;
import com.ynyes.lyz.repository.TdDiySiteInventoryLogRepo;

@Service
@Transactional
public class TdDiySiteInventoryLogService {

	@Autowired
	private TdDiySiteInventoryLogRepo repository;

	public TdDiySiteInventoryLog save(TdDiySiteInventoryLog e) {
		if (null == e) {
			return null;
		}
		return repository.save(e);
	}

	public void delete(Long id) {
		if (null != id) {
			repository.delete(id);
		}
	}

	public TdDiySiteInventoryLog findOne(Long id) {
		if (null == id) {
			return null;
		}
		return repository.findOne(id);
	}

	public List<TdDiySiteInventoryLog> findAll() {
		return (List<TdDiySiteInventoryLog>) repository.findAll();
	}
}
