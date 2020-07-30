package com.yunya.feign.patient_central.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
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
    private Integer id;

    /**
     * 诊所ID 添加患者的组织ID
     */
    @NotNull(message = "诊所ID为空！")
    private Integer orgId;

    /**
     * 患者姓名 字符串，长度64
     */
    @NotNull(message = "患者名称为空！")
    private String name;

    /**
     * 拼音姓名 字符串，长度64
     */
    private String pinyinName;

    /**
     * 头像地址 患者头像存储路径
     */

    private String avatarPath;

    /**
     * 手机号码 长度14
     */
    private String mobile;

    /**
     * 手机号所属人 手机号所属人字典ID
     */
    @NotNull(message = "手机号所属人为空！")
    private Integer mobileOwner;

    /**
     * 病历号 患者第一次就诊时生成
     */
    private String medicalNummber;

    /**
     * 性别 0-男；1-女；2-未知
     */
    private Byte gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 出生日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /**
     * 患者来源类型 患者来源分类ID
     */
    private Integer originType;

    /**
     * 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    @NotNull(message = "患者来源关联ID为空！")
    private Integer originId;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否有效 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    private Integer crtId;

    /**
     * 创建人姓名
     */
    private String crtName;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人ID
     */
    private Integer uptId;

    /**
     * 更新人姓名
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;
}
