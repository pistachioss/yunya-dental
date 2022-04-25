package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

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
//     */
//    @ApiModelProperty("出生日期")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    private Date birthday;

    /**
     * 出生日期
     */
    @ApiModelProperty("出生日期")
    private String birthday;

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
    private String lastVisitTime;

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


    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private Integer crtId;

    /** 属性类型：0-成人，1-儿童*/
    @ApiModelProperty("属性类型：0-成人，1-儿童")
    private Byte attribute;

    /** 是否去世*/
    @ApiModelProperty("是否去世")
    private Boolean hasDied;

    /** 患者分组id列表*/
    @ApiModelProperty("患者分组id列表")
    private List<Integer> patientGroupIds;
}
