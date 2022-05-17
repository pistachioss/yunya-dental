package com.yunya.modules.sms.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.sms.model.SmsAutosendEventModel;
import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.sms.SmsAutosendEvent;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsEnableEnum;
import com.yunya.modules.sms.mapper.SmsAutosendEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介：短信自动发送业务层
 *
 * @author: chenlin
 * @Description: 短信自动发送业务层
 * @Date: 2020/12/16 12:51
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsAutosendEventBiz extends BaseBiz<SmsAutosendEventMapper, SmsAutosendEvent> {

    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;

    /**
     * 分页查询短信自动发送列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<SmsAutosendEventVO> findSmsAutosendEventList(SmsAutosendEventQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findSmsAutosendEventList(queryForm);
    }

    /**
     * 启用/关闭
     *
     * @param id 主键id
     */
    public void openOrClose(Integer id) {
        SmsAutosendEvent autosendEvent = selectById(id);
        if (autosendEvent == null) {
            throw new ClientServiceException("事件不存在", DATA_NOT_EXIST);
        }
        Byte status = autosendEvent.getStatus();
        if (SmsEnableEnum.DISABLE.getCode().equals(status)) {
            status = SmsEnableEnum.ENABLE.getCode();
        } else {
            status = SmsEnableEnum.DISABLE.getCode();
        }
        // 未关联模板的同时想启用
        if (autosendEvent.getTemplateId()==null && status.equals(SmsEnableEnum.ENABLE.getCode())) {
            throw new ClientServiceException("请先关联短信模板", OPERATION_NOT_ALLOW);
        }
        SmsAutosendEvent smsAutosendEvent = new SmsAutosendEvent();
        smsAutosendEvent.setId(id);
        smsAutosendEvent.setStatus(status);
        updateSelectiveById(smsAutosendEvent);
    }

    /**
     * 提交关联模板
     *
     * @param smsAutosendEventModel 签名设置添加模型
     */
    public void submit(SmsAutosendEventModel smsAutosendEventModel) {
        Integer id = smsAutosendEventModel.getId();
        SmsAutosendEvent autosendEvent = selectById(id);
        if (autosendEvent == null) {
            throw new ClientServiceException("事件不存在", DATA_NOT_EXIST);
        }
        Integer templateId = smsAutosendEventModel.getTemplateId();
        if (templateId != null) {
            SmsTemplateSetVO smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateSetById(templateId, false);
            if (smsTemplateSetVO == null) {
                throw new ClientServiceException("该短信模板不存在", DATA_NOT_EXIST);
            }
            if (!SmsApprovalStatusEnum.APPROVAL_PASS.getCode().equals(smsTemplateSetVO.getTemplateStatus())) {
                throw new ClientServiceException("该短信模板暂不可用", QUERY_RESULT_INVALID);
            }
        }
        autosendEvent.setTemplateId(templateId);
        updateById(autosendEvent);
    }

    /**
     * 初始化短信自动发送事件
     *
     * @param orgId
     * @param isClinic true-门诊端，false-公司端
     * @return
     */
    public ResponseResult initAutoSendEvent(Integer orgId, boolean isClinic) {
        List<SmsAutosendEventEnum> events = SmsAutosendEventEnum.values(isClinic);
        String userId = BaseContextHandler.getUserID();
        if (StringHelper.isNotEmpty(userId)) {// 创建门诊时
            events.forEach(event -> {
                SmsAutosendEvent entity = new SmsAutosendEvent();
                entity.setOrgId(orgId);
                entity.setStatus(SmsEnableEnum.DISABLE.getCode());
                entity.setEventCode(event.getCode());
                entity.setEventName(event.getValue());
                insert(entity);
            });
        } else {// 初始化创建
            Date now = new Date(System.currentTimeMillis());
            events.forEach(event -> {
                SmsAutosendEvent entity = new SmsAutosendEvent();
                entity.setEventCode(event.getCode());
                entity.setEventName(event.getValue());
                entity.setOrgId(orgId);
                entity.setStatus(SmsEnableEnum.DISABLE.getCode());
                entity.setCrtId(-999);
                entity.setCrtTime(now);
                entity.setUptId(-999);
                entity.setUptTime(now);
                mapper.insertSelective(entity);
            });
        }
        return ResponseUtil.success();
    }
}
