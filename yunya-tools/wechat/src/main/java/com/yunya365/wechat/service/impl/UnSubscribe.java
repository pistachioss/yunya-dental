package com.yunya365.wechat.service.impl;

import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;
import com.yunya365.wechat.config.NotifyType;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.service.WeChatNotify;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xy
 * @date 2021/3/25 10:27
 **/
@Service
@NotifyType(NotifyEnum.UNSUBSCRIBE)
public class UnSubscribe implements WeChatNotify {

    @Override
    public WxSendMsgVo weChatNotify(WxUserMsgModel msgReq) throws Exception {
        return null;
    }
}
