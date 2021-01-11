package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author A
 */
@ApiModel(value = "XRayFilmModel",description = "图片影像参数模型")
@Data
public class XRayFilmModel implements Serializable {
    @ApiModelProperty(value = "上传照片列表", required = true)
    @NotNull(message = "上传照片列表不能为空")
    private List<XRayFilmInfoModel> list;
}