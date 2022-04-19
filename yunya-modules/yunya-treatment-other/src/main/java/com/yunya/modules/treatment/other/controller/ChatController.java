package com.yunya.modules.treatment.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/4/18 11:25
 * @since: 1.0.0
 */
@Controller
public class ChatController {

    /**
     * 获取Netty聊天服务器信息
     *
     * @return
     */
    @GetMapping("/chat")
    public String chatHtml() {
        return "chat";
    }
}
