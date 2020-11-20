package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "XRayFilmModel",description = "图片影像参数模型")
@Data
public class XRayFilmModel implements Serializable {
    @ApiModelProperty(value = "X-光片类型",required = true,
            allowableValues = "0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片")
    @NotNull(message = "X-光片类型不能为空")
    private Byte type;

    @ApiModelProperty(value = "牙位编号",notes = "上传根尖片时，需要传牙位编号")
    private Integer toothNo;

    @ApiModelProperty(value = "图片资源定位路径",required = true)
    @NotBlank(message = "图片资源定位路径不能为空")
    private String url;

    @ApiModelProperty(value = "图片名称",required = true)
    @NotBlank(message = "图片名称不能为空")
    private String photoName;
}