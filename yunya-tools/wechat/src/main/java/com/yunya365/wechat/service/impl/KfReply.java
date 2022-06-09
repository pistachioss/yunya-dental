package com.yunya365.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.wechat.domain.form.WxAutoReplyForm;
import com.yunya.feign.wechat.domain.model.WxAutoReplyModel;
import com.yunya.feign.wechat.domain.model.WxAutoReplyText;
import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxKfOnlineVo;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;
import com.yunya.feign.wechat.domain.vo.WxSignatureVo;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.wechat.WxAutomsg;
import com.yunya.models.wechat.WxMsgTemplates;
import com.yunya.models.wechat.WxTemplateMsgRecords;
import com.yunya365.wechat.config.NotifyType;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.enums.WeChatError;
import com.yunya365.wechat.mapper.WxAutomsgMapper;
import com.yunya365.wechat.service.WeChatNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/3/25 10:27
 */
@Slf4j
@Service
@NotifyType({NotifyEnum.TEXT, NotifyEnum.IMAGE, NotifyEnum.VOICE, NotifyEnum.VIDEO})
public class KfReply extends AbstractWxBaseApi implements WeChatNotify {

  @Resource private WXService wxService;
  @Resource private WxAutomsgMapper wxAutomsgMapper;
  @Resource private MsgReply msgReply;

  @Override
  public WxSendMsgVo weChatNotify(WxUserMsgModel msg) throws Exception {
    log.info("消息接受：{}，时间：{}", msg, LocalDateTime.now());
    // 创建消息响应对象
    WxSendMsgVo out = new WxSendMsgVo();
    // 把原来的发送方设置为接收方
    out.setToUserName(msg.getFromUserName());
    // 把原来的接收方设置为发送方
    out.setFromUserName(msg.getToUserName());
    // 获取接收的消息类型
    String msgType = msg.getMsgType();
    // 设置消息创建时间
    out.setCreateTime(new Date().getTime());
    // 设置消息的响应类型
    out.setMsgType("transfer_customer_service");
    //        out.setContent("您好");
    if (msgType.equals("text") && !"公众号关注自动回复".equals(msg.getContent())) {
      msgReply.setTextReply(msg, msg.getContent());
    }
    WxKfOnlineVo kfOnlineRes = super.listOnlineKf();
    if (kfOnlineRes != null) {
      out.setKfAccount(kfOnlineRes.getKf_account());
    }
    log.info("回复消息：{}，时间：{}", out, LocalDateTime.now());
    return out;
  }

  public List<WxAutomsg> list() {
    return wxAutomsgMapper.selectAll();
  }

  public int create(WxAutoReplyForm wxAutoReplyForm) {
    Example example = new Example(WxAutomsg.class);
    example.createCriteria().andEqualTo("eventname", wxAutoReplyForm.getEventname());
    if (wxAutomsgMapper.selectByExample(example).size() > 0) {
      throw new ClientServiceException(
          WeChatError.WX_AUTO_REPLY_EXIST.setErrorMsg(wxAutoReplyForm.getEventname()));
    }

    WxAutomsg wxAutomsg = new WxAutomsg();
    wxAutomsg.setEventname(wxAutoReplyForm.getEventname());
    wxAutomsg.setCompid("A000446");
    wxAutomsg.setEventkey("");
    wxAutomsg.setMsgtext(wxAutoReplyForm.getMsgtext());
    wxAutomsg.setIsvalid(true);

    Date now = new Date(System.currentTimeMillis());
    Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
    wxAutomsg.setCreateuser(userId.toString());
    wxAutomsg.setCreatetime(now);
    wxAutomsg.setUpdateuser(userId.toString());
    wxAutomsg.setUpdatetime(now);
    return wxAutomsgMapper.insert(wxAutomsg);
  }

  public int edit(WxAutoReplyForm wxAutoReplyForm, Integer id) {
    if (wxAutomsgMapper.selectByPrimaryKey(id) == null) {
      return 0;
    }

    Example example = new Example(WxAutomsg.class);
    example.createCriteria().andEqualTo("eventname", wxAutoReplyForm.getEventname());
    if (wxAutomsgMapper.selectByExample(example).size() > 0) {
      if (wxAutomsgMapper.selectByExample(example).size() == 1
          && wxAutomsgMapper.selectByExample(example).get(0).getId() != id) {
        throw new ClientServiceException(
            WeChatError.WX_AUTO_REPLY_EXIST.setErrorMsg(wxAutoReplyForm.getEventname()));
      }
    }

    WxAutomsg wxAutomsg = new WxAutomsg();
    wxAutomsg.setId(id);
    wxAutomsg.setCompid("A000446");
    wxAutomsg.setEventkey("");
    wxAutomsg.setEventname(wxAutoReplyForm.getEventname());
    wxAutomsg.setMsgtext(wxAutoReplyForm.getMsgtext());
    wxAutomsg.setIsvalid(true);

    Date now = new Date(System.currentTimeMillis());
    Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
    wxAutomsg.setCreateuser(userId.toString());
    wxAutomsg.setCreatetime(now);
    wxAutomsg.setUpdateuser(userId.toString());
    wxAutomsg.setUpdatetime(now);
    return wxAutomsgMapper.updateByPrimaryKey(wxAutomsg);
  }

  public int delete(Integer id) {
    if (wxAutomsgMapper.selectByPrimaryKey(id) == null) {
      return 0;
    }
    return wxAutomsgMapper.deleteByPrimaryKey(id);
  }
}
