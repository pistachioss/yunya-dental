package com.yunya.framework.common.model;

import lombok.Data;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.T;

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
public class ResponseResult implements Serializable {
  /** 响应状态 */
  private Integer status;
  /** 提示信息 */
  private String msg;
  /** 响应数据 */
  private Object data;
  /** 审核状态 */
  private Boolean audit;

  public static ResponseResult success(T data) {
    return new ResponseResult("0", "ok", data);
  }
}
