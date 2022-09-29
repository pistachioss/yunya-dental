package com.yunya.feign.oss.domain.model;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
@ApiModel("资源文件获取URL 参数模型")
public class OssUrlForm extends OssFolderForm implements Serializable {

    @ApiModelProperty("资源文件存储名称，包括后缀")
    @NotNull(message = "资源文件存储名称不能为空")
    private String ossFilename;

    @ApiModelProperty(value = "是否缩略图", dataType = "Boolean")
    private Boolean isThumb;

    @ApiModelProperty(value = "缩略图尺寸宽度，默认100", dataType = "Integer")
    private Integer thumbWidth;

    @ApiModelProperty(value = "缩略图尺寸高度，默认100", dataType = "Integer")
    private Integer thumbHeight;
}
