package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 照片列表参数模型
 * @author: LHB
 * @create: 2020-11-18 14:01
 **/
@ApiModel(value = "PhotoDetailListModel",description = "照片列表参数模型")
@Data
public class PhotoDetailListModel implements Serializable {
    @ApiModelProperty(value = "图片uri",required = true)
    @NotBlank(message = "图片uri不能为空")
    private String uri;

    @ApiModelProperty(value = "图片名称",required = true)
    @NotBlank(message = "图片名称不能为空")
    private String filmName;
}
