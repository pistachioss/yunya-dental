package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.query.SmsSignatureSetQueryForm;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.models.sms.SmsSignatureSet;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SmsSignatureSetMapper extends Mapper<SmsSignatureSet> {

    /**
     * 分页查询短信签名列表
     *
     * @param smsSignatureSetQueryForm 查询参数
     * @return
     */
    List<SmsSignatureSetVO> findSmsSignatureSetList(@Param("queryForm") SmsSignatureSetQueryForm smsSignatureSetQueryForm);

    @Override
    int insert(SmsSignatureSet smsSignatureSet);
}