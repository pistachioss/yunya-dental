package com.yunya.feign.ivy_mini.domain.bo;

import com.yunya.feign.ivy_mini.domain.enums.LoginEnum;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/11/17 16:05
 **/
@Data
public class LoginUserBO {
    private String userName;
    private Integer userId;
    private LoginEnum loginType;
    /** 0-注销 1-禁用 2-正常 */
    private Integer memberStatus;
    private String openId;
}
