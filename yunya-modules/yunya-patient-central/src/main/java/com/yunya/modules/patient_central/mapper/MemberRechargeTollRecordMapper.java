package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.RechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.RechargeRecordVo;
import com.yunya.models.patient_central.MemberRechargeTollRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MemberRechargeTollRecordMapper extends Mapper<MemberRechargeTollRecord> {
    /**
     *
     * @param form
     * @return
     */
    List<RechargeRecordVo> RechargeRecordList(RechargeRecordQueryForm form);
}