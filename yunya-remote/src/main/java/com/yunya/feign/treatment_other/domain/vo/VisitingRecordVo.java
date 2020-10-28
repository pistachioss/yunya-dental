package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 随访记录视图模型
 * @author: LHB
 * @create: 2020-08-21 20:25
 **/
@ApiModel(value = "VisitingRecordVo", description = "随访记录视图模型")
@Data
@ToString
public class VisitingRecordVo implements Serializable {
    /**
     * 随访记录ID
     */
    @ApiModelProperty(value = "随访记录ID")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 患者就诊ID
     */
    @ApiModelProperty(value = "患者就诊ID")
    private Integer treatmentId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 医生ID 默认为末诊医生
     */
    @ApiModelProperty(value = "医生ID 默认为末诊医生")
    private Integer dentistId;

    /**
     * 科室ID 默认末诊科室
     */
    @ApiModelProperty(value = "科室ID")
    private Integer deptRoomId;

    /**
     * 科室名字 默认末诊科室
     */
    @ApiModelProperty(value = "科室名字 默认末诊科室")
    private String deptRoomName;

    /**
     * 就诊日期
     */
    @ApiModelProperty(value = "就诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date treatmentDate;

    /**
     * 随访日期
     */
    @ApiModelProperty(value = "随访日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date visitingDate;

    /**
     * 随访时间
     */
    @ApiModelProperty(value = "随访时间")
    private String visitingTime;

    /**
     * 随访原因 新建随访
     */
    @ApiModelProperty(value = "随访原因 新建随访")
    private String reason;

    /**
     * 随访内容 执行随访
     */
    @ApiModelProperty(value = "随访内容 执行随访")
    private String visitingContent;

    /**
     * 是否启用 0-不启用；1-启用
     */
    @ApiModelProperty(value = "是否启用 0-不启用；1-启用")
    private Boolean inservice;

    /** 随访状态 0-待随访；1-随访完成*/
    @ApiModelProperty(value = "随访状态 0-待随访；1-随访完成")
    private Boolean status;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名")
    private String crtName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date crtTime;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /** 后续随访个数 */
    @ApiModelProperty(value = "后续随访个数")
    private Integer visitingCount;

    /** 更新人ID(随访人ID) */
    @ApiModelProperty(value = "更新人ID(随访人ID)")
    private Integer uptId;

    /** 更新人名字(随访人名字) */
    @ApiModelProperty(value = "更新人名字(随访人名字)")
    private String updName;
    /** 随访时间（更新时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updTime;

    /******************************* 患者信息 ********************************/
    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 患者病历号 */
    @ApiModelProperty(value = "患者病历号")
    private String medicalNumber;

    /** 患者手机号 */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    /** 患者出生日期 */
    @ApiModelProperty(value = "患者出生日期")
    private String birthday;

    /** 性别 */
    @ApiModelProperty(value = "性别")
    private Byte gender;

    /** 年龄 */
    @ApiModelProperty(value = "年龄")
    private Integer age;


    /** 会员图标 */
    @ApiModelProperty(value = "会员图标")
    private Byte memberIcon;

    /** 患者过敏原 */
    @ApiModelProperty(value = "患者过敏原")
    private String allergen;

    /** 欠费总额 */
    @ApiModelProperty(value = "欠费总额")
    private BigDecimal arrears;

    /** 档案备注 */
    @ApiModelProperty(value = "档案备注")
    private String patientRemark;

}
