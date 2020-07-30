package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 杨柳絮
 * @className ExaminationsVO
 * @description 检查等字段牙位与对应检查结果描述实体类
 * @date 2020/7/29 13:29
 */
@Data
@ToString
public class ExaminationsVO {

  @ApiModelProperty("牙位，多个牙位用逗号隔开")
  private String tooth_position;

  @ApiModelProperty("描述")
  private String describe;

}
