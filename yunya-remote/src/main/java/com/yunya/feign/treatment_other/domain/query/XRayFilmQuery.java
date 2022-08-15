package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 照片影像查询参数模型
 * @author: LHB
 * @create: 2020-11-19 18:33
 **/
@ApiModel(value = "XRayFilmQuery",description = "照片影像查询参数模型")
@Data
public class XRayFilmQuery implements Serializable {
    @ApiModelProperty("是否开启分页，默认开启")
    private Boolean whetherPage = true;
    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    @ApiModelProperty(value = "X-光片类型 0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片",
            allowableValues = "0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片")
    private Byte type;

    /** 主键id列表*/
    private List<Integer> ids;

}
