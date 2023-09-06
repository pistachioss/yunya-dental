package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * @author: chenlin
 * @date: 2023/9/6 14:41
 * @description: 预付款间转账记录数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("预付款间转账记录数据模型")
public class BasePrepaidTransferVO implements Serializable {

    /** 转账日期 */
    @Excel(name = "转账日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("转账日期")
    private Date transferDate;
    
    /** 患者 */
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;

    /** 预付款账号 */
    @Excel(name = "预付款账号")
    @ApiModelProperty("预付款账号")
    private String prepaidNumber;

    /** 转出本金 */
    @Excel(name = "转出本金", type = EXPORT, isStatistics = true, cellType = NUMERIC)
    @ApiModelProperty("转出本金")
    private BigDecimal outPrincipal;

    /** 转出赠金 */
    @Excel(name = "转出赠金", type = EXPORT, isStatistics = true, cellType = NUMERIC)
    @ApiModelProperty("转出赠金")
    private BigDecimal outBonus;

    /** 转出后本金 */
    @Excel(name = "转出后本金", type = EXPORT, isStatistics = true, cellType = NUMERIC)
    @ApiModelProperty("转出后本金")
    private BigDecimal currentPrincipal;

    /** 转出后赠金 */
    @Excel(name = "转出后赠金", type = EXPORT, isStatistics = true, cellType = NUMERIC)
    @ApiModelProperty("转出后赠金")
    private BigDecimal currentBonus;

    /** 转入患者 */
    @Excel(name = "转入患者")
    @ApiModelProperty("转入患者")
    private String inPatientName;

    /** 转入患者手机号 */
    @Excel(name = "转入患者手机号")
    @ApiModelProperty("转入患者手机号")
    private String inPatientMobile;

    /** 转入预付款账号 */
    @Excel(name = "转入预付款账号")
    @ApiModelProperty("转入预付款账号")
    private String inPrepaiNumber;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remarks;
}
