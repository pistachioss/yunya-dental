package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 字典明细vo
 *
 * @author: chow
 * @date: 2020/6/4 11:22
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class DictionaryItemVO implements Serializable {
  /** 字典明细ID */
  private Integer id;
  /** 字典数据名称 */
  private String name;
  /** 字典类型ID */
  private Integer dictTypeId;
  /** 字典类型名称 */
  private String dictTypeName;
}
