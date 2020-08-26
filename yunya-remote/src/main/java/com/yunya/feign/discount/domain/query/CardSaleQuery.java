package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
@ApiModel(value = "产品销售-查看配给")
public class CardSaleQuery {
    @ApiModelProperty(value = "优惠券id")
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "组织id")
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "售出类型（0:售出 1:置换 2:赠送）")
    private List<Integer> soldTypeList;
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;
    @ApiModelProperty(value = "卡券状态（0:待售出 1:待激活 2:已激活）")
    private List<Integer> cardStatsList;
    @ApiModelProperty(value = "页码", required = true)
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", required = true)
    @NotNull
    private Integer pageSize;
}
