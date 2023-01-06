package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.PatientManageQuery;
import com.yunya.feign.report.domain.vo.PatientBirthdayVo;
import com.yunya.feign.report.domain.vo.PatientManageVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.report.ultimate.biz.PatientBaseInfoBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介: 公司端报表测试
 *
 * @author: chow
 * @date: 2020/10/26 20:25
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PatientControllerTest {
  @Autowired private PatientBaseInfoController patientBaseInfoController;
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

  @Test
  public void testPatientInfo() {
    String param = "{\"patientGroupId\":2,\"patientKeyWord\":\"\",\"patientType\":null,\"memberType\":null,\"gender\":null,\"firstOrgId\":null,\"firstDentistName\":null,\"lastDentistName\":null,\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10,\"startAge\":null,\"endAge\":null,\"startOweAmount\":null,\"endOweAmount\":null,\"startPrincipalBalance\":null,\"endPrincipalBalance\":null,\"startMemberBalance\":null,\"endMemberBalance\":null,\"startConsumeAmount\":null,\"endConsumeAmount\":null,\"startTreatQuantity\":null,\"endTreatQuantity\":null}";
    PatientManageQuery query = JSONObject.parseObject(param, PatientManageQuery.class);
    ResponseResult<PageInfo<PatientManageVo>> data = patientBaseInfoController.patientInfo(query);
    System.out.println(JSONObject.toJSON(data));
  }

  @Test
  public void testaaa() {
    PatientManageQuery query = new PatientManageQuery();
    query.setLastOrgId(64);
    query.setWhetherPage(true);
    PageInfo<PatientBirthdayVo> page = patientBaseInfoBiz.getPatientBirthdayPage(query);
  }
}
