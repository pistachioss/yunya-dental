package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 积分记录
 * @author: LHB
 * @create: 2021-04-23 10:51
 **/
@ApiModel(value = "CreditsRecordVO",description = "积分记录")
@Data
public class CreditsRecordVO implements Serializable {
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("渠道")
    private String channel;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("本次扣除或增加的积分")
    private String credits;
    @ApiModelProperty("时间")
    private String crtTime;
}
