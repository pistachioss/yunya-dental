package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者信息模型
 *
 * @author: WY
 * @date 2020/7/25 15:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者信息模型")
public class PatientBaseInfoVo implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty("患者ID")
    private Integer id;

    /**
     * 患者姓名 字符串，长度64
     */
    @ApiModelProperty("患者姓名")
    private String name;

    /**
     * 患者头像url
     */
    @ApiModelProperty("患者头像路径")
    private String faceUrl;

    /**
     * 手机号码 长度14
     */
    @ApiModelProperty("手机号码")
    private String mobile;

    /**
     * 手机号所属人 手机号所属人字典ID
     */
    @ApiModelProperty("手机号所属人字典ID")
    private Integer mobileOwner;

    /**
     * 手机号所属名称
     */
    @ApiModelProperty("手机号所属名称")
    private String mobileOwnerName;

    /**
     * 病历号 患者第一次就诊时生成
     */
    @ApiModelProperty("病历号")
    private String medicalNumber;

    /**
     * 性别 0-男；1-女；2-未知
     */
    @ApiModelProperty("性别 0-男；1-女；2-未知")
    private Byte gender;

    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private Integer age;

    /**
     * 出生日期
     */
    @ApiModelProperty("出生日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /**
     * 患者来源类型 患者来源分类ID
     */
    @ApiModelProperty("患者来源分类ID")
    private Integer originType;

    /**
     * 患者来源类型名称
     */
    @ApiModelProperty("患者来源类型名称")
    private String originTypeName;

    /**
     * 患者来源关联ID 患者来源关联ID（活动ID）
     */
    @ApiModelProperty("患者来源关联ID（活动ID）")
    private Integer originId;

    /**
     * 来源名称 或 推荐人名称
     */
    @ApiModelProperty("来源名称 或 推荐人名称")
    private String originName;

    /**
     * 备注 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

    /**
     * 末诊时间
     */
    @ApiModelProperty("末诊时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date lastVisitTime;

    /**
     * 末诊医生
     */
    @ApiModelProperty("末诊医生")
    private String lastVisit;

    /**
     * 来源id
     */
    @ApiModelProperty("来源id")
    private Integer sourceId;

    /**
     * 来源父级id
     */
    @ApiModelProperty("来源父级id")
    private Integer sourceParentId;

    /**
     * 来源名称（活动名称 或者 患者来源类型名称）
     */
    @ApiModelProperty("来源名称")
    private String sourceName;

}
