package com.yunya.feign.treatment.domain.form;

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
@Data
public class BillPrintInfoForm {
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("账单编号（ZD+门诊ID+时间戳）")
    private String billNumber;
    @ApiModelProperty("账单收费记录Id")
    private Integer billPayId;

}
