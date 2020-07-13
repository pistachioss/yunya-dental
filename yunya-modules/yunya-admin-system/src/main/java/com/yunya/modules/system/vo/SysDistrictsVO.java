package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 地区信息VO类
 *
 * @author: chow
 * @date: 2020/6/15 10:12
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class SysDistrictsVO implements Serializable {
  /** 地区ID */
  private Integer id;
  /** 地区父ID */
  private Integer parentId;
  /** 地区层级 */
  private Byte deep;
  /** 地区名称 */
  private String name;
  /** 地区扩展名 */
  private String extName;
  /** 地区拼音 */
  private String pinyin;
  /** 地区拼音简拼 */
  private String pinyinShort;
}
