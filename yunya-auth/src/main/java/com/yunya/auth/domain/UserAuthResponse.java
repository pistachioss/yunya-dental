package com.yunya.auth.domain;

import com.yunya.feign.system.vo.FrontUserInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户鉴权结果响应
 *
 * @author chow
 */
@Data
@ToString
@ApiModel("用户登录信息")
public class UserAuthResponse implements Serializable {

  private static final long serialVersionUID = 1250166508152483573L;
  /** token */
  @ApiModelProperty("token令牌")
  private final String token;
  /** 用户信息 */
  private final FrontUserInfoVO userInfo;
}
