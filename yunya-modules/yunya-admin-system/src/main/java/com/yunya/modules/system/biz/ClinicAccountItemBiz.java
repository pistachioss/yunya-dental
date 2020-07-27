package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.ClinicAccountItem;
import com.yunya.modules.system.domain.model.ClinicAccountItemModel;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.mapper.ClinicAccountItemMapper;
import com.yunya.modules.system.vo.ClinicAccountItemVO;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

  /** 注入对象 */
  private final OrganizationBiz organizationBiz;

  public ClinicAccountItemBiz(OrganizationBiz organizationBiz) {
    this.organizationBiz = organizationBiz;
  }

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
    mapper.insertSelective(entity);
  }

  /**
   * 批量新增诊所入账方式
   *
   * @param accountItemId 入账方式ID
   */
  public void batchSave(Integer accountItemId) {
    OrganizationQueryForm queryForm = new OrganizationQueryForm();
    queryForm.setWhetherPage(false);
    Byte[] types = new Byte[] {0, 2};
    queryForm.setTypes(types);
    List<OrganizationInfoVO> resultList = organizationBiz.findList(queryForm).getList();
    if (resultList.size() > 0) {
      ClinicAccountItem entity;
      for (OrganizationInfoVO vo : resultList) {
        entity = new ClinicAccountItem();
        entity.setCompanyId(vo.getId());
        entity.setAccountItemId(accountItemId);
        ClinicAccountItem resultData = mapper.selectOne(entity);
        if (null == resultData) {
          mapper.insertSelective(entity);
        }
      }
    }
    throw new ClientServiceException(
        "一键新增组织入账方式失败，未查询到组织信息，请联系管理员添加组织！", OperationCodeConstants.QUERY_RESULT_INVALID);
  }

  /**
   * 设置组织支付方式启用状态
   *
   * @param id 组织支付方式ID
   */
  public void switchClinicAccountItem(Integer id) {
    ClinicAccountItem resultData = mapper.selectByPrimaryKey(id);
    if (resultData != null) {
      resultData.setInservice(!resultData.getInservice());
      mapper.updateByPrimaryKeySelective(resultData);
    }
    throw new ClientServiceException(
        "ID为'" + id + "'的组织支付方式不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
  }
}
