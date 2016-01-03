package com.ynyes.lyz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 价目表项实体类
 * 
 * @author dengxiao
 */
@Entity
public class TdPriceListItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 价目表编号
	@Column
	private Long priceListId;

	// 价目表名称
	@Column
	private String priceListName;

	// 所属区域id
	@Column
	private Long regionId;

	// 所属区域城市名称
	@Column
	private String cityName;

	// 所属价目表编号 zhangji
	@Column
	private String priceListNumber;

	// 所属分公司名称
	@Column
	private String companyName;

	// 所属分公司id
	@Column
	private Long companyId;

	// 商品id
	@Column
	private Long goodsId;

	// 商品名称 zhangji
	@Column
	private String goodsTitle;

	// 该件商品的虚拟销售价
	@Column(scale = 2)
	private Double salePrice;

	// 该件商品名的实际销售价
	@Column(scale = 2)
	private Double realSalePrice;

	// 该件商品的虚拟进货价
	@Column(scale = 2)
	private Double stockPrice;

	// 该件商品的实际进货价
	@Column(scale = 2)
	private Double realStockPrice;

	// 发货地点
	@Column
	private String dispatch;

	// 该商品在本地区是否参与促销
	@Column
	private Boolean isPromotion;

	// 该件商品是否首页推荐
	@Column
	private Boolean isCommendIndex;

	// 是否过期
	@Column
	private Boolean isOutDate;

	// 参加的活动id（多个以","隔开）
	@Column
	private String activities;

	// 排序号
	@Column
	private Double sortId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPriceListId() {
		return priceListId;
	}

	public void setPriceListId(Long priceListId) {
		this.priceListId = priceListId;
	}

	public String getPriceListName() {
		return priceListName;
	}

	public void setPriceListName(String priceListName) {
		this.priceListName = priceListName;
	}

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public Double getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(Double stockPrice) {
		this.stockPrice = stockPrice;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public Boolean getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public Boolean getIsCommendIndex() {
		return isCommendIndex;
	}

	public void setIsCommendIndex(Boolean isCommendIndex) {
		this.isCommendIndex = isCommendIndex;
	}

	public Double getSortId() {
		return sortId;
	}

	public void setSortId(Double sortId) {
		this.sortId = sortId;
	}

	public Double getRealSalePrice() {
		return realSalePrice;
	}

	public void setRealSalePrice(Double realSalePrice) {
		this.realSalePrice = realSalePrice;
	}

	public Double getRealStockPrice() {
		return realStockPrice;
	}

	public void setRealStockPrice(Double realStockPrice) {
		this.realStockPrice = realStockPrice;
	}

	public Boolean getIsOutDate() {
		return isOutDate;
	}

	public void setIsOutDate(Boolean isOutDate) {
		this.isOutDate = isOutDate;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getPriceListNumber() {
		return priceListNumber;
	}

	public void setPriceListNumber(String priceListNumber) {
		this.priceListNumber = priceListNumber;
	}

	public String getActivities() {
		return activities;
	}

	public void setActivities(String activities) {
		this.activities = activities;
	}
}