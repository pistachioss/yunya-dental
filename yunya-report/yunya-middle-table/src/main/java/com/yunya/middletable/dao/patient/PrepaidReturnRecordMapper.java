package com.yunya.middletable.dao.patient;

import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PrepaidMeturnRecordVo;
import com.yunya.models.patient_central.PrepaidReturnRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
public interface PrepaidReturnRecordMapper extends Mapper<PrepaidReturnRecord> {

    /**
     * 根据患者id和预付款卡号 查询退款信息
     * @param form 预付款退费记录列表
     * @return PrepaidMeturnRecordVo
     */
    List<PrepaidMeturnRecordVo> refundList(@Param("form") PrepaidMeturnRecordQueryForm form);
}