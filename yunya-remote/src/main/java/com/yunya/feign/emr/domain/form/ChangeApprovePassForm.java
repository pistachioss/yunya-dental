package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/8/5
 */
@Getter
@Setter
@ApiModel(value = "病例变更申请通过模型")
public class ChangeApprovePassForm {

    @ApiModelProperty(value = "事件Id", required = true)
    @NotNull
    private Integer eventId;

    @ApiModelProperty(value = "允许变更截止时间")
    @NotNull
    private LocalDate changeDeadTime;
}
