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
	@Query("select o.diySiteName,o.diySitePhone,o.mainOrderNumber,o.orderTime,o.cashBalanceUsed,o.unCashBalanceUsed,"
			+" if(omr.orderNumber is null,o.totalPrice,omr.payed+omr.owned) as payPrice,omr.payed,omr.owned,"
			+" u.realName,u.username,o.shippingName,o.shippingPhone,o.shippingAddress,o.remark,o.cashCoupon,"
			+" o.statusId,di.whNo,o.totalPrice,o.deliveryDate,o.deliveryDetailId,o.deliveryTime"
	 +" from TdOrder o "
	 +" left JOIN TdOwnMoneyRecord omr on omr.orderNumber=o.mainOrderNumber"
	 +" LEFT JOIN TdDeliveryInfoDetail did on did.subOrderNumber=o.orderNumber"
	 +" left JOIN TdDeliveryInfo di on did.taskNo = di.taskNo"
	 +" left JOIN TdUser u on u.opUser=did.opUser"
	 +" where   o.mainOrderNumber is not null and order_time>=?1 and order_time<=?2"
	 +" GROUP BY o.mainOrderNumber")
	List<TdAgencyFund> searchAllByTime(Date start,Date end);
	
	/**
	 * 根据订单时间,门店查询代收款报表数据
	 * @return
	 */
	@Query("select o.diySiteName,o.diySitePhone,o.mainOrderNumber,o.orderTime,o.cashBalanceUsed,o.unCashBalanceUsed,"
			+" if(omr.orderNumber is null,o.totalPrice,omr.payed+omr.owned) as payPrice,omr.payed,omr.owned,"
			+" u.realName,u.username,o.shippingName,o.shippingPhone,o.shippingAddress,o.remark,o.cashCoupon,"
			+" o.statusId,di.whNo,o.totalPrice,o.deliveryDate,o.deliveryDetailId,o.deliveryTime"
	 +" from TdOrder o "
	 +" left JOIN TdOwnMoneyRecord omr on omr.orderNumber=o.mainOrderNumber"
	 +" LEFT JOIN TdDeliveryInfoDetail did on did.subOrderNumber=o.orderNumber"
	 +" left JOIN TdDeliveryInfo di on did.taskNo = di.taskNo"
	 +" left JOIN TdUser u on u.opUser=did.opUser"
	 +" where   o.mainOrderNumber is not null and o.diySiteCode = ?1 and order_time>=?2 and order_time<=?3"
	 +" GROUP BY o.mainOrderNumber")
	List<TdAgencyFund> searchAllbyDiyCodeAndTime(String diyCode,Date start,Date end);
	
}
