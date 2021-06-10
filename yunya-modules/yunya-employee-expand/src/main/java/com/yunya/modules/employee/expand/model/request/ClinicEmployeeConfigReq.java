package com.yunya.modules.employee.expand.model.request;

import com.yunya.feign.appointment.domain.form.OnlineAppointItemSettingForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Data
@ApiModel("门诊员工配置模型")
public class ClinicEmployeeConfigReq {
    /**
     * 门诊科室ID
     */
    @ApiModelProperty(value = "门诊科室ID")
    private Integer clinicDepartmentRoomId;
    /**
     * 助手ID
     */
    @ApiModelProperty(value = "助手ID")
    private Integer assistantEmployeeId;
    /**
     * 是否可预约
     */
    @ApiModelProperty(value = "是否可预约",required = true)
    @NotNull
    private Integer enableAppoint;
    /**
     * 是否可挂号
     */
    @ApiModelProperty(value = "是否可挂号", required = true)
    @NotNull
    private Integer enableRegistry;

    /**
     * 线上可预约项目
     */
    @ApiModelProperty(value = "线上可预约项目")
    @Valid
    @NotNull(message = "线上可预约项目设置不能为空")
    private OnlineAppointItemSettingForm onlineAppointItemInfo;
}
