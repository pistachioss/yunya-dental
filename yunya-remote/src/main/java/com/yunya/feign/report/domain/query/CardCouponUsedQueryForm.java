package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 简介：产品卡券使用统计查询
 *
 * @author: chenlin
 * @Description: 产品卡券使用统计查询
 * @Date: 2021/12/21 9:20
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("产品卡券使用统计查询")
public class CardCouponUsedQueryForm extends MultiClinicDateRangeQueryForm implements Serializable {

    /** 产品ID列表*/
    @ApiModelProperty("产品ID列表")
    private Collection<Integer> couponIds;
}
