package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment_other.ChatMessageRecord;
import com.yunya.modules.treatment.other.mapper.ChatMessageRecordMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
    @Resource(name = "customizeThreadPool")
    private ExecutorService executorService;

    public PageInfo<ChatMessageBody> findChatMessageHisotry(ChatMessageRecordQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<ChatMessageBody> result = mapper.selectChatMessageHistory(query);
        return new PageInfo<>(result);
    }

    /**
     * 异步记录聊天消息
     *
     * @param message
     * @param receiveId
     */
    public void asyncArchiveChatMessage(ChatMessageBody message, Integer receiveId) {
        executorService.submit(()->{
            ChatMessageRecord entity = new ChatMessageRecord();
            BeanUtils.copyProperties(message, entity);
            entity.setCrtId(message.getSendId());
            entity.setCrtTime(new Date(System.currentTimeMillis()));
            Byte mode = 0;
            if (!ObjectUtils.isEmpty(receiveId)) {
                mode = 1;
                entity.setReceiveId(receiveId);
            }
            entity.setMode(mode);
            entity.setHadRead(false);
            mapper.insertSelective(entity);
        });
    }

    /**
     * 异步更新消息已读
     *
     * @param message
     */
    public void asyncUptMessageHadRead(ChatMessageBody message) {
        executorService.submit(()->{
            ChatMessageRecord entity = new ChatMessageRecord();
            BeanUtils.copyProperties(message, entity);
            entity.setUptId(message.getReceiveId());
            entity.setUptTime(new Date(System.currentTimeMillis()));
            entity.setHadRead(true);
            Example example = new Example(ChatMessageRecord.class);
            Example.Criteria c = example.createCriteria();
            c.andEqualTo("msgCode", message.getMsgCode());
            mapper.updateByExampleSelective(entity, example);
        });
    }
}
