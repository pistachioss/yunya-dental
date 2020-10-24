package com.yunya.models.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "base_coupon_item")
public class BaseCouponItem {
	/**
	 * 产品ID
	 */
	@Column(name = "coupon_id")
	private Integer couponId;

	/**
	 * 明细类型(0-价目、1-商品)
	 */
	private Integer type;

	/**
	 * 明细选择范围(0全部、１分类、２项目)
	 */
	@Column(name = "choice_rang_type")
	private Integer choiceRangType;

	/**
	 * 对应明细ID(分类、项目)
	 */
	@Column(name = "item_id")
	private Integer itemId;

	/**
	 * 售出单价
	 */
	@Column(name = "sale_unit_price")
	private BigDecimal saleUnitPrice;

	/**
	 * 数量
	 */
	private Integer quantity;

	/**
	 * 单个工作量
	 */
	@Column(name = "workload_load")
	private BigDecimal workloadLoad;

}