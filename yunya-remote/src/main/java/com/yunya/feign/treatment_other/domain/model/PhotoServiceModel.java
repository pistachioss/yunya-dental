package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "PhotoServiceModel",description = "添加图片影像模块")
@Data
@ToString
public class PhotoServiceModel {

    @ApiModelProperty(value = "诊所id",required = true)
    @NotNull(message = "诊所id不能为空")
    private Integer orgId;

    @ApiModelProperty(value = "患者id",required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    @ApiModelProperty(value = "就诊id",required = true)
    @NotNull(message = "就诊id不能为空")
    private Integer treatmentRecordId;

    @ApiModelProperty(value = "医生id",required = true)
    @NotNull(message = "医生id不能为空")
    private Integer dentistId;

    @ApiModelProperty(value = "图片类型(0=照片,1=根尖片,2=全景片,3=正位片,4=侧位片,5=关节片,6=正畸片,7=其他片)")
    @NotNull(message = "图片类型不能为空")
    private Integer photoType;

    @ApiModelProperty(value = "照片列表",required = true)
    @Valid
    private List<PhotoDetailListModel> photoDetailListModels;
}