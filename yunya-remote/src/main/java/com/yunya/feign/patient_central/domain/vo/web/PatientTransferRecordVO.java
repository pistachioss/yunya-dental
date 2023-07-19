package com.yunya.feign.patient_central.domain.vo.web;

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
 * @date: 2023/7/18 16:15
 * @description: 患者转账记录数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者转账记录数据模型")
public class PatientTransferRecordVO implements Serializable {
    
    /** 转账账户 */
    @ApiModelProperty("转账账户")
    private String transferorNumber;

    /** 转账患者 */
    @ApiModelProperty("转账患者")
    private String transferor;

    /** 转账本金 */
    @ApiModelProperty("转账本金")
    private BigDecimal principal;

    /** 转账赠金 */
    @ApiModelProperty("转账赠金")
    private BigDecimal bonus;
    
    /** 操作时间 */
    @ApiModelProperty("操作时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;

    /** 收款账户 */
    @ApiModelProperty("收款账户")
    private String acceptorNumber;

    /** 收款患者 */
    @ApiModelProperty("收款患者")
    private String acceptor;

    /** 转入类型：1-转入，2-转出 */
    @ApiModelProperty(value = "转入类型：1-转入，2-转出", hidden = true)
    private Byte type;
    
    /** 门诊id */
    @ApiModelProperty(value = "门诊id", hidden = true)
    private Integer orgId;
    
    /** 门诊简称 */
    @ApiModelProperty("门诊简称")
    private String abbreviation;

    /** 操作人 */
    @ApiModelProperty("操作人")
    private String operator;
}
