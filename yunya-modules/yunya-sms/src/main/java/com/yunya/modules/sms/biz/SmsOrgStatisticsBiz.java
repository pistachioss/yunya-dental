package com.yunya.modules.sms.biz;

import cn.hutool.core.bean.BeanUtil;
import com.yunya.feign.sms.vo.SmsOrgStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.sms.SmsOrgStatistics;
import com.yunya.modules.sms.mapper.SmsOrgStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.OperationCodeConstants.OPERATION_NOT_ALLOW;

/**
 * 简介：短信统计业务层
 *
 * @author: chenlin
 * @Description: 短信统计业务层
 * @Date: 2020/12/16 13:37
 * @since: 1.0.0
 */
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
        Date now = new Date(System.currentTimeMillis());
        try {
            redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId),
                    RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS);
            SmsOrgStatisticsVO smsOrgStatisticsVO = findSmsOrgStatisticsByOrgId(orgId);
            SmsOrgStatistics smsOrgStatistics = new SmsOrgStatistics();
            if (smsOrgStatisticsVO != null) {
                BeanUtil.copyProperties(smsOrgStatisticsVO, smsOrgStatistics);
                smsOrgStatistics.setCrtId(-999);
                smsOrgStatistics.setCrtTime(now);
            }
            BigDecimal chargeMoney = smsOrgStatistics.getChargeMoney();
            Integer chargeNum = smsOrgStatistics.getChargeNum();
            Integer surplusNum = smsOrgStatistics.getSurplusNum();
            if (chargeMoney == null) {
                chargeMoney = new BigDecimal(0);
            }
            if (chargeNum == null) {
                chargeNum = 0;
            }
            if (surplusNum == null) {
                surplusNum = 0;
            }
            smsOrgStatistics.setOrgId(orgId);
            smsOrgStatistics.setChargeNum(chargeNum + smsNum);
            smsOrgStatistics.setSurplusNum(surplusNum + smsNum);
            if (price != null) {
                smsOrgStatistics.setChargeMoney(chargeMoney.add(price));
            }
            smsOrgStatistics.setUptId(-999);
            smsOrgStatistics.setUptTime(now);
            if (smsOrgStatisticsVO == null) {
                smsOrgStatistics.setCrtId(-999);
                smsOrgStatistics.setCrtTime(now);
                mapper.insertSelective(smsOrgStatistics);
            } else {
                mapper.updateByPrimaryKeySelective(smsOrgStatistics);
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
    public void decrByOrgId(int usedNum, Integer orgId) {
        Date now = new Date(System.currentTimeMillis());
        try {
            redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId),
                    RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS);
            SmsOrgStatisticsVO smsOrgStatisticsVO = findSmsOrgStatisticsByOrgId(orgId);
            SmsOrgStatistics smsOrgStatistics = new SmsOrgStatistics();
            if (smsOrgStatisticsVO == null) {
                redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId);
                throw new ClientServiceException("短信余额不足", OPERATION_NOT_ALLOW);
            }
            Integer chargeNum = smsOrgStatistics.getChargeNum();
            Integer surplusNum = smsOrgStatistics.getSurplusNum();
            if (chargeNum == null) {
                chargeNum = 0;
            }
            if (surplusNum == null) {
                surplusNum = 0;
            }
            smsOrgStatistics.setOrgId(orgId);
            smsOrgStatistics.setChargeNum(chargeNum - usedNum);
            smsOrgStatistics.setSurplusNum(surplusNum - usedNum);
            smsOrgStatistics.setUptId(-999);
            smsOrgStatistics.setUptTime(now);
            mapper.updateByPrimaryKeySelective(smsOrgStatistics);
            redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId);
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
    }
}
