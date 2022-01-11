package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：普通电子病历-检查记录新增或修改模型
 *
 * @author: chenlin
 * @Description: 普通电子病历-检查记录新增或修改模型
 * @Date: 2022/1/10 11:36
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("普通电子病历-检查记录新增或修改模型")
public class MedicalCheckRecordModel implements Serializable {

    /** 检查记录id*/
    @ApiModelProperty("检查记录id")
    private Integer recordId;

    /** 检查id*/
    @ApiModelProperty("检查id")
    private Integer checkId;

    /** 牙位*/
    @ApiModelProperty("牙位")
    private Short toothPosition;

    /** 症状id*/
    @ApiModelProperty("症状id")
    private Integer symptomId;

    /** 备注*/
    @ApiModelProperty("备注")
    private String remark;

    /** 操作类型：0-新增，1-修改，2-删除*/
    private Byte operation;

    /** 创建人id*/
    private Integer crtId;

    /** 创建时间*/
    private Date crtTime;

}
