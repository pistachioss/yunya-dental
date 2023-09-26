package com.yunya.modules.treatment.other.biz;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadForm;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.vo.*;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.modules.treatment.other.utils.WebServiceParam;
import com.yunya.modules.treatment.other.utils.WebServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
    /** 方法名 */
    private static final String METHOD_NAME = "HIPMessageServer";
    /** 入参1 */
    private static final String INPUT1 = "input1";
    /** 入参2 */
    private static final String INPUT2 = "input2";

    /**
     * 医嘱查询
     *
     * @return
     */
    public List<QcPatientInfoVO> findDoctorAdviceRecommondList(QcDoctorAdviceQuery query) {
        String messageCode = "MES0085";
        QcDoctorAdviceRecordVO qcResult = null;
        try {
            qcResult = callByJson(messageCode, query, QcDoctorAdviceRecordVO.class);
            logResultErr(qcResult, messageCode);
        } catch (Exception e) {
            log.error("QcMedical doctorAdvice query error: ", e);
            throw new ClientServiceException(e);
        }
        return qcResult.getPat_info();
    }

    private void logResultErr(QcResult qcResult, String messageCode) {
        if (!"0".equals(qcResult.getCode())) {
            log.error("qcMedical doctorAdvice message_code: {}, result fail, error: {}", messageCode, qcResult.getMsg());
            throw new ClientServiceException(qcResult.getMsg(), Integer.parseInt(qcResult.getCode()));
        }
    }

    /**
     * 医嘱状态变更
     *
     * @param statusForm
     * @return
     */
    public QcAdviceStatusVO updateAdviceItemStatus(QcAdviceStatusForm statusForm) {
        String messageCode = "MES0090";
        QcAdviceStatusVO qcResult = null;
        try {
            qcResult = callByJson(messageCode, statusForm, QcAdviceStatusVO.class);
            logResultErr(qcResult, messageCode);
//            noticeAdviceItemStatus(qcResult);
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
        String messageCode = "MES0091";
        QcAdviceStatusNoticeVO qcResult = null;
        try {
            qcResult = callByJson(messageCode, form, QcAdviceStatusNoticeVO.class);
            logResultErr(qcResult, messageCode);


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
    public QcAdviceStatusVO uploadAdvice2MallPlatform(QcAdviceUploadForm form) {
        String messageCode = "MES0083";
        QcAdviceStatusVO qcResult = null;
        try {
            qcResult = callByJson(messageCode, form, QcAdviceStatusVO.class);
            logResultErr(qcResult, messageCode);
//            noticeAdviceItemStatus(qcResult);
        } catch (Exception e) {
            log.error("QcMedical doctorAdvice upload to qc_mall_platform error: ", e);
            throw new ClientServiceException(e);
        }
        return qcResult;
    }

    /**
     * 通过messageCode决定接口操作
     *
     * @param messageCode 消息代码-决定调用方法
     * @param json
     * @param clzz 调用接口结果后得到的数据类型
     * @return
     */
    public <T> T callByJson(String messageCode, Object json, Class<T> clzz){
        String data = JSONObject.toJSONString(json);
        WebServiceParam input1 = new WebServiceParam();
        input1.setInName(INPUT1);
        input1.setData(messageCode);
        WebServiceParam input2 = new WebServiceParam();
        input2.setInName(INPUT2);
        input2.setData(data);
        String result = WebServiceUtils.callWebService(wsdlUrl, targetNamespace, METHOD_NAME, input1, input2);
        return JSONObject.parseObject(result, clzz);
    }

}
