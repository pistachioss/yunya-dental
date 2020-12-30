package com.yunya.modules.system.domain.form;

import com.yunya.framework.common.constant.BusinessConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 忘记密码
 * @author: LHB
 * @create: 2020-11-02 17:52
 **/
@ApiModel(value = "ForgetPasswordForm", description = "忘记密码")
@Data
public class ForgetPasswordForm implements Serializable {
    /** 新密码 */
    @ApiModelProperty(value = "新密码",required = true)
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
    /** 确认密码 */
    @ApiModelProperty(value = "确认密码",required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPwd;
    /** 短信验证码 */
    @ApiModelProperty(value = "短信验证码",required = true)
    @NotBlank(message = "短信验证码不能为空")
    private String authCode;
    /** 手机号 */
    @ApiModelProperty(value = "手机号",required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = BusinessConstants.MOBILE_REGEXP)
    private String mobile;
}
