package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：考勤打卡信息响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡信息响应模型
 * @Date: 2020/11/9 15:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤打卡信息响应模型")
public class AttendancePunchInfoVO implements Serializable {

    /** 打卡记录主键id */
    @ApiModelProperty(value = "打卡记录主键id")
    private Integer id;

    /** 是否绑定了考勤设备 */
    @ApiModelProperty(value = "是否绑定了考勤设备")
    private boolean hasDeviceBinding;

    /** 打卡类型：0:上班打卡 1：迟到打卡  2:下班打卡  3:早退打卡  4:无效卡  5:不在考勤范围  6：未排班*/
    @ApiModelProperty(value = "打卡类型: 0:上班打卡 1：迟到打卡  2:下班打卡  3:早退打卡  4:无效卡  5:不在考勤范围  6：未排班")
    private Byte punchStatus;

    /** 考勤地址id */
    @ApiModelProperty(value = "考勤地址id")
    private Integer attendanceAddressId;

    /** 打卡地址或者打卡Wifi名称 */
    @ApiModelProperty(value = "打卡地址或者打卡Wifi名称")
    private String punchName;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 组织名称 */
    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /** 考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤 */
    @ApiModelProperty(value = "考勤项目来源: 0：上班班次； 1：休息班次；2：按天请假； 3：按班次请假；4：加班；5：外勤")
    private Byte source;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date endTime;

    /** 班次名称 */
    @ApiModelProperty(value = "班次名称")
    private String name;

    /** 打卡方式: 0-地址打卡，1-Wifi打卡 */
    @ApiModelProperty(value = "打卡方式: 0-地址打卡，1-Wifi打卡")
    private Integer punchMode;

    /** 工作时长*/
    @ApiModelProperty(value = "工作时长")
    private Long workLength;

    /** 打卡次数 */
    @ApiModelProperty(value = "打卡次数")
    private Integer punchCount;

    /** 打卡项目列表 */
    @ApiModelProperty(value = "打卡项目列表")
    private List<AttendancePunchItemVO> attendancePunchItemVOS;
}
