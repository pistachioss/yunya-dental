package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/5/15 14:53
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者活跃天数")
public class BasePatientActivityDayVO implements Serializable {
    
    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;
    
    /** 距末次就诊的天数 */
    @ApiModelProperty("距末次就诊的天数")
    private Integer dayOfLastVisit;

    /** 行为标签名 */
    @ApiModelProperty("行为标签名")
    private String tagName;
}
