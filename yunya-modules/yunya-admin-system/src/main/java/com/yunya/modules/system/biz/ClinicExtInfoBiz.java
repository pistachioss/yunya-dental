package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.models.system.Company;
import com.yunya.modules.system.form.MedicalOrganizationInfoForm;
import com.yunya.modules.system.mapper.ClinicExtInfoMapper;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.vo.MedicalOrganizationInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简单介绍:</br> 医疗机构扩展信息控制层
 *
 * @author: chow
 * @date: 2020/6/6 11:04
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicExtInfoBiz extends BaseBiz<ClinicExtInfoMapper, ClinicExtInfo> {

  /** 注入对象 */
  @Autowired private CompanyMapper companyMapper;

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
  public void edit(Integer companyId, MedicalOrganizationInfoForm form) {
    Company company = companyMapper.selectByPrimaryKey(companyId);
    if (null == company) {
      throw new ClientServiceException(
          "修改医疗机构信息失败，名称为'" + form.getAbbreviation() + "'的数据不存在",
          OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    company.setCreditCode(form.getCreditCode());
    companyMapper.updateByPrimaryKeySelective(company);
    ClinicExtInfo entity = new ClinicExtInfo();
    entity.setCompanyId(companyId);
    ClinicExtInfo extInfo = mapper.selectOne(entity);
    checkClinicExtInfoAbbreviation(form.getAbbreviation(), extInfo.getAbbreviation());
    ClinicExtInfo build = EntityUtils.build(form, ClinicExtInfo.class);
    build.setId(extInfo.getId());
    mapper.updateByPrimaryKeySelective(build);
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
