package com.yunya365.aliyunoss.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
@ApiModel("资源文件上传 参数模型")
public class OssUploadForm extends OssFolderForm {

    @ApiModelProperty(value = "上传的资源文件", dataType = "__file")
    private MultipartFile file;
}
