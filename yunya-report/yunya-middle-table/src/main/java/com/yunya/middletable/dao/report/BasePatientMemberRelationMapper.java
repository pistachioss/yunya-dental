package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BasePatientMemberRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BasePatientMemberRelationMapper extends Mapper<BasePatientMemberRelation> {

    /**
     * 根据关系id 和 会员卡类型进行删除
     * @param relationId 会员卡关系id
     * @param type 会员类型 会员卡 预付款
     */
    void deleteByPrimaryKeyAndType(@Param("relationId") Integer relationId, @Param("type") Integer type);
}