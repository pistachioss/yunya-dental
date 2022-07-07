package com.yunya.modules.discount.task.quartz;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobDataMap;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 任务信息
 */
@Data
public class TaskInfo {
    /** 任务名称 */
    private String name;
    /** 任务组*/
    private String group;
    /*开始时间*/
    private Date startAT;
    /*结束时间*/
    private Date endAT;
    /*执行次数*/
    private int count;
    /*间隔时间*/
    private long intervalTime;
    /*时间单位*/
    private TimeUnit intervalUnit;
    /*一直循环执行*/
    private boolean forever;
    /*复杂定时器的corn表达式，没有代表普通定时器*/
    private String corn;
    /*定时任务执行类*/
    private Class<? extends Job> jobClass;
    /*定时器内传递的消息*/
    private JobDataMap jobMap;
    /*修改任务*/
    private boolean modify = false;
}