package com.ynyes.lyz.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 优惠券
 * 
 * @author Sharon
 *
 */

@Entity
public class TdCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 优惠券获取方式：1. 后台手动发放 2. 用户抢券 3. 退货新生成券
	@Column
	private Long typeId;

	// 优惠券限用分类类型ID: 1. 通用现金券；2. 指定商品现金券；3. 产品券；4.数据导入
	@Column
	private Long typeCategoryId;

	// 券所属公司ID
	@Column
	private Long brandId;

	// 券所属公司名称
	@Column
	private String brandTitle;

	/*
	 * 可使用商品id（针对于通用 现金券，其值为null）
	 * 
	 * @author dengxiao
	 */
	@Column
	private Long goodsId;

	/*
	 * 针对于产品券，所显示的产品的图片
	 */
	@Column
	private String picUri;

	// 可使用的商品名称
	@Column
	private String goodsName;

	// 优惠券名称
	@Column
	private String typeTitle;

	// 优惠券描述
	@Column
	private String typeDescription;

	// 优惠券图片
	@Column
	private String typePicUri;

	// 金额
	@Column(scale = 2)
	private Double price;

	// 实际使用金额
	@Column
	private Double realPrice;

	// 是否已领用
	@Column
	private Boolean isDistributted;

	// 领用日期
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date getTime;

	// 领用用户
	@Column
	private String username;

	// 是否已使用
	@Column
	private Boolean isUsed;

	// 使用日期
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date useTime;

	// 使用订单id
	@Column
	private Long orderId;

	// 使用订单号
	@Column
	private String orderNumber;

	// 是否过期
	@Column
	private Boolean isOutDate;

	// 过期日期
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireTime;

	// 剩余数量
	@Column
	private Long leftNumber;

	// 领取数量
	@Column
	private Long getNumber;

	// 手机号
	@Column
	private String mobile;

	// 排序号
	@Column
	private Double sortId;

	// 使用日期
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date addTime;
	// 门店编号
	@Column
	private String diySiteCode;
	// 门店名称
	@Column
	private String diySiteTital;
	// 城市id
	@Column
	private Long cityId;
	// 城市名
	@Column
	private String cityName;

	// 用户编号（ebs）
	@Column
	private String customerId;

	// sku
	@Column
	private String sku;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getTypeCategoryId() {
		return typeCategoryId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public void setTypeCategoryId(Long typeCategoryId) {
		this.typeCategoryId = typeCategoryId;
	}

	public String getTypeTitle() {
		return typeTitle;
	}

	public void setTypeTitle(String typeTitle) {
		this.typeTitle = typeTitle;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getTypePicUri() {
		return typePicUri;
	}

	public void setTypePicUri(String typePicUri) {
		this.typePicUri = typePicUri;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getIsDistributted() {
		return isDistributted;
	}

	public void setIsDistributted(Boolean isDistributted) {
		this.isDistributted = isDistributted;
	}

	public Date getGetTime() {
		return getTime;
	}

	public void setGetTime(Date getTime) {
		this.getTime = getTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public Date getUseTime() {
		return useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Boolean getIsOutDate() {
		return isOutDate;
	}

	public void setIsOutDate(Boolean isOutDate) {
		this.isOutDate = isOutDate;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Long getLeftNumber() {
		return leftNumber;
	}

	public void setLeftNumber(Long leftNumber) {
		this.leftNumber = leftNumber;
	}

	public Long getGetNumber() {
		return getNumber;
	}

	public void setGetNumber(Long getNumber) {
		this.getNumber = getNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Double getSortId() {
		return sortId;
	}

	public void setSortId(Double sortId) {
		this.sortId = sortId;
	}

	public String getPicUri() {
		return picUri;
	}

	public void setPicUri(String picUri) {
		this.picUri = picUri;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getBrandTitle() {
		return brandTitle;
	}

	public void setBrandTitle(String brandTitle) {
		this.brandTitle = brandTitle;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Double getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(Double realPrice) {
		this.realPrice = realPrice;
	}

	public String getDiySiteCode() {
		return diySiteCode;
	}

	public void setDiySiteCode(String diySiteCode) {
		this.diySiteCode = diySiteCode;
	}

	public String getDiySiteTital() {
		return diySiteTital;
	}

	public void setDiySiteTital(String diySiteTital) {
		this.diySiteTital = diySiteTital;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
