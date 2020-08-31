package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;
import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
@ApiModel(value = "患者卡券查询模型")
public class PatientCardQuery {
    @ApiModelProperty(value = "产品名称")
    private String couponName;
    @ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券）")
    private List<Integer> couponTypeList;
    @ApiModelProperty(value = "查询类型（0：自有 1：共享）")
    @NotNull
    private Integer queryType;
    @ApiModelProperty(value = "页码", required = true)
    @NotNull
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", required = true)
    @NotNull
    private Integer pageSize;
}
