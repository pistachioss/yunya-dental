package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 回访记录保存模型
 * @author: LHB
 * @create: 2022-09-21 17:13
 **/
@ApiModel(value = "回访记录保存模型")
@Data
@ToString
public class ReturnVisitingRecordForm implements Serializable {
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
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生id不能为空")
    private Integer dentistId;

    /** 回访内容 */
    @ApiModelProperty(value = "回访内容列表",required = true)
    @NotEmpty(message = "回访内容列表不能为空!")
    private List<ReturnVisitingContentForm> visitingContents;
}
