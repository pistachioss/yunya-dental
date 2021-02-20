package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 牙根尖图片详情列表
 * @author: LHB
 * @create: 2020-11-19 20:42
 **/
@Data
@ApiModel(value = "ToothRootDetailVo",description = "牙根尖图片详情列表")
public class ToothRootDetailVo implements Serializable {
    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "图片资源定位路径")
    private String url;

    @ApiModelProperty(value = "图片名称")
    private String photoName;

    @ApiModelProperty(value = "图片上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date updTime;
}
