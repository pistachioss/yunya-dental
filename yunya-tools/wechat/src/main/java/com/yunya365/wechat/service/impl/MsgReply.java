package com.yunya365.wechat.service.impl;

import com.yunya.feign.wechat.domain.model.WxAutoReplyModel;
import com.yunya.feign.wechat.domain.model.WxAutoReplyText;
import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.models.wechat.WxAutomsg;
import com.yunya365.wechat.mapper.WxAutomsgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MsgReply {

    @Resource private WXService wxService;
    @Resource private WxAutomsgMapper wxAutomsgMapper;

    public void setTextReply(WxUserMsgModel msg, String keyString) {
        String reply = "";
        Example example = new Example(WxAutomsg.class);
        example.createCriteria().andEqualTo("eventname", keyString);
        List<WxAutomsg> res = wxAutomsgMapper.selectByExample(example);
        if (res != null && res.size() > 0) {
            reply = res.get(0).getMsgtext();
        }
        if (!"".equals(reply)) {
            WxAutoReplyModel msgModel = new WxAutoReplyModel();
            WxAutoReplyText msgText = new WxAutoReplyText();
            msgText.setContent(reply);
            msgModel.setMsgtype(msg.getMsgType());
            msgModel.setText(msgText);
            msgModel.setTouser(msg.getFromUserName());
            wxService.pushAutoReplyMsg(msgModel);
        }
    }
}
