package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者亲属关系模型
 *
 * @author: WY
 * @date 2020/7/29 9:32
 * @description: 患者亲属信息列表
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者亲属关系模型")
public class PatientKinRelationVo implements Serializable {
    /**
     * 主键 , id为0表示转介绍数据
     */
    @ApiModelProperty("主键id, id为-1表示转介绍数据")
    private Integer id;

    /**
     * 患者ID
     */
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /**
     * 关联患者ID
     */
    @ApiModelProperty("关联患者ID")
    private Integer linkedPatientId;

    /**
     * 关系人姓名
     */
    @ApiModelProperty("关系人姓名")
    private String relationName;

    /**
     * 亲属关系字典类型ID 字典管理
     */
    @ApiModelProperty("亲属关系字典类型ID 字典管理")
    private Integer kinshipId;

    /**
     * 性别 0-男；1-女；2-未知
     */
    @ApiModelProperty("性别 0-男；1-女；2-未知")
    private Byte gender;

    /**
     * 手机号码 长度14
     */
    @ApiModelProperty("手机号码")
    private String mobile;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date crtTime;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private Boolean inservice;
}
