package com.yunya.modules.discount.form;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@Data
public class CouponCommonInfoIsInserviceForm {
    /**
     * 主键
     */
    @ApiModelProperty("卡券Id")
    private Integer id;
    @ApiModelProperty("是否启用")
    private Boolean isInservice;
}
