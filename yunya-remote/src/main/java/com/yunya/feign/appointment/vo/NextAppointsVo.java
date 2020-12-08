package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 后续预约视图参数封装
 * @author: LHB
 * @create: 2020-12-08 10:04
 **/
@ApiModel(value = "NextAppointsVo",description = "后续预约视图参数封装")
@Data
public class NextAppointsVo implements Serializable {
    @ApiModelProperty("预约ID")
    private String id;
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("预约医生ID")
    private String dentistId;
    @ApiModelProperty("预约助手ID")
    private String assistantId;
    @ApiModelProperty("预约科室ID")
    private String deptRoomId;
    @ApiModelProperty("后续预约数量")
    private Integer count;
}
