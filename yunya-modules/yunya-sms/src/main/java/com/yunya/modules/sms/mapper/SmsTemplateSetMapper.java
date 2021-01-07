package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.query.SmsTemplateSetQueryForm;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.models.sms.SmsTemplateSet;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SmsTemplateSetMapper extends Mapper<SmsTemplateSet> {

    @Override
    int insert(SmsTemplateSet smsTemplateSet);

    /**
     * 分页查询短信模板列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<SmsTemplateSetVO> findSmsTemplateSetList(@Param("queryForm") SmsTemplateSetQueryForm queryForm);

    /**
     * 根据主键id获取短信模板设置
     *
     * @param id 主键id
     * @return
     */
    SmsTemplateSetVO findSmsTemplateSetById(@Param("id") Integer id);

    /**
     * 根据事件code查询模板信息
     *
     * @param eventCode 事件模板
     * @param orgId
     * @return list
     */
    SmsTemplateSetVO findSmsTemplateByEventCode(@Param("eventCode") String eventCode, @Param("orgId") Integer orgId);
}