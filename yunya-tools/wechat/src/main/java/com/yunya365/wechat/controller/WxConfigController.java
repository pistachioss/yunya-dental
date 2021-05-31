package com.yunya365.wechat.controller;

import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxSignatureVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.wechat.config.NotifyFactory;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.service.WeChatNotify;
import com.yunya365.wechat.service.impl.WXService;
import com.yunya365.wechat.service.impl.WxServerConfigVerify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description:
 * @author: xy
 * @date 2021/3/25 14:21
 **/
@RestController
@Slf4j
public class WxConfigController {
    @Resource
    private WxServerConfigVerify serverConfigVerify;
    @Resource
    private NotifyFactory notifyFactory;
    @Resource
    private WXService wxService;

    /**
     * Validate Token
     *
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return 若确认此次GET请求来自微信服务器，请原样返回echostr参数内容
     */
    @GetMapping("/callback/WeChatMsg")
    public String validateToken(@RequestParam("signature") String signature,
                                @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce,
                                @RequestParam("echostr") String echostr) {
        log.info("signature: {}, timestamp: {}, nonce: {}, echostr: {}",
                signature, timestamp, nonce, echostr);
        // 开发者通过检验signature对请求进行校验（下面有校验方式）。
        // 1）将token、timestamp、nonce三个参数进行字典序排序
        // 2）将三个参数字符串拼接成一个字符串进行sha1加密
        // 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        String encryptedParams = serverConfigVerify.verifyServerConfig(timestamp, nonce);
        if (encryptedParams.equals(signature)) {
            return echostr;
        } else {
            return "error";
        }
    }

    @PostMapping(value = "/callback/WeChatMsg", produces = MediaType.TEXT_XML_VALUE)
    public Object msgChat(@RequestBody WxUserMsgModel msg) throws Exception {
        //获取推送事件类型  可以拿到的事件: 1 关注/取消关注事件  2:扫描带参数二维码事件 3: 用户已经关注公众号 扫描带参数二维码事件 ...等等
        NotifyEnum notifyEnum = NotifyEnum.resolveEvent(msg.getMsgType(), msg.getEvent());
        WeChatNotify infoType = notifyFactory.loadWeChatNotify(notifyEnum);
        return infoType.weChatNotify(msg);
    }

    @GetMapping(value = "/wxVip/jsApi/ticket")
    public ResponseResult<WxSignatureVo> getAccessToken(@RequestParam(required = true) String url) {
        return ResponseUtil.success(wxService.getSignInfo(url));
    }

}
