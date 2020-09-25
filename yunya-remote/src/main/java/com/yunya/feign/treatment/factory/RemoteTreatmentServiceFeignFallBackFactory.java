package com.yunya.feign.treatment.factory;

import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * 简介: 就诊、价目表服务调用降级处理
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
  public List<TreatmentRecord> findTreatmentRecordByIds(@NotEmpty Set<Integer> ids) {
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
}
