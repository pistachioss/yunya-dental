package com.yunya365.wechat.mapper;

import com.yunya.models.wechat.WxMsgTemplates;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WxMsgTemplatesMapper extends Mapper<WxMsgTemplates> {

    void batchInsert(@Param("list") List<WxMsgTemplates> list);
}