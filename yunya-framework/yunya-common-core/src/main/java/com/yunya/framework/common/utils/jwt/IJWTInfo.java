package com.yunya.framework.common.utils.jwt;

/**
 * JWT信息获取接口
 *
 * @author ace
 * @date 2017/9/10
 */
public interface IJWTInfo {
  /**
   * 获取用户名
   *
   * @return
   */
  String getUniqueName();

  /**
   * 获取用户ID
   *
   * @return
   */
  String getId();

  /**
   * 获取名称
   *
   * @return
   */
  String getName();

  /**
   * 获取设备类型
   * @return
   */
  String getDeviceType();
}
