package com.yunya.modules.treatment.biz.ws.impl;

import com.yunya.feign.treatment.domain.vo.OrderBill4AppVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.treatment.biz.ws.MyWebService;
import com.yunya.modules.treatment.mapper.BillRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service
@WebService(serviceName = "MyWebService", // 与接口中指定的name一致, 都可以不写
        targetNamespace = "http://ws.biz.treatment.modules.yunya.com", // 与接口中的命名空间一致,一般是接口的包名倒，都可以不用写
        endpointInterface = "com.yunya.modules.treatment.biz.ws.MyWebService" // 接口类全路径
)
public class MyWebServiceImpl implements MyWebService {
//    @Autowired
//    private RemotePatientCentralServiceFeign patientFeign;

    @Autowired
    private BillRecordMapper billRecordBiz;

    @Override
    public String findPatientInfoById(Integer id) {
        System.out.println(">>>>>>>>获取到请求参数：" + id);
//        PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
//        if (StringHelper.isNotNull(patient)) {
//            return patient.getName();
//        }
        OrderBill4AppVO result = billRecordBiz.findOrderAndBill4App(id);
        if (StringHelper.isNotNull(result)) {
            return result.getBillNumber();
        }
        return StringHelper.EMPTY;
    }
}