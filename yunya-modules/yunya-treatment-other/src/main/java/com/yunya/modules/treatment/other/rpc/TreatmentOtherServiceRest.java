package com.yunya.modules.treatment.other.rpc;

import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.other.biz.VisitingRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: 就诊扩展外部服务调用接口
 * @author: LHB
 * @create: 2020-08-25 19:48
 **/
@Api(tags = "就诊扩展外部服务调用接口")
@RestController
@RequestMapping("api/treatment/other")
public class TreatmentOtherServiceRest {
    /** 随访管理服务 */
    @Autowired
    private VisitingRecordBiz visitingRecordBiz;

    /**
     * 插入随访记录
     * @param visitingRecords 表单
     */
    @ApiOperation(value = "插入随访记录")
    @RequestMapping(value = "/visiting/record/add",method = RequestMethod.POST)
    public void insertVisitingRecordRest(@RequestBody List<VisitingRecord> visitingRecords){
        visitingRecordBiz.insertEntity(visitingRecords);
    }

    /**
     * 根据条件查询随访记录
     * @param query 查询条件
     * @return List<VisitingRecordVo>
     */
    @ApiOperation(value = "根据条件查询随访记录")
    @RequestMapping(value = "/visiting/record/find", method = RequestMethod.POST)
    public List<VisitingRecordVo> findVisitingRecordByConditionRest(@RequestBody VisitingRecordQuery query) {
        return visitingRecordBiz.findVisitingRecordByConditionRest(query);
    }

    /**
     * 根据就诊记录ID删除随访
     * @param treatmentId 就诊记录ID
     */
    @ApiOperation(value = "根据就诊记录ID删除随访")
    @RequestMapping(value = "/visiting/record/delete/{treatmentId}", method = RequestMethod.DELETE)
    public void deleteVisitingRecordByTreatmentIdRest(@PathVariable(value = "treatmentId") Integer treatmentId) {
        visitingRecordBiz.deleteVisitingRecordByTreatmentId(treatmentId);
    }

}
