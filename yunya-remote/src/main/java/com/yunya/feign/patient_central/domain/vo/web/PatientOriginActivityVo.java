package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/19 17:28
 * @description: 活动推荐列表结果返回模板
 * @since: 1.0.0
 */
@ApiModel(value = "activityInfoVo",description = "活动推荐列表结果返回模板")
@Data
public class PatientOriginActivityVo {

    /**
     * 活动id
     */
    @ApiModelProperty(value ="活动id",required = false)
    private Integer originId;

    /**
     * 活动名称
     */
    @Excel(name = "活动名称")
    @ApiModelProperty(value ="活动名称",required = false)
    private String activityName;

    /**
     * 患者数量
     */
    @Excel(name = "患者数量")
    @ApiModelProperty(value ="患者数量",required = false)
    private String patientNumber;

    /**
     * 已收工作量合计
     */
    @Excel(name = "已收工作量合计")
    @ApiModelProperty(value ="已收工作量合计",required = false)
    private BigDecimal receivedTotalWorkload;

    /**
     * 其中免单支付工作量合计
     */
    @Excel(name = "其中免单支付工作量合计")
    @ApiModelProperty(value ="其中免单支付工作量合计",required = false)
    private BigDecimal freeTotalWorkload;

    /**
     * 退费金额合计
     */
    @Excel(name = "退费金额合计")
    @ApiModelProperty(value ="退费金额合计",required = false)
    private BigDecimal totalRefundAmount;

    /**
     * 补入工作量合计
     */
    @Excel(name = "补入工作量合计")
    @ApiModelProperty(value ="补入工作量合计",required = false)
    private BigDecimal makeUpWorkload;


}