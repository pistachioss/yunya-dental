package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.DictionaryType;
import com.yunya.modules.system.domain.form.DictForm;
import com.yunya.modules.system.domain.model.DictionaryTypeModel;
import com.yunya.modules.system.domain.query.DictQueryForm;
import com.yunya.modules.system.mapper.DictionaryItemMapper;
import com.yunya.modules.system.mapper.DictionaryTypeMapper;
import com.yunya.modules.system.vo.DictionaryTypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍:</br> 字典类型业务层
 *
 * @author: chow
 * @date: 2020/6/3 16:58
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryTypeBiz extends BaseBiz<DictionaryTypeMapper, DictionaryType> {

  /** 注入对象 */
  @Autowired private DictionaryItemMapper dictionaryItemMapper;

  /**
   * 根据条件查询字典类型列表（可分页）
   *
   * @param queryForm 查询参数封装
   * @return
   */
  public PageInfo<DictionaryTypeVO> findList(DictQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<DictionaryTypeVO> resultList = mapper.selectBrandByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增字典类型
   *
   * @param resource 参数封装
   */
  public void add(DictionaryTypeModel resource) {
    String name = resource.getName();
    DictionaryType entity = new DictionaryType();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增字典类型'" + name + "'失败，该字典类型名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    mapper.insertSelective(entity);
  }

  /**
   * 编辑字典类型
   *
   * @param id 字典ID
   * @param form 参数封装
   */
  public void edit(Integer id, DictForm form) {
    DictionaryType type = mapper.selectByPrimaryKey(id);
    if (null == type) {
      throw new ClientServiceException(
          "修改字典失败，字典类型名称为'" + form.getName() + "'的数据不存在",
          OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    if (!type.getName().equals(form.getName())) {
      String name = form.getName();
      DictionaryType entity = new DictionaryType();
      entity.setName(name);
      DictionaryType result = mapper.selectOne(entity);
      if (null != result) {
        throw new ClientServiceException(
            "修改字典类型'" + name + "'失败，该字典类型名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
      type.setName(name);
    }
    if (null != form.getInservice()) {
      type.setInservice(form.getInservice());
    }
    mapper.updateByPrimaryKeySelective(type);
  }

  /**
   * 删除字典类型
   *
   * @param id 字典ID
   */
  public void deleteDict(Integer id) {
    DictionaryItem entity = new DictionaryItem();
    entity.setDictionaryTypeId(id);
    List<DictionaryItem> items = dictionaryItemMapper.select(entity);
    if (items.size() > 0) {
      throw new ClientServiceException(
          "删除ID为'" + id + "'的字典失败，该字典类型已被使用", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
