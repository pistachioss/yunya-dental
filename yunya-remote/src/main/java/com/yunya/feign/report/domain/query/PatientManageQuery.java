package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: xy
 * @date 2021/5/31 17:44
 **/
@ApiModel(value = "公司端-患者管理参数")
@Data
public class PatientManageQuery extends PageQuery {
    @ApiModelProperty(value = "患者")
    private String patientKeyWord;
    @ApiModelProperty(value = "患者类型")
    private String patientType;
    @ApiModelProperty(value = "会员卡 1-金藤卡  2-银藤卡  3-青藤卡  4-艾维会员")
    private Integer memberType;
    @ApiModelProperty(value = "性别 0-男；1-女")
    private Integer gender;
    @ApiModelProperty(value = "年龄初")
    private Integer startAge;
    @ApiModelProperty(value = "年龄末")
    private Integer endAge;
    @ApiModelProperty(value = "初诊门诊")
    private Integer firstOrgId;
    @ApiModelProperty(value = "初诊医生")
    private String firstDentistName;
    @ApiModelProperty(value = "末诊医生")
    private String lastDentistName;
    @ApiModelProperty(value = "累计消费初")
    private BigDecimal startConsumeAmount;
    @ApiModelProperty(value = "累计消费末")
    private BigDecimal endConsumeAmount;
    @ApiModelProperty(value = "欠费金额初")
    private BigDecimal startOweAmount;
    @ApiModelProperty(value = "欠费金额末")
    private BigDecimal endOweAmount;
    @ApiModelProperty(value = "预付款余额初")
    private BigDecimal startPrincipalBalance;
    @ApiModelProperty(value = "预付款余额末")
    private BigDecimal endPrincipalBalance;
    @ApiModelProperty(value = "会员卡余额初")
    private BigDecimal startMemberBalance;
    @ApiModelProperty(value = "会员卡余额末")
    private BigDecimal endMemberBalance;
    @ApiModelProperty(value = "就诊次数初")
    private Integer startTreatQuantity;
    @ApiModelProperty(value = "就诊次数末")
    private Integer endTreatQuantity;
    @ApiModelProperty(value = "同名患者")
    private Boolean limitName;
    @ApiModelProperty(value = "同手机号")
    private Boolean limitMobile;
    /** 患者分组Id*/
    @ApiModelProperty("患者分组id")
    private Integer patientGroupId;
    @ApiModelProperty(value = "末诊门诊")
    private Integer lastOrgId;
    @ApiModelProperty(value = "生日时间")
    private Date birthday;
}
