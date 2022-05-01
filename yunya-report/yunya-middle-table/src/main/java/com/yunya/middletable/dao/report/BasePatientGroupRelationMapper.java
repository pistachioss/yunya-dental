package com.yunya.middletable.dao.report;

import com.yunya.models.report.BasePatient;
import com.yunya.models.report.BasePatientGroupRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BasePatientGroupRelationMapper extends Mapper<BasePatientGroupRelation> {

    List<BasePatient> selectRegisterdPatientMobile(
            @Param("sDate") String sDate,
            @Param("eDate") String eDate,
            @Param("orgId") Integer orgId,
            @Param("mobiles") List<String> mobiles);
}