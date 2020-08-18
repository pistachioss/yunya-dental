package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.RechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.RechargeRecordVo;
import com.yunya.models.patient_central.MemberRechargeRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MemberRechargeRecordMapper extends Mapper<MemberRechargeRecord> {

    /**
     * 充值记录
     * @param form
     * @return RechargeRecordVo
     */
    List<RechargeRecordVo> RechargeRecord(RechargeRecordQueryForm form);
}