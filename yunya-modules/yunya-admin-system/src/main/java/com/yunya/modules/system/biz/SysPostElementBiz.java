package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.SysPostElement;
import com.yunya.modules.system.form.ResourceForm;
import com.yunya.modules.system.form.SysPostResourceForm;
import com.yunya.modules.system.mapper.SysPostElementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍:</br> 岗位功能关系业务层
 *
 * @author: chow
 * @date: 2020/6/24 15:53
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysPostElementBiz extends BaseBiz<SysPostElementMapper, SysPostElement> {

  /**
   * 新增岗位对应的权限
   *
   * @param resource 参数封装
   */
  public void add(SysPostResourceForm resource) {
    List<ResourceForm> elements = resource.getElements();
    if (elements.size() > 0) {
      Integer postId = resource.getPostId();
      SysPostElement sysPostElement;
      for (ResourceForm element : elements) {
        sysPostElement = new SysPostElement();
        sysPostElement.setPostId(postId);
        sysPostElement.setSysElementId(element.getResourceId());
        sysPostElement.setType(element.getResourceType());
        SysPostElement resultData = mapper.selectOne(sysPostElement);
        if (null == resultData) {
          mapper.insertSelective(sysPostElement);
        }
      }
    }
  }
}
