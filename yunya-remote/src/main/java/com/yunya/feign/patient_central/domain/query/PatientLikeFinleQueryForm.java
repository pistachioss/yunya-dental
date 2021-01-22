package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简单介绍:</br> 患者模糊查询模板
 *
 * @author: WY
 * @date 2020/7/31 13:02
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者模糊查询模板")
public class PatientLikeFinleQueryForm implements Serializable {

    /**
     * 患者查询条件合并字段
     */
    @ApiModelProperty(value = "患者查询条件(姓名or姓名拼音or手机号)", required = true)
    @NotBlank(message = "患者查询条件不能为空！")
    private String condition;


}
