package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BasePatientMemberOccurLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BasePatientMemberOccurLogMapper extends Mapper<BasePatientMemberOccurLog> {

    /**
     * 根据操作id 会员类型 操作类型 查询会员操作信息
     * @param id 操作id
     * @param type 会员类型
     * @param occurType  操作类型
     * @return BasePatientMemberOccurLog
     */
    BasePatientMemberOccurLog selectOneByPrimaryKeyAndtype(@Param("id") Integer id, @Param("type") Integer type, @Param("occurType") Integer occurType);

    /**
     * 根据操作id和操作类型 进行删除
     * @param id 操作id
     * @param occurType 操作类型 充值 消费 退费 撤销
     * @type type 会员 预付款
     */
    void deleteByPrimaryKeyAndtype(@Param("id") Integer id,@Param("type") Integer type,@Param("occurType") Integer occurType);
}