package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.MemberExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberExpendRecordVo;
import com.yunya.models.patient_central.MemberExpendRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface MemberExpendRecordMapper extends Mapper<MemberExpendRecord> {

    /**
     * 消费记录 条件（消费者id（患者），会员卡号）
     * @param queryForm 消费记录查询QueryForm
     * @return MemberExpendRecordVo
     */
    List<MemberExpendRecordVo> expendList(@Param("form") MemberExpendRecordQueryForm queryForm);
}