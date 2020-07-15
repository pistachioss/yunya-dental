package com.yunya.feign.system.form;

import com.yunya.framework.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 字典明细信息模型
 *
 * @author: chow
 * @date: 2020/7/15 12:50
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class DictionaryItemModel extends BaseEntity implements Serializable {

  private Integer id;

  /** 字典类型ID */
  private Integer dictionaryTypeId;

  /** 字典选项 */
  private String name;
}
