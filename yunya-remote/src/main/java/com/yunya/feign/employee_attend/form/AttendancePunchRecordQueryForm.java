package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：考勤打卡记录查询参数模型
 *
 * @author: chenlin
 * @Description: 考勤打卡记录查询参数模型
 * @Date: 2020/11/9 15:42
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡记录查询参数模型")
public class AttendancePunchRecordQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 打卡日期 */
    @ApiModelProperty(value = "打卡日期")
    private Date punchDate;

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期")
    private Date betweenDate;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期")
    private Date andDate;

    /** 打卡地址经度 */
    @ApiModelProperty(value = "打卡地址经度")
    private String longitude;

    /** 打卡地址纬度 */
    @ApiModelProperty(value = "打卡地址纬度")
    private String latitude;

    /** Wifi的mac地址 */
    @ApiModelProperty(value = "Wifi的mac地址")
    private String wifiMacAddress;

    /** Wifi的名称 */
    @ApiModelProperty(value = "Wifi的名称")
    private String wifiName;

    /** 打卡状态 */
    @ApiModelProperty(value = "打卡状态")
    private Byte punchStatus;

    /** 打卡状态列表 */
    @ApiModelProperty(value = "打卡状态列表", required = true)
    private Byte[] punchStatusList;

    /** 打卡状态不等于 */
    @ApiModelProperty(value = "打卡状态不等于", required = true)
    private Byte notEqualPunchStatus;

    /** 是否打卡：0-否，1-是 */
    @ApiModelProperty(value = "是否打卡：0-否，1-是", required = true)
    private Byte isPunch;

    /** 打卡类型 0：上班 1：下班 */
    @ApiModelProperty(value = "打卡类型 0：上班 1：下班")
    private Byte punchType;


    /** 不属于的id列表 */
    @ApiModelProperty(value = "不属于的id列表")
    private List<Integer> notInIds;
}
