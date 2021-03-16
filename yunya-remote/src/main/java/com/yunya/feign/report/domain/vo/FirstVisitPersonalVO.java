package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

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
@ApiModel("个人初诊记录报表VO")
@Data
@ToString
public class FirstVisitPersonalVO {

    @ApiModelProperty("门诊ID")
    private Integer orgId;

    @ApiModelProperty("门诊")
    @Excel(name = "门诊名称")
    private String orgName;

    @ApiModelProperty("初诊医生Id")
    private Integer registeredDentistId;

    @ApiModelProperty("初诊医生姓名")
    @Excel(name = "初诊医生姓名")
    private String registeredDentistName;

    @ApiModelProperty("初诊患者数量")
    @Excel(name = "初诊患者数量")
    private Integer count;

}
