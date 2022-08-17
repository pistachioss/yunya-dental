package com.yunya.modules.sms.async;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.sms.model.SmsAutoEventSendRecordModel;
import com.yunya.feign.sms.model.SmsTemplateIdRecordModel;
import com.yunya.feign.sms.model.SmsVerifyCodeModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.sms.biz.SmsSendRecordBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.yunya.framework.common.constant.BusinessConstants.COMPANY_ORGID;

/**
 * 简介：查询阿里云短信
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/15 15:17
 * @since: 1.0.0
 */
@Component
@EnableScheduling
public class SmsSendMessageScheduledAsync {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsSendRecordBiz smsSendRecordBiz;
    @Autowired
    private RedisUtils redisUtils;
    @Resource(name = "poolExecutor")
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 每隔5秒执行一次：
     * 消费redis队列的短信数据
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "*/5 * * * * ?")
    public void smsSendMessageAsyncByEventCode() {
        List<OrganizationInfoDetail> orgInfos = getOrganizationList();
        if (StringHelper.isEmpty(orgInfos)) {
            return;
        }
//        orgInfos.forEach(orgInfo-> threadPoolExecutor.submit(()->{
//            SmsAutoEventSendRecordModel smsAutoEventSendRecordModel = redisUtils.rPop(
//                    RedisConstants.SMS_SEND_MESSAGE_QUEUE + orgInfo.getId(), SmsAutoEventSendRecordModel.class);
//            if (smsAutoEventSendRecordModel == null) {
//                return;
//            }
//            log.info("开始处理短信：{}", JSONObject.toJSONString(smsAutoEventSendRecordModel));
//            try {
//                smsSendRecordBiz.batchSendByEventCode(smsAutoEventSendRecordModel);
//                log.info("处理短信完成");
//            } catch (Exception e) {
//                log.error("smsSendMessageAsync error", e);
//            }
//        }));

        orgInfos.forEach(orgInfo->{
            SmsAutoEventSendRecordModel smsAutoEventSendRecordModel = redisUtils.rPop(
                    RedisConstants.SMS_SEND_MESSAGE_QUEUE + orgInfo.getId(), SmsAutoEventSendRecordModel.class);
            if (smsAutoEventSendRecordModel == null) {
                return;
            }
            log.info("开始处理短信：{}", JSONObject.toJSONString(smsAutoEventSendRecordModel));
            try {
                smsSendRecordBiz.batchSendByEventCode(smsAutoEventSendRecordModel);
                log.info("处理短信完成");
            } catch (Exception e) {
                log.error("smsSendMessageAsync error", e);
            }
        }));
    }

    /**
     * 每隔5秒执行一次：
     * 消费redis队列的短信数据
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "*/5 * * * * ?")
    public void smsSendMessageAsyncByTemplateId() {
        SmsTemplateIdRecordModel smsTemplateIdRecordModel = redisUtils.rPop(
                RedisConstants.SMS_SEND_MESSAGE_QUEUE, SmsTemplateIdRecordModel.class);
        if (smsTemplateIdRecordModel == null) {
            return;
        }
        log.info("开始处理短信：{}", JSONObject.toJSONString(smsTemplateIdRecordModel));
        threadPoolExecutor.submit(()->{
            try {
                smsSendRecordBiz.batchSendByTemplateId(smsTemplateIdRecordModel);
                log.info("处理短信完成");
            } catch (Exception e) {
                log.error("smsSendMessageAsync error", e);
            }
        });
    }

    /**
     * 每隔5秒执行一次：
     * 消费redis队列的短信验证码
     */
    @Async("customizeExecutor")
    @Scheduled(cron = "*/5 * * * * ?")
    public void smsSendVerifyCodeAsync() {
        SmsVerifyCodeModel smsVerifyCodeModel = redisUtils.rPop(
                RedisConstants.SMS_SEND_VERIFYCODE_QUEUE + COMPANY_ORGID, SmsVerifyCodeModel.class);
        if (smsVerifyCodeModel == null) {
            return;
        }
        log.info("开始处理短信验证码：{}", JSONObject.toJSONString(smsVerifyCodeModel));
        try {
            smsSendRecordBiz.sendVerifyCode(smsVerifyCodeModel);
            log.info("处理短信验证码完成");
        } catch (Exception e) {
            log.error("smsSendVerifyCodeAsync error", e);
        }
    }

    /**
     * 查询组织信息列表
     * @return
     */
    private List<OrganizationInfoDetail> getOrganizationList() {
        List<OrganizationInfoDetail> orgInfos = redisUtils.getJSONArray(RedisConstants.REDIS_KEY_ORG_LIST, OrganizationInfoDetail.class);
        if (StringHelper.isEmpty(orgInfos)) {
            OrganizationModel model = new OrganizationModel();
            model.setWhetherPage(false);
            orgInfos = remoteSystemServiceFeign.findOrgInfoList(model);
            redisUtils.set(RedisConstants.REDIS_KEY_ORG_LIST, orgInfos);
        }
        return orgInfos;
    }
}
