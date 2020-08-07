package com.yunya.feign.appointment.domain.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 时长分解参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:19
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel("时长分解参数封装")
@Data
@ToString
public class AppointmentSplitBase {

    /** 拆分开始时间 */
    @ApiModelProperty(value = "拆分开始时间", required = true)
    @NotNull(message = "拆分开始时间不能为空！")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String splitStartTime;

    /** 拆分结束时间 */
    @ApiModelProperty(value = "拆分结束时间",required = true)
    @NotNull(message = "拆分结束时间不能为空！")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String splitEndTime;

    /** 医生/助手ID */
    @ApiModelProperty(value = "医生/助手ID", required = true)
    @NotNull(message = "医生/助手ID不能为空！")
    private Integer assistantId;

}
