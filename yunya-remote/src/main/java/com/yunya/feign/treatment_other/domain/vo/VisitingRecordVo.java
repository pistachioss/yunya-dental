package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 随访记录视图模型
 * @author: LHB
 * @create: 2020-08-21 20:25
 **/
@ApiModel(value = "随访记录视图模型")
@Data
@ToString
public class VisitingRecordVo implements Serializable {
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
     * 医生ID 默认为末诊医生
     */
    @ApiModelProperty(value = "医生ID 默认为末诊医生")
    private Integer dentistId;

    /**
     * 科室ID 默认末诊科室
     */
    @ApiModelProperty(value = "科室ID")
    private Integer deptRoomId;

    /**
     * 就诊日期
     */
    @ApiModelProperty(value = "就诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date treatmentDate;

    /**
     * 随访时间 精确到分
     */
    @ApiModelProperty(value = "随访时间 精确到分")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date visitingDateTime;

    /**
     * 随访原因 新建随访
     */
    @ApiModelProperty(value = "随访原因 新建随访")
    private String reason;

    /**
     * 随访内容 执行随访
     */
    @ApiModelProperty(value = "随访内容 执行随访")
    private String visitingContent;

    /**
     * 是否启用 0-不启用；1-启用
     */
    @ApiModelProperty(value = "是否启用 0-不启用；1-启用")
    private Boolean inservice;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名")
    private String crtName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;
}
