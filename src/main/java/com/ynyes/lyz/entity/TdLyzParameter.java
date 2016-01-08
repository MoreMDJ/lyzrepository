package com.ynyes.lyz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 商品类型<->属性关联表
 * 
 * Lyz
 * 
 * @author Sharon
 *
 */

@Entity
public class TdLyzParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	// 物料类别组合
    @Column
	private String concatenatedSegments;
    
    // 类别集名称
    @Column
    private String categorySetName;
    
    //物料id
    @Column
    private Long categoryId;
    
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConcatenatedSegments() {
		return concatenatedSegments;
	}

	public void setConcatenatedSegments(String concatenatedSegments) {
		this.concatenatedSegments = concatenatedSegments;
	}

	public String getCategorySetName() {
		return categorySetName;
	}

	public void setCategorySetName(String categorySetName) {
		this.categorySetName = categorySetName;
	}
    
    
}
