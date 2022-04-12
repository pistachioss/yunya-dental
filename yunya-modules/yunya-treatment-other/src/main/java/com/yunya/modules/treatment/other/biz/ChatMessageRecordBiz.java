package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment_other.ChatMessageRecord;
import com.yunya.modules.treatment.other.mapper.ChatMessageRecordMapper;
import org.springframework.stereotype.Service;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/4/11 17:52
 * @since: 1.0.0
 */
@Service
public class ChatMessageRecordBiz extends BaseBiz<ChatMessageRecordMapper, ChatMessageRecord> {

    public void findChatMessageHisotry(ChatMessageRecordQuery query) {

    }
}
