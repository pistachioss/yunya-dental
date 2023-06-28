package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.query.PatientOriginEmployeeQuery;
import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginEmployeeVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseBillPayShare;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayShareMapper extends Mapper<BaseBillPayShare> {

    /**
     * 根据条件查询收费记录ID、订单ID列表
     *
     * @param query 查询条件
     * @param isExecutor 是否查询执行人
     * @return list
     */
    List<BillChargeVO> selectBillIdsAndBillPayIds(@Param("query") DataStatisticsQuery query, @Param("isExecutor") Boolean isExecutor);

    /**
     * 根据月份分组求已收工作量合计
     *
     * @param query 查询条件
     * @param isExecutor 是否查询执行人
     * @return
     */
    List<BillWorkloadVO> selectRecievedWorkloadsGroupByMonth(
            @Param("query") DataStatisticsQuery query, @Param("isExecutor") Boolean isExecutor);

    /**
     * 根据条件查询个人开单项目实收金额统计明细表
     *
     * @param query
     * @return
     */
    List<BillItemReceivedStatisticsVO> billItemReceivedStatistics(
            @Param("query") BillItemInfoQuery query);

    /**
     * 根据条件查询员工免单支付工作量明细项目列表
     *
     * @param query
     * @return
     */
    List<EmployeeReceivedDetailWorkloadVO> selectEmployeeFreePaymentDetailList(@Param("query") EmployeeFreePaymentWorkloadDetailQuery query);

    /**
     * 查询执行人的项目实收工作量
     *
     * @param query
     * @return
     */
    List<EmployeeTariffWorkloadVO> selectClinicExecutorTariffWorkload(
            @Param("query") BillItemTollWorkloadQuery query);

    /**
     * 查询执行人的项目实收工作量列表（含免单）
     *
     * @param query
     * @return
     */
    List<PersonalBillItemReceivedWorkloadDetailVO> selectPersonalBillItemReceivedWorkloadDetail(@Param("query") PersonalBillItemTollAndWorkloadQuery query);

    /**
     * 查询执行人的项目免单工作量列表
     *
     * @param query
     * @return
     */
    List<PersonalBillItemFreeWorkloadDetailVO> selectPersonalBillItemFreeWorkloadDetail(@Param("query") PersonalBillItemTollAndWorkloadQuery query);

    /**
     * 查询执行人的项目开单工作量一体化列表
     *
     * @param query
     * @return
     */
    List<EmployeeTariffWorkloadVO> allExportfindClinicExecutorTariffReceivedWorkload(@Param("query") BillItemTollWorkloadQuery query);

    /**
     * 查询员工推荐人的工作量相关数据
     *
     * @param query
     * @return
     */
    List<PatientOriginEmployeeVo> selectEmployeeReferrerWorkloadList(@Param("query") PatientOriginEmployeeQuery query);

    /**
     * 查询被推荐患者产生的各项工作量明细
     *
     * @param query
     * @return
     */
    List<ReceivedWorkloadDetailsVo> selectRefereePatientWorkloadBreakdown(@Param("query") ReceiverkLoadQuery query);
}