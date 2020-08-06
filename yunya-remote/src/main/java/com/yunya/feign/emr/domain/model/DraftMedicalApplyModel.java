package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/7/30
 */
@Getter
@Setter
@ApiModel(value = "草稿病例申请对象模型")
public class DraftMedicalApplyModel {

    @ApiModelProperty(value = "申请基础信息")
    private ApplyBaseModel applyBase;
}
