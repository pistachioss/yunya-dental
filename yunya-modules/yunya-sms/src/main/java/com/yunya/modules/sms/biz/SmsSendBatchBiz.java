package com.yunya.modules.sms.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.sms.query.SmsSendBatchQueryForm;
import com.yunya.feign.sms.vo.SmsSendBatchVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.sms.SmsSendBatch;
import com.yunya.modules.sms.mapper.SmsSendBatchMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介：短信发送批次业务层
 *
 * @author: chenlin
 * @Description: 短信发送批次业务层
 * @Date: 2020/12/14 14:16
 * @since: 1.0.0
 */
@Service
@Transactional
public class SmsSendBatchBiz extends BaseBiz<SmsSendBatchMapper, SmsSendBatch> {

    /**
     * 分页查询短信发送批次列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<SmsSendBatchVO> findSmsSendBatchList(SmsSendBatchQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findSmsSendBatchList(queryForm);
    }

    /**
     * 添加并返回将主键id装配到实体上
     *
     * @param orgId 组织id
     * @param templateId 短信模板id
     * @param sendUserId 发送人id
     * @param sendTime 发送时间
     * @param crtUser 发送人姓名
     * @param type 短信类型
     * @param sendNum 发送人数
     * @return
     */
    public int insertEntity(Integer orgId, Integer templateId, Integer sendUserId, Date sendTime, String crtUser, Byte type, Integer sendNum) {
        SmsSendBatch smsSendBatch = new SmsSendBatch();
        smsSendBatch.setOrgId(orgId);
        smsSendBatch.setTemplateId(templateId);
        smsSendBatch.setCrtId(sendUserId);
        smsSendBatch.setCrtTime(sendTime);
        smsSendBatch.setCrtUser(crtUser);
        smsSendBatch.setType(type);
        smsSendBatch.setSendNum(sendNum);
        int count = mapper.insert(smsSendBatch);
        if (count != 1) {
            throw new ClientServiceException("插入数据失败", OperationCodeConstants.INSERT_MODEL);
        }
        return smsSendBatch.getId();
    }
}
