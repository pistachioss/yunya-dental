package com.yunya.modules.sms.controller;

import com.yunya.feign.sms.vo.SmsOrgStatisticsVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsOrgStatisticsBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

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

    /**
     * 根据orgId查询短信统计详情
     *
     * @param orgId 门诊id
     * @return
     */
    @ApiOperation(value = "根据orgId查询短信统计详情")
    @GetMapping("/info/{orgId}")
    public ResponseResult<SmsOrgStatisticsVO> findSmsOrgStatisticsByOrgId(@PathVariable(value = "orgId") @NotNull Integer orgId) {
        SmsOrgStatisticsVO smsOrgStatisticsVO = smsOrgStatisticsBiz.findSmsOrgStatisticsByOrgId(orgId);
        return ResponseUtil.success(smsOrgStatisticsVO);
    }
}
