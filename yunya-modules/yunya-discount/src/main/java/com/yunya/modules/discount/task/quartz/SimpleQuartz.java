package com.yunya.modules.discount.task.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定时器
 */
@Component
public class SimpleQuartz {
    /*调度器*/
    private Scheduler scheduler;
    /*避免名称重复计算器*/
    private AtomicInteger counter = new AtomicInteger();
    /*默认执行任务类，这里没有则必须在调用的时候传入*/
    private Class<? extends Job> jobClass;
    /*默认时间单位（秒）*/
    private TimeUnit defaultTimeUnit = TimeUnit.SECONDS;
    /*定时任务信息*/
    private JobDataMap jobMap = new JobDataMap();
    /*可自定义任务信息*/
    private TaskInfo taskInfo = null;
    private final String RUN_MGR = "runnable";
    private static final String SIMPLE_NAME = "SimpleQuartz";
    private static final String CORN_NAME = "CornQuartz";

    public Scheduler getScheduler() {
        return scheduler;
    }

    public SimpleQuartz() {
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
            e.printStackTrace();
        }
    }

    public SimpleQuartz setTaskInfo(TaskInfo taskInfo){
        this.taskInfo = taskInfo;
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
    public JobKey commonTimmer(TaskInfo info) throws SchedulerException {
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
                .usingJobData(info.getJobMap())
                .withIdentity(info.getName(), info.getGroup()).build();
        Trigger trigger = null;
        if (info.getCorn() == null) {
            //简单定时器
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(info.getName(), info.getGroup())
                    //定时器开始时间
                    .startAt(info.getStartAT())
                    //定时器结束时间
                    .endAt(info.getEndAT())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            //任务执行间隔时间
                            .withIntervalInMilliseconds(info.getIntervalTime())
                            //执行次数
                            .withRepeatCount(info.getCount()))
                    .build();
        } else {
            //复杂定时器
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(info.getName(), info.getGroup())
                    .startAt(info.getStartAT())
                    .endAt(info.getEndAT())
                    .withSchedule(CronScheduleBuilder.cronSchedule(info.getCorn()))
                    .build();
        }
        if (trigger != null) {
            scheduler.scheduleJob(jobDetail, trigger);
        }
        taskInfo = null;
        return jobDetail.getKey();
    }
    /**
     * 设置默认参数
     *
     * @param info
     */
    private void setBaseInfo(TaskInfo info) {
        counter.set(counter.get() + 1);
        if (info.isForever()) {
            info.setCount(SimpleTrigger.REPEAT_INDEFINITELY);
        }
        if (info.getStartAT() == null) {
            info.setStartAT(new Date());
        }
        if (info.getIntervalTime() <= 0) {
            info.setIntervalTime(1000);
        }
        if (info.getName() == null) {
            if (info.getCorn() == null) {
                info.setName(SIMPLE_NAME);
            }else {
                info.setName(CORN_NAME);
            }
        }
        if (info.getGroup() == null) {
            info.setGroup(String.valueOf(counter));
        }
        if (info.getJobMap() == null) {
            info.setJobMap(new JobDataMap());
        }
        TimeUnit timeUnit = info.getIntervalUnit();
        if (timeUnit != null) {
            long intervalTime = info.getIntervalTime();
            info.setIntervalTime(getRealTime(intervalTime, timeUnit));
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
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        Date date = new Date();
        date.setTime(date.getTime() + getRealTime(time, timeUnit));
        jobMap.put(RUN_MGR, runnable);
        info.setStartAT(date);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey delayTimmer(long time, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        Date date = new Date();
        date.setTime(date.getTime() + getRealTime(time, defaultTimeUnit));
        jobMap.put(RUN_MGR, runnable);
        info.setStartAT(date);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    /**
     * 倒计时单次任务
     * 延迟到指定时间，执行一次
     */
    public JobKey delayTimmer(Date startDate, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setStartAT(startDate);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey intervalTimmer(long time, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalTime(getRealTime(time, defaultTimeUnit));
        info.setForever(true);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey intervalTimmer(long time, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalTime(getRealTime(time, timeUnit));
        info.setForever(true);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey intervalTimmer(Date startDate, Date endDate, long interval, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalTime(getRealTime(interval, defaultTimeUnit));
        info.setStartAT(startDate);
        info.setEndAT(endDate);
        info.setForever(true);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey intervalTimmer(Date startDate, Date endDate, long interval, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalTime(getRealTime(interval, timeUnit));
        info.setStartAT(startDate);
        info.setEndAT(endDate);
        info.setForever(true);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey intervalTimmer(Date startDate, int count, long interval, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalTime(getRealTime(interval, defaultTimeUnit));
        info.setStartAT(startDate);
        info.setCount(count - 1);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    public JobKey intervalTimmer(Date startDate, int count, long interval, TimeUnit timeUnit, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setIntervalTime(getRealTime(interval, timeUnit));
        info.setStartAT(startDate);
        info.setCount(count - 1);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
    /**
     * 添加复杂定时任务
     * cron可以在线生成
     */
    public JobKey cornTimmer(String corn, Runnable runnable) throws SchedulerException {
        TaskInfo info = new TaskInfo();
        if(taskInfo != null){
            info = taskInfo;
        }
        jobMap.put(RUN_MGR, runnable);
        info.setCorn(corn);
        info.setJobClass(jobClass);
        info.setJobMap(jobMap);
        return commonTimmer(info);
    }
}