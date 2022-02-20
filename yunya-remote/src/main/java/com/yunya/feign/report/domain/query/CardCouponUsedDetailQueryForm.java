package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：产品卡券使用统计-激活/复购查询
 *
 * @author: chenlin
 * @Description: 产品卡券使用统计-激活/复购查询
 * @Date: 2021/12/21 9:20
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("产品卡券使用统计-激活or复购查询")
public class CardCouponUsedDetailQueryForm extends MultiClinicDateRangeQueryForm implements Serializable {
    /** 门诊ID*/
    @ApiModelProperty(value = "门诊ID", required = true)
    @NotNull(message = "门诊ID")
    private Integer orgId;

    /** 产品ID*/
    @ApiModelProperty(value = "产品ID", required = true)
    @NotNull(message = "产品ID不能为空")
    private Integer couponId;

    /** 查询明细类型：0-激活，1-复购*/
    @ApiModelProperty(value = "查询明细类型：0-激活，1-复购", required = true)
    @NotNull(message = "查询明细类型不能为空")
    private Integer detailType;
}
