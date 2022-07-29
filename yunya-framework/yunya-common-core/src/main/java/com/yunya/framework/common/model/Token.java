package com.yunya.framework.common.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * token令牌
 *
 * @author: xiangyang
 * @date: 2021/6/9 13:27
 * @description:
 * @since: 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {

  private static final long serialVersionUID = -8482946147572784305L;
  /** token */
  private String token;
  /** 有效时间：单位：秒 */
  private Long expire;

  private LocalDateTime expiration;
}
