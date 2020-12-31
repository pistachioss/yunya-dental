package com.yunya.feign.treatment.factory;

import com.yunya.feign.clinic_base.domain.form.SpecialistProjectReportForm;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.RegisteredVO;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.BusinessCompletedWorkGoalVO;
import com.yunya.feign.treatment.domain.vo.SpecialistProjectTariffCompletedInfoVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * 简介: 就诊、价目表服务调用降级处理 111
 *
 * @author: chow
 * @date: 2020/8/6 17:53
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteTreatmentServiceFeignFallBackFactory implements RemoteTreatmentServiceFeign {
  @Override
  public BaseOralTariffCategory findBaseOralTariffCategoryById(Integer id) {
    return null;
  }

  @Override
  public List<BaseOralTariffCategory> findBaseOralTariffCategoryList(
      BaseOralTariffCategory queryForm) {
    return null;
  }

  @Override
  public BaseOralTariff findBaseOralTariffById(Integer id) {
    return null;
  }

  @Override
  public List<BaseOralTariff> findBaseOralTariffList(BaseOralTariff entity) {
    return null;
  }

  @Override
  public BaseTariffCategory findBaseTariffCategoryById(Integer id) {
    return null;
  }

  @Override
  public List<BaseTariffCategory> findBaseTariffCategoryList(BaseTariffCategory entity) {
    return null;
  }

  @Override
  public String findBaseTariffNamesByIds(String[] ids) {
    return null;
  }

  @Override
  public BaseTariff findBaseTariffById(Integer id) {
    return null;
  }

  @Override
  public List<BaseTariff> findBaseTariffList(BaseTariff entity) {
    return null;
  }

  @Override
  public ClinicTariff findClinicTariff(ClinicTariff entity) {
    return null;
  }

  @Override
  public ClinicOralTariff findClinicOralTariff(ClinicOralTariff entity) {
    return null;
  }

  @Override
  public ClinicTariffMemberPrice findClinicTariffMemberPrice(
      Integer orgId, Integer memberType, Integer itemId) {
    return null;
  }

  @Override
  public ClinicOralTariffMemberPrice findClinicOralTariffMemberPrice(
      Integer orgId, Integer memberType, Integer itemId) {
    return null;
  }

  @Override
  public Registered findRegisteredById(Integer id) {
    return null;
  }

  @Override
  public Registered findRegisteredByExample(Registered entity) {
    return null;
  }

  @Override
  public List<Registered> findRegisteredList(Registered entity) {
    return null;
  }

  @Override
  public TreatmentRecord findTreatmentRecordById(Integer id) {
    return null;
  }

  @Override
  public TreatmentRecord findTreatmentRecordByExample(TreatmentRecord entity) {
    return null;
  }

  @Override
  public List<TreatmentRecordExtendVO> findTreatmentRecordByIds(@NotEmpty Set<Integer> ids) {
    return null;
  }

  @Override
  public List<TreatmentRecord> findTreatmentRecordList(TreatmentRecord entity) {
    return null;
  }

  @Override
  public void updateTreatmentRecord(Integer id) {}

  @Override
  public OrderRecord findOrderRecordById(Integer id) {
    return null;
  }

  @Override
  public List<OrderDetail> findOrderDetailByOrderRecordId(Integer orderRecordId) {
    return null;
  }

  @Override
  public List<DebtAmountModel> findDebtAmountList(List<Integer> patientIds) {
    return null;
  }

  @Override
  public List<RegisteredVO> registeredInfoDetails(List<Integer> registeredIds) {
    return null;
  }

  @Override
  public BusinessCompletedWorkGoalVO findClinicCompletedBusinessWorkGoal(
      CompletedWorkGoalQuery query) {
    return null;
  }

  @Override
  public SpecialistProjectTariffCompletedInfoVO findClinicTariffOrderCompletedInfo(
      SpecialistProjectTariffCompletedInfoQuery query) {
    return null;
  }

  @Override
  public List<SpecialistProjectReportVO> findTariffSpecialistPercentage(SpecialistProjectReportModel specialistProjectReportModel) {
    return null;
  }


}
