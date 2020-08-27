package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.PrepaidRechargeRecordVo;
import com.yunya.models.patient_central.PrepaidRechargeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PrepaidRechargeRecordMapper extends Mapper<PrepaidRechargeRecord> {
    /**
     * 预付款充值记录查询
     * @param form
     * @return List<PrepaidRechargeRecordVo>
     */
    List<PrepaidRechargeRecordVo> RechargeRecord(@Param("form") PrepaidRechargeRecordQueryForm form);
}