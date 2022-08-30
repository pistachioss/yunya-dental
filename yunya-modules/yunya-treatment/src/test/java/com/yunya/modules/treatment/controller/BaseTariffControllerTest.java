package com.yunya.modules.treatment.controller;

import com.yunya.feign.treatment.domain.form.TariffUnitePriceForm;
import com.yunya.feign.treatment.domain.model.TariffUniteModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.treatment.controller.web.BaseOralTariffController;
import com.yunya.modules.treatment.controller.web.BaseTariffAssociationController;
import com.yunya.modules.treatment.controller.web.BaseTariffController;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 简介: 价目表控制层测试
 *
 * @author: chow
 * @date: 2021/5/19 10:04
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTariffControllerTest {
  @Resource private BaseTariffController baseTariffController;
  @Resource private BaseOralTariffController baseOralTariffController;
  @Resource private BaseTariffAssociationController baseTariffAssociationController;

  @Test
  public void test1() {
    baseTariffController.operateBaseTariffStatus(644, false);
  }

  @Test
  public void test2() {
    baseOralTariffController.operateBaseOralTariffStatus(1, true);
  }

  @Test
  public void test3() {
    ResponseResult<String> number = baseTariffController.getBaseTariffNumber(1);
    System.out.println(number);
  }

  @Test
  public void test4() {
    ResponseResult<String> number = baseOralTariffController.getBaseOralTariffNumber(1);
    System.out.println(number);
  }

  @Test
  public void test5() {
    TariffUnitePriceForm form = new TariffUnitePriceForm();
    Set<Integer> orgIds = new HashSet<>();
    orgIds.add(26);
    orgIds.add(27);
    orgIds.add(28);
    form.setOrgIds(orgIds);
    Set<TariffUniteModel> objects = new HashSet<>();
    TariffUniteModel model = new TariffUniteModel();
    model.setId(106);
    model.setPrice(BigDecimal.valueOf(90L));
    // objects.add(model);
    form.setTariffUniteModels(objects);
    ResponseResult<T> result = baseOralTariffController.uniteOralTariffPrice(form);
    System.out.println(result);
  }

  @Test
  public void test6() {
    TariffUnitePriceForm form = new TariffUnitePriceForm();
    Set<Integer> orgIds = new HashSet<>();
    orgIds.add(26);
    orgIds.add(27);
    orgIds.add(28);
    form.setOrgIds(orgIds);
    Set<TariffUniteModel> objects = new HashSet<>();
    TariffUniteModel model = new TariffUniteModel();
    model.setId(1653);
    model.setPrice(BigDecimal.valueOf(90L));
    objects.add(model);
    form.setTariffUniteModels(objects);
    ResponseResult<T> result = baseTariffController.uniteTariffPrice(form);
    System.out.println(result);
  }
/*
  @Test
  public void test7() {
    BaseTariffAssociationForm form = new BaseTariffAssociationForm();
    form.setEmr("");
    form.setAttention("");
    form.setFellowUps(new String[]{""});
    baseTariffAssociationController.modifyTariffAssociation(201,form);
  }*/
}
