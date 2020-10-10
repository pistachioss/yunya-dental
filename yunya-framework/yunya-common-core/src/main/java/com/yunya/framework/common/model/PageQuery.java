package com.yunya.framework.common.model;

import com.yunya.framework.common.utils.StringHelper;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 分页条件
 *
 * @author: chow
 * @date: 2020/9/29 16:20
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PageQuery implements Serializable {
  private Boolean whetherPage = true;

  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;

  /** 排序列 */
  private String orderByColumn;

  /** 排序的方向desc或者asc */
  private String isAsc = "asc";

  public String getOrderBy() {
    if (StringHelper.isEmpty(orderByColumn)) {
      return "";
    }
    return StringHelper.toUnderScoreCase(orderByColumn) + " " + isAsc;
  }
}
