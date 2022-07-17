package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介：卡券公用信息VO
 *
 * @author: chenlin
 * @Description: 卡券公用信息VO
 * @Date: 2022/7/12 14:19
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("卡券公用信息VO")
public class CouponCommonInfoVO implements Serializable {
    /** 卡券id */
    private Integer id;

    /** 卡券名称*/
    @ApiModelProperty("卡券名称")
    private String name;

    /** 产品分类id */
    @ApiModelProperty("产品分类id")
    private Integer productTypeId;

    /** 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券） */
    @ApiModelProperty("卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
    private Byte type;

    /** 产品编号 */
    @ApiModelProperty("产品编号")
    private String couponCode;

    /** 是否制作实体卡*/
    @ApiModelProperty("是否制作实体卡")
    private Boolean isMakePhysicalCard;

    /** 售出金额 */
    @ApiModelProperty("售出金额")
    private BigDecimal soldAmount;

    /** 可售开始时间 */
    @ApiModelProperty("可售开始时间")
    private Date availableSaleStartDate;

    /** 可售截止时间 */
    @ApiModelProperty("可售截止时间")
    private Date availableSaleEndDate;
}
