package com.yunya.modules.sms.controller;

import com.yunya.feign.sms.vo.SmsOrgStatisticsVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.sms.biz.SmsOrgStatisticsBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介：短信统计管理
 *
 * @author: chenlin
 * @Description: 短信统计管理
 * @Date: 2020/12/16 12:45
 * @since: 1.0.0
 */
@Api(tags = "短信统计管理")
@RestController
@RequestMapping("smsOrgStatistics")
public class SmsOrgStatisticsController {

    @Autowired
    private SmsOrgStatisticsBiz smsOrgStatisticsBiz;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 根据orgId查询短信统计详情
     *
     * @return
     */
    @ApiOperation(value = "根据orgId查询短信统计详情")
    @CurrentUser
    @GetMapping("/info")
    public ResponseResult<SmsOrgStatisticsVO> findSmsOrgStatisticsByOrgId() {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsOrgStatisticsVO smsOrgStatisticsVO = smsOrgStatisticsBiz.findSmsOrgStatisticsByOrgId(orgId);
        return ResponseUtil.success(smsOrgStatisticsVO);
    }

    /**
     * 清除缓存redis
     *
     * @return
     */
    @ApiOperation(value = "清除缓存redis")
    @GetMapping("/clear")
    public ResponseResult<T> clear(Integer orgId) {
        redisUtils.delete(RedisConstants.SMS_STATISTICS_SURPLUS_ORG +orgId);
        redisUtils.delete(RedisConstants.REDIS_KEY_ORG_LIST);
        return ResponseUtil.success(null);
    }
}
