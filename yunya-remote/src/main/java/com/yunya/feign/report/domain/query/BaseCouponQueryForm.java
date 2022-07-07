package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 简介：产品卡券查询模型
 *
 * @author: chenlin
 * @Description: 产品卡券查询模型
 * @Date: 2022/7/6 10:29
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("产品卡券查询模型")
public class BaseCouponQueryForm extends PageQuery implements Serializable {
    /** 产品id列表 */
    @ApiModelProperty("产品id列表")
    private Collection<Integer> couponIds;

    /** 卡券类型列表 */
    @ApiModelProperty("卡券类型列表")
    private Collection<Integer> couponTypes;

    /** 产品类型id列表 */
    @ApiModelProperty("产品类型id列表")
    private Collection<Integer> productTypeIds;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String couponName;
}
