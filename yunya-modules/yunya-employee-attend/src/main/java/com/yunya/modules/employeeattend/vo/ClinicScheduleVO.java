package com.yunya.modules.employeeattend.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-05 13:24
 */
@Data
public class ClinicScheduleVO implements Serializable {
    private Integer id;

    private Integer clinicId;

    private Integer scheduleId;

    private String name;

    private String color;

    private String range;

    private String type;

    /**
     * 开始时间点1
     */
    private Date firstStartTime;

    /**
     * 结束时间点1
     */
    private Date firstEndTime;

    /**
     * 开始时间点2
     */
    private Date secondStartTime;

    /**
     * 结束时间点2
     */
    private Date secondEndTime;

    private String concatName;

    private Boolean inservice;
}
