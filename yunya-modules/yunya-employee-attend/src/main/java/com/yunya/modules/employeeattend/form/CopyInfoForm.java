package com.yunya.modules.employeeattend.form;

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
public class CopyInfoForm {

    private Integer Id;
    @ApiModelProperty("申请类型对应表的Id")
    private Integer applyId;
    @ApiModelProperty("申请类型 0：请假 1：加班 2：外勤")
    private Integer applyType;

}
