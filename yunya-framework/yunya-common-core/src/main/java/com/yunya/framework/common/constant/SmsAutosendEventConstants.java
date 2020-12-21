package com.yunya.framework.common.constant;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 短信自动发送事件code
 *
 * @author: chow
 * @date: 2020/7/9 13:16
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class SmsAutosendEventConstants implements Serializable {

  /** ---------------------------用户信息缓存通用key------------------------------- */
  /** 忘记密码事件 */
  public static final String FORGET_PASSWORD_EVENT = "sys_user";
  /** 考勤设备绑定事件 */
  public static final String DEVICE_BINDING_EVENT = "attendance_device_binding";
}
