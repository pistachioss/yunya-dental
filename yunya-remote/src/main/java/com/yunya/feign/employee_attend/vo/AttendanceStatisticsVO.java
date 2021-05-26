package com.yunya.feign.employee_attend.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：考勤统计响应模型
 *
 * @author: chenlin
 * @Description: 考勤统计响应模型
 * @Date: 2020/11/10 16:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("考勤统计响应模型")
public class AttendanceStatisticsVO implements Serializable {

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 组织id */
    @ApiModelProperty(value = "组织id")
    private Integer orgId;

    /** 姓名 */
    @Excel(name = "姓名")
    @ApiModelProperty(value = "姓名")
    private String employeeName;

    /** 门诊 */
    @Excel(name = "门诊")
    @ApiModelProperty(value = "门诊")
    private String orgName;

    /** 出勤天数 */
    @Excel(name = "出勤天数")
    @ApiModelProperty(value = "出勤天数")
    private Integer attendanceNum;

    /** 是否满勤*/
    @Excel(name = "是否满勤")
    @ApiModelProperty(value = "是否满勤")
    private String isFull;

    /** 工作日时长（分钟）*/
    @Excel(name = "工作日时长（分钟）")
    @ApiModelProperty(value = "工作日时长（分钟）")
    private Long workDateMinute;

    /** 工作日加班时长（分钟）*/
    @Excel(name = "工作日加班时长（分钟）")
    @ApiModelProperty(value = "工作日加班时长（分钟）")
    private Long workDateOvertimeMinute;

    /** 工作日加班超30分钟以上时长（分钟）*/
    @Excel(name = "工作日加班超30分钟以上时长（分钟）")
    @ApiModelProperty(value = "工作日加班超30分钟以上时长（分钟）")
    private Long workDateOvertime30Minute;

    /** 休息日加班时长（分钟）*/
    @Excel(name = "休息日加班时长（分钟）")
    @ApiModelProperty(value = "休息日加班时长（分钟）")
    private Long restDateOvertimeMinute;

    /** 请假时长（分钟）*/
    @Excel(name = "请假时长（分钟）")
    @ApiModelProperty(value = "请假时长（分钟）")
    private Long leaveMinute;

    /** 外勤时长（分钟）*/
    @Excel(name = "外勤时长（分钟）")
    @ApiModelProperty(value = "外勤时长（分钟）")
    private Long fieldMinute;

    /** 休息天数 */
    @ApiModelProperty(value = "休息天数")
    private Integer restNum;

    /** 迟到次数 */
    @Excel(name = "迟到次数")
    @ApiModelProperty(value = "迟到次数")
    private Integer lateNum;

    /** 迟到时长（分钟） */
    @ApiModelProperty(value = " 迟到时长（分钟）")
    private Long lateMinute;

    /** 早退次数 */
    @Excel(name = "早退次数")
    @ApiModelProperty(value = "早退次数")
    private Integer earlyNum;

    /** 早退时长 */
    @ApiModelProperty(value = "早退时长")
    private Long earlyMinute;

    /** 请假次数 */
    @ApiModelProperty(value = "请假次数")
    private Integer leaveNum;

    /** 加班次数 */
    @ApiModelProperty(value = "加班次数")
    private Integer workOvertimeNum;

    /** 缺卡次数 */
    @Excel(name = "缺卡次数")
    @ApiModelProperty(value = "缺卡次数")
    private Integer unpunchNum;

    /** 外勤次数 */
    @ApiModelProperty(value = "外勤次数")
    private Integer fieldNum;

    /** 无效卡次数 */
    @Excel(name = "无效卡次数")
    @ApiModelProperty(value = "无效卡次数")
    private Integer invalidNum;

    /** 休息列表 */
    @ApiModelProperty(value = "休息列表")
    private List<AttendancePunchRecordVO> restStatisticsList;

    /** 迟到列表 */
    @ApiModelProperty(value = "迟到列表")
    private List<AttendancePunchRecordVO> lateStatisticsList;

    /** 早退列表 */
    @ApiModelProperty(value = "早退列表")
    private List<AttendancePunchRecordVO> earlyStatisticsList;

    /** 请假列表 */
    @ApiModelProperty(value = "请假列表")
    private List<AttendancePunchRecordVO> leaveStatisticsList;

    /** 加班列表 */
    @ApiModelProperty(value = "加班列表")
    private List<AttendancePunchRecordVO> workOvertimeStatisticsList;

    /** 缺卡列表 */
    @ApiModelProperty(value = "缺卡列表")
    private List<AttendancePunchRecordVO> unpunchStatisticsList;

    /** 外勤列表 */
    @ApiModelProperty(value = "外勤列表")
    private List<AttendancePunchRecordVO> fieldStatisticsList;

    /** 无效卡列表 */
    @ApiModelProperty(value = "无效卡列表")
    private List<AttendancePunchRecordVO> invalidStatisticsList;
}
