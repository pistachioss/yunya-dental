package com.yunya.feign.system.form;

import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 品牌信息模型
 *
 * @author: chow
 * @date: 2020/7/15 10:51
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class BrandModel extends BaseEntity implements Serializable {

  private Integer id;

  /** 品牌 */
  private String name;

  /** 自定义排序 */
  private Integer orderNum;
}
