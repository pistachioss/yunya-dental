package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBillPayShare;
import com.yunya.models.report.StatEmpPay;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayShareMapper extends Mapper<BaseBillPayShare> {

    void batchSave(List<BaseBillPayShare> data);

    /**
     * 统计指定门诊、指定日期、指定执行人下的各项目的总免单和总实收
     * @param orgId
     * @param startDate
     * @param endDate
     * @param executorId
     * @param itemType
     * @param itemId
     * @return
     */
    List<StatEmpPay> statisitcsItemPayShareDetails(
            @Param("orgId") Integer orgId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("executorId") Integer executorId,
            @Param("itemType") Byte itemType,
            @Param("itemId") Integer itemId);
}