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
import java.util.Objects;

/**
 * @description:
 * @author: xy
 * @date 2021/3/25 10:27
 */
@Service
@NotifyType(NotifyEnum.CLICK)
@Slf4j
public class eventReply implements WeChatNotify {

  @Resource private WXService wxService;
  @Resource private MsgReply msgReply;

  @Override
  public WxSendMsgVo weChatNotify(WxUserMsgModel msgReq) throws Exception {
    log.info("用户点击菜单click回调结果：{}", msgReq);
    msgReply.setAutoReply(msgReq, msgReq.getEventKey());
    return null;
  }
}
