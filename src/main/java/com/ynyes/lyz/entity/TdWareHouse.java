package com.ynyes.lyz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 仓库
 * @author 华仔
 *
 */
@Entity
public class TdWareHouse {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	//仓库名称
	@Column
	private String Name;
	
	//仓库类型
	@Column
	private int Type;
	
	// 创建时间
	@Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date creatTime;
	
	// 排序号
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Double sortId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public Double getSortId() {
		return sortId;
	}

	public void setSortId(Double sortId) {
		this.sortId = sortId;
	}

	@Override
	public String toString() {
		return "TdWareHouse [id=" + id + ", Name=" + Name + ", Type=" + Type + ", creatTime=" + creatTime + ", sortId="
				+ sortId + "]";
	}
	
	
}
