package com.yunya.modules.emr;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffInfoModel;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.feign.emr.domain.query.PlanTypeStatisticsQuery;
import com.yunya.feign.emr.domain.query.TreatPlanRecordQuery;
import com.yunya.feign.emr.domain.vo.TreatPlanRecordInfoVO;
import com.yunya.feign.emr.domain.vo.TreatPlanRecordVO;
import com.yunya.feign.emr.domain.vo.TreatPlanTypeStatisticsVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.modules.emr.controller.TreatPlanRecordController;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import com.yunya.modules.emr.rpc.EmrRest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
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
    private TreatPlanRecordController treatPlanRecordController;
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Resource(name = "customizeThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private EmrRest emrRest;

    @Test
    public void testTreatPlanWriteOffQunatity() {
        TreatPlanDetailWriteoffModel model = new TreatPlanDetailWriteoffModel();
        TreatPlanDetailWriteoffInfoModel obj = new TreatPlanDetailWriteoffInfoModel();
        obj.setTreatmentId(441848);
        obj.setQuantity(1);
        obj.setOrderDetailId(1111);
        obj.setPlanDetailIds(Arrays.asList(44));
        obj.setCrtId(635);
        emrRest.treatPlanWriteOffQunatity(model);
    }

    @Test
    public void testSave() {
        BaseContextHandler.setUserID("636");
        String param = "{\"patientId\":78307,\"dentistId\":636,\"planTypeId\":552,\"orgId\":42,\"planName\":\"计划1\",\"planId\":null,\"summary\":\"\",\"remark\":\"\",\"operationReason\":\"\",\"status\":1,\"showStatus\":1,\"treatPlanSteps\":[{\"stepName\":\"步骤1\",\"treatPlanDetails\":[{\"billingItemId\":201,\"billingItemName\":\"初诊挂号费【1】\",\"price\":190,\"quantity\":2,\"remark\":\"\",\"toothBit\":\"\",\"type\":0,\"unit\":\"次\",\"orignPrice\":380,\"index\":0},{\"billingItemId\":203,\"billingItemName\":\"器械消毒费\",\"price\":20,\"quantity\":1,\"remark\":\"\",\"toothBit\":\"\",\"type\":0,\"unit\":\"次\",\"orignPrice\":20,\"index\":1}]}]}";
        TreatPlanRecordModel model = JSONObject.parseObject(param, TreatPlanRecordModel.class);
        treatPlanRecordController.save(model);
    }
    
    @Test
    public void test() {
        PlanTypeStatisticsQuery query = new PlanTypeStatisticsQuery();
        query.setDateType((byte)0);
        query.setStartDate("2022-01-01");
        query.setEndDate("2022-06-01");
        query.setOrgIds(Arrays.asList(63));
        query.setDentistIds(Arrays.asList(732));
        query.setPlanTypeId(null);
        PageInfo<TreatPlanTypeStatisticsVO> data = treatPlanRecordController.findTreatPlanTypeStatistics(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }
    
    @Test
    public void testFindList() {
        TreatPlanRecordQuery query = new TreatPlanRecordQuery();
        query.setPatientId(78307);
        query.setDateType((byte) 0);
        query.setStartDate("2021-01-01");
        query.setEndDate("2022-05-01");
        PageInfo<TreatPlanRecordInfoVO> data = treatPlanRecordController.findList(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }

    @Test
    public void testFindPatientTreatPlanList() {
        TreatPlanRecordQuery query = new TreatPlanRecordQuery();
        query.setPatientId(78307);
        query.setDateType((byte) 0);
        query.setStartDate("2021-01-01");
        query.setEndDate("2022-05-01");
        PageInfo<TreatPlanRecordVO> data = treatPlanRecordController.findPatientTreatPlanList(42, query).getData();
        System.out.println(JSONObject.toJSON(data));
    }
}
