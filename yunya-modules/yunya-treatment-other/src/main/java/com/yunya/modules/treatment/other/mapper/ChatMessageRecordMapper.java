package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.form.ChatMessageBody;
import com.yunya.feign.treatment_other.domain.query.ChatMessageRecordQuery;
import com.yunya.models.treatment_other.ChatMessageRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ChatMessageRecordMapper extends Mapper<ChatMessageRecord> {
    List<ChatMessageBody> selectChatMessageHistory(@Param("query") ChatMessageRecordQuery query);
}