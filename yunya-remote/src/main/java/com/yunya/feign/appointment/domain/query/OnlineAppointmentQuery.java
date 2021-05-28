package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 在线预约申请参数
 * @author: LHB
 * @create: 2021-05-19 10:02
 **/
@ApiModel(value = "OnlineAppointmentQuery",description = "在线预约申请参数")
@Data
public class OnlineAppointmentQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 门诊ID
     */
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID",hidden = true)
    private Integer itemId;

    /**
     * 微信用户唯一标识
     */
    @ApiModelProperty("微信用户唯一标识")
    private String openId;

    /**
     * 医生ID
     */
    @ApiModelProperty("医生ID")
    private Integer dentistId;

    /**
     * 患者ID,可能为空
     */
    @ApiModelProperty("患者ID,可能为空")
    private Integer patientId;

    /**
     * 预约开始日期
     */
    @ApiModelProperty("预约开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String appointStartDate;

    /**
     * 预约结束日期
     */
    @ApiModelProperty("预约结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String appointEndDate;

    /**
     * 患者名字/手机号
     */
    @ApiModelProperty("患者名字/手机号")
    private String searchStr;

    /**
     * 患者名字
     */
    @ApiModelProperty(value = "患者名字",hidden = true)
    private String patientName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",hidden = true)
    private String phone;

    /**
     * 预约申请状态 0-申请中；1-通过；2-取消
     */
    @ApiModelProperty("预约申请状态 0-申请中；1-通过；2-取消")
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    @ApiModelProperty("是否有效，是否删除(默认有效) 1-有效；0删除")
    private Boolean inservice;

    /**
     * 预约申请时间
     */
    @ApiModelProperty("预约申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String crtTime;

    /**
     * 预约确认状态
     */
    @ApiModelProperty("预约确认状态 ")
    private Boolean confirmStatus;
}
