package com.yunya365.aliyunoss.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
@ApiModel("资源文件获取URL 参数模型")
public class OssUrlForm extends OssFolderForm {

    @ApiModelProperty("资源文件存储名称，包括后缀")
    private String ossFilename;

    @ApiModelProperty(value = "是否缩略图", dataType = "Boolean")
    private Boolean isThumb;
}
