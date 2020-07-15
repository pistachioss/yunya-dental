package com.yunya.framework.common.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 通用信息模型
 *
 * @author: chow
 * @date: 2020/7/15 13:09
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseEntity implements Serializable {
  /** 是否启用 */
  private Boolean inservice;

  /** 创建人ID */
  private Integer crtId;

  /** 创建人名称 */
  private String crtName;

  /** 创建时间 */
  private Date crtTime;

  /** 更新人姓名 */
  private Integer updId;

  /** 修改人名称 */
  private String updName;

  /** 修改时间 */
  private Date updTime;
}
