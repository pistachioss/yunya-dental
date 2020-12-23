package com.yunya.feign.report;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.vo.BaseTreatmentProcessVO;
import com.yunya.feign.report.factory.RemoteMiddleTableServiceFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.report.BaseTreatmentProcess;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_MIDDLE_TABLE, fallbackFactory = RemoteMiddleTableServiceFactory.class)
public interface RemoteMiddleTableServiceFeign {
    /**
     * APP端就诊列表
     * @param query 查询参数
     * @return 分页实体
     */
    @RequestMapping(value = "api/app/treatment/list", method = RequestMethod.POST)
    PageInfo<BaseTreatmentProcessVO> treatmentList4App(@RequestBody TreatmentList4AppQuery query);
}
