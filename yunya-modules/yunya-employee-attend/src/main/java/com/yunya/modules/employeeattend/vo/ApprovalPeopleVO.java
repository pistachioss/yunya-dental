package com.yunya.modules.employeeattend.vo;

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
public class ApprovalPeopleVO {

    private Integer id;

    private Integer userId;
    @ApiModelProperty("员工姓名")
    private String userName;
    @ApiModelProperty("岗位")
    private String posts;
    @ApiModelProperty("手机号")
    private String iphone;
}
