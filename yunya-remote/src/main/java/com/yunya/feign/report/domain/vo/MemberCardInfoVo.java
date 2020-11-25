package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/11/25 09:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("会员卡级别Vo")
@Data
@ToString
public class MemberCardInfoVo implements Serializable {

    /** 会员级别id */
    @ApiModelProperty(value = "会员级别id")
    private Integer memberLevelId;

    /** 会员卡级别名称 */
    @ApiModelProperty(value = "会员卡级别名称")
    private String memberLevelName;

}