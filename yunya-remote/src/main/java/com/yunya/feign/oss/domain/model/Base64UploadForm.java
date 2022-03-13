package com.yunya.feign.oss.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@ToString
@ApiModel("资源文件上传 参数模型")
public class Base64UploadForm extends OssUploadForm implements Serializable {

    @ApiModelProperty(value = "base64文件", required = true)
    @NotEmpty(message = "数据不能为空")
    private String data;

    @ApiModelProperty(value = "文件名", required = true)
    @NotEmpty(message = "文件名不能为空")
    private String fileName;
}
