package com.yunya.modules.emr;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffInfoModel;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.feign.emr.domain.query.PlanTypeStatisticsQuery;
import com.yunya.feign.emr.domain.vo.TreatPlanTypeStatisticsVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
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
    public void test() {
        PlanTypeStatisticsQuery query = new PlanTypeStatisticsQuery();
        query.setDateType((byte)0);
        query.setStartDate("2021-01-01");
        query.setEndDate("2022-05-01");
        query.setOrgIds(Arrays.asList(26));
        query.setPlanTypeId(null);
        PageInfo<TreatPlanTypeStatisticsVO> data = treatPlanRecordController.findTreatPlanTypeStatistics(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }
}
