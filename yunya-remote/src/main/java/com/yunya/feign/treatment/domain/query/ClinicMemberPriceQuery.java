package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：门诊商品表or价目表会员价查询模型
 *
 * @author: chenlin
 * @Description: 门诊商品表or价目表会员价查询模型
 * @Date: 2022/2/10 15:55
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊商品表or价目表会员价查询模型")
public class ClinicMemberPriceQuery implements Serializable {
    /** 门诊id*/
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    /** 会员类型id*/
    @ApiModelProperty(value = "会员类型id", required = true)
    @NotNull(message = "会员类型id不能为空")
    private Integer memberTypeId;

    /** 商品表id*/
    @ApiModelProperty("商品表id")
    private Collection<Integer> oralIds;

    /** 价目表id*/
    @ApiModelProperty("价目表id")
    private Collection<Integer> tariffIds;
}
