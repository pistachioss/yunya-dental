package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.modules.system.form.MedicalOrganizationInfoForm;
import com.yunya.modules.system.mapper.ClinicExtInfoMapper;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.vo.MedicalOrganizationInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

/**
 * 简单介绍:</br> 医疗机构扩展信息控制层
 *
 * @author: chow
 * @date: 2020/6/6 11:04
 * @description:
 * @since: 1.0.0
 */
@Service
public class ClinicExtInfoBiz extends BaseBiz<ClinicExtInfoMapper, ClinicExtInfo> {

  /** 注入对象 */
  @Autowired private CompanyMapper companyMapper;

  @Resource
  private OrganizationBiz organizationBiz;

  /**
   * 根据组织ID获取医疗机构详细信息
   *
   * @param companyId 组织ID
   * @return MedicalOrganizationInfoVO
   */
  public MedicalOrganizationInfoVO findMedicalOrganizationInfo(Integer companyId) {
    return mapper.selectClinicExtInfoByCompanyId(companyId);
  }

  /**
   * 编辑医疗机构
   *
   * @param companyId 组织ID
   * @param form 参数封装
   */
  @Transactional(rollbackFor = Exception.class)
  public void edit(Integer companyId, MedicalOrganizationInfoForm form) {
    //校验信用代码
    String creditCode = form.getCreditCode();
    String abbreviation = form.getAbbreviation();
    //校验时间
    judgeBusinessTime(form.getBusinessStartTime(), form.getBusinessEndTime());
    //更新组织表信用代码
    organizationBiz.updateCompanyCredit(companyId, creditCode);
    Integer count = mapper.countByAbbreviation(abbreviation, companyId);
    if (count > 0) {
      throw new BaseException(
              String.format("%s名称已存在",abbreviation), OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    ClinicExtInfo entity = new ClinicExtInfo();
    entity.setCompanyId(companyId);
    ClinicExtInfo extInfo = mapper.selectOne(entity);
    ClinicExtInfo build = EntityUtils.build(form, ClinicExtInfo.class);
    build.setId(extInfo.getId());
    mapper.updateByPrimaryKeySelective(build);
  }

  private void judgeBusinessTime(String startBusinessTime, String endBusinessTime) {
    if (StringUtils.isNotBlank(startBusinessTime) && StringUtils.isNotBlank(endBusinessTime)) {
      LocalTime startTime = LocalTime.parse(startBusinessTime, DateTimeFormatter
              .ofPattern(BusinessConstants.CLINIC_BUSINESS_PATTER));
      LocalTime endTime = LocalTime.parse(endBusinessTime, DateTimeFormatter
              .ofPattern(BusinessConstants.CLINIC_BUSINESS_PATTER));
      if (startTime.isAfter(endTime)) {
        throw new ClientServiceException("结束时间不可以小于等于开始时间",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }
  }

  /**
   * 校验医疗机构简称是否重复
   *
   * @param formAbbreviation 请求参数
   * @param dataAbbreviation 当前数据简称
   */
  private void checkClinicExtInfoAbbreviation(String formAbbreviation, String dataAbbreviation) {
    if (!dataAbbreviation.equals(formAbbreviation)) {
      ClinicExtInfo info = new ClinicExtInfo();
      info.setAbbreviation(formAbbreviation);
      ClinicExtInfo result = mapper.selectOne(info);
      if (null != result) {
        throw new ClientServiceException(
            "修改医疗机构简称'" + formAbbreviation + "'失败，该简称名称已存在",
            OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }
  }
}
