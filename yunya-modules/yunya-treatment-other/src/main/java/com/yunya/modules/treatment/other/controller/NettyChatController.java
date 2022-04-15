package com.yunya.modules.treatment.other.controller;

import com.yunya.feign.treatment_other.domain.vo.NettyChatInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 聊天接口
 *
 * @author Administrator
 */
@Controller("netty")
public class NettyChatController {
    /** netty服务器ip*/
    @Value("${netty.ip}")
    private String ip;
    /** netty服务器端口*/
    @Value("${netty.port}")
    private Integer port;

    @RequestMapping("/")
    public String chat() {
        return "chat";
    }

    /**
     * 获取Netty聊天服务器信息
     *
     * @return
     */
    @RequestMapping("/info")
    public ResponseResult<NettyChatInfoVO> findInfo() {
        NettyChatInfoVO info = new NettyChatInfoVO(ip, port);
        return ResponseUtil.success(info);
    }
}