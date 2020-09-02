package com.yunya.framework.common.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 请求统一响应结果
 *
 * @author: chow
 * @date: 2020/7/3 18:01
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ResponseResult<T> implements Serializable {
  /** 响应状态 */
  private Integer status;
  /** 提示信息 */
  private String msg;
  /** 响应数据 */
  private T data;
  /** 审核状态 */
  private Boolean audit;
  /** 人脸识别回调返回状态 */
  private Integer result;
  /** 人脸识别回调返回结果 */
  private Boolean success;
}
