package com.yunya.middletable.dao.patient;

import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PrepaidRechargeRecordVo;
import com.yunya.models.patient_central.PrepaidRechargeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
public interface PrepaidRechargeRecordMapper extends Mapper<PrepaidRechargeRecord> {
    /**
     * 预付款充值记录查询
     * @param form 充值记录queryForm
     * @return List<PrepaidRechargeRecordVo>
     */
    List<PrepaidRechargeRecordVo> RechargeRecord(@Param("form") PrepaidRechargeRecordQueryForm form);
}