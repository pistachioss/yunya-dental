package com.yunya.feign.treatment_other.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 查询随访提醒
 * @author: LHB
 * @create: 2020-08-21 17:42
 **/
@ApiModel(value = "查询随访提醒")
@Data
@ToString
public class VisitingRemindQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "检索类型：1-按随访创建时间检索；2-按随访执行时间检索，默认按随访执行时间检索", required = true)
    private Integer searchId = 2;

    @ApiModelProperty(value = "检索开始时间(患者档案检索用)")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date searchBeginTime;

    @ApiModelProperty(value = "检索结束时间(患者档案检索用)")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date searchEndTime;

    /** 医生id */
    @ApiModelProperty(value = "医生id(用户岗位只有医生的时候必传，其他情况不传)", notes = "权限控制")
    private Integer dentistId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer orgId;

    /** 患者id */
    @ApiModelProperty(value = "患者id(患者档案中查询需要传入)")
    private Integer patientId;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String distentName;

    /**
     * 提醒日期
     */
    @ApiModelProperty(value = "提醒日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date remindDate;

    /** 患者姓名、手机号 */
    @ApiModelProperty(value = "患者姓名、手机号")
    private String search;

    /** 患者病历号 */
    @ApiModelProperty(value = "患者病历号")
    private String medicalNumber;

    /** 是否启用 */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;

    /** 提醒状态 0-待提醒；1-提醒完成 */
    @ApiModelProperty(value = "提醒状态 false-待提醒；true-提醒完成")
    private Boolean status;

    /** 创建人ID */
    @ApiModelProperty(value = "创建人ID")
    private Integer crtId;
}
