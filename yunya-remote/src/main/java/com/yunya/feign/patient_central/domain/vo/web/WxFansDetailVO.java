package com.yunya.feign.patient_central.domain.vo.web;

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
@Data
@ApiModel("服中心-用户管理列表-查看详情VO")
public class WxFansDetailVO {

    @ApiModelProperty("患者姓名")
    private String patientName;
    @ApiModelProperty("患者Id")
    private Integer patientId;
    @ApiModelProperty("关系")
    private String dictionaryName;
    @ApiModelProperty("关系字典ID")
    private Integer dictionaryId;
    @ApiModelProperty("患者年龄")
    private Integer age;

}
