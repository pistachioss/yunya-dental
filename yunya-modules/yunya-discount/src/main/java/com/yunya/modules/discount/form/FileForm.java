package com.yunya.modules.discount.form;

import com.yunya.models.discount.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 杨柳絮
 * @className FileForm
 * @description
 * @date 2020/8/20 11:12
 */
@Data
public class FileForm {

  @ApiModelProperty("所关联的卡券ID")
  private Integer Id;
  /**
   * 图像
   */
  @ApiModelProperty("图像")
  private List<FileInfo> paths;

  /**
   * 文档
   */
  @ApiModelProperty("文档")
  private List<FileInfo> docs;

  /**
   * 图像
   */
  @ApiModelProperty("产品介绍图像")
  private List<FileInfo> proPaths;
}
