package com.yunya.feign.treatment_other.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 修改随访记录模型
 * @author: LHB
 * @create: 2020-08-21 17:25
 **/
@ApiModel(value = "修改随访记录模型")
@Data
@ToString
public class VisitingRecordForm implements Serializable {

    /**
     * 随访记录ID
     */
    @ApiModelProperty(value = "随访记录ID", required = true)
    @NotNull(message = "随访记录ID不能为空!")
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
    @PastOrPresent(message = "就诊日期必须是过去某个或现在日期")
    private Date treatmentDate;

    /**
     * 随访日期
     */
    @ApiModelProperty(value = "随访日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date visitingDate;

    /** 随访时间 */
    @ApiModelProperty(value = "随访时间")
    private String visitingTime;

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
     * 随访状态 0-待随访；1-随访完成
     */
    @ApiModelProperty(value = "随访状态 0-待随访；1-随访完成")
    private Boolean status;
}
