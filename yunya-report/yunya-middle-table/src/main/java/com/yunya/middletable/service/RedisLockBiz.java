package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/13 15:31
 * @since: 1.0.0
 */
@Slf4j
@Service
public class RedisLockBiz {

    @Autowired private RedisUtils redisUtils;

    public void lockedApply(String lockKey, String lockVal, Function function) {
        boolean locked = false;
        try {
            locked = redisUtils.setLock(lockKey, lockVal, 1, TimeUnit.SECONDS);
            while (!locked) {
                TimeUnit.SECONDS.sleep(1);
            }
            function.apply(null);
        } catch (Exception e) {
            log.error(Joiner.on(":").join(lockKey, lockVal, "error"), e);
        } finally {
            if (locked) {
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }
}
