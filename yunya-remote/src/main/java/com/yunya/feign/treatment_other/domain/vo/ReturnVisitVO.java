package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 回访记录数据模型
 * @author: LHB
 * @create: 2022-09-21 17:13
 **/
@ApiModel(value = "回访记录数据模型")
@Data
@ToString
public class ReturnVisitVO implements Serializable {
    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 患者就诊ID
     */
    @ApiModelProperty(value = "患者就诊ID")
    private Integer treatmentId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 患者姓名
     */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /**
     * 患者手机号
     */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    /**
     * 初复诊类型：0-初诊，1-复诊
     */
    @ApiModelProperty(value = "初复诊类型：0-初诊，1-复诊")
    private Integer treatType;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Integer dentistId;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    private String dentistName;

    /**
     * 开单日期
     */
    @ApiModelProperty(value = "开单日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date orderDate;

    /** 回访内容列表 */
    @ApiModelProperty(value = "回访内容列表")
    private List<ReturnVisitContentVO> visitingContents;
}
