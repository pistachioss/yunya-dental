package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 医生排班表卡片模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 20:35
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "医生排班表卡片模型")
@Data
@ToString
public class DentistScheduleVo implements Serializable {
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 类型 是否工作；
     */
    @ApiModelProperty(value = "休假类型")
    private String type;

    /**
     * 时间段
     */
    private String stime;


}
