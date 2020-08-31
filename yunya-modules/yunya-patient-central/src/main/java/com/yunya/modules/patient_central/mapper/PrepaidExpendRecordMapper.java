package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.PrepaidExpendRecordVo;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PrepaidExpendRecordMapper extends Mapper<PrepaidExpendRecord> {

    /**
     * 查询预付款消费记录
     * @param queryForm
     * @return
     */
    List<PrepaidExpendRecordVo> expendList(@Param("form") PrepaidExpendRecordQueryForm queryForm);
}