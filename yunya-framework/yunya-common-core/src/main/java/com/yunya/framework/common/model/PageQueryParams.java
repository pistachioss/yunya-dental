package com.yunya.framework.common.model;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 分页查询查询参数
 *
 * @author chow
 */
public class PageQueryParams extends LinkedHashMap<String, Object> {

  private static final long serialVersionUID = 1L;
  /** 是否分页 */
  private Boolean whetherPage = true;
  /** 查询页 */
  private Integer page = 1;
  /** 每页显示条数 */
  private Integer limit = 10;

  public PageQueryParams() {}

  public PageQueryParams(Map<String, Object> params) {
    this.putAll(params);
    String pageParam = "whetherPage";
    String numParam = "pageNum";
    String sizeParam = "pageSize";
    if (null != params.get(pageParam)) {
      this.whetherPage = (Boolean) params.get(pageParam);
    }
    String pageNum = params.get(numParam).toString();
    String pageSize = params.get(sizeParam).toString();
    // 分页参数
    if (StringUtils.isNotBlank(pageNum)) {
      if (Integer.parseInt(pageNum) > 0) {
        this.page = Integer.parseInt(pageNum);
      }
    }
    if (StringUtils.isNotBlank(pageSize)) {
      if (Integer.parseInt(pageSize) > 0) {
        this.limit = Integer.parseInt(pageSize);
      }
    }
    this.remove(pageParam);
    this.remove(numParam);
    this.remove(sizeParam);
  }

  public Integer getPageNum() {
    return page;
  }

  public void setPageNum(Integer pageNum) {
    this.page = pageNum;
  }

  public Integer getPageSize() {
    return limit;
  }

  public void setPageSize(Integer pageSize) {
    this.limit = pageSize;
  }

  public Boolean getWhetherPage() {
    return whetherPage;
  }

  public void setWhetherPage(Boolean whetherPage) {
    this.whetherPage = whetherPage;
  }
}
