package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/8/3
 */
@Getter
@Setter
@ApiModel(value = "病例审批拒绝对象模型")
public class MedicalApproveRejectForm {
    @ApiModelProperty(value = "审批通过对象")
    private ApproveRejectForm rejectForm;
}
