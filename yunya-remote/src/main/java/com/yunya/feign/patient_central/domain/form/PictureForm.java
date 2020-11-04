package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> Wo平台照片删除Form
 *
 * @author: WY
 * @date 2020/8/11 13:39
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("Wo平台照片删除Form")
public class PictureForm implements Serializable {

    /**
     * 照片id
     */
    @ApiModelProperty(value = "心跳版-照片id",required = true)
    private String faceId;


}
