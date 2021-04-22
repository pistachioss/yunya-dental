package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;
import com.yunya.framework.common.constant.WXConstant;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.wechat.WxTemplateMsgRecords;
import com.yunya365.wechat.config.NotifyType;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.mapper.WxTemplateMsgRecordsMapper;
import com.yunya365.wechat.service.WeChatNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @author: xy
 * @date 2021/4/25 10:27
 **/
@Service
@NotifyType(NotifyEnum.TEMPLATE)
@Slf4j
public class TemplatePush implements WeChatNotify {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private WxTemplateMsgRecordsMapper recordsMapper;

    @Override
    public WxSendMsgVo weChatNotify(WxUserMsgModel msgReq) throws Exception {
        log.info("模板消息推送回调结果：{}", msgReq);
        String status = msgReq.getStatus();
        String msgID = msgReq.getMsgID();
        String msgCallBack = redisUtils.get(WXConstant.WX_TEMPLATE_MSGID_KEY + msgID);
        WxTemplateMsgRecords records = JSONObject.parseObject(msgCallBack, WxTemplateMsgRecords.class);
        if ("success".equals(status)) {
            records.setMsgStatus(0);
        }
        //用户拒收
        if ("failed:user block".equals(status)) {
            records.setMsgStatus(1);
        }
        //其他原因失败
        if ("failed: system failed".equals(status)) {
            records.setMsgStatus(2);
        }
        log.info("发送给用户的消息是：{}", records);
        recordsMapper.insertSelective(records);
        return null;
    }
}
