package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.MemberReturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberReturnRecordVo;
import com.yunya.models.patient_central.MemberReturnRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MemberReturnRecordMapper extends Mapper<MemberReturnRecord> {

    /**
     * 根据患者id和会员卡号 查询退费记录
     * @param form
     * @return MemberReturnRecordVo
     */
    List<MemberReturnRecordVo> refundList(@Param("form") MemberReturnRecordQueryForm form);
}