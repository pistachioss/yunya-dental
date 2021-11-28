package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.ClinicOralTariff;
import com.yunya.models.tariff.ClinicTariff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/15 16:59
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClinicTariffMapperTest {
  /** 注入对象 */
  @Autowired private ClinicTariffMapper clinicTariffMapper;

  @Autowired private BaseTariffMapper baseTariffMapper;


  @Autowired private ClinicOralTariffMapper clinicOralTariffMapper;

  @Autowired private BaseOralTariffMapper baseOralTariffMapper;

  @Test
  public void list() {
    ClinicTariffQueryForm form = new ClinicTariffQueryForm();
    form.setOrgId(26);
    form.setKeyWord("美白");
    List<ClinicTariffVO> tariffVOS = clinicTariffMapper.selectClinicTariffList(form);
    System.out.println(tariffVOS.size());
  }

  @Test
  public void setClinicTariff() {
    List<BaseTariff> tariffs = baseTariffMapper.selectAll();
    if (!CollectionUtils.isEmpty(tariffs)) {
      ClinicTariff clinicTariff = new ClinicTariff();
      clinicTariff.setClinicId(45);
      for (BaseTariff tariff : tariffs) {
        clinicTariff.setTariffId(tariff.getId());
        List<ClinicTariff> tariffList = clinicTariffMapper.select(clinicTariff);
        if (CollectionUtils.isEmpty(tariffList)) {
          clinicTariff.setPrice(tariff.getPrice());
          clinicTariff.setInservice(false);
          clinicTariff.setCrtId(552);
          clinicTariff.setCrtName("方珏");
          clinicTariff.setUpdId(552);
          clinicTariff.setUpdName("方珏");
          clinicTariffMapper.insertSelective(clinicTariff);
        }
      }
    }
  }

  @Test
  public void setClinicOralTariff() {
    List<BaseOralTariff> tariffs = baseOralTariffMapper.selectAll();
    if (!CollectionUtils.isEmpty(tariffs)) {
      ClinicOralTariff clinicTariff = new ClinicOralTariff();
      clinicTariff.setClinicId(45);
      for (BaseOralTariff tariff : tariffs) {
        clinicTariff.setOralTariffId(tariff.getId());
        List<ClinicOralTariff> tariffList = clinicOralTariffMapper.select(clinicTariff);
        if (CollectionUtils.isEmpty(tariffList)) {
          clinicTariff.setPrice(tariff.getPrice());
          clinicTariff.setInservice(false);
          clinicTariff.setCrtId(552);
          clinicTariff.setCrtName("方珏");
          clinicTariff.setUpdId(552);
          clinicTariff.setUpdName("方珏");
          clinicOralTariffMapper.insertSelective(clinicTariff);
        }
      }
    }
  }
}
