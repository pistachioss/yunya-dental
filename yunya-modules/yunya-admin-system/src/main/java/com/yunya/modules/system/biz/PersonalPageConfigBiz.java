package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.PersonalPageConfig;
import com.yunya.modules.system.domain.model.PageFieldModel;
import com.yunya.modules.system.domain.model.PersonalPageConfigModel;
import com.yunya.modules.system.mapper.PersonalPageConfigMapper;
import com.yunya.modules.system.vo.PersonalPageFieldVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简介: 个人页面字段配置业务层
 *
 * @author: chow
 * @date: 2020/8/31 20:42
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonalPageConfigBiz extends BaseBiz<PersonalPageConfigMapper, PersonalPageConfig> {

  /**
   * 根据条件查询用户页面配置字段列表
   *
   * @param userId 用户ID
   * @param pageName 页面名称
   * @return
   */
  public List<PersonalPageFieldVO> findFieldList(Integer userId, String pageName) {
    List<PersonalPageFieldVO> resultList = mapper.selectFieldList(userId, pageName);
    return resultList;
  }

  /**
   * 保存用户自定义页面字段
   *
   * @param model 参数模型
   */
  public void save(PersonalPageConfigModel model) {
    LinkedHashSet<PageFieldModel> fieldList = model.getFieldList();
    if (StringHelper.isNotEmpty(fieldList)) {
      String pageName = model.getPageName();
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      PersonalPageConfig entity = new PersonalPageConfig();
      entity.setUserId(userId);
      entity.setPageName(pageName);
      mapper.delete(entity);
      AtomicInteger orderNum = new AtomicInteger();
      fieldList.forEach(
          field -> {
            entity.setFieldName(field.getFieldName());
            entity.setHide(field.getHide());
            entity.setIsDefault(field.getDefaultValue());
            entity.setOrderNum(orderNum.get());
            entity.setCrtId(userId);
            entity.setCrtName(name);
            mapper.insertSelective(entity);
            orderNum.getAndIncrement();
          });
    }
  }
}
