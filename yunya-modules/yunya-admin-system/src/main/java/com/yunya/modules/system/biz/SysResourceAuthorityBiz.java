package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.SysResourceAuthority;
import com.yunya.modules.system.form.ResourceAuthorityForm;
import com.yunya.modules.system.form.ResourceForm;
import com.yunya.modules.system.form.SysPostResourceForm;
import com.yunya.modules.system.mapper.SysResourceAuthorityMapper;
import com.yunya.modules.system.vo.SysResourceAuthorityVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍: 资源权限业务层
 *
 * @author: chow
 * @date: 2020/6/30 17:23
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysResourceAuthorityBiz
    extends BaseBiz<SysResourceAuthorityMapper, SysResourceAuthority> {

  /**
   * 新增岗位的资源权限
   *
   * @param resource 参数封装
   */
  public void add(SysPostResourceForm resource) {
    List<ResourceForm> elements = resource.getElements();
    if (elements.size() > 0) {
      Integer postId = resource.getPostId();
      Integer postGroupId = resource.getPostGroupId();
      SysResourceAuthority resourceAuthority;
      for (ResourceForm element : elements) {
        resourceAuthority = new SysResourceAuthority();
        resourceAuthority.setPostId(postId);
        resourceAuthority.setPostGroupId(postGroupId);
        resourceAuthority.setResourceId(element.getResourceId());
        resourceAuthority.setResourceType(element.getResourceType());
        SysResourceAuthority resultData = mapper.selectOne(resourceAuthority);
        if (null == resultData) {
          mapper.insertSelective(resourceAuthority);
        }
      }
    }
  }

  /**
   * 获取岗位的资源权限列表
   *
   * @param resourceAuthorityForm 参数封装
   * @return list
   */
  public List<SysResourceAuthorityVO> findResourceAuthorityList(
      ResourceAuthorityForm resourceAuthorityForm) {
    List<SysResourceAuthorityVO> resultList =
        mapper.selectResourceAuthorityList(resourceAuthorityForm);
    return resultList;
  }

  /**
   * 删除岗位资源权限
   *
   * @param resourceForm 参数封装
   */
  public void deleteResourceAuthority(SysPostResourceForm resourceForm) {
    List<ResourceForm> elements = resourceForm.getElements();
    if (elements.size() > 0) {
      SysResourceAuthority authority;
      Integer postId = resourceForm.getPostId();
      for (ResourceForm element : elements) {
        authority = new SysResourceAuthority();
        authority.setPostId(postId);
        authority.setResourceId(element.getResourceId());
        authority.setResourceType(element.getResourceType());
        mapper.delete(authority);
      }
    }
  }
}
