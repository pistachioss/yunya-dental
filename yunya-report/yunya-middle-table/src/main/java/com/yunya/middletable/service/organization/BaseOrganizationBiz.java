package com.yunya.middletable.service.organization;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.report.BaseOrganizationMapper;
import com.yunya.middletable.dao.system.ClinicExtInfoMapper;
import com.yunya.middletable.dao.system.CompanyMapper;
import com.yunya.models.middletable.BaseOrganization;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.models.system.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 报表服务组织信息同步
 *
 * @author: chow
 * @date: 2020/10/14 17:40
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseOrganizationBiz extends BaseBiz<BaseOrganizationMapper, BaseOrganization> {

  /** 注入对象 */
  @Autowired private CompanyMapper companyMapper;

  @Autowired private ClinicExtInfoMapper clinicExtInfoMapper;

  /**
   * 根据消息类型操作（新增/修改/删除）中间表组织信息
   *
   * @param msg 消息
   */
  public void operateOrganization(MessageModel msg) {
    Integer dataId = msg.getId();
    Integer operateType = msg.getOperateType();
    BaseOrganization organization = setOrganizationValue(dataId);
    if (null == organization) {
      return;
    }
    switch (operateType) {
      case 0:
        mapper.delete(organization);
        mapper.insertSelective(organization);
        break;
      case 1:
        mapper.updateByPrimaryKeySelective(organization);
        break;
      case 2:
        mapper.delete(organization);
        break;
      default:
        break;
    }
  }

  /**
   * 设置组织字段属性
   *
   * @param orgId 组织ID
   */
  private BaseOrganization setOrganizationValue(Integer orgId) {
    Company company = companyMapper.selectByPrimaryKey(orgId);
    if (null != company) {
      BaseOrganization organization = new BaseOrganization();
      Integer companyId = company.getId();
      organization.setOrgId(companyId);
      organization.setOrgType(company.getType());
      ClinicExtInfo clinicExtInfo = new ClinicExtInfo();
      clinicExtInfo.setCompanyId(companyId);
      ClinicExtInfo extInfo = clinicExtInfoMapper.selectOne(clinicExtInfo);
      if (null != extInfo) {
        organization.setAbbreviation(extInfo.getAbbreviation());
        organization.setClinicNumber(extInfo.getClinicNumber());
      }
      return organization;
    }
    return null;
  }
}
