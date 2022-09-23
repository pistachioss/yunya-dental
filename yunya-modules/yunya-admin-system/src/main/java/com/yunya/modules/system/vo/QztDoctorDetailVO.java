package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * 简介: 岗位新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 19:51
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("全诊通医生详情")
public class QztDoctorDetailVO {
    /**
     * 是否开启医生认证（0-否 1-是）
     */
    @ApiModelProperty(value = "是否开启医生认证（0-否 1-是）")
    private Boolean enableCert;

    /**
     * 医生姓名
     */
    @ApiModelProperty(value = "医生姓名")
    private String doctorName;

    /**
     * 身份证
     */
    @ApiModelProperty(value = "身份证")
    @Size(max = 18, message = "身份证号长度不能超过18个字符")
    private String idCard;

    /**
     * 科室id
     */
    @ApiModelProperty(value = "科室id")
    private Integer departId;

    /**
     * 科室名称
     */
    @ApiModelProperty(value = "科室名称")
    private String departName;

    /**
     * 医疗机构ID （全诊医学提供）
     */
    @ApiModelProperty(value = "医疗机构ID （全诊医学提供）")
    private String institutionId;

    /**
     * 医生资格证号
     */
    @ApiModelProperty(value = "医生资格证号")
    private String qualification;

    /**
     * 医生执业证号
     */
    @ApiModelProperty(value = "医生执业证号")
    private String doctorLicence;

    /**
     * 职称(01-主任医师 02-副主任医师 03-主治医师 04-医师 05-助理医师 10-理疗师)
     */
    @ApiModelProperty(value = "职称(01-主任医师 02-副主任医师 03-主治医师 04-医师 05-助理医师 10-理疗师)")
    private String technicalTitle;

    /**
     * 职业范围(多个职业范围逗号隔开  007-口腔 012-医学影像和放射治疗)
     */
    @ApiModelProperty(value = "职业范围(多个职业范围逗号隔开  007-口腔 012-医学影像和放射治疗)")
    private String scopePractice;

    /**
     * 抗菌药物处方权（0:非抗菌药物 1:非限制级 2:限制级 3:特殊级）
     */
    @ApiModelProperty(value = "抗菌药物处方权（0:非抗菌药物 1:非限制级 2:限制级 3:特殊级）")
    private String antibiosisAuthority;

    /**
     * 麻醉药品和第一类精神药品处方资格（0:无 1:有）
     */
    @ApiModelProperty(value = "麻醉药品和第一类精神药品处方资格（0:无 1:有）")
    private String anestheticAuthority;

    /**
     * 执业门诊(多选逗号)
     */
    @ApiModelProperty(value = "执业门诊(多选逗号)")
    private String practiceClinic;

}
