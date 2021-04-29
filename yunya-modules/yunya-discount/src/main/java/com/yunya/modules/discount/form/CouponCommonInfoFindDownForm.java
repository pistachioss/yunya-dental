package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModel;
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
@ApiModel("全部卡券下拉框")
@Data
public class CouponCommonInfoFindDownForm {
    @ApiModelProperty("卡券名称")
    private String name;
}
