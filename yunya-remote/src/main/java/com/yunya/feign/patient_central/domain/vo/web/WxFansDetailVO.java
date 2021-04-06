package com.yunya.feign.patient_central.domain.vo.web;

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
public class WxFansDetailVO {

    @ApiModelProperty("患者姓名")
    private String patientName;
    @ApiModelProperty("关系")
    private String dictionaryName;
    @ApiModelProperty("关系字典ID")
    private Integer dictionaryId;

}
