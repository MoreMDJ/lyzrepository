package com.ynyes.lyz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 充值/提现记录实体类
 * 
 * @author dengxiao
 */

@Entity
public class TdBalanceLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 所属用户
	@Column
	private Long userId;
	
	// 所属用户名
	@Column
	private String username;
	
	//金额 变化金额
	@Column(scale = 2)
	private Double money;

	// 类型（0. 代表充值；1. 代表提现; 2. 管理员修改）
	@Column
	private Long type;

	// 生成时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	// 完成时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date finishTime;

	// 是否成功
	@Column
	private Boolean isSuccess;

	// 未成功的原因
	@Column
	private String reason;

	// 成功后的用户余额
	@Column(scale = 2)
	private Double balance;
	
	// 管理员改变预存款的类型(0: balance 1: cashBalance 2:unCashBalance）
	@Column
	private Long balanceType;
	
	// 操作人员
	@Column
	private String operator;
	
	// ip
	@Column
	private String operatorIp;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperatorIp() {
		return operatorIp;
	}

	public void setOperatorIp(String operatorIp) {
		this.operatorIp = operatorIp;
	}

	public Long getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(Long balanceType) {
		this.balanceType = balanceType;
	}
	
}
