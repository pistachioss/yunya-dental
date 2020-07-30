package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.DictionaryType;
import com.yunya.modules.system.domain.form.DictForm;
import com.yunya.modules.system.domain.model.DictionaryItemModel;
import com.yunya.modules.system.domain.query.DictQueryForm;
import com.yunya.modules.system.mapper.DictionaryItemMapper;
import com.yunya.modules.system.mapper.DictionaryTypeMapper;
import com.yunya.modules.system.vo.DictionaryItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 字典明细业务层
 *
 * @author: chow
 * @date: 2020/6/4 10:09
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryItemBiz extends BaseBiz<DictionaryItemMapper, DictionaryItem> {

  /** 注入对象 */
  @Autowired private DictionaryTypeMapper dictionaryTypeMapper;

  /**
   * 新增字典数据
   *
   * @param resource 参数封装
   */
  public void add(DictionaryItemModel resource) {
    String name = resource.getName();
    Integer dictTypeId = resource.getDictionaryTypeId();
    DictionaryType typeResult = dictionaryTypeMapper.selectByPrimaryKey(dictTypeId);
    if (null == typeResult) {
      throw new ClientServiceException(
          "添加的字典明细'" + name + "失败，'所属的字典类型不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    DictionaryItem entity = new DictionaryItem();
    entity.setDictionaryTypeId(dictTypeId);
    entity.setName(name);
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "添加字典数据'" + name + "'失败，该字典下已存在相同名称数据", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    mapper.insertSelective(entity);
  }

  /**
   * 修改字典明细数据
   *
   * @param id 字典数据ID
   * @param form 参数封装
   */
  public void edit(Integer id, DictForm form) {
    DictionaryItem item = mapper.selectByPrimaryKey(id);
    if (null == item) {
      throw new ClientServiceException(
          "修改字典明细数据失败，名称为'" + form.getName() + "'，的数据不存在",
          OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    if (!item.getName().equals(form.getName())) {
      String name = form.getName();
      DictionaryItem entity = new DictionaryItem();
      entity.setDictionaryTypeId(item.getDictionaryTypeId());
      entity.setName(name);
      DictionaryItem result = mapper.selectOne(entity);
      if (null != result) {
        throw new ClientServiceException(
            "修改字典数据" + name + "'，该名称字典数据已存在", OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      item.setName(name);
    }
    if (form.getInservice() != null) {
      item.setInservice(form.getInservice());
    }
    item.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    item.setUpdName(BaseContextHandler.getName());
    item.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(item);
  }

  /**
   * 根据条件查询字典明细列表（可分页）
   *
   * @param queryForm 参数封装
   * @return list
   */
  public PageInfo<DictionaryItemVO> findList(DictQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<DictionaryItemVO> resultList = mapper.selectBrandByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 删除字典明细
   *
   * @param id 字典明细ID
   */
  public void deleteDictItem(Integer id) {
    mapper.deleteByPrimaryKey(id);
  }
}
