package com.ynyes.lyz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 订单商品
 *
 * 记录了订单商品的相关信息
 * 
 * @author Sharon
 *
 */

@Entity
public class TdOrderGoods {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 商品ID
	@Column
	private Long goodsId;

	// 商品名称
	@Column
	private String goodsTitle;

	// 商品简介
	@Column
	private String goodsSubTitle;

	// 商品封面
	@Column
	private String goodsCoverImageUri;

	// 商品的SKU
	@Column
	private String sku;

	// 商品版本颜色
	@Column
	private String goodsColor;

	// 商品版本容量
	@Column
	private String goodsCapacity;

	// 商品版本名称
	@Column
	private String goodsVersion;

	// 商品销售方式
	@Column
	private Integer goodsSaleType;

	// 成交价
	@Column(scale = 2)
	private Double price;

	// 真实价格
	@Column
	private Double realPrice;

	// 商品数量
	@Column
	private Long quantity;

	// 发货数量
	@Column
	private Long deliveredQuantity;

	// 积分
	@Column
	private Long points;

	// 是否申请了退还该商品？
	@Column
	private Boolean isReturnApplied;

	// 是否已评价
	@Column
	private Boolean isCommented;

	// 评论ID
	@Column
	private Long commentId;

	// 商品品牌标题
	@Column
	private String brandTitle;

	// 商品的品牌id
	@Column
	private Long brandId;

	// 可退数量
	@Column
	private Long canReturnNumber;

	// 退款单价
	@Column
	private Double returnUnitPrice;

	// 使用产品券的数量，默认为0
	@Column
	private Long couponNumber;

	// 退货数量
	@Column
	private Long returnNumber;
	
	public Boolean getIsCommented() {
		return isCommented;
	}

	public void setIsCommented(Boolean isCommented) {
		this.isCommented = isCommented;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getGoodsSubTitle() {
		return goodsSubTitle;
	}

	public void setGoodsSubTitle(String goodsSubTitle) {
		this.goodsSubTitle = goodsSubTitle;
	}

	public String getGoodsCoverImageUri() {
		return goodsCoverImageUri;
	}

	public void setGoodsCoverImageUri(String goodsCoverImageUri) {
		this.goodsCoverImageUri = goodsCoverImageUri;
	}

	public String getGoodsColor() {
		return goodsColor;
	}

	public void setGoodsColor(String goodsColor) {
		this.goodsColor = goodsColor;
	}

	public String getGoodsCapacity() {
		return goodsCapacity;
	}

	public void setGoodsCapacity(String goodsCapacity) {
		this.goodsCapacity = goodsCapacity;
	}

	public String getGoodsVersion() {
		return goodsVersion;
	}

	public void setGoodsVersion(String goodsVersion) {
		this.goodsVersion = goodsVersion;
	}

	public Integer getGoodsSaleType() {
		return goodsSaleType;
	}

	public void setGoodsSaleType(Integer goodsSaleType) {
		this.goodsSaleType = goodsSaleType;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getDeliveredQuantity() {
		return deliveredQuantity;
	}

	public void setDeliveredQuantity(Long deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}

	public Boolean getIsReturnApplied() {
		return isReturnApplied;
	}

	public void setIsReturnApplied(Boolean isReturnApplied) {
		this.isReturnApplied = isReturnApplied;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getBrandTitle() {
		return brandTitle;
	}

	public void setBrandTitle(String brandTitle) {
		this.brandTitle = brandTitle;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public Double getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(Double realPrice) {
		this.realPrice = realPrice;
	}

	public Long getCouponNumber() {
		return couponNumber;
	}

	public void setCouponNumber(Long couponNumber) {
		this.couponNumber = couponNumber;
	}

	public Long getCanReturnNumber() {
		return canReturnNumber;
	}

	public void setCanReturnNumber(Long canReturnNumber) {
		this.canReturnNumber = canReturnNumber;
	}

	public Double getReturnUnitPrice() {
		return returnUnitPrice;
	}

	public void setReturnUnitPrice(Double returnUnitPrice) {
		this.returnUnitPrice = returnUnitPrice;
	}

	public Long getReturnNumber() {
		return returnNumber;
	}

	public void setReturnNumber(Long returnNumber) {
		this.returnNumber = returnNumber;
	}

	@Override
	public String toString() {
		return "TdOrderGoods [id=" + id + ", goodsId=" + goodsId + ", goodsTitle=" + goodsTitle + ", goodsSubTitle="
				+ goodsSubTitle + ", goodsCoverImageUri=" + goodsCoverImageUri + ", sku=" + sku + ", goodsColor="
				+ goodsColor + ", goodsCapacity=" + goodsCapacity + ", goodsVersion=" + goodsVersion
				+ ", goodsSaleType=" + goodsSaleType + ", price=" + price + ", realPrice=" + realPrice + ", quantity="
				+ quantity + ", deliveredQuantity=" + deliveredQuantity + ", points=" + points + ", isReturnApplied="
				+ isReturnApplied + ", isCommented=" + isCommented + ", commentId=" + commentId + ", brandTitle="
				+ brandTitle + ", brandId=" + brandId + ", canReturnNumber=" + canReturnNumber + ", returnUnitPrice="
				+ returnUnitPrice + ", couponNumber=" + couponNumber + ", returnNumber=" + returnNumber + "]";
	}

}
