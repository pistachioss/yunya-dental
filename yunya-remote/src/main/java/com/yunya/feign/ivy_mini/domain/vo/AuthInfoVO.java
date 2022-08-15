package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangyang
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "平台登录认证信息")
public class AuthInfoVO implements Serializable{

  @ApiModelProperty(value = "令牌")
  private String token;

  @ApiModelProperty(value = "用户名")
  private String userName;

  @ApiModelProperty(value = "用户id")
  private Integer userId;

  @ApiModelProperty(value = "用户是否禁用")
  private Boolean disabled;

  @ApiModelProperty(value = "禁用原因")
  private String disableReason;

  @ApiModelProperty(value = "openId")
  private String openId;

  @ApiModelProperty(value = "unionid")
  private String unionId;

  @ApiModelProperty(value = "最近一次访问时间")
  private LocalDateTime lastEnterDate;

}
