package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.models.employee_attend.AttendancePunchRecord;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.ClinicSchedule;
import com.yunya.models.employee_attend.EmployeeSchedule;
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
@ApiModel("考勤打卡日历信息响应模型")
public class AttendancePunchCalendarInfoVO implements Serializable {
    
    /** 今日排班 */
    @ApiModelProperty(value = "今日排班")
    private List<AttendancePunchRecordVO> attendancePunchRecordVOS;

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
