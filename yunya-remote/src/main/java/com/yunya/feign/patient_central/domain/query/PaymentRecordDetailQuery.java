package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 外部服务调用参数封装
 * @author: LHB
 * @create: 2020-11-11 10:28
 **/
@ApiModel(value = "PaymentRecordQuery",description = "外部服务调用参数封装")
@Data
public class PaymentRecordDetailQuery implements Serializable {
    @ApiModelProperty(value = "卡ID")
    private String cardId;
    @ApiModelProperty(value = "账单记录ID")
    private Integer billRecordId;
}
