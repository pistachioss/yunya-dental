package com.yunya365.wechat.mapper;

import com.yunya.models.wechat.WxAutomsg;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WxAutomsgMapper extends Mapper<WxAutomsg> {
    List<WxAutomsg> selectAllOrderBy();
}