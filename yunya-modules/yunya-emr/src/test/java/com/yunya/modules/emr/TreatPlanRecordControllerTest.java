package com.yunya.modules.emr;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.model.TreatPlanDetailModel;
import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.feign.emr.domain.model.TreatPlanStepModel;
import com.yunya.feign.emr.domain.vo.ExaminationsVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.TreatPlanStatusEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.emr.biz.TreatPlanRecordBiz;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/9 14:08
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TreatPlanRecordControllerTest {
    @Autowired
    private MedicalCommonRecordMapper medicalCommonRecordMapper;
    @Autowired
    private TreatPlanRecordBiz treatPlanRecordBiz;
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Resource(name = "customizeThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;

    @Before
    public void before() {

    }

    @Test
    public void testAdd() throws InterruptedException {
        List<MedicalCommonRecord> list = medicalCommonRecordMapper.selectAll();
        if (StringHelper.isNotEmpty(list)) {
            Integer status = TreatPlanStatusEnum.UNCONFIRM.getCode();
            List<List<MedicalCommonRecord>> parts = Lists.partition(list, 500);
            CountDownLatch cdl = new CountDownLatch(parts.size());
            List<Future> resultFutures = new ArrayList<>();
            parts.forEach(part-> resultFutures.add(
                threadPoolExecutor.submit(()->{
                    try {
                        batchSave(part, status);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cdl.countDown();
                    }
                })));
            cdl.await();
            printExceptionLog(resultFutures, log);
        }
    }

    /**
     * 打印错误信息
     *
     * @param resultFutures 异常信息
     * @param log
     */
    public static void printExceptionLog(List<Future> resultFutures, Logger log) {
        if (StringHelper.isNotEmpty(resultFutures)) {
            resultFutures.forEach(
                    future -> {
                        try {
                            Object o = future.get();
                            if (o instanceof ClientServiceException) {
                                ClientServiceException cexp = (ClientServiceException) o;
                                log.info(cexp.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void batchSave(List<MedicalCommonRecord> list, Integer status) {
        BaseContextHandler.setUserID("-999");
        list.forEach(vo->{
            String planStr = vo.getPlan();
            if (StringHelper.isNotEmpty(planStr)) {
                JSONArray jsonArray = JSONArray.parseArray(planStr);
                List<ExaminationsVO> plans = jsonArray.toJavaList(ExaminationsVO.class);

                StringBuilder builder = new StringBuilder();
                Iterator<ExaminationsVO> it = plans.iterator();
                while (it.hasNext()) {
                    ExaminationsVO plan = it.next();
                    String describe = plan.getDescribe();
                    String toothPosition = plan.getTooth_position();
                    if (StringHelper.isEmpty(describe) && StringHelper.isEmpty(toothPosition)) {
                        it.remove();
                        continue;
                    }
                    if (StringHelper.isNotEmpty(describe)) {
                        if (builder.length()>0) {
                            builder.append("，");
                        }
                        builder.append(describe);
                    }
                }
                if (StringHelper.isNotEmpty(plans)) {
                    TreatPlanRecordModel model = new TreatPlanRecordModel();
                    model.setMedicalRecordId(vo.getId());
                    TreatmentRecord treatment = remoteTreatmentServiceFeign.findTreatmentRecordById(vo.getTreatmentId());
                    int orgId = 0;
                    if (ObjectUtils.isNotEmpty(treatment)) {
                        orgId = treatment.getOrgId();
                    }
                    model.setOrgId(orgId);
                    model.setPatientId(vo.getPatientId());
                    model.setDentistId(vo.getMajorDentistId());
                    model.setStatus(status);
                    String crtTime = DateUtil.format(vo.getCrtTime(), "yyyy-MM-dd");
                    String visitType = vo.getType()==0?"初诊":"复诊";
                    model.setPlanName(crtTime + visitType + "治疗计划");

                    TreatPlanStepModel stepModel = new TreatPlanStepModel();
                    stepModel.setStatus(status);
                    stepModel.setStepName("步骤1");
                    List<TreatPlanDetailModel> details = new ArrayList<>();
                    for (ExaminationsVO plan : plans) {
                        String describe = plan.getDescribe();
                        String toothPosition = plan.getTooth_position();
                        TreatPlanDetailModel detail = new TreatPlanDetailModel();
                        detail.setRemark(describe);
                        detail.setBillingItemId(-1);
                        detail.setType((byte)-1);
                        detail.setStatus(status);
                        detail.setPrice(new BigDecimal(0.00));
                        detail.setQuantity(0);
                        detail.setToothBit(toothPosition);
                        details.add(detail);
                    }
                    stepModel.setTreatPlanDetails(details);
                    model.setTreatPlanSteps(Arrays.asList(stepModel));
                    model.setSummary(builder.toString());
                    treatPlanRecordBiz.save(model, (byte) 0);
                }
            }
        });
    }

}
