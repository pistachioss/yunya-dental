package com.yunya.auth.form;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 客户端服务请求参数封装
 *
 * @author: chow
 * @date: 2020/7/5 17:07
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClientRequestForm implements Serializable {
  /** 客户端服务编码 */
  private String clientCode;

  /** 客户端服务密钥 */
  private String secret;
}
