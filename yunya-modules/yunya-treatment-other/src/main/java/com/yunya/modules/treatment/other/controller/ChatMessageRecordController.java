package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.NettyChatInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.ChatMessageRecordBiz;
import com.yunya.modules.treatment.other.config.WsProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介：聊天消息记录控制层
 *
 * @author: chenlin
 * @Description: 聊天消息记录控制层
 * @Date: 2021/10/19 17:31
 * @since: 1.0.0
 */
@Api(tags = "聊天消息记录控制层")
@RestController
@RequestMapping("/chatMessage")
public class ChatMessageRecordController {
    /** netty服务器ip*/
    @Autowired
    private WsProperties wsProperties;

    @Autowired
    private ChatMessageRecordBiz chatMessageRecordBiz;

    /**
     * 根据条件查询消息历史记录
     *
     * @param query
     * @return
     */
    @ApiOperation("根据条件查询消息历史记录")
    @PostMapping("/history")
    @CurrentUser
    public ResponseResult<PageInfo<ChatMessageBody>> findChatMessageHisotry(@Validated @RequestBody ChatMessageRecordQuery query) throws Exception {
        PageInfo<ChatMessageBody> pageInfo = chatMessageRecordBiz.findChatMessageHisotry(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 获取Netty聊天服务器信息
     *
     * @return
     */
    @GetMapping("/nettyServer/info")
    @ApiOperation("获取Netty聊天服务器信息")
    public ResponseResult<NettyChatInfoVO> findInfo() {
        NettyChatInfoVO info = new NettyChatInfoVO(wsProperties.getProtocol(), wsProperties.getPath());
        return ResponseUtil.success(info);
    }
}
