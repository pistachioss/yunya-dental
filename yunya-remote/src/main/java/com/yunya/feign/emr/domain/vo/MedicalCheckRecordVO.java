package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：普通电子病历-检查记录VO
 *
 * @author: chenlin
 * @Description: 普通电子病历-检查记录VO
 * @Date: 2022/1/10 15:08
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("普通电子病历-检查记录VO")
public class MedicalCheckRecordVO implements Serializable {

    /** 检查记录id*/
    @ApiModelProperty("检查记录id")
    private Integer recordId;

    /** 检查id*/
    @ApiModelProperty("检查id")
    private Integer checkId;

    /** 检查名称*/
    @ApiModelProperty("检查名称")
    private String checkName;

    /** 牙位*/
    @ApiModelProperty("牙位")
    private Short toothPosition;

    /** 症状id*/
    @ApiModelProperty("症状id")
    private Integer symptomId;

    /** 症状名称*/
    @ApiModelProperty("症状名称")
    private String symptomName;

    /** 备注*/
    @ApiModelProperty("备注")
    private String remark;

    /** 记录日期*/
    @ApiModelProperty("记录日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date recordDate;
}
