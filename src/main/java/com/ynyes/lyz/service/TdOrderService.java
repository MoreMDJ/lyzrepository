package com.ynyes.lyz.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.ynyes.lyz.util.Criteria;
import com.ynyes.lyz.util.Restrictions;

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

	public List<TdOrder> findByCompleteOrder() {
		return repository
				.findByStatusIdAndCashCouponIdNotNullOrStatusIdAndCashCouponIdNotNullOrStatusIdAndProductCouponIdNotNullOrStatusIdAndProductCouponIdNotNullOrderByOrderTimeDesc(
						5L, 6L, 5L, 6L);
	}

	/**
	 * 根据门店查询订单
	 * 
	 * @param diyCode
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<TdOrder> findByDiyCode(String diyCode, int page, int size) {
		if (diyCode == null) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size, new Sort(Direction.DESC, "id"));
		return repository.findByDiySiteCode(diyCode, pageRequest);
	}

	public Page<TdOrder> findByDiyCodeAndStatusIdOrderByIdDesc(String diyCode, Long statusId, Integer page,
			Integer size) {
		if (diyCode == null || statusId == null || page == null || size == null) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByDiySiteCodeAndStatusIdOrderByIdDesc(diyCode, statusId, pageRequest);
	}

	public Page<TdOrder> findByDiySiteCodeAndOrderNumberContainingOrDiySiteCodeAndUsernameContainingOrderByIdDesc(
			String diyCode, String orderNumbers, String username, int page, int size) {
		if (diyCode == null || orderNumbers == null || username == null) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByDiySiteCodeAndOrderNumberContainingOrDiySiteCodeAndUsernameContainingOrderByIdDesc(
				diyCode, orderNumbers, diyCode, username, pageRequest);
	}

	public Page<TdOrder> findByOrderNumberContainingOrUsernameContainingOrderByIdDesc(String orderNumbers,
			String username, int size, int page) {
		if (orderNumbers == null || username == null) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByOrderNumberContainingOrUsernameContainingOrderByIdDesc(orderNumbers, username,
				pageRequest);
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

	/**
	 * 根据时间查找
	 * 
	 * @return
	 */
	public List<TdOrder> findByBeginAndEndOrderByOrderTimeDesc(Date begin, Date end) {
		return repository.findByOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(begin, end);
	}

	public List<TdOrder> findByDiySiteCodeAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(String diyCode,
			Date begin, Date end) {
		return repository.findByDiySiteCodeAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(diyCode, begin, end);
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

		return repository.findDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(statusId,
				time, orderNumberList);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeAfterOrStatusIdAndOrderTimeAfter(Long statusId, Long statusId2,
			List<String> orderNumberList, Date time) {
		if (null == statusId || null == statusId2 || null == time) {
			return null;
		}

		return repository
				.findDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(
						statusId, time, orderNumberList, statusId2, time, orderNumberList);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeBetween(Long statusId, List<String> orderNumberList, Date start,
			Date end) {

		if (null == statusId || null == start || null == end || null == orderNumberList
				|| orderNumberList.size() == 0) {
			return null;
		}

		return repository.findDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(
				statusId, start, end, orderNumberList);
	}

	public List<TdOrder> findByStatusIdAndOrderTimeBetweenOrStatusIdAndOrderTimeBetween(Long statusId, Long statusId2,
			List<String> orderNumberList, Date start, Date end) {

		if (null == statusId || null == statusId2 || null == start || null == end || null == orderNumberList
				|| orderNumberList.size() == 0) {
			return null;
		}

		return repository
				.findDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(
						statusId, start, end, orderNumberList, statusId2, start, end, orderNumberList);
	}

	public Integer countByStatusIdAndOrderTimeAfter(Long statusId, Date time, List<String> orderNumberList) {
		if (null == statusId || null == time || null == orderNumberList || orderNumberList.size() == 0) {
			return null;
		}

		return repository.countDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(statusId,
				time, orderNumberList);
	}

	public Integer countByStatusIdAndOrderTimeAfterOrStatusIdAndOrderTimeAfter(Long statusId, Long statusId2,
			List<String> orderNumberList, Date time) {
		if (null == statusId || null == statusId2 || null == time) {
			return null;
		}

		return repository
				.countDistinctMainOrderNumberByStatusIdAndOrderTimeAfterAndOrderNumberInOrStatusIdAndOrderTimeAfterAndOrderNumberInOrderByIdDesc(
						statusId, time, orderNumberList, statusId2, time, orderNumberList);
	}

	public Integer countByStatusIdAndOrderTimeBetween(Long statusId, List<String> orderNumberList, Date start,
			Date end) {

		if (null == statusId || null == start || null == end || null == orderNumberList
				|| orderNumberList.size() == 0) {
			return null;
		}

		return repository.countDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(
				statusId, start, end, orderNumberList);
	}

	public Integer countByStatusIdAndOrderTimeBetweenOrStatusIdAndOrderTimeBetween(Long statusId, Long statusId2,
			List<String> orderNumberList, Date start, Date end) {

		if (null == statusId || null == statusId2 || null == start || null == end || null == orderNumberList
				|| orderNumberList.size() == 0) {
			return null;
		}

		return repository
				.countDistinctMainOrderNumberByStatusIdAndOrderTimeBetweenAndOrderNumberInOrStatusIdAndOrderTimeBetweenAndOrderNumberInOrderByIdDesc(
						statusId, start, end, orderNumberList, statusId2, start, end, orderNumberList);
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

	// 查找用户所有非删除的订单
	public Page<TdOrder> findByUsernameAndStatusIdNotOrderByOrderTimeDesc(String username, int page, int size) {
		if (null == username) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByUsernameAndStatusIdNotOrderByOrderTimeDesc(username, 8L, pageRequest);
	}

	public List<TdOrder> findByOrderNumberContaining(String orderNumber) {
		if (null == orderNumber) {
			return null;
		}
		return repository.findByOrderNumberContaining(orderNumber);
	}

	public List<TdOrder> findByMainOrderNumberIgnoreCase(String mainOrderNumber) {
		if (null == mainOrderNumber) {
			return null;
		}
		return repository.findByMainOrderNumberIgnoreCase(mainOrderNumber);
	}

	public List<TdOrder> findByStatusIdOrderByOrderTimeDesc(Long statusId) {
		if (null == statusId) {
			return null;
		}
		return repository.findByStatusIdOrderByOrderTimeDesc(statusId);
	}

	/**
	 * 根据时间查询 只查询总单号
	 * 
	 * @return
	 */
	public List<TdOrder> searchMainOrderNumberByTime(Date begin, Date end) {
		return repository.searchOrderByTime(begin, end);
	}

	/**
	 * 根据时间 配送门店 查询总单号
	 * 
	 * @return
	 */
	public List<TdOrder> searchMainOrderNumberByTimeAndDiySiteCode(String diyCode, Date begin, Date end) {
		return repository.searchMainOrderNumberByOrderTimeAndDiySiteCode(diyCode, begin, end);
	}

	/**
	 * 订单条件查询 分页
	 * 
	 * @return
	 */
	public Page<TdOrder> findAll(String keywords, String orderStartTime, String orderEndTime, String realName,
			String sellerRealName, String shippingAddress, String shippingPhone, String deliveryTime, String userPhone,
			String shippingName, String sendTime, Long statusId, String diyCode,String city, int size, int page) {
		PageRequest pageRequest = new PageRequest(page, size);
		Criteria<TdOrder> c = new Criteria<TdOrder>();
		if (null != keywords && !keywords.equalsIgnoreCase("")) {
			c.add(Restrictions.like("orderNumber", keywords, true));
		}
		if (null != orderStartTime && !orderStartTime.equals("")) {
			c.add(Restrictions.gte("orderTime", stringToDate(orderStartTime, null), true));

		}
		if (null != orderEndTime && !orderEndTime.equals("")) {
			c.add(Restrictions.lte("orderTime", stringToDate(orderEndTime, null), true));
		}

		if (null != userPhone && !"".equals(userPhone)) {
			c.add(Restrictions.like("username", userPhone, true));
		}
		if (null != shippingName && !"".equals(shippingName)) {
			c.add(Restrictions.like("shippingName", shippingName, true));
		}
		if (null != shippingPhone && !"".equals(shippingPhone)) {
			c.add(Restrictions.like("shippingPhone", shippingPhone, true));
		}
		if (null != shippingAddress && !"".equals(shippingAddress)) {
			c.add(Restrictions.like("shippingAddress", shippingAddress, true));
		}

		if (null != realName && !"".equals(realName)) {
			c.add(Restrictions.eq("username", realName, true));
		}
		if (null != deliveryTime && !deliveryTime.equals("")) {
			c.add(Restrictions.eq("realUserRealName", stringToDate(deliveryTime, null), true));
		}
		if (null != sendTime && !sendTime.equals("")) {
			c.add(Restrictions.eq("sendTime", stringToDate(sendTime, null), true));
		}
		if (null != sellerRealName && !"".equals(sellerRealName)) {
			c.add(Restrictions.eq("sellerRealName", sellerRealName, true));
		}
		if (null != statusId && !statusId.equals(0L)) {
			c.add(Restrictions.eq("statusId", statusId, true));
		}
		if (null != diyCode && !"".equals(diyCode)) {
			c.add(Restrictions.eq("diySiteCode", diyCode, true));
		}
		if (null != city && !"".equals(city)) {
			c.add(Restrictions.eq("city", city, true));
		}
		c.setOrderByDesc("orderTime");
		return repository.findAll(c, pageRequest);
	}

	/**
	 * 字符串转换时间默认格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	private Date stringToDate(String time, String dateFormat) {
		if (null == dateFormat || "".equals(dateFormat)) {
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		if (null != time && !time.equals("")) {
			try {
				date = sdf.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 查询指定归属销顾的订单
	 * 
	 * @author DengXiao
	 */
	public List<TdOrder> findBySellerIdAndStatusIdNotOrderByOrderTimeDesc(Long sellerId) {
		if (null == sellerId) {
			return null;
		}
		return repository.findBySellerIdAndStatusIdNotOrderByOrderTimeDesc(sellerId, 8L);
	}

	/**
	 * 查询指定归属销顾的订单
	 * 
	 * @author DengXiao
	 */
	public Page<TdOrder> findBySellerIdAndStatusIdNotOrderByOrderTimeDesc(Long sellerId, int page, int size) {
		if (null == sellerId) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findBySellerIdAndStatusIdNotOrderByOrderTimeDesc(sellerId, 8L, pageRequest);
	}

	/**
	 * 查询指定归属销顾的指定状态的订单
	 * 
	 * @author DengXiao
	 */
	public List<TdOrder> findBySellerIdAndStatusIdOrderByOrderTimeDesc(Long sellerId, Long statusId) {
		if (null == sellerId || null == statusId) {
			return null;
		}
		return repository.findBySellerIdAndStatusIdOrderByOrderTimeDesc(sellerId, statusId);
	}

	/**
	 * 根据门店的id查询门店下所有的订单
	 * 
	 * @author DengXiao
	 */
	public Page<TdOrder> findByDiySiteIdAndStatusIdNotOrderByOrderTimeDesc(Long diySiteId, int page, int size) {
		if (null == diySiteId) {
			return null;
		}
		PageRequest pageRequest = new PageRequest(page, size);
		return repository.findByDiySiteIdAndStatusIdNotOrderByOrderTimeDesc(diySiteId, 8L, pageRequest);
	}

	/**
	 * 根据门店id查询门店下指定状态的订单
	 * 
	 * @author DengXiao
	 */
	public List<TdOrder> findByDiySiteIdAndStatusIdOrderByOrderTimeDesc(Long diySiteId, Long statusId) {
		if (null == diySiteId || null == statusId) {
			return null;
		}
		return repository.findByDiySiteIdAndStatusIdOrderByOrderTimeDesc(diySiteId, statusId);
	}

	/**
	 * 用户模糊查找订单，参与参数：username，orderNumber
	 * 
	 * @author DengXiao
	 */
	public List<TdOrder> findByUsernameContainingAndUsernameOrOrderNumberContainingAndUsernameOrderByOrderTimeDesc(
			String keywords, String username) {
		if (null == keywords || null == username) {
			return null;
		}
		return repository.findByUsernameContainingAndUsernameOrOrderNumberContainingAndUsernameOrderByOrderTimeDesc(
				keywords, username, keywords, username);
	}

	/**
	 * 销顾模糊查询订单，参与参数：username，orderNumber
	 * 
	 * @author DengXiao
	 */
	public List<TdOrder> findByUsernameContainingAndSellerIdOrOrderNumberContainingAndSellerIdOrderByOrderTimeDesc(
			String keywords, Long sellerId) {
		if (null == keywords || null == sellerId) {
			return null;
		}
		return repository.findByUsernameContainingAndSellerIdOrOrderNumberContainingAndSellerIdOrderByOrderTimeDesc(
				keywords, sellerId, keywords, sellerId);
	}

	/**
	 * 店长模糊查询订单，参与参数：username,orderNumber
	 * 
	 * @author DengXiao
	 */
	public List<TdOrder> findByUsernameContainingAndDiySiteIdOrOrderNumberContainingAndDiySiteIdOrderByOrderTimeDesc(
			String keywords, Long diySiteId) {
		if (null == keywords || null == diySiteId) {
			return null;
		}
		return repository.findByUsernameContainingAndDiySiteIdOrOrderNumberContainingAndDiySiteIdOrderByOrderTimeDesc(
				keywords, diySiteId, keywords, diySiteId);
	}
	
	/**
	 * 根据城市名称和订单时间查询订单
	 * @return
	 */
	public List<TdOrder> findByCityAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(String city,
			Date begin, Date end) {
		return repository.findByCityAndOrderTimeAfterAndOrderTimeBeforeOrderByOrderTimeDesc(city, begin, end);
	}
	
}
