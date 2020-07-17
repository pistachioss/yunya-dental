package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysResourceAuthority;
import com.yunya.modules.system.form.ResourceAuthorityForm;
import com.yunya.modules.system.vo.SysResourceAuthorityVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysResourceAuthorityMapper extends Mapper<SysResourceAuthority> {

  /**
   * 根据岗位ID查询资源权限列表
   *
   * @param resourceAuthorityForm 参数封装
   * @return list
   */
  List<SysResourceAuthorityVO> selectResourceAuthorityList(@Param("resourceAuthorityForm") ResourceAuthorityForm resourceAuthorityForm);
}
