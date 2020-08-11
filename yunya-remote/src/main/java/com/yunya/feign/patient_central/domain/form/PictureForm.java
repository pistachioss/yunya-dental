package com.yunya.feign.patient_central.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
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
    @ApiModelProperty(value = "照片id",required = true)
    private String faceGuid;

    /**
     * 照片所有者（人员）guid
     */
    @ApiModelProperty(value = "照片所有者（人员id）personGuid",required = true)
    private String personGuid;

}
