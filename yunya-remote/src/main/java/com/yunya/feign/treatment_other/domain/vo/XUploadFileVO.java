package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：文件上传VO
 *
 * @author: chenlin
 * @Description: 文件上传VO
 * @Date: 2022/1/10 18:14
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("文件上传VO")
public class XUploadFileVO implements Serializable {

    @ApiModelProperty("文件ID")
    private Integer fileId;

    @ApiModelProperty("来源ID")
    private Integer sourceId;

    /** 文件来源类型：0-其他，1-治疗计划; 2-普通电子病历*/
    @ApiModelProperty(value = "文件来源类型：0-其他，1-治疗计划; 2-普通电子病历")
    private Byte sourceType;

    /** 文件类型：1-pdf; 2-doc; 3-jgp; 4-png*/
    @ApiModelProperty(value = "文件类型：1-pdf; 2-doc; 3-jgp; 4-png",
            allowableValues = "1-pdf; 2-doc; 3-jgp; 4-png")
    private Byte fileType;

    /** 文件资源定位路径*/
    @ApiModelProperty("文件资源定位路径")
    private String fileLocation;

    /** 文件名*/
    @ApiModelProperty("文件名")
    private String fileName;

    /** 上传时间*/
    @ApiModelProperty("上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date uploadTime;

    /** 创建人id*/
    @ApiModelProperty("创建人id")
    private Integer crtId;

    /** 创建时间*/
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;

    /** 更新人id*/
    @ApiModelProperty("更新人id")
    private Integer updId;

    /** 修改时间*/
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date updTime;
}
