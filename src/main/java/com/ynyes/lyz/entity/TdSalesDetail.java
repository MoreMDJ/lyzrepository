package com.ynyes.lyz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 销售明细报表
 * @author zp
 *
 */
@Entity
public class TdSalesDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	//门店名称
	@Column
	private String diySiteName;
	//主单号
	@Column
	private String mainOrderNumber;
	//分单号
	@Column
	private String orderNumber;
	//下单时间
	@Column
	private Date orderTime;
	//订单状态
	@Column
	private Long statusId;
	//会员电话
	@Column
	private String username;
	//客户名称
	@Column
	private String realName;
	//收货人
	@Column
	private String shippingName;
	//产品编号
	@Column
	private String sku;
	//产品名称
	@Column
	private String goodsTitle;
	//数量
	@Column
	private Long quantity;
	//单价
	@Column
	private Double price;
	//总价
	@Column
	private Double totoalPrice;
	//使用可提现金额
	@Column
	private Double cashBalanceUsed;
	//使用不可体现金额
	@Column
	private Double unCashBalanceUsed;
	//备注
	@Column
	private String remark;
	//中转仓
	@Column
	private String whNo;
	//配送人员
	@Column
	private String deliverRealName;
	//配送人员电话
	@Column
	private String deliverUsername;
	//导购姓名
	@Column
	private String sellerRealName;
	//商品类型
	@Column
	private String title;
	//配送方式
	@Column
	private String deliverTypeTitle;
	//收货人地址
	@Column
	private String shippingAddress;
	// 城市
	@Column
	private String city;
	// 配送门店id
	@Column
	private String diySiteCode;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDiySiteName() {
		return diySiteName;
	}
	public void setDiySiteName(String diySiteName) {
		this.diySiteName = diySiteName;
	}
	public String getMainOrderNumber() {
		return mainOrderNumber;
	}
	public void setMainOrderNumber(String mainOrderNumber) {
		this.mainOrderNumber = mainOrderNumber;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getGoodsTitle() {
		return goodsTitle;
	}
	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getTotoalPrice() {
		return totoalPrice;
	}
	public void setTotoalPrice(Double totoalPrice) {
		this.totoalPrice = totoalPrice;
	}
	public Double getCashBalanceUsed() {
		return cashBalanceUsed;
	}
	public void setCashBalanceUsed(Double cashBalanceUsed) {
		this.cashBalanceUsed = cashBalanceUsed;
	}
	public Double getUnCashBalanceUsed() {
		return unCashBalanceUsed;
	}
	public void setUnCashBalanceUsed(Double unCashBalanceUsed) {
		this.unCashBalanceUsed = unCashBalanceUsed;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getWhNo() {
		return whNo;
	}
	public void setWhNo(String whNo) {
		this.whNo = whNo;
	}
	public String getDeliverRealName() {
		return deliverRealName;
	}
	public void setDeliverRealName(String deliverRealName) {
		this.deliverRealName = deliverRealName;
	}
	public String getDeliverUsername() {
		return deliverUsername;
	}
	public void setDeliverUsername(String deliverUsername) {
		this.deliverUsername = deliverUsername;
	}
	public String getSellerRealName() {
		return sellerRealName;
	}
	public void setSellerRealName(String sellerRealName) {
		this.sellerRealName = sellerRealName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDeliverTypeTitle() {
		return deliverTypeTitle;
	}
	public void setDeliverTypeTitle(String deliverTypeTitle) {
		this.deliverTypeTitle = deliverTypeTitle;
	}
	public String getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDiySiteCode() {
		return diySiteCode;
	}
	public void setDiySiteCode(String diySiteCode) {
		this.diySiteCode = diySiteCode;
	}
	public String getShippingName() {
		return shippingName;
	}
	public void setShippingName(String shippingName) {
		this.shippingName = shippingName;
	}
	
	
}
