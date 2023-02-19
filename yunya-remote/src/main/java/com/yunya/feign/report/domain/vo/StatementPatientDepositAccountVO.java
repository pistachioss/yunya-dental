package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/2/9 9:49
 * @description: 对账单-患者储蓄卡数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("对账单-患者储蓄卡数据模型")
public class StatementPatientDepositAccountVO implements Serializable {

    /** 患者ID */
    @ApiModelProperty("患者ID")
    private Integer patientId;
    /** 患者姓名 */
    @Excel(name = "患者")
    @ApiModelProperty("患者姓名")
    private String patientName;
    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String patientMobile;
    /** 患者储值卡（会员卡or预付卡）ID */
    @ApiModelProperty("患者储值卡（会员卡or预付卡）ID")
    private Integer patientCardId;
    /** 会员卡或预付卡卡号 */
    @Excel(name = "卡号")
    @ApiModelProperty("会员卡或预付卡卡号")
    private String patientCardNumber;
    /** 卡类型 */
    @Excel(name = "卡类型")
    @ApiModelProperty("卡类型名称")
    private String cardTypeName;
}
