package com.ynyes.lyz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 门店库存核减记录表
 * 
 * @author DengXiao
 */

@Entity
public class TdDiySiteInventoryLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 门店名称
	@Column
	private String diySiteTitle;

	// 门店城市编号
	@Column
	private String diySiteSobId;

	// 门店id
	@Column
	private Long diySiteId;

	// 商品id
	@Column
	private Long goodsId;

	// 商品名称
	@Column
	private String goodsTitle;

	// 商品SKU
	@Column
	private String goodsSku;

	// 变更时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date changeDate;

	// 描述
	@Column(length = 255)
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDiySiteTitle() {
		return diySiteTitle;
	}

	public void setDiySiteTitle(String diySiteTitle) {
		this.diySiteTitle = diySiteTitle;
	}

	public String getDiySiteSobId() {
		return diySiteSobId;
	}

	public void setDiySiteSobId(String diySiteSobId) {
		this.diySiteSobId = diySiteSobId;
	}

	public Long getDiySiteId() {
		return diySiteId;
	}

	public void setDiySiteId(Long diySiteId) {
		this.diySiteId = diySiteId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getGoodsSku() {
		return goodsSku;
	}

	public void setGoodsSku(String goodsSku) {
		this.goodsSku = goodsSku;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "TdDiySiteInventoryLog [id=" + id + ", diySiteTitle=" + diySiteTitle + ", diySiteSobId=" + diySiteSobId
				+ ", diySiteId=" + diySiteId + ", goodsId=" + goodsId + ", goodsTitle=" + goodsTitle + ", goodsSku="
				+ goodsSku + ", changeDate=" + changeDate + ", description=" + description + "]";
	}
}
