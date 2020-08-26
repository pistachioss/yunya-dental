package com.yunya.modules.treatment.other.rpc;

import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.modules.treatment.other.biz.VisitingRecordBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yunya-dental
 * @description: 就诊扩展外部调用接口
 * @author: LHB
 * @create: 2020-08-25 19:48
 **/
@RestController
@RequestMapping("api/treatment/other")
public class TreatmentOtherServiceRest {
    /** 随访管理服务 */
    @Autowired
    private VisitingRecordBiz visitingRecordBiz;

    /**
     * 插入随访记录
     * @param model 表单
     * @return 是否成功
     */
    @RequestMapping(value = "/visiting/record/add",method = RequestMethod.POST)
    public Integer insertVisitingRecord(@RequestBody VisitingRecordModel model){
        return visitingRecordBiz.insertVisitingRecord(model);
    }
}
