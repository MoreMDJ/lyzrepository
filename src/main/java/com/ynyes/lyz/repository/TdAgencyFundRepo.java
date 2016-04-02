package com.ynyes.lyz.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ynyes.lyz.entity.TdAgencyFund;

/**
 * TdAgencyFund 虚拟数据库操作接口
 * 
 * @author Sharon
 *
 */

public interface TdAgencyFundRepo extends PagingAndSortingRepository<TdAgencyFund, Long>, JpaSpecificationExecutor<TdAgencyFund> {
	
	/**
	 * 根据订单时间查询代收款报表数据
	 * @return
	 */
	@Query(value = "select o.id,o.diy_site_name,o.diy_site_phone,o.main_order_number,o.order_time,o.cash_balance_used,o.un_cash_balance_used,if(omr.order_number is null,o.total_price,omr.payed+omr.owned) as pay_price,omr.payed,omr.owned,u.real_name,u.username,o.shipping_name,o.shipping_phone,o.shipping_address,o.remark,o.cash_coupon,o.status_id,di.wh_no,o.total_price,o.delivery_date,o.delivery_detail_id,o.delivery_time"
			+ " from td_order o "
			+ " left JOIN td_own_money_record omr on omr.order_number=o.main_order_number"
			+ " LEFT JOIN td_delivery_info_detail did on did.sub_order_number=o.order_number"
			+ " left JOIN td_delivery_info di on did.task_no = di.task_no"
			+ " left JOIN td_user u on u.op_user=did.op_user"
			+ " where o.main_order_number is not null and order_time>=?1 and order_time<=?2"
//			+ " GROUP BY o.main_order_number"
			,nativeQuery = true)
	List<TdAgencyFund> searchAllByTime(Date start,Date end);
	
	/**
	 * 根据订单时间,门店查询代收款报表数据
	 * @return
	 */
	@Query(value = "select o.id,o.diy_site_name,o.diy_site_phone,o.main_order_number,o.order_time,o.cash_balance_used,o.un_cash_balance_used,if(omr.order_number is null,o.total_price,omr.payed+omr.owned) as pay_price,omr.payed,omr.owned,u.real_name,u.username,o.shipping_name,o.shipping_phone,o.shipping_address,o.remark,o.cash_coupon,o.status_id,di.wh_no,o.total_price,o.delivery_date,o.delivery_detail_id,o.delivery_time"
			+ " from td_order o "
			+ " left JOIN td_own_money_record omr on omr.order_number=o.main_order_number"
			+ " LEFT JOIN td_delivery_info_detail did on did.sub_order_number=o.order_number"
			+ " left JOIN td_delivery_info di on did.task_no = di.task_no"
			+ " left JOIN td_user u on u.op_user=did.op_user"
			+ " where   o.main_order_number is not null and"
			+ " o.diy_site_code = ?1 and order_time>=?2 and order_time<=?3"
			+ " GROUP BY o.main_order_number",nativeQuery = true)
	List<TdAgencyFund> searchAllbyDiyCodeAndTime(String diyCode,Date start,Date end);
	
	/**
	 * 根据订单时间,城市查询代收款报表数据
	 * @return
	 */
	@Query(value = "select o.id,o.diy_site_name,o.diy_site_phone,o.main_order_number,o.order_time,o.cash_balance_used,o.un_cash_balance_used,if(omr.order_number is null,o.total_price,omr.payed+omr.owned) as pay_price,omr.payed,omr.owned,u.real_name,u.username,o.shipping_name,o.shipping_phone,o.shipping_address,o.remark,o.cash_coupon,o.status_id,di.wh_no,o.total_price,o.delivery_date,o.delivery_detail_id,o.delivery_time"
			+ " from td_order o "
			+ " left JOIN td_own_money_record omr on omr.order_number=o.main_order_number"
			+ " LEFT JOIN td_delivery_info_detail did on did.sub_order_number=o.order_number"
			+ " left JOIN td_delivery_info di on did.task_no = di.task_no"
			+ " left JOIN td_user u on u.op_user=did.op_user"
			+ " where   o.main_order_number is not null and"
			+ " o.city = ?1 and order_time>=?2 and order_time<=?3"
			+ " GROUP BY o.main_order_number",nativeQuery = true)
	List<TdAgencyFund> searchAllbyCityAndTime(String city,Date start,Date end);
	
	/**
	 * 调用存储过程
	 * @return
	 */
	@Query(value = "{call insertAgencyFund_initial(?1,?2)}",nativeQuery = true)
	void callInsertAgencyFund(Date start,Date end);
	
}
