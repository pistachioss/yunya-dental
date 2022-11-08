package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

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

    @ApiModelProperty("事件点")
    private String timePoint;

    /** 门诊 */
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 患者轨迹事件码 */
    @ApiModelProperty("患者轨迹事件码")
    private Integer eventCode;

    /** 作用体 */
    @ApiModelProperty("作用体")
    private String effectBody;
}
