package com.yunya.feign.discount.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DeductionItemPeriodVO {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 卡券ID
     */
    private Integer couponId;

    /**
     * 类型 0:基础价目表,1:基础商品表 2:选项目明细
     */
    private Integer type;

    /**
     * 明细ID
     */
    private Integer itemId;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 套餐单价
     */
    private BigDecimal packageUnitPrice;

    /**
     * 套餐价
     */
    private BigDecimal saleAmount;

    /**
     * 单个明细工作量
     */
    private BigDecimal workloadLoad;
    /**
     * 套餐券匹配项目小程序是否显示 0否 1是
     */
    private Integer isShowApp;
    /**
     * 创建人
     */
    private Integer crtId;

    /**
     * 创建时间
     */
    private Date crtTime;

}