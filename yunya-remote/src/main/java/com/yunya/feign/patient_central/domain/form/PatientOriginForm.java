package com.yunya.feign.patient_central.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 患者来源修改Form
 *
 * @author: WY
 * @date 2020/8/5 15:20
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者来源修改Form")
public class PatientOriginForm implements Serializable {
    /**
     * 患者来源ID
     */
    @ApiModelProperty(value = "患者来源主键ID")
    private Integer id;

    /**
     * 患者来源父ID（顶级为0）
     */
    @ApiModelProperty(value = "患者来源父ID（顶级为0）")
    private Integer parentId;

    /**
     * 患者来源名称
     */
    @NotNull(message = "患者来源名称不能为空！")
    @ApiModelProperty(value = "患者来源父ID（顶级为0）",required = true)
    private String name;

    /**
     * 患者来源类型 1.员工 2.老患者 3.活动 4.合作商
     */
    private Integer originType;

    /**
     * 二维码地址
     */
    @ApiModelProperty(value = "二维码地址")
    private String qrCodePath;

    /**
     * 是否允许操作（编辑、删除）
     */
    @ApiModelProperty(value = "是否允许操作（编辑、删除）默认是")
    private Boolean allowOperate;

    /**
     * 是否有时间限制（0-否；1-是））
     */
    @ApiModelProperty(value = "是否有时间限制（0-否；1-是））默认0")
    private Boolean timeLimit;

    /**
     * 限制开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "限制开始时间")
    private Date limitStartDate;

    /**
     * 限制介绍时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "限制介绍时间")
    private Date limitEndDate;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;
}
