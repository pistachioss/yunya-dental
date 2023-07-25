package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.query.DeductionOrderQuery;
import com.yunya.feign.discount.domain.vo.PatientDeductionOrderVO;
import com.yunya.models.discount.CouponOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface CouponOrderMapper extends Mapper<CouponOrder> {
    String selectOrderNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);

    List<PatientDeductionOrderVO> listPatientDeductionByParam(Integer patientId, DeductionOrderQuery query);
}