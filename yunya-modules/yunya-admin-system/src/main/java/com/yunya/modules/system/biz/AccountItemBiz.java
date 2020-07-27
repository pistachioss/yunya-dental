package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.AccountType;
import com.yunya.models.system.ClinicAccountItem;
import com.yunya.modules.system.domain.form.AccountItemForm;
import com.yunya.modules.system.domain.model.AccountItemModel;
import com.yunya.modules.system.domain.query.AccountItemQueryForm;
import com.yunya.modules.system.mapper.AccountItemMapper;
import com.yunya.modules.system.vo.AccountItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简介: 入账方式业务层
 *
 * @author: chow
 * @date: 2020/7/24 16:14
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountItemBiz extends BaseBiz<AccountItemMapper, AccountItem> {

  /** 注入对象 */
  private final AccountTypeBiz accountTypeBiz;

  private final ClinicAccountItemBiz clinicAccountItemBiz;

  public AccountItemBiz(AccountTypeBiz accountTypeBiz, ClinicAccountItemBiz clinicAccountItemBiz) {
    this.accountTypeBiz = accountTypeBiz;
    this.clinicAccountItemBiz = clinicAccountItemBiz;
  }

  /**
   * 根据ID查询入账方式
   *
   * @param id 入账方式ID
   * @return
   */
  public AccountItemVO findById(Integer id) {
    AccountItemVO accountItemVO = mapper.selectById(id);
    return accountItemVO;
  }

  /**
   * 根据条件查询入账方式列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<AccountItemVO> findList(AccountItemQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<AccountItemVO> resultList = mapper.selectAccountItemList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增入账方式
   *
   * @param model 参数模型
   */
  public void add(AccountItemModel model) {
    Integer accountTypeId = model.getAccountTypeId();
    AccountType accountType = accountTypeBiz.selectById(accountTypeId);
    if (null == accountType) {
      throw new ClientServiceException(
          "新增失败，请选择正确的入账方式分类！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    String name = model.getName();
    AccountItem entity = new AccountItem();
    entity.setAccountTypeId(accountTypeId);
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，当前入账方式分类下已存在名称为'" + name + "'的入账方式", OperationCodeConstants.SAME_DATA_EXIST);
    }
    Byte type = model.getType();
    entity.setType(type);
    mapper.insertSelective(entity);
  }

  /**
   * 修改入账方式
   *
   * @param id 入账方式ID
   * @param form 修改参数封装
   */
  public void modify(Integer id, AccountItemForm form) {
    AccountItem resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "'的入账方式不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    String name = form.getName();
    if (!resultData.getName().equals(name)) {
      throw new ClientServiceException(
          "修改失败，当前入账方式分类下已存在名称为'" + name + "'的入账方式", OperationCodeConstants.SAME_DATA_EXIST);
    }
    resultData = new AccountItem();
    resultData.setName(name);
    Byte type = form.getType();
    resultData.setType(type);
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据ID删除入账方式
   *
   * @param id 入账方式ID
   */
  public void deleteAccountItemById(Integer id) {
    ClinicAccountItem entity = new ClinicAccountItem();
    entity.setAccountItemId(id);
    Long count = clinicAccountItemBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "删除失败，ID为" + id + "'的入账方式已被门诊关联，不允许删除！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
