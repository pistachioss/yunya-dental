package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/17
 */
@Getter
@Setter
@ApiModel(value = "产品生成分配分页查询模型")
public class CouponAllocateQuery {

    @ApiModelProperty(value = "产品名称")
    private String keyword;

    @ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
    private List<Integer> couponTypeList;
}
