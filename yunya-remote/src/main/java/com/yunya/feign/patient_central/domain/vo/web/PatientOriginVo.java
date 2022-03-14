package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.models.patient_central.PatientOrigin;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 返回患者来源类型信息模型
 *
 * @author: WY
 * @date 2020/8/14 17:01
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者来源类型信息模型")
public class PatientOriginVo implements Serializable {
    /**
     * 患者来源ID
     */
    @ApiModelProperty("患者来源ID")
    private Integer id;

    /**
     * 患者来源名称
     */
    @ApiModelProperty("患者来源名称")
    private String name;

    /**
     * 患者来源英文名称
     */
    @ApiModelProperty("患者来源英文名称")
    private String englishName;

    /**
     * 患者来源类型
     */
    @ApiModelProperty("患者来源类型")
    private Integer originType;

    /** 患者来源类型的子类型列表*/
    @ApiModelProperty("患者来源类型的子类型列表")
    private List<PatientOrigin> children;
}
