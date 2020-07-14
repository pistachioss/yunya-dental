package com.yunya.auth.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 用户请求头信息配置
 *
 * @author ace
 * @create 2017/12/26.
 */
@Configuration
@Data
public class UserConfiguration {
  @Value("${jwt.token-header}")
  private String userTokenHeader;
}
