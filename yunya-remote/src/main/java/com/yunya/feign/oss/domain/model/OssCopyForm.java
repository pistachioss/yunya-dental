package com.yunya.feign.oss.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@ApiModel("资源文件复制 参数模型")
public class OssCopyForm implements Serializable {

    @ApiModelProperty(value = "复制的源：通常为临时文件夹", dataType = "OssUrlForm")
    private OssUrlForm srcForm;

    @ApiModelProperty(value = "复制到的<目的地>：其中", dataType = "OssUrlForm")
    private OssUrlForm destForm;

    @ApiModelProperty(value = "是否改变复制后的文件名：可防止重名", dataType = "OssUrlForm")
    private Boolean isNewFileName;
}
