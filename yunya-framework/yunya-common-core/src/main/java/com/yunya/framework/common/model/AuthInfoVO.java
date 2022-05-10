package com.yunya.framework.common.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangyang
 */
@Data
@Accessors(chain = true)
public class AuthInfoVO implements Serializable{

  private String token;

  private String userName;

  private Integer userId;

  private Boolean disabled;

  private String disableReason;

  private Integer loginType;

  private String openId;

  private LocalDateTime lastEnterDate;

}
