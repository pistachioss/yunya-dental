package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：考勤打卡记录修改模型
 *
 * @author: chenlin
 * @Description: 考勤打卡记录修改模型
 * @Date: 2020/11/9 20:40
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡记录修改模型")
public class AttendancePunchRecordForm implements Serializable {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    @NotNull(message = "考勤数据不存在")
    private Integer id;

    /** 用户id */
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    /** 打卡日期 */
    @ApiModelProperty(value = "打卡日期")
    private Date punchDate;

    /** 打卡时间 */
    @ApiModelProperty(value = "打卡时间")
    private Date punchTime;

    /** 打卡经度 */
    @ApiModelProperty(value = "打卡经度")
    private String longitude;

    /** 打卡纬度 */
    @ApiModelProperty(value = "打卡纬度")
    private String latitude;

    /** 打卡类型 0：上班 1：下班 */
    @ApiModelProperty(value = "打卡类型 0：上班 1：下班")
    private Byte punchType;

    /** 打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡 */
    @ApiModelProperty(value = "打卡状态 0:上班正常 1：迟到  2:下班正常  3:早退  4:无效卡")
    private Byte punchStatus;

    /** 考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤 */
    @ApiModelProperty(value = "考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤")
    private Byte source;

    /** 考勤地址id */
    @ApiModelProperty(value = "考勤地址id")
    private Integer attendanceAddressId;

    /** 打卡地址/wifi名称 */
    @ApiModelProperty(value = "打卡地址/wifi名称")
    private String punchAddress;

    /** 打卡项目来源：员工排班id、请假id、加班id、外勤id */
    @ApiModelProperty(value = "打卡项目来源：员工排班id、请假id、加班id、外勤id")
    private Integer sourceId;

    /** wifi的mac地址 */
    @ApiModelProperty(value = "wifi的mac地址")
    private String wifiMacAddress;
}
