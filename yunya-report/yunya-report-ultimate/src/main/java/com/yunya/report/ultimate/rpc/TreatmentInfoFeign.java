package com.yunya.report.ultimate.rpc;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.vo.BaseTreatmentProcessVO;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yunya-dental
 * @description: 就诊信息
 * @author: LHB
 * @create: 2020-12-23 09:45
 **/
@Api("就诊信息")
@RestController
@RequestMapping("api/app/treatment")
public class TreatmentInfoFeign {
    @Autowired
    private BaseTreatmentProcessBiz baseTreatmentProcessBiz;

    /**
     * APP端就诊列表
     * @param query 查询参数
     * @return 分页实体
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageInfo<BaseTreatmentProcessVO> treatmentList4App(@RequestBody TreatmentList4AppQuery query) {
        return this.baseTreatmentProcessBiz.treatmentList4App(query);
    }
}
