package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@ApiModel("确认收费后返回")
@Data
public class TollConfirmVO {

    @ApiModelProperty("账单编号（ZD+门诊号+时间戳）")
    private String BillNumber;

    @ApiModelProperty("账单ID")
    private Integer BillPayRecordId;

}
