package com.yunya.feign.employee_attend.form;

import com.yunya.feign.treatment.domain.form.ClinicItemPriceForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 考勤地址设置修改参数模型
 *
 * @author: chenlin
 * @date: 2020/11/5 15:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("考勤地址设置修改参数模型")
@Data
@ToString
public class AttendanceAddressSetForm implements Serializable {
    /** 组织ID */
    @ApiModelProperty(value = "组织ID", required = true)
    @NotNull(message = "组织ID不能为空！")
    private Integer orgId;

    /** 考勤地址 */
    @NotBlank(message = "考勤地址不能为空！")
    @ApiModelProperty(value = "考勤地址", required = true)
    private String attendanceAddress;

    /** 经度 */
    @ApiModelProperty(value = "经度", required = true)
    @NotNull(message = "经度不能为空！")
    private String longitude;

    /** 纬度 */
    @ApiModelProperty(value = "纬度", required = true)
    @NotNull(message = "纬度不能为空！")
    private String latitude;

    /** 考勤范围 */
    @ApiModelProperty(value = "考勤范围", required = true)
    @NotNull(message = "考勤范围不能为空！")
    private Integer attendanceRange;
}
