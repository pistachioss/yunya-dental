package com.yunya.modules.discount.task.quartz;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.quartz.Job;
import org.quartz.JobDataMap;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 任务信息
 *
 */
@Data
@ToString
@ApiModel("任务信息")
@NoArgsConstructor
public class Task implements Serializable {
    /** 任务名称 */
    private String name;

    /** 任务组*/
    private String group;

    /*开始时间*/
    private Date startDate;

    /*结束时间*/
    private Date endDate;

    /*执行次数*/
    private int exeCount;

    /*间隔时间*/
    private long intervalDuration;

    /*时间单位*/
    private TimeUnit intervalUnit;

    /*默认一直循环执行*/
    private boolean forever;

    /*复杂定时器的cron表达式，没有代表普通定时器*/
    private String cronExp;

    /*定时任务执行类*/
    private Class<? extends Job> jobClass;

    /*定时器内传递的消息*/
    private JobDataMap jobDataMap;

    /*修改任务*/
    private boolean modify = false;
}