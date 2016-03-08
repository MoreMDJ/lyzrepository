package com.ynyes.lyz.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

// 退货单
@Entity
public class TdReturnNote {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 退货单编号
	@Column(unique = true)
	private String returnNumber;

	// 订单商品
	@OneToMany
	@JoinColumn(name = "tdReturnId")
	private List<TdOrderGoods> returnGoodsList;

	// 订单号
	@Column
	private String orderNumber;

	// 退货单状态 1:生成单据 2: 待取货     3: 待验货确认    4 待退款（物流确认） 5 已完成
	@Column
	private Long statusId;

	// 支付方式
	@Column
	private Long payTypeId;

	// 支付方式名称
	@Column
	private String payTypeTitle;

	// 门店id
	@Column
	private Long diySiteId;

	// 门店名称
	@Column
	private String diySiteTitle;

	// 门店地址
	@Column
	private String diySiteAddress;

	// 门店电话
	@Column
	private String diySiteTel;

	// 客户备注
	@Column
	private String remarkInfo;

	// 后台备注
	@Column
	private String managerRemarkInfo;

	// 申请用户
	@Column
	private String username;

	// 退货单下单时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date orderTime;


	// 取货时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date recvTime;
	
	// 取消时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date cancelTime;

	// 确认时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date checkTime;

	// 退款时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date returnTime;

	// 排序号
	@Column
	private Double sortId;
	
	// 退货方式 1 到店退货， 2 物流取货
	@Column
	private Long turnType;
	
	// 原订单配送方式
	@Column
	private String deliverTypeTitle;
	
	// 原订单配送地址
	@Column
	private String shoppingAddress;
	
	// 销售顾问名字
    @Column
    private String sellerRealName;
    
	// 退货金额
	@Column(scale=2)
	private Double turnPrice;
	
	// 快递员
	@Column
	private String driver;

	public Long getId() {
		return id;
	}

	public String getShoppingAddress() {
		return shoppingAddress;
	}

	public void setShoppingAddress(String shoppingAddress) {
		this.shoppingAddress = shoppingAddress;
	}

	public String getSellerRealName() {
		return sellerRealName;
	}

	public void setSellerRealName(String sellerRealName) {
		this.sellerRealName = sellerRealName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReturnNumber() {
		return returnNumber;
	}

	public void setReturnNumber(String returnNumber) {
		this.returnNumber = returnNumber;
	}

	public List<TdOrderGoods> getReturnGoodsList() {
		return returnGoodsList;
	}

	public void setReturnGoodsList(List<TdOrderGoods> returnGoodsList) {
		this.returnGoodsList = returnGoodsList;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public Long getPayTypeId() {
		return payTypeId;
	}

	public void setPayTypeId(Long payTypeId) {
		this.payTypeId = payTypeId;
	}

	public String getPayTypeTitle() {
		return payTypeTitle;
	}

	public void setPayTypeTitle(String payTypeTitle) {
		this.payTypeTitle = payTypeTitle;
	}

	public Long getDiySiteId() {
		return diySiteId;
	}

	public void setDiySiteId(Long diySiteId) {
		this.diySiteId = diySiteId;
	}

	public String getDiySiteTitle() {
		return diySiteTitle;
	}

	public void setDiySiteTitle(String diySiteTitle) {
		this.diySiteTitle = diySiteTitle;
	}

	public String getDiySiteAddress() {
		return diySiteAddress;
	}

	public void setDiySiteAddress(String diySiteAddress) {
		this.diySiteAddress = diySiteAddress;
	}

	public String getDiySiteTel() {
		return diySiteTel;
	}

	public void setDiySiteTel(String diySiteTel) {
		this.diySiteTel = diySiteTel;
	}

	public String getRemarkInfo() {
		return remarkInfo;
	}

	public void setRemarkInfo(String remarkInfo) {
		this.remarkInfo = remarkInfo;
	}

	public String getManagerRemarkInfo() {
		return managerRemarkInfo;
	}

	public void setManagerRemarkInfo(String managerRemarkInfo) {
		this.managerRemarkInfo = managerRemarkInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public Date getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(Date returnTime) {
		this.returnTime = returnTime;
	}

	public Double getSortId() {
		return sortId;
	}

	public void setSortId(Double sortId) {
		this.sortId = sortId;
	}

	public Long getTurnType() {
		return turnType;
	}

	public void setTurnType(Long turnType) {
		this.turnType = turnType;
	}

	public String getDeliverTypeTitle() {
		return deliverTypeTitle;
	}

	public void setDeliverTypeTitle(String deliverTypeTitle) {
		this.deliverTypeTitle = deliverTypeTitle;
	}

	public Double getTurnPrice() {
		return turnPrice;
	}

	public void setTurnPrice(Double turnPrice) {
		this.turnPrice = turnPrice;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Date getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(Date recvTime) {
		this.recvTime = recvTime;
	}

	@Override
	public String toString() {
		return "TdReturnNote [id=" + id + ", returnNumber=" + returnNumber + ", returnGoodsList=" + returnGoodsList
				+ ", orderNumber=" + orderNumber + ", statusId=" + statusId + ", payTypeId=" + payTypeId
				+ ", payTypeTitle=" + payTypeTitle + ", diySiteId=" + diySiteId + ", diySiteTitle=" + diySiteTitle
				+ ", diySiteAddress=" + diySiteAddress + ", diySiteTel=" + diySiteTel + ", remarkInfo=" + remarkInfo
				+ ", managerRemarkInfo=" + managerRemarkInfo + ", username=" + username + ", orderTime=" + orderTime
				+ ", cancelTime=" + cancelTime + ", checkTime=" + checkTime + ", returnTime=" + returnTime + ", sortId="
				+ sortId + ", turnType=" + turnType + ", deliverTypeTitle=" + deliverTypeTitle + ", turnPrice="
				+ turnPrice + "]";
	}
}
