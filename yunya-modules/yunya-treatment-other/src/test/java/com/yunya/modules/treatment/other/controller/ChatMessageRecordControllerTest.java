package com.yunya.modules.treatment.other.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/4/13 13:47
 * @since: 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatMessageRecordControllerTest {

    @Autowired
    private ChatMessageRecordController chatMessageRecordController;

    @Test
    public void testHistory() throws Exception {
        ChatMessageRecordQuery query = new ChatMessageRecordQuery();
        query.setWhetherPage(false);
        query.setSendId(634);
        query.setReceiveId(636);
        query.setQueryDate("2022-04-19");
        PageInfo<ChatMessageBody> data = chatMessageRecordController.findChatMessageHisotry(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }
}
