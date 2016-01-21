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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 退货单
 * 
 * @author Sharon
 *
 */

@Entity
public class TdBackMain {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// 仓库编号
	@Column
	private Long c_wh_no;

	// 委托业主
	@Column
	private Boolean c_owner_no;

	// 商品名称
	@Column
	private String c_rec_no;

	// 商品标题
	@Column
	private String c_print_times;

	// 副标题
	@Column
	private String c_back_no;

	// 封面图片
	@Column
	private String c_back_type;

	// 封面图片宽度
	@Column
	private Double c_back_class;

	// 封面图片高度
	@Column
	private Double c_customer_no;

	// 视频
	@Column
	private String c_plat_no;

	// 轮播展示图片，多张图片以,隔开
	@Column
	private String c_rec_user;

	// 促销
	@Column
	private String c_op_tools;

	// 评价平均分
	@Column
	private Double c_op_status;

	// 商品配置
	@Column
	private String c_note;

	// 商品服务
	@Column
	private String c_mk_userno;

	// 配送区域
	@Column
	private String c_modified_userno;

	// 商品详情
	@Column
	private String c_po_no;

	// 上架时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date c_begin_dt;

	// 创建日期
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date c_end_dt;
	
	// 创建日期
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date c_mk_dt;
		
}
