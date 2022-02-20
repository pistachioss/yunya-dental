package com.yunya.modules.emr.mapper;

import com.yunya.models.emr.TreatPlanDetail;
import com.yunya.models.emr.TreatPlanDetailWriteoff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TreatPlanDetailWriteoffMapper extends Mapper<TreatPlanDetailWriteoff> {
    List<TreatPlanDetail> sumTreatPlanDetailEnableQuantity(@Param("detailIds") List<Integer> detailIds);

    /**
     * 批量添加
     *
     * @param datas
     */
    void insertBatch(@Param("list") List<TreatPlanDetailWriteoff> datas);

    void deleteWriteoffByOrderDetailId(@Param("detailIds") List<Integer> detailIds);

    List<TreatPlanDetailWriteoff> selectOrderWithPlanDetailById(@Param("orderDetailIds") List<Integer> orderDetailIds);
}