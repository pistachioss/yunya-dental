package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 返回预付款消费记录信息模型
 *
 * @author: WY
 * @date 2020/8/28 19:45
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回预付款消费记录信息模型")
public class PrepaidExpendRecordVo implements Serializable {

    /** 预付款消费记录id */
    @ApiModelProperty("预付款消费记录id")
    private Integer id;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 患者 */
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String consumerName;

    /** 户主 */
    @Excel(name = "户主")
    @ApiModelProperty("户主")
    private String masterCardName;

    /** 消费时间 */
    @Excel(name = "消费时间")
    @ApiModelProperty("消费时间")
    private String operatingTime;

    /** 消费本金 */
    @Excel(name = "消费本金")
    @ApiModelProperty("消费本金")
    private BigDecimal expendPrincipal;

    /** 消费赠金 */
    @Excel(name = "消费赠金")
    @ApiModelProperty("消费赠金")
    private BigDecimal expendGift;

    /** 消费卡号 */
    @Excel(name = "消费卡号")
    @ApiModelProperty("消费卡号")
    private String memberCard;

    /** 诊所 */
    @Excel(name = "诊所")
    @ApiModelProperty("诊所")
    private String orgName;

    /** 操作人id */
    @ApiModelProperty("操作人id")
    private Integer operatorId;

    /** 操作人员 */
    @Excel(name = "操作人员")
    @ApiModelProperty("操作人员")
    private String operatorName;

    /** 消费者 */
    @ApiModelProperty("消费者")
    private String expendName;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remarks;


}
