package com.yunya.modules.system.domain.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 修改密码
 * @author: LHB
 * @create: 2020-11-02 15:04
 **/
@Data
public class ModificationPasswordForm implements Serializable {
    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
    /** 旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;
    /** 确认密码 */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPwd;
    /** 短信验证码 */
    @NotBlank(message = "短信验证码不能为空")
    private String authCode;
}
