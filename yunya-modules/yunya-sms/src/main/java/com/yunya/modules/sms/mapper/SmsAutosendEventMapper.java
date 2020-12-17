package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.models.sms.SmsAutosendEvent;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SmsAutosendEventMapper extends Mapper<SmsAutosendEvent> {

    /**
     * 分页查询短信自动发送列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<SmsAutosendEventVO> findSmsAutosendEventList(@Param("queryForm") SmsAutosendEventQueryForm queryForm);

    /**
     * 根据id查询自动发送详情
     *
     * @param id 主键id
     * @return
     */
    SmsAutosendEventVO findSmsAutosendEventById(@Param("id") Integer id);
}