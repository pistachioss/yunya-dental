package com.yunya.feign.oss.domain.model;

import io.swagger.annotations.*;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@ApiModel("资源文件获取URL 参数模型")
public class OssUrlForm extends OssFolderForm implements Serializable {

    @ApiModelProperty("资源文件存储名称，包括后缀")
    private String ossFilename;

    @ApiModelProperty(value = "是否缩略图", dataType = "Boolean")
    private Boolean isThumb;
}
