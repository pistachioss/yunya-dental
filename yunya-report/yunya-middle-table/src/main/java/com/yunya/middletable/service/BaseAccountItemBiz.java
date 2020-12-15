package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseAccountItemMapper;
import com.yunya.middletable.dao.system.AccountItemMapper;
import com.yunya.middletable.dao.system.AccountTypeMapper;
import com.yunya.models.report.BaseAccountItem;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 简介: 入账方式同步业务层
 *
 * @author: chow
 * @date: 2020/12/11 10:41
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseAccountItemBiz extends BaseBiz<BaseAccountItemMapper, BaseAccountItem> {

  /** 入账方式 */
  @Autowired private AccountItemMapper accountItemMapper;
  /** 入账方式分类 */
  @Autowired private AccountTypeMapper accountTypeMapper;

  /**
   * 根据消息类型操作（新增/修改/删除）中间表入账方式
   *
   * @param msg 消息
   */
  public void operateAccountItem(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseAccountItem accountItem = generateBaseAccountItem(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.deleteByPrimaryKey(dataId);
        if (null != accountItem) {
          mapper.insertSelective(accountItem);
        }
        break;
      case 1:
        if (null != accountItem) {
          BaseAccountItem result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(accountItem);
          } else {
            mapper.updateByPrimaryKeySelective(accountItem);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == accountItem) {
          mapper.deleteByPrimaryKey(dataId);
        } else {
          mapper.insertSelective(accountItem);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 构建中间表入账方式信息
   *
   * @param accountItemId 入账方式ID
   * @return BaseAccountItem
   */
  private BaseAccountItem generateBaseAccountItem(Integer accountItemId) {
    AccountItem accountItem = accountItemMapper.selectByPrimaryKey(accountItemId);
    return null != accountItem ? setBaseAccountItemValue(accountItemId, accountItem) : null;
  }

  /**
   * 设置中间表入账方式属性
   *
   * @param accountItemId 入账方式ID
   * @param accountItem 原始入账方式
   * @return BaseAccountItem
   */
  private BaseAccountItem setBaseAccountItemValue(Integer accountItemId, AccountItem accountItem) {
    BaseAccountItem baseAccountItem = new BaseAccountItem();
    baseAccountItem.setAccountItemId(accountItemId);
    baseAccountItem.setAccountItemName(accountItem.getName());
    Integer accountTypeId = accountItem.getAccountTypeId();
    baseAccountItem.setAccountTypeId(accountTypeId);
    AccountType accountType = accountTypeMapper.selectByPrimaryKey(accountTypeId);
    if (null != accountType) {
      baseAccountItem.setAccountTypeName(accountType.getName());
    }
    return baseAccountItem;
  }

  /**
   * 拉取某段时间内的入账方式数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullAccountItem(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(AccountItem.class);
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    List<AccountItem> accountItems = accountItemMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(accountItems)) {
      accountItems.forEach(
          accountItem -> {
            Integer accountItemId = accountItem.getId();
            mapper.deleteByPrimaryKey(accountItemId);
            BaseAccountItem baseAccountItem = setBaseAccountItemValue(accountItemId, accountItem);
            mapper.insertSelective(baseAccountItem);
          });
    }
  }
}
