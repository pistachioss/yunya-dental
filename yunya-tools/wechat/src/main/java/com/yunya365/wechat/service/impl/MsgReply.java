package com.yunya365.wechat.service.impl;

import com.yunya.feign.wechat.domain.model.*;
import com.yunya.models.wechat.WxAutomsg;
import com.yunya365.wechat.mapper.WxAutomsgMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MsgReply {

    @Resource private WXService wxService;
    @Resource private WxAutomsgMapper wxAutomsgMapper;

    public void setAutoReply(WxUserMsgModel msg, String keyString) {
        WxAutomsg reply = new WxAutomsg();
        Example example = new Example(WxAutomsg.class);
        example.createCriteria().andEqualTo("eventname", keyString);
        List<WxAutomsg> res = wxAutomsgMapper.selectByExample(example);
        if (res != null && res.size() > 0) {
            reply = res.get(0);
        }
        if (!"".equals(reply.getMsgtext())) {
            if(StringUtils.isEmpty(reply.getMsgpicurl())) {
                WxAutoTextReplyModel msgModel = new WxAutoTextReplyModel();
                WxAutoReplyText msgText = new WxAutoReplyText();
                msgText.setContent(reply.getMsgtext());
                msgModel.setMsgtype("text");//"text"  //msg.getMsgType()
                msgModel.setText(msgText);
                msgModel.setTouser(msg.getFromUserName());
                wxService.pushAutoReplyMsg(msgModel);
            }
            else {
                WxAutoNewsReplyModel msgModel = new WxAutoNewsReplyModel();
                WxAutoReplyNews msgNews = new WxAutoReplyNews();
                List<WxAutoReplyNewsArticles> msgArticles = new ArrayList<>();
                WxAutoReplyNewsArticles articles = new WxAutoReplyNewsArticles();
                articles.setDescription(reply.getMsgtext());
                articles.setTitle(reply.getMsgtitle());
                articles.setUrl(reply.getMsgurl());
                articles.setPicurl(reply.getMsgpicurl());
                msgArticles.add(articles);
                msgNews.setArticles(msgArticles);
                msgModel.setMsgtype("news");//"text"  //msg.getMsgType()
                msgModel.setNews(msgNews);
                msgModel.setTouser(msg.getFromUserName());
                wxService.pushAutoReplyMsg(msgModel);
            }
        }
    }
}
