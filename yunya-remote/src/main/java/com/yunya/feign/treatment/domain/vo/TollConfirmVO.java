package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("确认收费后返回")
@Data
public class TollConfirmVO {

    @ApiModelProperty("账单编号（ZD+门诊号+时间戳）")
    private String billNumber;

    @ApiModelProperty("账单ID")
    private Integer billPayRecordId;

}
