package com.yunya.feign.emr.domain.form;

import com.yunya.feign.emr.valid.*;
import io.swagger.annotations.*;
import lombok.*;
import org.hibernate.validator.group.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel(value = "修改病例模板模型")
@GroupSequenceProvider(MedicalTempGroupSequenceProvider.class)
public class MedicalTemplateForm {

    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank
    @Size(max = 25)
    private String name;

    @ApiModelProperty(value = "模板类型（0：初诊  1：复诊）", required = true, example = "0：初诊  1：复诊")
    @NotNull
    private Integer type;

    @ApiModelProperty(value = "复诊")
    @Size(max = 1000)
    @Null(groups = FirstVisitGroupNotView.class, message = "复诊内容必须为空")
    private String reExamination;

    @ApiModelProperty(value = "主诉")
    @NotBlank
    @Size(max = 1000)
    @Null(groups = FollowUpGroupNotView.class, message = "主诉内容必须为空")
    private String chiefComplaint;

    @ApiModelProperty(value = "现病史")
    @NotBlank
    @Size(max = 1000)
    @Null(groups = FollowUpGroupNotView.class, message = "现病史内容必须为空")
    private String presentIllness;

    @ApiModelProperty(value = "既往史")
    @NotBlank
    @Size(max = 1000)
    @Null(groups = FollowUpGroupNotView.class, message = "既往史内容必须为空")
    private String pastHistory;

    @ApiModelProperty(value = "检查")
    @NotBlank
    @Size(max = 1000)
    private String examination;

    @ApiModelProperty(value = "诊断")
    @NotBlank
    @Size(max = 1000)
    private String diagnosis;

    @ApiModelProperty(value = "计划")
    @NotBlank
    @Size(max = 1000)
    private String plan;

    @ApiModelProperty(value = "处理")
    @NotBlank
    @Size(max = 1000)
    private String treatment;

    @ApiModelProperty(value = "处方")
    @Size(max = 1000)
    private String prescription;

    public interface FirstVisitGroupNotView{}

    public interface FollowUpGroupNotView{}

}
