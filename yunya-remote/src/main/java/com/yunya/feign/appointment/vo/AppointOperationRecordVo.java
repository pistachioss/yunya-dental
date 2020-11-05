package com.yunya.feign.appointment.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * 预约操作记录视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 12:21
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "AppointOperationRecordVo",description = "预约操作记录视图模型")
@Data
@ToString
public class AppointOperationRecordVo implements Serializable {
    /**
     * 预约操作记录id
     */
    @ApiModelProperty(value = "预约操作记录id")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 预约ID
     */
    @ApiModelProperty(value = "预约ID")
    private Integer appointmentId;

    /**
     * 操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)
     */
    @ApiModelProperty(value = "操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)")
    private Byte operateType;

    /**
     * 修改预约项目
     */
    @ApiModelProperty(value = "修改预约项目")
    private String operateItem;

    /** 预约修改之前的内容 */
    @ApiModelProperty(value = "预约修改之前的内容")
    private String beforeOperation;

    /** 预约修改之后的内容 */
    @ApiModelProperty(value = "预约修改之后的内容")
    private String afterOperation;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    /** 创建人名 */
    @ApiModelProperty(value = "创建人名")
    private String crtName;
}
