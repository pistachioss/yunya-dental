package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2023/9/21 13:53
 * @description: 待同步账单数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("待同步账单数据模型")
public class Wait4UploadTreatmentVO implements Serializable {
    
    /** 全程就诊记录id */
    @ApiModelProperty("全程就诊记录id")
    private Integer qcTreatmentId;

    /** 账单id */
    @ApiModelProperty("账单id")
    private Integer billId;
    
    /** 账单日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty("账单日期")
    private Date billDate;

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 患者手机号 */
    @ApiModelProperty("患者手机号")
    private String mobile;

    /** 关联全程登记单号 */
    @ApiModelProperty("关联全程登记单号")
    private String admNo;

    /** 账单总额 */
    @ApiModelProperty("账单总额")
    private BigDecimal receivedTotalAmount;

    /** 执行医嘱 */
    @ApiModelProperty("执行医嘱")
    private BigDecimal adviceItemExecAmount;

    /** 未执行医嘱 */
    @ApiModelProperty("未执行医嘱")
    private BigDecimal unAdviceItemExecAmount;

    /** 门诊实收 */
    @ApiModelProperty("门诊实收")
    private BigDecimal orderItemExecAmount;
}
