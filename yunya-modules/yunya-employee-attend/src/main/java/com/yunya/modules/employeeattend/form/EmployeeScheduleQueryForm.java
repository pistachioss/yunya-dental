package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleQueryForm
 * @description
 * @date 2020/7/10 9:47
 */
@Data
public class EmployeeScheduleQueryForm {

    /**
     * 医生或助手id
     */
    @ApiModelProperty("医生或助手id")
    private Integer userId;
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String name;
    /**
     * 岗位ID列表
     */
    @ApiModelProperty("岗位ID列表")
    private List<Integer> postNames;
    /**
     * 门诊ID
     */
    @ApiModelProperty("门诊ID")
    private Integer clinicId;
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startDate;

    /**
     * 截止时间
     */
    @ApiModelProperty("截止时间")
    private String endDate;

    @ApiModelProperty("页数")
    private Integer page = 1;

    @ApiModelProperty("每页个数")
    private Integer size = 10;

    @ApiModelProperty("是否分页")
    private Boolean whetherPage = false;

    /** 类型 */
    @ApiModelProperty(value = "类型")
    private String type;

    /** 工作日期 */
    @ApiModelProperty(value = "工作日期")
    private Date workDate;


    /** 工作开始日期 */
    @ApiModelProperty(value = "工作开始日期")
    private Date betweenWorkDate;

    /** 工作结束日期 */
    @ApiModelProperty(value = "工作结束日期")
    private Date andWorkDate;

    /** 工作日期列表 */
    @ApiModelProperty(value = "工作日期列表")
    private Date workDates;

    /**
     * 医生或助手id列表
     */
    @ApiModelProperty("医生或助手id列表")
    private List<Integer> userIds;
}
