package com.yunya.feign.emr.domain.vo;

import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：病历照片记录VO
 *
 * @author: chenlin
 * @Description: 病历照片记录VO
 * @Date: 2022/3/24 10:59
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("病历照片记录VO")
public class MedicalPictureRecordVO implements Serializable {

    /** 记录id*/
    @ApiModelProperty("记录id")
    private Integer id;

    /** 记录id*/
    @ApiModelProperty("日期")
    private String name;

    /** 照片*/
    @ApiModelProperty("照片")
    List<XUploadFileVO> files;
}
