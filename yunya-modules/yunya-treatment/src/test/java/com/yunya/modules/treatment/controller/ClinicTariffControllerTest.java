package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.form.ClinicItemMemberPriceForm;
import com.yunya.feign.treatment.domain.form.ClinicTariffForm;
import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.ClinicTariffController;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介: 门诊价目表控制层测试
 *
 * @author: chow
 * @date: 2020/11/15 12:55
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClinicTariffControllerTest {
  @Autowired private ClinicTariffController clinicTariffController;

  @Test
  public void testFind() {
    ResponseResult<ClinicTariffVO> result = clinicTariffController.findById(35, 201090);
    System.out.println(result);
  }

  @Test
  public void testSwitchTariff() {
    ResponseResult<T> result = clinicTariffController.switchClinicTariff(35, 201090);
    System.out.println(result);
  }

  @Test
  public void modifyClinicTariff() {
    ClinicTariffForm form = new ClinicTariffForm();
    form.setOrgId(26);
    form.setTariffId(1);
    form.setPrice(BigDecimal.valueOf(500));
    List<ClinicItemMemberPriceForm> list = new ArrayList<>();
    ClinicItemMemberPriceForm memberPriceForm = new ClinicItemMemberPriceForm();
    memberPriceForm.setMemberTypeId(1);
    memberPriceForm.setDiscountPrice(BigDecimal.valueOf(401));
    list.add(memberPriceForm);
    ClinicItemMemberPriceForm memberPriceForm1 = new ClinicItemMemberPriceForm();
    memberPriceForm1.setMemberTypeId(2);
    memberPriceForm1.setDiscountPrice(BigDecimal.valueOf(301));
    list.add(memberPriceForm1);
    ClinicItemMemberPriceForm memberPriceForm2 = new ClinicItemMemberPriceForm();
    memberPriceForm2.setMemberTypeId(3);
    memberPriceForm2.setDiscountPrice(BigDecimal.valueOf(201));
    list.add(memberPriceForm2);
    form.setClinicItemMemberPrices(list);
    ResponseResult<T> result = clinicTariffController.modify(form);
    System.out.println(result);
  }
}
