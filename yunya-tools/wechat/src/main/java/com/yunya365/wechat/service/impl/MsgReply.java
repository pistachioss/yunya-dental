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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MsgReply {

  @Resource private WXService wxService;
  @Resource private WxAutomsgMapper wxAutomsgMapper;

  public void setAutoReply(WxUserMsgModel msg, String keyString) {
    Example example = new Example(WxAutomsg.class);
    example.createCriteria().andEqualTo("eventname", keyString);
    List<WxAutomsg> res = wxAutomsgMapper.selectByExample(example);
    if (res != null && res.size() > 0) {
      res =
          res.stream()
              .sorted(Comparator.comparing(WxAutomsg::getEventdisp))
              .collect(Collectors.toList());
      res.forEach(
          reply -> {
            if (!"".equals(reply.getMsgtext())) {
              switch (reply.getMsgtype()) {
                case "":
                case "text":
                  WxAutoTextReplyModel msgModel1 = new WxAutoTextReplyModel();
                  WxAutoReplyText msgText = new WxAutoReplyText();
                  msgText.setContent(reply.getMsgtext());
                  msgModel1.setMsgtype("text"); // "text"  //msg.getMsgType()
                  msgModel1.setText(msgText);
                  msgModel1.setTouser(msg.getFromUserName());
                  wxService.pushAutoReplyMsg(msgModel1);
                  break;
                case "news":
                  WxAutoNewsReplyModel msgModel2 = new WxAutoNewsReplyModel();
                  WxAutoReplyNews msgNews = new WxAutoReplyNews();
                  List<WxAutoReplyNewsArticles> msgArticles = new ArrayList<>();
                  WxAutoReplyNewsArticles articles = new WxAutoReplyNewsArticles();
                  articles.setDescription(reply.getMsgtext());
                  articles.setTitle(reply.getMsgtitle());
                  articles.setUrl(reply.getMsgurl());
                  articles.setPicurl(reply.getMsgpicurl());
                  msgArticles.add(articles);
                  msgNews.setArticles(msgArticles);
                  msgModel2.setMsgtype("news"); // "text"  //msg.getMsgType()
                  msgModel2.setNews(msgNews);
                  msgModel2.setTouser(msg.getFromUserName());
                  wxService.pushAutoReplyMsg(msgModel2);
                  break;
                case "image":
                  WxAutoImageReplyModel msgModel3 = new WxAutoImageReplyModel();
                  WxAutoReplyImage msgImage = new WxAutoReplyImage();
                  msgImage.setMedia_id(reply.getMsgtext());
                  msgModel3.setMsgtype("image"); // "text"  //msg.getMsgType()
                  msgModel3.setImage(msgImage);
                  msgModel3.setTouser(msg.getFromUserName());
                  wxService.pushAutoReplyMsg(msgModel3);
                  break;
              }
            }
          });
    }
  }
}
