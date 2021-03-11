package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: PC台式机照片影像小程序视图模型
 * @author: LHB
 * @create: 2020-12-07 16:03
 **/
@Data
@ApiModel(value = "DesktopMiniProgramVO",description = "PC台式机照片影像小程序视图模型")
public class DesktopMiniProgramVO implements Serializable {
    @ApiModelProperty("就诊记录ID")
    private Integer id;
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("患者姓名")
    private String patientName;
    @ApiModelProperty("主治医师ID")
    private Integer dentistId;
    @ApiModelProperty("主治医生名字")
    private String dentistName;
    @ApiModelProperty("病历编号")
    private String medicalNumber;
    @ApiModelProperty("患者生日")
    private String birthday;
    @ApiModelProperty("就诊开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String  treatStartTime;
    @ApiModelProperty("是否为初诊,1-初诊；0-复诊")
    private Integer firstVisit;
    @ApiModelProperty("是否有上传图片")
    private Boolean hasImg;
    @ApiModelProperty("患者手机号")
    private String mobile;

}
