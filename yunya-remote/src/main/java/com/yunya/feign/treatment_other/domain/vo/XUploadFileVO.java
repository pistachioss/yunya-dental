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

    @ApiModelProperty("来源ID")
    private Integer sourceId;

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
}
