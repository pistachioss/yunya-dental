package com.yunya.modules.sms.biz;

import cn.hutool.core.bean.BeanUtil;
import com.yunya.feign.sms.vo.SmsOrgStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.sms.SmsOrgStatistics;
import com.yunya.modules.sms.mapper.SmsOrgStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.OperationCodeConstants.BALANCE_INSUFFICIENT;

/**
 * 简介：短信统计业务层
 *
 * @author: chenlin
 * @Description: 短信统计业务层
 * @Date: 2020/12/16 13:37
 * @since: 1.0.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsOrgStatisticsBiz extends BaseBiz<SmsOrgStatisticsMapper, SmsOrgStatistics> {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 根据orgId查询短信统计详情
     *
     * @param orgId 门诊id
     * @return
     */
    public SmsOrgStatisticsVO findSmsOrgStatisticsByOrgId(Integer orgId) {
        return mapper.findSmsOrgStatisticsByOrgId(orgId);
    }

    /**
     * 查询短信余额
     *
     * @param orgId
     * @return
     */
    public int findSmsOrgStatisticsSurplusByOrgId(Integer orgId) {
        String result = "0";
        if (redisUtils.hasKey(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId)) {
            result = redisUtils.get(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId);
        } else {
            SmsOrgStatisticsVO smsOrgStatisticsVO = findSmsOrgStatisticsByOrgId(orgId);
            if (smsOrgStatisticsVO != null) {
                int surplusNum = smsOrgStatisticsVO.getSurplusNum();
                redisUtils.set(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId,surplusNum);
                return surplusNum;
            }
        }
        return Integer.parseInt(result);
    }

    /**
     * 充值成功后，对总计项目进行累加
     *
     * @param smsNum 短信条数
     * @param price 短信价格
     * @param orgId 门诊id
     */
    public void incrByOrgId(Integer smsNum, BigDecimal price, Integer orgId) {
        incrByOrgId(smsNum, smsNum, price, orgId);
    }

    /**
     * 累加（充值或返补）
     *
     * @param smsNum 短信总数
     * @param surplus 短信余额
     * @param price 短信价格
     * @param orgId 门诊id
     */
    public void incrByOrgId(Integer smsNum, Integer surplus, BigDecimal price, Integer orgId) {
        Date now = new Date(System.currentTimeMillis());
        try {
            while (!redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId),
                    RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS)) {
                threadSleep(2);
            }
            SmsOrgStatisticsVO smsOrgStatisticsVO = findSmsOrgStatisticsByOrgId(orgId);
            SmsOrgStatistics entity = new SmsOrgStatistics();
            if (smsOrgStatisticsVO != null) {
                BeanUtil.copyProperties(smsOrgStatisticsVO, entity);
                entity.setCrtId(-999);
                entity.setCrtTime(now);
            }
            BigDecimal chargeMoney = entity.getChargeMoney();
            Integer chargeNum = entity.getChargeNum();
            Integer surplusNum = entity.getSurplusNum();
            if (chargeMoney == null) {
                chargeMoney = new BigDecimal(0);
            }
            if (chargeNum == null) {
                chargeNum = 0;
            }
            if (surplusNum == null) {
                surplusNum = 0;
            }
            entity.setOrgId(orgId);
            if (smsNum != null) {
                entity.setChargeNum(chargeNum + smsNum);
            }
            if (surplus != null) {
                entity.setSurplusNum(surplusNum + surplus);
            }
            if (price != null) {
                entity.setChargeMoney(chargeMoney.add(price));
            }
            entity.setUptId(-999);
            entity.setUptTime(now);
            if (smsOrgStatisticsVO == null) {
                entity.setCrtId(-999);
                entity.setCrtTime(now);
                mapper.insertSelective(entity);
            } else {
                mapper.updateByPrimaryKeySelective(entity);
            }
            redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId);
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
    }

    /**
     * 短信余额扣费
     *
     * @param usedNum：扣费条数
     * @param orgId 门诊
     */
    public void decrByOrgId(int usedNum, Integer orgId, Integer userId) {
        Date now = new Date(System.currentTimeMillis());
        try {
            while (!redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId),
                    RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS)) {
                threadSleep(2);
            }
            SmsOrgStatisticsVO smsOrgStatisticsVO = findSmsOrgStatisticsByOrgId(orgId);
            SmsOrgStatistics entity = new SmsOrgStatistics();
            if (smsOrgStatisticsVO == null) {
                redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId);
                throw new ClientServiceException("短信余额不足！", BALANCE_INSUFFICIENT);
            }
            Integer surplusNum = smsOrgStatisticsVO.getSurplusNum();
            if (surplusNum == null) {
                surplusNum = 0;
            }
            if (usedNum > surplusNum) {
                throw new ClientServiceException("短信余额不足！", BALANCE_INSUFFICIENT);
            }
            entity.setId(smsOrgStatisticsVO.getId());
            entity.setSurplusNum(usedNum);
            entity.setUptId(userId);
            entity.setUptTime(now);
            mapper.updateByPrimaryKeySelective(entity);
            redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId);
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
    }

    private void threadSleep(Integer second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            log.error("threadSleep error: {}", e);
        }
    }
}
