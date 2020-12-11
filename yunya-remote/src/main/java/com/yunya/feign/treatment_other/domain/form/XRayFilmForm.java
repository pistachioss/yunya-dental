package com.yunya.feign.treatment_other.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 图片影像编辑参数模型
 * @author: LHB
 * @create: 2020-11-19 19:53
 **/
@ApiModel(value = "XRayFilmForm",description = "图片影像编辑参数模型")
@Data
public class XRayFilmForm implements Serializable {
    @ApiModelProperty(value = "X-光片类型",required = true,
            allowableValues = "0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片")
    @NotNull(message = "X-光片类型不能为空")
    private Byte type;

    @ApiModelProperty(value = "上传时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "上传时间不能为空")
    private String uploadTime;
}
