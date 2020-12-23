package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.vo.SmsSendRecordVO;
import com.yunya.models.sms.SmsSendRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SmsSendRecordMapper extends Mapper<SmsSendRecord> {

    /**
     * 分页查询短信发送记录列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<SmsSendRecordVO> findSmsSendRecordList(@Param("queryForm") SmsSendRecordQueryForm queryForm);

    @Override
    int insert(SmsSendRecord smsSendRecord);
}