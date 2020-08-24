package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 简介: 牙周期列表模型
 *
 * @author: chow
 * @date: 2020/8/11 15:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("查询牙周期列表模型")
@Data
@ToString
public class PhotoServiceQuery {

    @ApiModelProperty(value = "患者id")
    @NotNull
    private int patientId;
    @ApiModelProperty(value = "图片类型(0=照片,1=根尖片,2=全景片,3=正位片,4=侧位片,5=关节片,6=正畸片,7=其他片)")
    @NotNull
    private int photoType;





}