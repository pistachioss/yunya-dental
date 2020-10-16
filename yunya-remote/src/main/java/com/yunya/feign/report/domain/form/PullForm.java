package com.yunya.feign.report.domain.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Getter
@Setter
@ToString
public class PullForm {
  /** 开始时间 */
  @NotBlank private String startDate;
  /** 结束时间 */
  @NotBlank private String endDate;
  /** 自定义数据类型，用来指定拉取原始表数据类型 */
  private Integer dataType;
}
