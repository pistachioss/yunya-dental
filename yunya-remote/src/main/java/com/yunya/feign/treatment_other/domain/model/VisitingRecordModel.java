package com.yunya.feign.treatment_other.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 新增随访记录模型
 * @author: LHB
 * @create: 2020-08-21 17:13
 **/
@ApiModel(value = "新增随访记录模型")
@Data
@ToString
public class VisitingRecordModel implements Serializable {
    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "诊所ID不能为空!")
    private Integer orgId;

    /**
     * 患者就诊ID
     */
    @ApiModelProperty(value = "患者就诊ID")
    private Integer treatmentId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID", required = true)
    @NotNull(message = "患者ID不能为空!")
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
    @ApiModelProperty(value = "就诊日期", required = true)
    @NotNull(message = "就诊日期不能为空!")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date treatmentDate;

    /** 随访内容 */
    @ApiModelProperty(value = "随访内容列表",required = true)
    @NotNull(message = "随访内容列表不能为空!")
    private List<VisitingContentModel> visitingContents;
}
