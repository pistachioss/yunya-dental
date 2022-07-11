package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简介：艾芽卡or365卡系列VO
 *
 * @author: chenlin
 * @Description: 艾芽卡or365卡系列VO
 * @Date: 2022/7/7 10:27
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("艾芽卡or365卡系列VO")
public class CardIyOr365VO implements Serializable {

    /** 卡id */
    @ApiModelProperty("卡id")
    private Integer id;

    /** 卡券id */
    @ApiModelProperty("卡券id")
    private Integer couponId;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 上次发送短信的时间，为空则以激活时间填充 */
    @ApiModelProperty("上次发送短信的时间，为空则以激活时间填充")
    private String lastSendDate;

    /** 激活时间 */
    @ApiModelProperty("激活时间")
    private LocalDateTime activeDate;

    /** 产品有效期 */
    @ApiModelProperty("产品有效期")
    private LocalDateTime activationDeadline;

    /** 激活后有效期（天） */
    @ApiModelProperty("激活后有效期（天）")
    private Integer effectiveDays;

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 售出手机号 */
    @ApiModelProperty("售出手机号")
    private String soldPhoneNumber;

    /** 售出对象 */
    @ApiModelProperty("售出对象")
    private String soldTarget;

    /** 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券） */
    @ApiModelProperty("卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
    private Byte type;

    /** 售出对象 */
    @ApiModelProperty("卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
    private String name;

    private String cron;

}
