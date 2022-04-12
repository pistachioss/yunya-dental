package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：消息推送未读数量VO
 *
 * @author: chenlin
 * @Description: 消息推送未读数量VO
 * @Date: 2022/4/6 9:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("消息推送未读数量VO")
public class EmpPushMsgUnReadCountVO implements Serializable {

    /** 未读数量*/
    @ApiModelProperty("未读数量")
    private Integer count;
}
