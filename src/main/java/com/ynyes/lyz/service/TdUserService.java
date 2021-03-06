package com.ynyes.lyz.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.repository.TdUserRepo;

@Service
@Transactional
public class TdUserService {

	@Autowired
	private TdUserRepo repository;

	public TdUser save(TdUser user) {
		if (null == user) {
			return null;
		}
		return repository.save(user);
	}

	public void delete(Long id) {
		if (null != id) {
			repository.delete(id);
		}
	}

	public TdUser findOne(Long id) {
		if (null == id) {
			return null;
		}
		return repository.findOne(id);
	}

	/**
	 * 按username查找，自身除外
	 * 
	 * @author Zhangji
	 * @param username
	 * @param id
	 * @return
	 */
	public TdUser findByUsernameAndIdNot(String username, Long id) {
		if (null == username || null == id) {
			return null;
		}

		return repository.findByUsernameAndIdNot(username, id);
	}

	public List<TdUser> findAll() {
		return (List<TdUser>) repository.findAll();
	}

	/**
	 * 根据账号密码查找用户
	 * 
	 * @author dengxiao
	 */
	public TdUser findByUsernameAndPasswordAndIsEnableTrue(String username, String password) {
		if (null == username || null == password) {
			return null;
		}
		return repository.findByUsernameAndPasswordAndIsEnableTrue(username, password);
	}

	/**
	 * 根据用户名查找用户
	 * 
	 * @author dengxiao
	 */
	public TdUser findByUsername(String username) {
		if (null == username) {
			return null;
		}
		return repository.findByUsername(username);
	}

	/**
	 * 根据用户名查找启用的用户
	 * 
	 * @author dengxiao
	 */
	public TdUser findByUsernameAndIsEnableTrue(String username) {
		if (null == username) {
			return null;
		}
		TdUser user = repository.findByUsernameAndIsEnableTrue(username);
		if (user != null) {
			user.setLastVisitTime(new Date());
			repository.save(user);
		}
		return repository.findByUsernameAndIsEnableTrue(username);
	}

	/**
	 * 根据用户名和城市名查找用户
	 * 
	 * @author dengxiao
	 */
	public TdUser findByUsernameAndCityNameAndIsEnableTrue(String username, String cityName) {
		if (null == username || null == cityName) {
			return null;
		}
		return repository.findByUsernameAndCityNameAndIsEnableTrue(username, cityName);
	}

	/**
	 * @author lc
	 * @注释：查找所有按id降序排序
	 */
	public Page<TdUser> findAllOrderByIdDesc(int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));

		return repository.findAll(pageRequest);
	}

	/**
	 * @author lc @注释：
	 */
	public Page<TdUser> findByUserTypeOrderByIdDesc(Long usertype, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));

		return repository.findByUserTypeOrderByIdDesc(usertype, pageRequest);
	}

	/**
	 * @author lc
	 * @注释：搜索用户
	 */
	public Page<TdUser> searchAndOrderByIdDesc(String keywords, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameContainingOrRealNameContainingOrderByIdDesc(keywords, keywords, pageRequest);
	}

	/**
	 * @author lc
	 * @注释：按等级搜索用户
	 */
	public Page<TdUser> searchAndfindByUserTypeOrderByIdDesc(String keywords, Long userType, int page, int size)
	{
		PageRequest pageRequest = new PageRequest(page, size);

		return repository.findByUsernameContainingAndUserTypeOrEmailContainingAndUserTypeOrderByIdDesc(keywords,userType, keywords, userType, pageRequest);
	}

	public TdUser findByOpUser(String opUser) {
		if (null == opUser) {
			return null;
		}
		return repository.findByOpUser(opUser);
	}

	/**
	 * 根据指定的门店查找销售顾问和店长
	 * 
	 * @author DengXiao
	 */
	public List<TdUser> findByCityIdAndCustomerIdAndUserTypeOrCityIdAndCustomerIdAndUserType(Long cityId,
			Long customerId) {
		if (null == cityId || null == customerId) {
			return null;
		}
		return repository.findByCityIdAndCustomerIdAndUserTypeOrCityIdAndCustomerIdAndUserType(cityId, customerId, 1L,
				cityId, customerId, 2L);
	}

	/**
	 * 根据关键字查找销售顾问和店长
	 * 
	 * @author DengXiao
	 */
	public List<TdUser> findByCityIdAndRealNameContainingAndUserTypeOrCityIdAndRealNameContainingAndUserType(
			Long cityId, String keywords) {
		if (null == cityId || null == keywords) {
			return null;
		}
		return repository.findByCityIdAndRealNameContainingAndUserTypeOrCityIdAndRealNameContainingAndUserType(cityId,
				keywords, 1L, cityId, keywords, 2L);
	}

	/**
	 * 根据指定的城市查找所有的销顾和店长
	 * 
	 * @author DengXiao
	 */
	public List<TdUser> findByCityIdAndUserTypeOrCityIdAndUserTypeOrderBySortIdAsc(Long cityId) {
		if (null == cityId) {
			return null;
		}
		return repository.findByCityIdAndUserTypeOrCityIdAndUserTypeOrderBySortIdAsc(cityId, 1L, cityId, 2L);
	}

	/**
	 * 查询指定门店下的所有用户
	 * 
	 * @author DengXiao
	 */
	public List<TdUser> findByCityIdAndCustomerIdAndUserTypeOrderBySortIdAsc(Long cityId, Long customerId) {
		if (null == cityId || null == customerId) {
			return null;
		}
		return repository.findByCityIdAndCustomerIdAndUserTypeOrderBySortIdAsc(cityId, customerId, 0L);
	}

	/**
	 * 根据关键词查询指定门店下的所有用户
	 * 
	 * @author DengXiao
	 */
	public List<TdUser> findByCityIdAndCustomerIdAndUserTypeAndRealNameContainingOrderBySortIdAsc(Long cityId,
			Long customerId, String keywords) {
		if (null == cityId || null == customerId || null == keywords) {
			return null;
		}
		return repository
				.findByCityIdAndCustomerIdAndUserTypeAndRealNameContainingOrCityIdAndCustomerIdAndUserTypeAndUsernameContainingOrderBySortIdAsc(
						cityId, customerId, 0L, keywords, cityId, customerId, 0L, keywords);
	}

	/**
	 * 根据真实姓名查询用户
	 * 
	 * @param realName
	 * @return
	 */
	public TdUser findByRealName(String realName) {
		if (null == realName) {
			return null;
		}
		return repository.findByRealName(realName);
	}

	/**
	 * 根据主单号查询快递员
	 * 
	 * @param mainOrderNumber
	 *            主单号
	 * @return
	 */
	public TdUser searchDriverByMainOrderNumber(String mainOrderNumber) {
		if (null == mainOrderNumber) {
			return null;
		}
		return repository.searchDriverByMainOrderNumber(mainOrderNumber);
	}

	/**
	 * @注释：根据城市查找所有按id降序排序
	 */
	public Page<TdUser> findByCityNameOrderByIdDesc(String cityName, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));

		return repository.findByCityNameOrderByIdDesc(cityName, pageRequest);
	}

	/**
	 * @注释：搜索城市下面的用户
	 */
	public Page<TdUser> searchcityNameAndOrderByIdDesc(String keywords, String cityName, int page, int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		if (StringUtils.isBlank(cityName) || StringUtils.isBlank(keywords))
		{
			return null;
		}
		return repository.findByCityNameAndUsernameContainingOrCityNameAndRealNameContainingOrderByIdDesc(cityName,keywords, cityName,keywords, pageRequest);
	}
	
	/**
	 * 根据用户类型查询用户
	 * @param userType 用户类型
	 * @return
	 */
	public List<TdUser> findByUserTypeOrderByIdDesc(Long userType){
		return repository.findByUserTypeOrderByIdDesc(userType);
	}
	
}
