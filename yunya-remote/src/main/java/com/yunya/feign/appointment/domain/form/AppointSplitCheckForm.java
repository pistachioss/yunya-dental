package com.yunya.feign.appointment.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 时长分解是否正确参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-07 9:19
 * @update yunya-lihuibin    2020-08-07    新建
 */
@ApiModel(value = "时长分解是否正确参数封装")
@Data
@ToString
public class AppointSplitCheckForm implements Serializable {

    /** 预约日期 */
    @ApiModelProperty(value = "预约日期",required = true)
    @NotNull(message = "预约日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date appointDate;

    /** 预约总时长 */
    @ApiModelProperty(value = "预约总时长",required = true)
    @NotNull(message = "预约总时长你不能为空，请先输入预约总时长！")
    private Integer appointDuration;

    /** 时长分解列表 */
    @ApiModelProperty(value = "时长分解列表",required = true)
    @NotEmpty(message = "时长分解列表不能为空！")
    private List<AppointmentSplitBaseInfo> splitList;
}
