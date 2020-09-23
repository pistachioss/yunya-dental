package com.yunya.auth.domain;

import com.yunya.feign.system.vo.FrontUserInfoVO;
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
public class UserAuthResponse implements Serializable {

  private static final long serialVersionUID = 1250166508152483573L;
  /** token */
  private final String token;
  /** 用户信息 */
  private final FrontUserInfoVO userInfo;
}
