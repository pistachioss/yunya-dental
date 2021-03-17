package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.annation.Excels;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介: 转诊报表VO
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
@ApiModel(value = "ReferredInfoVO",description = "转诊报表模型")
public class ReferredRrportVO {

    @ApiModelProperty("门诊id")
    private Integer orgId;

    @ApiModelProperty("转诊人id")
    private Integer userId;

    @ApiModelProperty("转诊人名称")
    @Excel(name = "转诊人名称")
    private String userName;

    @ApiModelProperty("转诊人的科室ID")
    private Integer depId;

    @ApiModelProperty("转诊人的科室名称")
    @Excel(name = "转诊人的科室名称")
    private String depName;

    @ApiModelProperty("被转诊人id")
    private Integer referredId;

    @ApiModelProperty("被转诊人名称")
    @Excel(name = "被转诊人名称")
    private String referredName;

    @ApiModelProperty("被转诊人的科室ID")
    private Integer referredDepId;

    @ApiModelProperty("被转诊人的科室名称")
    @Excel(name = "被转诊人的科室名称")
    private String referredDepName;

    @ApiModelProperty("转诊数量")
    @Excel(name = "转诊数量")
    private Integer count;
}
