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
 * @description: 查询随访记录模型
 * @author: LHB
 * @create: 2020-08-21 17:42
 **/
@ApiModel(value = "查询随访记录模型")
@Data
@ToString
public class VisitingRecordQuery implements Serializable {
    /** 是否分页 */
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;
    /** 页码 */
    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;
    /** 每页显示数量 */
    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;
    /** 检索开始时间(患者档案检索用) */
    @ApiModelProperty(value = "检索开始时间(患者档案检索用)")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date searchBeginTime;
    /** 检索结束时间(患者档案检索用) */
    @ApiModelProperty(value = "检索结束时间(患者档案检索用)")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date searchEndTime;
    /** 检索类型：1-按随访创建时间检索；2-按随访执行时间检索，默认按随访执行时间检索 */
    @ApiModelProperty(value = "检索类型：1-按随访创建时间检索；2-按随访执行时间检索，默认按随访执行时间检索", required = true)
    private Integer searchId = 2;
    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer orgId;
    /** 医生id */
    @ApiModelProperty(value = "医生id(用户岗位只有医生的时候必传，其他情况不传)", notes = "权限控制")
    private Integer dentistId;
    /** 患者id */
    @ApiModelProperty(value = "患者id(患者档案中查询需要传入)")
    private Integer patientId;
    /** 患者名字 */
    @ApiModelProperty(value = "患者名字")
    private String distentName;
    /** 随访日期*/
    @ApiModelProperty(value = "随访日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date visitingDate;
    /** 患者姓名、手机号 */
    @ApiModelProperty(value = "患者姓名、手机号")
    private String search;
    /** 患者病历号 */
    @ApiModelProperty(value = "患者病历号")
    private String medicalNumber;
    /** 是否启用 */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;
    /** 随访状态 0-待随访；1-随访完成 */
    @ApiModelProperty(value = "随访状态 false-待随访；true-随访完成")
    private Boolean status;
    /** 创建人ID */
    @ApiModelProperty(value = "创建人ID")
    private Integer crtId;
    /** 实际随访执行人ID */
    @ApiModelProperty(value = "实际随访执行人ID",hidden = true)
    private Integer executorId;
    /** 模糊匹配患者病历号 */
    @ApiModelProperty(value = "模糊匹配患者病历号")
    private String likeMedicalNumber;
}
