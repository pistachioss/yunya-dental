package com.yunya.modules.emr;

import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffInfoModel;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.emr.biz.TreatPlanRecordBiz;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import com.yunya.modules.emr.rpc.EmrRest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
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
    @Autowired
    private EmrRest emrRest;

    @Before
    public void before() {

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

    public void test() {
        TreatPlanDetailWriteoffModel model = new TreatPlanDetailWriteoffModel();
        TreatPlanDetailWriteoffInfoModel obj = new TreatPlanDetailWriteoffInfoModel();
        obj.setTreatmentId(441848);
        obj.setQuantity(1);
        obj.setOrderDetailId(1111);
        obj.setPlanDetailIds(Arrays.asList(44));
        obj.setCrtId(635);
        emrRest.treatPlanWriteOffQunatity(model);
    }

}
