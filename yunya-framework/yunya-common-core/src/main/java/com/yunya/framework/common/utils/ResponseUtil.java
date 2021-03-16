package com.yunya.framework.common.utils;

import com.yunya.framework.common.model.*;

/**
 * 响应统一返回工具类
 *
 * @author: GaoLuding
 * @since: 2018年10月28日 8:20
 * @description:
 */
public class ResponseUtil {

  private static Boolean ADUIT = true;
  private static final Integer SUCCESS_STATUS = 0;
  private static final String SUCCESS_MSG = "success";
  private static final Integer ERROR_STATUS = 500;
  private static final String ERROR_MSG = "error";
  private static final Integer RESULT = 1;
  private static final Boolean SUCCESS = true;

  /**
   * APP端设置审核状态
   * @param b
   */
  public static void setPass(Boolean b) {
    ResponseUtil.ADUIT = b;
  }


  /**
   * 无返回数据的成功处理结果
   *
   * @return
   */
  public static ResponseResult fail(Integer status, String msg, Object data) {
    return result(status, msg, data, ResponseUtil.ADUIT);
  }

  /**
   * 错误处理
   *
   * @param status 状态
   * @param msg 提示信息
   * @return
   */
  public static ResponseResult error(Integer status, String msg) {
    return result(status, msg, null, ResponseUtil.ADUIT);
  }

  /**
   * 无返回数据的成功处理结果
   *
   * @return
   */
  public static ResponseResult success() {
    return result(SUCCESS_STATUS, SUCCESS_MSG, null, ResponseUtil.ADUIT);
  }



  /**
   * 有返回数据的成功处理结果
   *
   * @param data 响应数据
   * @return
   */
  public static <T> ResponseResult<T> success(T data) {
    return result(SUCCESS_STATUS, SUCCESS_MSG, data, ResponseUtil.ADUIT);
  }


  /**
   * 有返回数据的成功处理结果
   *
   * @param data 响应数据, msg 返回消息提示语句
   * @return
   */

  public static ResponseResult success (String msg, Object data){
    return result(SUCCESS_STATUS, msg, data,ResponseUtil.ADUIT);
  }

  /**
   * 有返回数据的成功处理结果
   *
   * @param data 响应数据, msg 返回消息提示语句
   * @return
   */

  public static ResponseResult error (String msg, Object data){
    return result(ERROR_STATUS, msg, data,ResponseUtil.ADUIT);
  }

  /**
   * 无数据错误返回
   * @param error error
   * @param param 格式化数据
   * @return ResponseResult
   */
  public static ResponseResult error (RestError error, Object...param){
    return result(error.getCode(), String.format(error.getMessage(), param), null, ResponseUtil.ADUIT);
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
  public static <T>ResponseResult result(Integer status, String msg, T data, Boolean audit) {
    ResponseResult responseResult = new ResponseResult();
    responseResult.setStatus(status);
    responseResult.setMsg(msg);
    responseResult.setData(data);
    responseResult.setAudit(audit);
    return responseResult;
  }


}
