package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.SortUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment_other.ChatMessageRecord;
import com.yunya.modules.treatment.other.mapper.ChatMessageRecordMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Resource(name = "customizeThreadPool")
    private ExecutorService executorService;

    public PageInfo<ChatMessageBody> findChatMessageHisotry(ChatMessageRecordQuery query) throws Exception {
        ChatMessageRecordQuery queryForm = new ChatMessageRecordQuery();
        BeanUtils.copyProperties(query, queryForm);
        Future<List<ChatMessageBody>> thisFuture = multiFindChatMessageHistory(query);
        queryForm.setSendId(query.getReceiveId());
        queryForm.setReceiveId(query.getSendId());
        Future<List<ChatMessageBody>> thatFuture = multiFindChatMessageHistory(queryForm);
        List<ChatMessageBody> result = new ArrayList<>();
        List<ChatMessageBody> thisChat = thisFuture.get();
        if (StringHelper.isNotEmpty(thisChat)) {
            result.addAll(thisChat);
        }
        List<ChatMessageBody> thatChat = thatFuture.get();
        if (StringHelper.isNotEmpty(thatChat)) {
            result.addAll(thatChat);
        }
        result = SortUtil.sort(result, SortUtil.comparing(ChatMessageBody::getId));
        return PageUtl.doPage(query, result);
    }

    private Future<List<ChatMessageBody>> multiFindChatMessageHistory(ChatMessageRecordQuery query) {
        return executorService.submit(()-> findChatMessageList(query));
    }

    public List<ChatMessageBody> findChatMessageList(ChatMessageRecordQuery query) {
        List<ChatMessageBody> result = mapper.selectChatMessageHistory(query);
        if (StringHelper.isNotEmpty(result)) {
            result.forEach(vo-> putChatEmployeeName(vo));
        }
        return result;
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
            entity.setCrtTime(message.getSendTime());
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

    public List<ChatMessageBody> findChatMessageUnReadHisotry(ChatMessageBody message) {
        ChatMessageRecordQuery query = new ChatMessageRecordQuery();
        query.setWhetherPage(false);
        query.setReceiveId(message.getSendId());
        query.setHadRead(false);
        List<ChatMessageBody> list = findChatMessageList(query);
        if (StringHelper.isEmpty(list)) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 查找并装配员工姓名
     *
     * @param message
     */
    public void putChatEmployeeName(ChatMessageBody message) {
        Integer sendId = message.getSendId();
        if (!ObjectUtils.isEmpty(sendId)) {
            SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(sendId);
            if (!ObjectUtils.isEmpty(employee)) {
                message.setSendUser(employee.getName());
            }
        }
        Integer receiveId = message.getReceiveId();
        if (!ObjectUtils.isEmpty(receiveId)) {
            SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(receiveId);
            if (!ObjectUtils.isEmpty(employee)) {
                message.setReceiveUser(employee.getName());
            }
        }
    }
}
