package com.yunya.modules.discount.task.quartz;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 任务执行器
 */
@Data
public class QuartzJob implements Job {
    private Runnable runnable;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        runnable.run();
    }
}