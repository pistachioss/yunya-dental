package com.yunya.framework.common.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 人脸识别响应结果
 *
 * @author: chow
 * @date: 2020/7/3 18:01
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class IdentifyResponseResult<T> implements Serializable {
  /** 人脸识别回调返回状态 */
  private Integer result;
  /** 人脸识别回调返回结果 */
  private Boolean success;
}
