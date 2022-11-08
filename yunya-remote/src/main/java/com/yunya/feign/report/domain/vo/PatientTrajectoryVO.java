package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/7 17:04
 * @description: 患者轨迹数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者轨迹数据模型")
public class PatientTrajectoryVO implements Serializable {

    /** 时间点 */
    @ApiModelProperty("时间点")
    private String timePoint;
    
    /** 轨迹内容 */
    @ApiModelProperty("轨迹内容")
    private String content;
}
