package com.yunya.modules.system.controller;

import com.yunya.feign.system.form.BrandModel;
import com.yunya.feign.system.form.OrgDepartmentModel;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.models.system.Brand;
import com.yunya.models.system.CompanyDepartment;
import com.yunya.modules.system.rpc.SystemServiceRest;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 暴露接口测试
 *
 * @author: chow
 * @date: 2020/7/16 09:30
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SystemServiceRestController {

  /** 注入对象 */
  @Autowired private SystemServiceRest systemServiceRest;

  @Test
  public void test() {
    BrandModel model = new BrandModel();
    List<Brand> list = systemServiceRest.findBrandList(model);
    System.out.println(list);
  }

  @Test
  public void testFindOrgList() {
    OrganizationModel form = new OrganizationModel();
    form.setWhetherPage(false);
    form.setPageNum(2);
    form.setPageSize(2);
    // form.setName("门诊");
    form.setTypes(new Byte[] {0, 1});
    List<OrganizationInfoVO> list = systemServiceRest.findOrgInfoList(form);
    System.out.println(list);
  }

  @Test
  public void testFindOrgDept() {
    OrgDepartmentModel model = new OrgDepartmentModel();
    model.setCompanyId(21);
    List<CompanyDepartment> list = systemServiceRest.findCompanyDepartmentList(model);
    System.out.println(list);
  }

  @Test
  public void testFindUserList() {
    SysUserEmployeeModel model = new SysUserEmployeeModel();
    model.setWhetherPage(false);
    //model.setPageNum(1);
    //model.setPageSize(8);
    model.setKeyWord("15");
    List<SysUserInfoDetail> infoList = systemServiceRest.findSysUserEmployeeInfoList(model);
    System.out.println(infoList);
  }
}
