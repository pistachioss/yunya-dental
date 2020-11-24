package com.yunya.models.employee_attend;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "leave_schedule")
@Data
public class LeaveSchedule {
    @Id
    private Integer id;

    /**
     * 请假id
     */
    @Column(name = "leave_id")
    private Integer leaveId;

    /**
     * 排班id
     */
    @Column(name = "schedule_id")
    @ApiModelProperty("排班id")
    private Integer scheduleId;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 请假开始时间
     */
    @Column(name = "start_time")
    @ApiModelProperty("请假开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 请假结束时间
     */
    @Column(name = "end_time")
    @ApiModelProperty("请假开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}