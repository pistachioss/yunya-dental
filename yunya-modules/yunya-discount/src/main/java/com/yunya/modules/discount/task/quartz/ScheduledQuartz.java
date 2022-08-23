package com.yunya.modules.discount.task.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ScheduledQuartz
 *
 */
@Slf4j
@Component
public class ScheduledQuartz {
    /*调度器*/
    private Scheduler scheduler;

    /*避免重名的计算器*/
    private AtomicInteger counter = new AtomicInteger();

    /*默认执行任务类，这里没有则必须在调用的时候传入*/
    private Class<? extends Job> jobClass;

    /*默认时间单位（秒）*/
    private TimeUnit defaultTimeUnit = TimeUnit.SECONDS;

    /*定时任务信息*/
    private JobDataMap jobMap = new JobDataMap();

    /*任务信息*/
    private Task task = null;

    private final String RUN_MGR = "runnable";

    private static final String TASK_NAME = "ScheduledQuartz";

    private static final String CRON_NAME = "CronQuartz";

    public Scheduler getScheduler() {
        return scheduler;
    }

    public ScheduledQuartz() {
        setScheduler();
        jobClass = QuartzJob.class;
    }

    private void setScheduler() {
        try {
            //方法1
            SchedulerFactory sfact = new StdSchedulerFactory();
            scheduler = sfact.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("ScheduledQuartz set schedulder error: {}", e);
        }
    }

    public ScheduledQuartz setTaskInfo(Task task){
        this.task = task;
        return this;
    }

    /**
     * 关闭
     */
    public boolean shutdown(JobKey key) throws SchedulerException {
        //true表示等待定时任务执行完才关闭
        return scheduler.deleteJob(key);
    }
    /**
     * 获取任务状态
     * @param key
     * @return
     * @throws SchedulerException
     */
    public String getJobState(JobKey key) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(key.getName(), key.getGroup());
        return scheduler.getTriggerState(triggerKey).name();
    }

    /**
     * 暂停所有任务
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 恢复所有任务
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 暂停任务
     * @throws SchedulerException
     */
    public boolean pauseJob(JobKey jobKey) throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return false;
        }else {
            scheduler.pauseJob(jobKey);
            return true;
        }
    }

    /**
     * 恢复某个任务
     * @param jobKey
     * @return
     * @throws SchedulerException
     */
    public boolean resumeJob(JobKey jobKey) throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return false;
        }else {
            scheduler.resumeJob(jobKey);
            return true;
        }
    }

    /**
     * 添加通用定时器
     */
    public JobKey commonTimmer(Task info) throws SchedulerException {
        if(info.isModify()){
            JobKey jobKey = new JobKey(info.getName(),info.getGroup());
            TriggerKey triggerKey = TriggerKey.triggerKey(info.getName(),info.getGroup());
            if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
                shutdown(jobKey);
            }
        }
        //设置默认参数
        setBaseInfo(info);
        //绑定具体定时任务：执行任务的类、传递信息、名称、组
        JobDetail jobDetail = JobBuilder.newJob(info.getJobClass())
                .usingJobData(info.getJobDataMap())
                .withIdentity(info.getName(), info.getGroup()).build();
        Trigger trigger = null;
        if (info.getCronExp() == null) {
            //简单定时器
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(info.getName(), info.getGroup())
                    //定时器开始时间
                    .startAt(info.getStartDate())
                    //定时器结束时间
                    .endAt(info.getEndDate())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            //任务执行间隔时间
                            .withIntervalInMilliseconds(info.getIntervalDuration())
                            //执行次数
                            .withRepeatCount(info.getExeCount()))
                    .build();
        } else {
            //复杂定时器
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(info.getName(), info.getGroup())
                    .startAt(info.getStartDate())
                    .endAt(info.getEndDate())
                    .withSchedule(CronScheduleBuilder.cronSchedule(info.getCronExp()))
                    .build();
        }
        if (trigger != null) {
            scheduler.scheduleJob(jobDetail, trigger);
        }
        task = null;
        return jobDetail.getKey();
    }

    /**
     * 设置默认参数
     *
     * @param info
     */
    private void setBaseInfo(Task info) {
        counter.set(counter.get() + 1);
        if (info.isForever()) {
            info.setExeCount(SimpleTrigger.REPEAT_INDEFINITELY);
        }
        if (info.getStartDate() == null) {
            info.setStartDate(new Date());
        }
        if (info.getIntervalDuration() <= 0) {
            info.setIntervalDuration(1000);
        }
        if (info.getName() == null) {
            if (info.getCronExp() == null) {
                info.setName(TASK_NAME);
            }else {
                info.setName(CRON_NAME);
            }
        }
        if (info.getGroup() == null) {
            info.setGroup(String.valueOf(counter));
        }
        if (info.getJobDataMap() == null) {
            info.setJobDataMap(new JobDataMap());
        }
        TimeUnit timeUnit = info.getIntervalUnit();
        if (timeUnit != null) {
            long intervalTime = info.getIntervalDuration();
            info.setIntervalDuration(getRealTime(intervalTime, timeUnit));
        }
    }

    private long getRealTime(long time, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.SECONDS) {
            time *= 1000L;
        } else if (timeUnit == TimeUnit.MINUTES) {
            time *= 60000L;
        } else if (timeUnit == TimeUnit.HOURS) {
            time *= 3600000L;
        } else if (timeUnit == TimeUnit.DAYS) {
            time *= 86400000L;
        }
        return time;
    }

    /**
     * 倒计时单次任务
     * 延迟5秒执行一次
     */
    public JobKey delayTimmer(long time, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        Date date = new Date();
        date.setTime(date.getTime() + getRealTime(time, timeUnit));
        return delayTimmer(date, runnable);
    }

    public JobKey delayTimmer(long time, Runnable runnable) throws SchedulerException {
        Date date = new Date();
        date.setTime(date.getTime() + getRealTime(time, defaultTimeUnit));
        return delayTimmer(date, runnable);
    }

    /**
     * 倒计时单次任务
     * 延迟到指定时间，执行一次
     */
    public JobKey delayTimmer(Date startDate, Runnable runnable) throws SchedulerException {
        Task info = new Task();
        if(task != null){
            info = task;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setStartDate(startDate);
        info.setJobClass(jobClass);
        info.setJobDataMap(jobMap);
        return commonTimmer(info);
    }

    public JobKey intervalTimmer(long time, Runnable runnable) throws SchedulerException {
        return intervalTimmer(time, defaultTimeUnit, runnable);
    }

    public JobKey intervalTimmer(long time, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        Task info = new Task();
        if(task != null){
            info = task;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalDuration(getRealTime(time, timeUnit));
        info.setForever(true);
        info.setJobClass(jobClass);
        info.setJobDataMap(jobMap);
        return commonTimmer(info);
    }

    public JobKey intervalTimmer(Date startDate, Date endDate, long interval, Runnable runnable) throws SchedulerException {
        return intervalTimmer(startDate, endDate, interval, defaultTimeUnit, runnable);
    }

    public JobKey intervalTimmer(Date startDate, Date endDate, long interval, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        Task info = new Task();
        if(task != null){
            info = task;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalDuration(getRealTime(interval, timeUnit));
        info.setStartDate(startDate);
        info.setEndDate(endDate);
        info.setForever(true);
        info.setJobClass(jobClass);
        info.setJobDataMap(jobMap);
        return commonTimmer(info);
    }

    public JobKey intervalTimmer(Date startDate, int count, long interval, Runnable runnable) throws SchedulerException {
        Task info = new Task();
        if(task != null){
            info = task;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalDuration(getRealTime(interval, defaultTimeUnit));
        info.setStartDate(startDate);
        info.setExeCount(count - 1);
        info.setJobClass(jobClass);
        info.setJobDataMap(jobMap);
        return commonTimmer(info);
    }

    public JobKey intervalTimmer(Date startDate, int count, long interval, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        Task info = new Task();
        if(task != null){
            info = task;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalDuration(getRealTime(interval, timeUnit));
        info.setStartDate(startDate);
        info.setExeCount(count - 1);
        info.setJobClass(jobClass);
        info.setJobDataMap(jobMap);
        return commonTimmer(info);
    }

    /**
     * 添加定时任务，支持lambda表达式
     * @param cronExp cron表达式
     * @param runnable 任务执行方法
     *
     */
    public JobKey cronTimmer(String cronExp, Runnable runnable) throws SchedulerException {
        Task info = new Task();
        if(task != null){
            info = task;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setCronExp(cronExp);
        info.setJobClass(jobClass);
        info.setJobDataMap(jobMap);
        return commonTimmer(info);
    }
}