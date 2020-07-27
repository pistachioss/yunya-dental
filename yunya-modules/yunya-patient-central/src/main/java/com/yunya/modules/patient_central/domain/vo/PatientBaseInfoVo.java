package com.yunya.modules.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/25 15:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientBaseInfoVo implements Serializable {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 诊所ID 添加患者的组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 患者姓名 字符串，长度64
     */
    private String name;

    /**
     * 拼音姓名 字符串，长度64
     */
    @Column(name = "pinyin_name")
    private String pinyinName;

    /**
     * 头像地址 患者头像存储路径
     */
    @Column(name = "avatar_path")
    private String avatarPath;

    /**
     * 手机号码 长度14
     */
    private String mobile;

    /**
     * 手机号所属人 手机号所属人字典ID
     */
    @Column(name = "mobile_owner")
    private Integer mobileOwner;

    /**
     * 病历号 患者第一次就诊时生成
     */
    @Column(name = "medical_nummber")
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
    private Date birthday;

    /**
     * 患者来源类型 患者来源分类ID
     */
    @Column(name = "origin_type")
    private Integer originType;

    /**
     * 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    @Column(name = "origin_id")
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
    @Column(name = "crt_id")
    @NotNull(message = "创建人ID为空！")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 更新人姓名
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}
