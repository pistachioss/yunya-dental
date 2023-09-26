package com.yunya.modules.treatment.other.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.form.QcTreatmentImportForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcPatientInfoVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/22 10:45
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class QcTreatmentWSTest {
    @Autowired
    private QcTreatmentRecordBiz qcTreatmentRecordBiz;
    @Autowired
    private QcWebServiceClientBiz qcWebServiceClientBiz;
    @Autowired
    private RemoteTreatmentServiceFeign treatmentServiceFeign;

    @Before
    public void currentInfo() {
        BaseContextHandler.setUserID("732");
        BaseContextHandler.setName("chenlin");
        BaseContextHandler.setOrgId("63");
    }

    /**
     * 医嘱查询
     */
    @Test
    public void testAdviceQuery() {
        QcDoctorAdviceQuery queryForm = new QcDoctorAdviceQuery();
        queryForm.setStart_date("2023-09-01");
        queryForm.setEnd_date("2023-10-01");
        queryForm.setAdm_no("72");
        queryForm.setWerif_code("2484");
        List<QcPatientInfoVO> patientTreatments = qcWebServiceClientBiz.findDoctorAdviceRecommondList(queryForm);
        System.out.println(JSONObject.toJSONString(patientTreatments));
    }

    /**
     * 医嘱下载（查询+存本地）
     */
    @Test
    public void testAdviceDownload() {
        QcRecommondInfoQuery query = new QcRecommondInfoQuery();
        query.setStartDate("2023-09-01");
        query.setEndDate("2023-10-01");
        query.setVerifyCode("");
        qcTreatmentRecordBiz.syncPatientTreatmentList(query);
    }

    /**
     * 全程就诊记录绑定ivy患者
     */
    @Test
    public void testBindPatient() {
        String admNo = "73";
        Integer patientId = 137;
        QcTreatmentRecord qcTreatment = getQcTreatmentIdByAdmNo(admNo);
        qcTreatmentRecordBiz.bindPatient(qcTreatment.getId(), patientId);
    }

    /**
     * 医嘱状态变更-医嘱执行
     */
    @Test
    public void testAdviceExecEasy() {
        String admNo = "72";
        QcTreatmentRecord treatment = getQcTreatmentIdByAdmNo(admNo);
        qcTreatmentRecordBiz.executorAdviceItem(treatment);
    }

    public void testAdviceExec() {
        String admNo = "72";
        Integer orderRecordId = 1;
        Integer billId = 1;
        QcTreatmentImportForm form = new QcTreatmentImportForm();
        QcTreatmentRecord qcTreatment = getQcTreatmentIdByAdmNo(admNo);
        form.setQcTreatmentIds(Lists.newArrayList(qcTreatment.getId()));
        form.setOrderRecordId(orderRecordId);
        form.setBillPayId(billId);
        List<OrderDetailChargeVO> orderDetails = treatmentServiceFeign.findOrderDetailAndBillDetailByOrderRecordId(orderRecordId).getItemList();
        form.setOrderDetails(orderDetails);
        qcTreatmentRecordBiz.updateQcTreatmentAndItems(form);
    }


    public QcTreatmentRecord getQcTreatmentIdByAdmNo(String admNo) {
        Example example = new Example(QcTreatmentRecord.class);
        example.createCriteria().andEqualTo("admNo", admNo);
        List<QcTreatmentRecord> qcTreatments = qcTreatmentRecordBiz.selectByExample(example);
        return qcTreatments.get(0);
    }

    /**
     * 医嘱上传
     */
    @Test
    public void testAdviceUpload() {
        String admNo = "71";
        QcTreatmentRecord treatment = getQcTreatmentIdByAdmNo(admNo);
        List<Integer> qcTreatmentIds = Lists.newArrayList(treatment.getId());
        qcTreatmentRecordBiz.uploadAdviceItems(qcTreatmentIds);
    }
}
