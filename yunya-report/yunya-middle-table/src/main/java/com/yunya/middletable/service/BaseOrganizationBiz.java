package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseOrganizationMapper;
import com.yunya.middletable.dao.system.ClinicExtInfoMapper;
import com.yunya.middletable.dao.system.CompanyMapper;
import com.yunya.models.middletable.BaseOrganization;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.models.system.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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

  /** 组织基础信息 */
  @Autowired private CompanyMapper companyMapper;

  /** 组织扩展信息 */
  @Autowired private ClinicExtInfoMapper clinicExtInfoMapper;

  /**
   * 根据消息类型操作（新增/修改/删除）中间表组织信息
   *
   * @param msg 消息
   */
  public void operateOrganization(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseOrganization organization = generateOrganization(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        if (null != organization) {
          mapper.delete(organization);
          mapper.insertSelective(organization);
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 1:
        BaseOrganization result = mapper.selectByPrimaryKey(dataId);
        if (null == result) {
          mapper.insertSelective(organization);
        } else {
          mapper.updateByPrimaryKeySelective(organization);
        }
        break;
      case 2:
        if (null == organization) {
          mapper.deleteByPrimaryKey(dataId);
        } else {
          mapper.insertSelective(organization);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 构建中间表组织信息
   *
   * @param orgId 组织ID
   */
  private BaseOrganization generateOrganization(Integer orgId) {
    Company company = companyMapper.selectByPrimaryKey(orgId);
    return null != company ? setOrganizationValue(orgId, company.getType()) : null;
  }

  /**
   * 设置组织字段属性
   *
   * @param orgId 组织id
   * @param orgType 组织类型
   * @return
   */
  private BaseOrganization setOrganizationValue(Integer orgId, Byte orgType) {
    BaseOrganization organization = new BaseOrganization();
    organization.setOrgId(orgId);
    organization.setOrgType(orgType);
    ClinicExtInfo clinicExtInfo = new ClinicExtInfo();
    clinicExtInfo.setCompanyId(orgId);
    ClinicExtInfo extInfo = clinicExtInfoMapper.selectOne(clinicExtInfo);
    if (null != extInfo) {
      organization.setAbbreviation(extInfo.getAbbreviation());
      organization.setClinicNumber(extInfo.getClinicNumber());
    }
    return organization;
  }

  /**
   * 拉取某段时间内的组织数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullOrgData(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(Company.class);
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    List<Company> companies = companyMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(companies)) {
      companies.forEach(
          company -> {
            Integer orgId = company.getId();
            mapper.deleteByPrimaryKey(orgId);
            BaseOrganization organization = setOrganizationValue(orgId, company.getType());
            mapper.insertSelective(organization);
          });
    }
  }
}
