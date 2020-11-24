package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:欠费查询Vo
 *
 * @author: WY
 * @date: 2020/10/28 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者性别Vo")
public class AnalysisPatientGenderVo implements Serializable {

    /** 性别 */
    @ApiModelProperty("性别")
    private String gender;

    /** 男女数量 */
    @ApiModelProperty("男女数量")
    private Integer countGender;

    /** 占比 */
    @ApiModelProperty("占比")
    private String percentage;

}