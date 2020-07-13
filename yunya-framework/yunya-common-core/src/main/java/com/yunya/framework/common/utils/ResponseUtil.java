package com.yunya.framework.common.utils;

import com.yunya.framework.common.model.ResponseResult;

/**
 * 响应统一返回工具类
 *
 * @author: GaoLuding
 * @since: 2018年10月28日 8:20
 * @description:
 */
public class ResponseUtil {

  private static final Boolean PASS = true;
  private static final Integer SUCCESS_STATUS = 0;
  private static final String SUCCESS_MSG = "OK";

  /**
   * 无返回数据的成功处理结果
   *
   * @return
   */
  public static ResponseResult fail(Integer status, String msg, Object data) {
    return result(status, msg, data, PASS);
  }

  /**
   * 错误处理
   *
   * @param status 状态
   * @param msg 提示信息
   * @return
   */
  public static ResponseResult error(Integer status, String msg) {
    return result(status, msg, null, null);
  }

  /**
   * 无返回数据的成功处理结果
   *
   * @return
   */
  public static ResponseResult success() {
    return result(SUCCESS_STATUS, SUCCESS_MSG, null, PASS);
  }

  /**
   * 有返回数据的成功处理结果
   *
   * @param data 响应数据
   * @return
   */
  public static ResponseResult success(Object data) {
    return result(SUCCESS_STATUS, SUCCESS_MSG, data, PASS);
  }

  /**
   * 有返回数据的处理结果
   *
   * @param status 状态
   * @param msg 提示信息
   * @param data 响应数据
   * @param audit 审核状态
   * @return
   */
  public static ResponseResult result(Integer status, String msg, Object data, Boolean audit) {
    ResponseResult responseResult = new ResponseResult();
    responseResult.setStatus(status);
    responseResult.setMsg(msg);
    responseResult.setData(data);
    responseResult.setAudit(audit);
    return responseResult;
  }
}
