package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 修改密码Form
 *
 * @author: WY
 * @date 2020/9/3 16:26
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class UpdPassForm implements Serializable {

    /**
     * 旧密码
     */
    @ApiModelProperty(value = "旧密码",required = true)
    private String oldPass;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码",required = true)
    private String newPass;

}
