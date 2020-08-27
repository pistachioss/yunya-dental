package com.yunya.feign.patient_central.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 新增患者信息
 *
 * @author: WY
 * @date 2020/7/27 20:13
 * @description: 新增患者基本信息参数模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("新增患者信息参数模型")
public class PatientBaseInfoModel implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "患者ID")
    private Integer id;

    /**
     * 诊所ID 添加患者的组织ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 患者姓名 字符串，长度64
     */
    @NotNull(message = "患者名称为空！")
    @ApiModelProperty(value = "患者名称",required = true)
    private String name;

    /**
     * 拼音姓名 字符串，长度64
     */
    @ApiModelProperty(value = "患者名称拼音")
    private String pinyinName;

    /**
     * wo平台对应人员id
     */
    @ApiModelProperty(value = "wo平台对应人员id")
    private String woGuid;

    /**
     * 患者头像url
     */
    private String faceUrl;

    /**
     * 手机号码 长度14
     */
    @NotBlank(message = "患者手机号不能为空")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    @ApiModelProperty(value = "患者手机号码",required = true)
    private String mobile;

    /**
     * 手机号所属人 手机号所属人字典ID
     */
    @NotNull(message = "手机号所属人字典ID不能为空！")
    @ApiModelProperty(value = "手机号所属人字典ID",required = true)
    private Integer mobileOwner;

    /**
     * 病历号 患者第一次就诊时生成
     */
    @ApiModelProperty(value = "病历号")
    private String medicalNumber;

    /**
     * 性别 0-男；1-女；2-未知
     */
    @NotNull(message = "性别不能为空！")
    @ApiModelProperty(value = "性别 0-男；1-女；2-未知",required = true)
    private Byte gender;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空！")
    @ApiModelProperty(value = "出生日期",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /**
     * 患者来源类型 患者来源分类ID
     */
    @ApiModelProperty(value = "患者来源分类ID")
    private Integer originType;

    /**
     * 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    @NotNull(message = "患者来源关联ID不能为空！")
    @ApiModelProperty(value = "患者来源关联ID",required = true)
    private Integer originId;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否有效 是否有效
     */
    @ApiModelProperty(value = "是否有效(默认有效)")
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名")
    private String crtName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @ApiModelProperty(value = "更新人ID")
    private Integer uptId;

    /**
     * 更新人姓名
     */
    @ApiModelProperty(value = "更新人姓名")
    private String updName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updTime;

    /**
     * 推荐来源id
     */
    @ApiModelProperty(value = "推荐来源id")
    private Integer sourceId;
}
