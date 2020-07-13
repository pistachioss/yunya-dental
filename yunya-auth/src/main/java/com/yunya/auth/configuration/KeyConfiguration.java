package com.yunya.auth.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * 用户鉴权密钥配置信息
 *
 * @author ace
 * @create 2017/12/17.
 */
@Data
@ToString
@Configuration
public class KeyConfiguration implements Serializable {

  /** 用户鉴权密钥 */
  @Value("${jwt.rsa-secret}")
  private String userSecret;

  /** 用户认证公钥 */
  private byte[] userPubKey;

  /** 用户认证私钥 */
  private byte[] userPriKey;
}
