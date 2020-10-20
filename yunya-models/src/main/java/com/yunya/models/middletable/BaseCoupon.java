package com.yunya.models.middletable;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "base_coupon")
public class BaseCoupon {
	/**
	 * 产品ID
	 */
	@Id
	@Column(name = "coupon_id")
	private Integer couponId;

	/**
	 * 产品名称
	 */
	@Column(name = "coupon_name")
	private String couponName;

	/**
	 * 产品分类ID
	 */
	@Column(name = "product_type_id")
	private Integer productTypeId;

	/**
	 * 产品分类名称
	 */
	@Column(name = "product_type_name")
	private String productTypeName;

	/**
	 * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
	 */
	@Column(name = "coupon_type")
	private Integer couponType;

	/**
	 * 创建人ID
	 */
	@Column(name = "crt_id")
	private Integer crtId;

	/**
	 * 创建时间
	 */
	@Column(name = "crt_time")
	private LocalDateTime crtTime;

	/**
	 * 售出金额（本金）
	 */
	@Column(name = "sold_amount")
	private BigDecimal soldAmount;

	/**
	 * 赠金
	 */
	private BigDecimal bonus;

	/**
	 * 补入工作量折扣率
	 */
	@Column(name = "workload_rate")
	private BigDecimal workloadRate;

}