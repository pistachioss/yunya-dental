package com.yunya.feign.oss.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@ToString
@ApiModel("资源文件上传 参数模型")
public class OssUploadForm extends OssFolderForm implements Serializable {

    @ApiModelProperty(value = "上传的资源文件", dataType = "__file")
    private MultipartFile file;
}
