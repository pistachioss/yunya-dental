package com.yunya.modules.treatment.other.biz;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadTreatmentForm;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.vo.QcAdviceStatusNoticeVO;
import com.yunya.feign.treatment_other.domain.vo.QcAdviceStatusVO;
import com.yunya.feign.treatment_other.domain.vo.QcDoctorAdviceRecordVO;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.modules.treatment.other.utils.WebServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author: chenlin
 * @date: 2023/9/12 9:42
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class QcWebServiceClientBiz {

    @Value("${qc.mall.webservice.wsdlUrl}")
    private String wsdlUrl;
    @Value("${qc.mall.webservice.targetNamespace}")
    private String targetNamespace;

    /**
     * 医嘱查询
     *
     * @return
     */
    public QcDoctorAdviceRecordVO findDoctorAdviceRecommondList(QcDoctorAdviceQuery query) {
        String methodName = "";
        String paramName = "";
        QcDoctorAdviceRecordVO qcResult = null;
        try {
            String result = WebServiceUtils.callByJson(methodName, paramName, query);
            qcResult = JSONObject.parseObject(result, QcDoctorAdviceRecordVO.class);
        } catch (Exception e) {
            log.error("QcMedical doctorAdvice item status update error: ", e);
            throw new ClientServiceException(e);
        }
        return qcResult;
    }

    /**
     * 医嘱状态变更
     *
     * @param statusForm
     * @return
     */
    public QcAdviceStatusVO updateAdviceItemStatus(QcAdviceStatusForm statusForm) {
        String methodName = "";
        String paramName = "";
        QcAdviceStatusVO qcResult = null;
        try {
            String result = WebServiceUtils.callByJson(methodName, paramName, statusForm);
            qcResult = JSONObject.parseObject(result, QcAdviceStatusVO.class);
            noticeAdviceItemStatus(qcResult);
        } catch (Exception e) {
            log.error("QcMedical doctorAdvice item status update error: ", e);
            throw new ClientServiceException(e);
        }
        return qcResult;
    }

    /**
     * 医嘱状态变更通知
     *
     * @param form
     */
    public QcAdviceStatusNoticeVO noticeAdviceItemStatus(QcAdviceStatusVO form) {
        String methodName = "";
        String paramName = "";
        QcAdviceStatusNoticeVO qcResult = null;
        try {
            String result = WebServiceUtils.callByJson(methodName, paramName, form);
            qcResult = JSONObject.parseObject(result, QcAdviceStatusNoticeVO.class);
        } catch (Exception e) {
            log.error("QcMedical doctorAdvice item status notice error: ", e);
            throw new ClientServiceException(e);
        }
        return qcResult;
    }

    /**
     * 医嘱上传至mall平台
     *
     * @param form
     * @return
     */
    public QcAdviceStatusVO uploadAdvice2MallPlatform(QcAdviceUploadTreatmentForm form) {
        String methodName = "";
        String paramName = "";
        QcAdviceStatusVO qcResult = null;
        try {
            String result = WebServiceUtils.callByJson(methodName, paramName, form);
            qcResult = JSONObject.parseObject(result, QcAdviceStatusVO.class);
            noticeAdviceItemStatus(qcResult);
        } catch (Exception e) {
            log.error("QcMedical doctorAdvice upload to qc_mall_platform error: ", e);
            throw new ClientServiceException(e);
        }
        return qcResult;
    }
}
