package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author A
 */
@ApiModel(value = "XRayFilmModel",description = "图片影像参数模型")
@Data
public class XRayFilmModel implements Serializable {
    private List<XRayFilmInfoModel> list;
}