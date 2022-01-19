package com.yunya.feign.treatment_other.domain.model;

import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/10 17:17
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("普通电子病历-照片影像新增模型")
public class MedicalRayFilmModel implements Serializable {
    @ApiModelProperty("普通电子病历Id")
    @NotNull(message = "普通电子病历id不能为空")
    private Integer medicalId;

    @ApiModelProperty("文件类型")
    @NotNull(message = "文件类型不能为空")
    private Byte sourceType;

    @ApiModelProperty("照片影像列表")
    private List<XUploadFileVO> rayFiles;

    @ApiModelProperty("电子病历提交人id")
    @NotNull(message = "电子病历提交人id不能为空")
    private Integer crtId;

    @ApiModelProperty("电子病历提交时间")
    @NotNull(message = "电子病历提交时间不能为空")
    private Date crtTime;
}
