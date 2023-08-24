package com.yunya.modules.treatment.biz.ws;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;

import javax.jws.WebService;

@WebService(
        name = "MyWebService", // 暴露服务名称
        targetNamespace = "http://ws.biz.treatment.modules.yunya.com"// 命名空间,一般是接口的包名倒序
)
public interface MyWebService {

//    @WebMethod
    PageInfo<BillRestReceivableAmountVO> findPatientInfoById(/*@WebParam(name = "id", targetNamespace = "http://ws.biz.treatment.modules.yunya.com")*/ BillOfReceivableQuery query);
}