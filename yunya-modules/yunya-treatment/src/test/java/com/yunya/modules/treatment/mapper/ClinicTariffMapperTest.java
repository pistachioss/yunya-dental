package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

  @Test
  public void list() {
    ClinicTariffQueryForm form = new ClinicTariffQueryForm();
    form.setOrgId(35);
    form.setKeyWord("yy");
    List<ClinicTariffVO> tariffVOS = clinicTariffMapper.selectClinicTariffList(form);
    System.out.println(tariffVOS);
  }
}
