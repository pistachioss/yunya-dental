package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author A
 */
@ApiModel(value = "XRayFilmModel",description = "图片影像参数模型")
@Data
public class XRayFilmModel implements Serializable {
    @ApiModelProperty("图片影像信息")
    @Valid
    @NotNull(message = "图片信息列表不能为空")
    private List<XRayFilmInfoModel> xRayFilmInfoList;
}