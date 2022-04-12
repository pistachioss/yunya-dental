package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.ChatMessage;
import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.ChatMessageRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseResult<PageInfo<ChatMessage>> findChatMessageHisotry(@Validated @RequestBody ChatMessageRecordQuery query) {
        chatMessageRecordBiz.findChatMessageHisotry(query);
        return ResponseUtil.success();
    }
}
