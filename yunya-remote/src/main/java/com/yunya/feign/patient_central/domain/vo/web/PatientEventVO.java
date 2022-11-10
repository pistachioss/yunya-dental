package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2022/11/7 17:24
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者操作数据模型")
public class PatientEventVO implements Serializable {

    @ApiModelProperty("时间点")
    private Date timePoint;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 患者轨迹事件码 */
    @ApiModelProperty("患者轨迹事件码")
    private Integer eventCode;

    /** 内容第一部分 */
    private String firstContent = "";

    /** 内容第二部分 */
    @ApiModelProperty("内容第二部分")
    private String secondContent = "";

    /** 作用体 */
    @ApiModelProperty("作用体")
    private String effectBody;
}
