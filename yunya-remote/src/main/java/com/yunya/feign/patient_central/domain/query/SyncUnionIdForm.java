package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/9/26
 * @description:
 */
@Data
@ApiModel(value = "SyncUnionIdForm",description = "老数据同步UnoinId字段form")
public class SyncUnionIdForm {

    @ApiModelProperty("openID")
    private String openId;

    @ApiModelProperty("unionId")
    private String unionId;
}
