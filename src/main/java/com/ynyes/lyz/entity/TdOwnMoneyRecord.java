package com.ynyes.lyz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * 欠款记录
 * 
 * @author Sharon
 *
 */

@Entity
public class TdOwnMoneyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
    // 用户名
    @Column
    private String username;
    
    // 订单号
    @Column
    private String orderNumber;
    
    // 已付
    @Column
    private Double payed;
    
    // 欠款
    @Column
    private Double owned;
    
    // 创建时间
    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    // 排序号
    @Column
    private Long sortId;

    // 是否审批通过
    @Column
    private Boolean isEnable;
    
    // 是否还清
    @Column
    private Boolean isPayed;
    
    // 门店编码
    @Column
    private String diyCode;
    
    // 是否审核 0:不同过，1：通过
    @Column
    private Boolean ispassed;
    

	public Boolean getIspassed() {
		return ispassed;
	}

	public void setIspassed(Boolean ispassed) {
		this.ispassed = ispassed;
	}

	public String getDiyCode() {
		return diyCode;
	}

	public void setDiyCode(String diyCode) {
		this.diyCode = diyCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Double getPayed() {
		return payed;
	}

	public void setPayed(Double payed) {
		this.payed = payed;
	}

	public Double getOwned() {
		return owned;
	}

	public void setOwned(Double owned) {
		this.owned = owned;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getSortId() {
		return sortId;
	}

	public void setSortId(Long sortId) {
		this.sortId = sortId;
	}

	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public Boolean getIsPayed() {
		return isPayed;
	}

	public void setIsPayed(Boolean isPayed) {
		this.isPayed = isPayed;
	}
	
}
