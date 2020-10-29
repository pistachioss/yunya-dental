package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.vo.ArrearsVo;
import com.yunya.models.report.BaseBill;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillMapper extends Mapper<BaseBill> {
    /**
     * 欠费查询
     * @param form 欠费查询
     * @param patientIds 患者id
     * @return List<ArrearsVo>
     */
    List<ArrearsVo> arrears(ArrearsQueryForm form, List<Integer> patientIds);
}