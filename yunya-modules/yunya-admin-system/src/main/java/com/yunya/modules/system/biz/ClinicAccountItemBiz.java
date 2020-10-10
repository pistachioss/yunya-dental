package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.system.form.ClinicAccountItemConfigureQueryForm;
import com.yunya.feign.system.vo.AccountItemVO;
import com.yunya.feign.system.vo.ClinicAccountItemListVO;
import com.yunya.feign.system.vo.ClinicAccountItemVO;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.ClinicAccountItem;
import com.yunya.modules.system.domain.model.ClinicAccountItemModel;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.mapper.AccountItemMapper;
import com.yunya.modules.system.mapper.ClinicAccountItemMapper;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;

/**
 * 简介: 门诊入账方式业务层
 *
 * @author: chow
 * @date: 2020/7/27 11:13
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicAccountItemBiz extends BaseBiz<ClinicAccountItemMapper, ClinicAccountItem> {

  @Autowired private CompanyMapper companyMapper;

  @Autowired private AccountItemMapper accountItemMapper;

  /**
   * 根据ID查询门诊入账方式信息
   *
   * @param id 门诊入账方式ID
   * @return
   */
  public ClinicAccountItemVO findById(Integer id) {
    ClinicAccountItemVO resultData = mapper.selectById(id);
    return resultData;
  }

  /**
   * 根据条件查询组织入账方式列表
   *
   * @param queryForm 查询参数
   * @return
   */
  public PageInfo<ClinicAccountItemVO> findList(ClinicAccountItemQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<ClinicAccountItemVO> resultList = mapper.selectClinicAccountItemList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据门诊ID查询门诊可用的支付方式列表
   *
   * @param orgId 门诊ID
   * @return
   */
  public ClinicAccountItemListVO findClinicAccountItemList(Integer orgId) {
    ClinicAccountItemListVO resultData = new ClinicAccountItemListVO();
    OrganizationInfo organizationInfo = companyMapper.selectOrgInfoById(orgId);
    if (null != organizationInfo) {
      resultData.setOrgId(orgId);
      resultData.setOrgName(organizationInfo.getAbbreviation());
      List<AccountItemVO> clinicAccountItems = Lists.newArrayList();
      List<AccountItem> accountItems = accountItemMapper.selectAll();
      if (StringHelper.isNotEmpty(accountItems)) {
        clinicAccountItems =
            accountItems.stream()
                .map(item -> mapper.selectAccountItemVO(orgId, item.getId(), true))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
      }
      resultData.setClinicAccountItems(clinicAccountItems);
    }
    return resultData;
  }

  /**
   * 根据条件查询门诊支付方式配置列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<ClinicAccountItemVO> configure(ClinicAccountItemConfigureQueryForm queryForm) {
    Integer accountItemId = queryForm.getAccountItemId();
    List<ClinicAccountItemVO> resultList = Lists.newArrayList();
    // todo 从缓存中查询
    AccountItem accountItem = accountItemMapper.selectByPrimaryKey(accountItemId);
    if (null != accountItem) {
      if (queryForm.getWhetherPage()) {
        PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
      }
      OrganizationQueryForm form = new OrganizationQueryForm();
      form.setTypes(new Byte[] {0, 2});
      List<OrganizationInfoVO> organizations = companyMapper.selectOrganizationByExample(form);
      if (StringHelper.isNotEmpty(organizations)) {
        organizations.forEach(
            vo -> {
              Integer orgId = vo.getId();
              ClinicAccountItemVO item = mapper.selectClinicAccountItem(orgId, accountItemId);
              if (null != item) {
                item.setOrgId(orgId);
                item.setOrgName(vo.getAbbreviation());
                resultList.add(item);
              }
            });
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 新增门诊入账方式
   *
   * @param model 参数模型
   */
  public void add(ClinicAccountItemModel model) {
    Integer companyId = model.getCompanyId();
    Integer accountItemId = model.getAccountItemId();
    ClinicAccountItem entity = new ClinicAccountItem();
    entity.setCompanyId(companyId);
    entity.setAccountItemId(accountItemId);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      return;
    }
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 批量新增诊所入账方式
   *
   * @param accountItemId 入账方式ID
   */
  public void batchSave(Integer accountItemId) {
    OrganizationQueryForm queryForm = new OrganizationQueryForm();
    Byte[] types = new Byte[] {0, 2};
    queryForm.setTypes(types);
    List<OrganizationInfoVO> resultList = companyMapper.selectOrganizationByExample(queryForm);
    if (StringHelper.isEmpty(resultList)) {
      throw new ClientServiceException("一键新增组织入账方式失败，未查询到组织信息，请联系管理员添加组织！", QUERY_RESULT_INVALID);
    }
    ClinicAccountItem entity;
    for (OrganizationInfoVO vo : resultList) {
      entity = new ClinicAccountItem();
      entity.setCompanyId(vo.getId());
      entity.setAccountItemId(accountItemId);
      ClinicAccountItem resultData = mapper.selectOne(entity);
      if (null == resultData) {
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtName(BaseContextHandler.getName());
        mapper.insertSelective(entity);
      }
    }
  }

  /**
   * 设置组织支付方式启用状态
   *
   * @param id 组织支付方式ID
   */
  public void switchClinicAccountItem(Integer id) {
    ClinicAccountItem resultData = mapper.selectByPrimaryKey(id);
    if (resultData == null) {
      throw new ClientServiceException("ID为'" + id + "'的组织支付方式不存在！", QUERY_RESULT_INVALID);
    }
    resultData.setInservice(!resultData.getInservice());
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 设置支付方式在门诊是否可用
   *
   * @param orgId 组织ID
   * @param accountItemId 支付方式ID
   */
  public void switchClinicAccountItem(Integer orgId, Integer accountItemId) {
    ClinicAccountItem entity = new ClinicAccountItem();
    entity.setCompanyId(orgId);
    entity.setAccountItemId(accountItemId);
    ClinicAccountItem result = mapper.selectOne(entity);
    if (null == result) {
      entity.setInservice(false);
      entity.setCrtId(Integer.valueOf(BaseContextHandler.getOrgId()));
      entity.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(entity);
    } else {
      result.setInservice(!result.getInservice());
      result.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      result.setUpdName(BaseContextHandler.getName());
      mapper.updateByPrimaryKeySelective(result);
    }
  }
}
