package com.yunya.models.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 杨柳絮
 * @className FileInfo
 * @description
 * @date 2020/8/18 14:01
 */
@Data
public class FileInfo {
  /**
   * 图片地址
   */
  @ApiModelProperty("图片地址")
  private String path;

  @ApiModelProperty("描述")
  private String mark;

  @ApiModelProperty("文件名")
  private String fileName;

}
