package com.yunya.feign.emr.domain.model;

import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：病历照片记录添加模型
 *
 * @author: chenlin
 * @Description: 病历照片记录添加模型
 * @Date: 2022/3/24 10:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("病历照片记录添加or修改模型")
public class MedicalPictureRecordModel implements Serializable {

    /** 记录id*/
    @ApiModelProperty("记录id（修改时用）")
    private Integer id;

    /** 患者id*/
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 日期*/
    @ApiModelProperty(value = "日期", required = true)
    @NotEmpty(message = "日期不能为空")
    private String name;

    @ApiModelProperty("照片列表")
    private List<XUploadFileVO> files;
}
