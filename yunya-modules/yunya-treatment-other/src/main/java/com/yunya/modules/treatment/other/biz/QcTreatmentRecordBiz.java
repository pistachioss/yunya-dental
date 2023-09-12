package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.treatment_other.domain.form.QcAdviceItemStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment_other.QcTreatmentItem;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import com.yunya.modules.treatment.other.mapper.QcTreatmentItemMapper;
import com.yunya.modules.treatment.other.mapper.QcTreatmentRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:42
 * @description:
 * @since: 1.0.0
 */
@Service
public class QcTreatmentRecordBiz extends BaseBiz<QcTreatmentRecordMapper, QcTreatmentRecord> {
    @Autowired
    private QcTreatmentItemMapper qcTreatmentItemMapper;
    @Autowired
    private QcWebServiceClientBiz qcWebServiceClientBiz;

    public PageInfo<QcRecommondInfoVO> findMallRecommondList(QcRecommondInfoQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<QcRecommondInfoVO> result = mapper.selectMallRecommondList(query);
        return new PageInfo<>(result);
    }

    /**
     * mall平台核销
     *
     * @param id
     * @param verifyCode
     */
    public void verify(Integer id, String verifyCode) {
        QcTreatmentRecord qcTreatmentRecord = selectById(id);
        if (StringHelper.isNull(qcTreatmentRecord)) {
            throw new ClientServiceException("数据不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        List<QcTreatmentItem> items = qcTreatmentItemMapper.selectListByQcTreatmentId(id);
        if (StringHelper.isNotEmpty(items)) {
            QcAdviceStatusForm statusForm = new QcAdviceStatusForm();
            List<QcAdviceItemStatusForm> orderInfos = Lists.newArrayList();
            items.forEach(item->{
                QcAdviceItemStatusForm form = new QcAdviceItemStatusForm();
                form.setMall_order_no(item.getOrderNo());
                form.setStatus("1");
                form.setVerifCode(verifyCode);
//                form.setForceFlag();
                orderInfos.add(form);
            });
            statusForm.setOrder_infos(orderInfos);
            qcWebServiceClientBiz.updateAdviceItemStatus(statusForm);

            Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
            Date now = DateUtil.now();
            qcTreatmentRecord.setVerifyId(userId);
            qcTreatmentRecord.setVerifyCode(verifyCode);
            qcTreatmentRecord.setVerifyDate(now);
            qcTreatmentRecord.setUpdId(userId);
            qcTreatmentRecord.setUpdTime(now);
            mapper.updateByPrimaryKey(qcTreatmentRecord);
        }
    }
}
