package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.vo.BillPayRecordVO;
import com.yunya.feign.treatment.domain.vo.DesktopMiniProgramVO;
import com.yunya.feign.treatment.domain.vo.PatientBillStatistics;
import com.yunya.models.treatment.BillRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface BillRecordMapper extends Mapper<BillRecord> {

    /**
     * 查询门诊某天最大的账单编号
     *
     * @param orgId 组织ID
     * @param date  日期
     * @return
     */
    String selectBillNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);

    /**
     * 根据开单记录ID查询订单支付记录列表信息
     *
     * @param orderRecordId 开单记录ID
     * @return
     */
    List<BillPayRecordVO> selectBillPayRecord(@Param("orderRecordId") Integer orderRecordId);

    /**
     * 通过患者ID批量查询患者欠费总额
     *
     * @param patientIds 患者ID集合
     * @return 返回欠费总额集合
     */
    List<DebtAmountModel> selectPatientDebtAmountList(@Param("patientIds") List<Integer> patientIds);

    /**
     * 根据患者ID查询患者账单统计数据
     *
     * @param patientId 患者ID
     * @return
     */
    PatientBillStatistics selectPatientBillStatistics(@Param("patientId") Integer patientId);

    /**
     * 根据患者ID查询患者账单统计数据列表
     *
     * @param patientIds 患者ID集合
     * @return 实体数据列表
     */
    List<PatientBillStatistics> selectPatientBillStatisticsByPatientIds(@Param("patientIds") List<Integer> patientIds);

    /**
     * 根据患者就诊记录ID查询订单支付记录
     *
     * @param treatmentIds 患者就诊记录ID
     * @return 实例对象集合
     */
    List<BillRecord> selectBillRecordsByTreatmentIds(@Param("treatmentIds") List<Integer> treatmentIds);

    /**
     * PC照片影像小程序已结账列表
     *
     * @param currentDate 当前时间
     * @param orgId       门诊ID
     * @return 返回实体列表
     */
    List<DesktopMiniProgramVO> desktopBillingList(@Param("currentDate") String currentDate,
                                                  @Param("orgId") Integer orgId);

}
