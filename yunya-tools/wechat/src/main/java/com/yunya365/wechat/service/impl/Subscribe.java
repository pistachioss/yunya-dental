package com.yunya365.wechat.service.impl;

import com.yunya.feign.wechat.domain.model.WxRegisterModel;
import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.wechat.config.NotifyType;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.service.WeChatNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @author: xy
 * @date 2021/3/25 10:27
 **/
@Service
@NotifyType(NotifyEnum.SUBSCRIBE)
@Slf4j
public class Subscribe implements WeChatNotify {

    @Resource
    private WXService wxService;

    @Override
    public WxSendMsgVo weChatNotify(WxUserMsgModel msgReq) throws Exception {
        log.info("用户关注公众号回调结果：{}", msgReq);
        String openId = msgReq.getFromUserName();
        WxFans wxFansReg = wxService.getOwnInfo(openId, null);
        if (wxFansReg != null) {
            log.info("用户已关注公众号：{}", wxFansReg);
            return null;
        }
        //因为微信官方文档调整，没有昵称、头像信息
        WxRegisterModel model = new WxRegisterModel();
        wxService.saveWxPatient(openId, model);
        return null;
    }
}
