package com.yunya.middletable.dao.report;

import com.yunya.models.report.BasePatientMember;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author YK
 */
public interface BasePatientMemberMapper extends Mapper<BasePatientMember> {

    /**
     * 根据会员卡号修改会员信息
     * @param basePatientMember 会员对象
     */


    /**
     * 根据会员id和tyep 删除中间表消息
     * @param memberId 会员id
     * @param type 会员类型 会员 预付款
     */
    void deleteByPrimaryKeyAndType(@Param("cardId") Integer memberId,@Param("type") int type);
}