package com.yunya.feign.patient_central.domain.vo.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/14
 * @description:
 */
@ToString
@Data
@ApiModel("小程序返回消费信息详情参数模型")
public class MasertMemberRechargeRecordDetailVo {

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String operatingTime;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal rechargePrincipal;

    @ApiModelProperty("种类 0:充值  1:消费  2:退费")
    private Integer type;

    @ApiModelProperty("操作人")
    private String consumerName;

    @ApiModelProperty("消费人")
    private String patientName;



}
