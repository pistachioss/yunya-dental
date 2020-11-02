package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 修改密码表单
 * @author: LHB
 * @create: 2020-11-02 15:04
 **/
@Data
@ApiModel(value = "ModificationPasswordForm",description = "修改密码表单")
public class ModificationPasswordForm implements Serializable {
    /** 新密码 */
    @ApiModelProperty(value = "新密码",required = true)
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
    /** 旧密码 */
    @ApiModelProperty(value = "旧密码",required = true)
    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;
    /** 确认密码 */
    @ApiModelProperty(value = "确认密码",required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPwd;
    /** 短信验证码 */
    @ApiModelProperty(value = "短信验证码",required = true)
    @NotBlank(message = "短信验证码不能为空")
    private String authCode;
}
