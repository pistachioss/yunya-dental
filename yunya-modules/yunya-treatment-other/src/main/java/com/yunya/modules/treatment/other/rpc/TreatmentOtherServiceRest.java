package com.yunya.modules.treatment.other.rpc;

import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.other.biz.VisitingRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
     * @param visitingRecord 表单
     */
    @ApiOperation(value = "插入随访记录")
    @RequestMapping(value = "/visiting/record/add",method = RequestMethod.POST)
    public void insertVisitingRecord(@RequestBody VisitingRecord visitingRecord){
        visitingRecordBiz.insertSelective(visitingRecord);
    }
}
