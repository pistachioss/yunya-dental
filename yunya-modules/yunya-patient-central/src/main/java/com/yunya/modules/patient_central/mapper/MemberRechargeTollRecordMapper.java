package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.MemberRechargeTollRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MemberRechargeTollRecordMapper extends Mapper<MemberRechargeTollRecord> {

    /**
     * 充值记录-获取支付方式
     * @param id
     * @return
     */
    List<MemberRechargeTollRecord> selectMemberRechargeRecord(@Param("rechargeRecordId") Integer id);
}