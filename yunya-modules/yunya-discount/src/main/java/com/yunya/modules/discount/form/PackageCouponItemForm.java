package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className PackageCouponItemForm
 * @description
 * @date 2020/8/26 16:17
 */
@Data
public class PackageCouponItemForm {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 优惠券id
     */
    @NotNull(message = "优惠券id不能为空")
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    /**
     * 类型 0:基础价目表,1:基础商品表
     */
    @NotNull(message = "项目类型不能为空")
    @ApiModelProperty("项目类型  0:基础价目表,1:基础商品表")
    private Integer type;

    /**
     * 明细ID
     */
    @NotNull(message = "明细ID不能为空")
    @ApiModelProperty("明细ID")
    private Integer itemId;

    /**
     * 售出单价
     */
    @ApiModelProperty("售出单价")
    @NotNull(message = "售出单价不能为空")
    private BigDecimal saleUnitPrice;

    /**
     * 售出金额
     */
    @ApiModelProperty("售出金额")
    @NotNull(message = "售出金额不能为空")
    private BigDecimal saleAmount;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    @NotNull(message = "数量不能为空")
    private Integer count;

    /**
     * 单个明细工作量
     */
    @ApiModelProperty("单个明细工作量")
    @NotNull(message = "单个明细工作量不能为空")
    private Integer workloadRate;

    /**
     * 创建人
     */
    private Integer crtId;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人
     */
    private Integer updId;

    /**
     * 更新时间
     */
    private Date updTime;
}
