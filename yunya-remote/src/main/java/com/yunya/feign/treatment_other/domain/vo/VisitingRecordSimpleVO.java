package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @description: 随访记录数据模型
 * @create: 2020-08-21 20:25
 **/
@ApiModel(value = "VisitingRecordVo", description = "随访记录数据模型")
@Data
@ToString
public class VisitingRecordSimpleVO implements Serializable {
    /**
     * 随访记录ID
     */
    @ApiModelProperty(value = "随访记录ID")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 门诊
     */
    @ApiModelProperty(value = "门诊")
    private String abbreviation;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 随访日期
     */
    @ApiModelProperty(value = "随访日期")
    private String visitingDate;

    /**
     * 随访原因
     */
    @ApiModelProperty(value = "随访原因")
    private String reason;

    /**
     * 随访内容 执行随访
     */
    @ApiModelProperty(value = "随访内容")
    private String visitingContent;

    /** 随访状态 0-待随访；1-随访完成*/
    @ApiModelProperty(value = "随访状态 0-待随访；1-随访完成")
    private Boolean status;

    /** 实际随访执行人ID */
    @ApiModelProperty(value = "实际随访执行人ID")
    private Integer executorId;

    /** 实际随访执行人名字 */
    @ApiModelProperty(value = "实际随访执行人名字")
    private String executorName;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;
}
