package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 员工信息参数封装模型
 *
 * @author: lihuibin
 * @date: 2020/7/9 16:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "员工信息参数封装模型", parent = PageQueryParams.class)
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysEmployeeQueryForm extends PageQueryParams implements Serializable {
    /** 岗位ID */
    @ApiModelProperty(value = "岗位ID")
    private Integer postId;
    /** 岗位状态 */
    @ApiModelProperty(value = "岗位状态")
    private Integer workStatus;
    /** 姓名 */
    @ApiModelProperty(value = "姓名")
    private String name;
}
