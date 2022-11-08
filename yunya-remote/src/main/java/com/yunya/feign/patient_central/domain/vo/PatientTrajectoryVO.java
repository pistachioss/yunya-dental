package com.yunya.feign.patient_central.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @ApiModelProperty("时间点")
    private Date timePoint;
    
    /** 轨迹内容 */
    @ApiModelProperty("轨迹内容")
    private String content;
}
