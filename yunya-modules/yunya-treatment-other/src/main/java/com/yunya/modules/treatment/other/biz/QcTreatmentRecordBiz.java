package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.treatment_other.domain.form.QcAdviceItemStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadTreatmentForm;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcAdviceStatusVO;
import com.yunya.feign.treatment_other.domain.vo.QcDoctorAdviceRecordVO;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.treatment_other.QcTreatmentItem;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import com.yunya.modules.treatment.other.mapper.QcTreatmentItemMapper;
import com.yunya.modules.treatment.other.mapper.QcTreatmentRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;

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
    @Autowired
    private RemotePatientCentralServiceFeign patientFeign;

    public PageInfo<QcRecommondInfoVO> findMallRecommondList(QcRecommondInfoQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        QcDoctorAdviceQuery queryForm = new QcDoctorAdviceQuery();
        queryForm.setStart_date(query.getStartDate());
        queryForm.setEnd_date(query.getEndDate());
        queryForm.setWerif_code(query.getVerifyCode());
        QcDoctorAdviceRecordVO recommondList = qcWebServiceClientBiz.findDoctorAdviceRecommondList(queryForm);

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
        QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
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
            updateSelectiveById(qcTreatmentRecord);
        }
    }

    private QcTreatmentRecord checkQcTreatmentRecord(Integer id) {
        QcTreatmentRecord qcTreatmentRecord = selectById(id);
        if (StringHelper.isNull(qcTreatmentRecord)) {
            throw new ClientServiceException("全程医疗推荐数据不存在", DATA_NOT_EXIST);
        }
        return qcTreatmentRecord;
    }

    public void bindPatient(Integer id, Integer patientId) {
        QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
        PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
        if (StringHelper.isNull(patient)) {
            throw new ClientServiceException("患者信息不存在", DATA_NOT_EXIST);
        }
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        qcTreatmentRecord.setPatientId(patientId);
        String remark = StringHelper.defaultString(qcTreatmentRecord.getRemark());
        if (StringHelper.isNotEmpty(remark)) {
            remark += ";";
        }
        remark += "实际使用人:" + patient.getName();
        qcTreatmentRecord.setRemark(remark);
        qcTreatmentRecord.setUpdId(userId);
        qcTreatmentRecord.setUpdTime(now);
        updateSelectiveById(qcTreatmentRecord);
    }

    public void execute(Integer id) {
        checkQcTreatmentRecord(id);
        List<QcTreatmentItem> items = qcTreatmentItemMapper.selectListByQcTreatmentId(id);
        if (StringHelper.isNotEmpty(items)) {
            QcAdviceStatusForm statusForm = new QcAdviceStatusForm();
            List<QcAdviceItemStatusForm> orderInfos = Lists.newArrayList();
            items.forEach(item -> {
                QcAdviceItemStatusForm form = new QcAdviceItemStatusForm();
                form.setMall_order_no(item.getOrderNo());
                form.setStatus("6");
//                form.setForceFlag();
                orderInfos.add(form);
            });
            statusForm.setOrder_infos(orderInfos);
            qcWebServiceClientBiz.updateAdviceItemStatus(statusForm);
        }
    }

    public void doctorAdviceUpload(List<Integer> qcTreatmentIds) {
        List<QcAdviceUploadTreatmentForm> forms = Lists.newArrayList();
        qcTreatmentIds.forEach(id->{
            QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);

        });

        forms.forEach(form->{
            QcAdviceStatusVO result = qcWebServiceClientBiz.uploadAdvice2MallPlatform(form);
        });
    }
}
