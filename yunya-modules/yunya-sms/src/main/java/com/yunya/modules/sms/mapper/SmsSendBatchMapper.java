package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.query.SmsSendBatchQueryForm;
import com.yunya.feign.sms.vo.SmsSendBatchVO;
import com.yunya.models.sms.SmsSendBatch;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SmsSendBatchMapper extends Mapper<SmsSendBatch> {
    /**
     * 分页查询短信发送批次列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<SmsSendBatchVO> findSmsSendBatchList(@Param("queryForm") SmsSendBatchQueryForm queryForm);

    @Override
    int insert(SmsSendBatch smsSendBatch);
}