package com.yunya.feign.treatment_other;

import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.feign.treatment_other.factory.RemoteTreatmentOtherFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 就诊扩展外部调用接口
 * @author A
 */
@FeignClient(
        value = YunyaServiceNameConstants.YUNYA_TREATMENT_OTHER,
        fallbackFactory = RemoteTreatmentOtherFactory.class)
public interface RemoteTreatmentOtherFeign {
    /**
     * 插入随访记录
     * @param model 表单
     * @return 是否成功
     */
    @RequestMapping(value = "api/treatment/other/visiting/record/add",method = RequestMethod.POST)
    Integer insertVisitingRecord(@RequestBody VisitingRecordModel model);

    /**
     * 根据条件查询随访记录
     * @param query 查询条件
     * @return List<VisitingRecordVo>
     */
    @ApiOperation(value = "根据条件查询随访记录")
    @RequestMapping(value = "api/treatment/other/visiting/record/find", method = RequestMethod.POST)
    List<VisitingRecordVo> findVisitingRecordByConditionRest(@RequestBody VisitingRecordQuery query);
}
