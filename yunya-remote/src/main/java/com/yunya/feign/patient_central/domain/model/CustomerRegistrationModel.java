package com.yunya.feign.patient_central.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介: 客户登记model
 *
 * @author: WY
 * @date: 2020/11/5 17:36
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class CustomerRegistrationModel implements Serializable {

    /**
     * 患者姓名 字符串，长度64
     */
    @NotNull(message = "患者名称为空！")
    @ApiModelProperty(value = "患者名称",required = true)
    private String name;


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
     * 性别 0-男；1-女；2-未知
     */
    @NotNull(message = "性别不能为空！")
    @ApiModelProperty(value = "性别 0-男；1-女；2-未知",required = false)
    private Byte gender;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期",required = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /**
     * 患者来源类型 患者来源分类ID
     */
    @ApiModelProperty(value = "患者来源分类ID",required = false)
    private Integer originType;

    /**
     * 患者来源关联ID 患者来源关联ID（员工ID/患者ID/活动ID）
     */
    @ApiModelProperty(value = "患者来源关联ID",required = false)
    private Integer originId;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;


    /**
     * 推荐来源id
     */
    @ApiModelProperty(value = "推荐来源id")
    private Integer sourceId;
}