package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.RechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.RechargeRecordVo;
import com.yunya.models.patient_central.MemberRechargeRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface MemberRechargeRecordMapper extends Mapper<MemberRechargeRecord> {

    /**
     * 充值记录
     * @param form 充值记录QueryForm
     * @param type 类型
     * @return RechargeRecordVo
     */
    List<RechargeRecordVo> RechargeRecord(@Param("form") RechargeRecordQueryForm form, @Param("type") Integer type);
}