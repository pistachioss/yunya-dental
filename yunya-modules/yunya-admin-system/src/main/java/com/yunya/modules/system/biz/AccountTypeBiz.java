package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.AccountType;
import com.yunya.modules.system.domain.form.AccountTypeForm;
import com.yunya.modules.system.domain.model.AccountTypeModel;
import com.yunya.modules.system.domain.query.AccountTypeQueryForm;
import com.yunya.modules.system.mapper.AccountTypeMapper;
import com.yunya.modules.system.vo.AccountTypeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介: 入账方式类型业务层
 *
 * @author: chow
 * @date: 2020/7/24 14:09
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountTypeBiz extends BaseBiz<AccountTypeMapper, AccountType> {

  /** 注入对象 */
  private final AccountItemBiz accountItemBiz;

  public AccountTypeBiz(AccountItemBiz accountItemBiz) {
    this.accountItemBiz = accountItemBiz;
  }

  /**
   * 根据ID查询入账方式分类信息
   *
   * @param id 入账方式分类ID
   * @return
   */
  public AccountTypeVO findById(Integer id) {
    AccountTypeVO resultVO = mapper.selectAccountTypeVOById(id);
    return resultVO;
  }

  /**
   * 根据条件查询入账方式分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<AccountTypeVO> findList(AccountTypeQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<AccountTypeVO> resultList = mapper.selectAccountTypeVOList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增入账方式分类
   *
   * @param model 参数模型
   */
  public void add(AccountTypeModel model) {
    String name = model.getName();
    AccountType entity = new AccountType();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，名称为'" + name + "'的入账方式分类已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 修改入账方式分类
   *
   * @param id 入账方式分类ID
   * @param form 参数封装
   */
  public void modify(Integer id, AccountTypeForm form) {
    AccountType resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "'的数据不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    Boolean sysDefault = resultData.getSysDefault();
    if (sysDefault) {
      throw new ClientServiceException(
          "修改失败，系统默认的入账方式分类不允许被修改！", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    String name = form.getName();
    if (!resultData.getName().equals(name)) {
      resultData = new AccountType();
      resultData.setName(name);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，名称为'" + name + "'的数据已存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
      }
    }
    Boolean inservice = form.getInservice();
    if (null != inservice) {
      resultData.setInservice(inservice);
    }
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据ID删除入账方式分类
   *
   * @param id 入账方式分类ID
   */
  public void deleteAccountTypeById(Integer id) {
    AccountType resultData = mapper.selectByPrimaryKey(id);
    Boolean sysDefault = resultData.getSysDefault();
    if (sysDefault) {
      throw new ClientServiceException(
          "删除失败，系统默认的入账方式分类不允许被删除！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    AccountItem entity = new AccountItem();
    entity.setAccountTypeId(id);
    Long count = accountItemBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "删除失败，ID为'" + id + "'的入账方式分类已被关联", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
