package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel("图片影像视图模型")
@Data
public class XRayFilmVo implements Serializable {
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("患者ID")
    private Integer patientId;

    @ApiModelProperty(value = "X-光片类型",
            allowableValues = "0-照片；1-根尖片；2-全景片；3-正位片；4-侧位片；5-关节片；6-正畸片；7-其他片")
    private Byte type;

    @ApiModelProperty("图片资源定位路径")
    private String url;

    @ApiModelProperty("图片名")
    private String photoName;

    @ApiModelProperty("上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date updTime;
}