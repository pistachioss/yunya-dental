package com.yunya.modules.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 预约冲突信息实体
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 14:55
 * @update yunya-lihuibin    2020-07-28    新建
 */
@ApiModel("预约冲突信息实体")
@Data
public class AppointConflictInfoVo extends AppointmentBaseVo implements Serializable {

    @ApiModelProperty("门诊名称")
    private String clinicName;

    @ApiModelProperty("门诊编号")
    private String clinicNumber;

    @ApiModelProperty("门诊简称")
    private String abbreviation;
}
