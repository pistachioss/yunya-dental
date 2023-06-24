package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.vo.EmployeePersonalReceivedWorkloadDetailVO;
import com.yunya.models.report.BaseBillPayShare;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayShareMapper extends Mapper<BaseBillPayShare> {

    /**
     * 根据条件查询员工个人已收工作量明细列表
     *
     * @param query 查询条件
     * @return List<EmployeePersonalReceivedWorkloadDetailVO>
     */
    List<EmployeePersonalReceivedWorkloadDetailVO> selectEmployeePersonalReceivedWorkloadDetailList(
            @Param("query") EmployeePersonalWorkloadDetailQuery query);
}