package com.yunya.feign.discount.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：卡券公用信息查询模型
 *
 * @author: chenlin
 * @Description: 卡券公用信息查询模型
 * @Date: 2022/7/12 14:15
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("卡券公用信息查询模型")
public class CouponCommonInfoQuery extends PageQuery implements Serializable {

    /** 产品分类id */
    @ApiModelProperty("产品分类id")
    private Integer productTypeId;

    /** 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券） */
    @ApiModelProperty("卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
    private Integer type;
}
