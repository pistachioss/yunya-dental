package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.vo.PostVO;
import com.yunya.modules.system.vo.SysUserLoginOrgVO;
import com.yunya.modules.system.vo.SysUserPostOrgVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysUserPostMapper extends Mapper<SysUserPost> {
  /**
   * 校验同一组织部门下员工是否重复
   *
   * @param userId 用户ID
   * @param orgDeptId 组织部门ID
   * @return int
   */
  Integer checkOrgDeptUnique(
      @Param("userId") Integer userId, @Param("orgDeptId") Integer orgDeptId);

  /**
   * 检查同一组织同一岗位是否存在同一员工
   *
   * @param userId 用户ID
   * @param orgId 组织ID
   * @param postId 岗位ID
   * @return int
   */
  Integer checkOrgPostUnique(
      @Param("userId") Integer userId,
      @Param("orgId") Integer orgId,
      @Param("postId") Integer postId);

  /**
   * 根据用户ID查询全部可登陆组织
   *
   * @param userId 用户ID
   * @return
   */
  List<SysUserLoginOrgVO> selectListByUserId(@Param("userId") Integer userId);

  /**
   * 根据用户ID查询全部可登陆组织(用户管理-可登录组织)
   *
   * @param userId 用户ID
   * @return
   */
  List<SysUserPostOrgVO> selectUserPostListByUserId(@Param("userId") Integer userId);

  /**
   * 根据用户ID、组织ID查询岗位列表
   *
   * @param orgId 岗位ID
   * @param userId 用户ID
   * @return
   */
  List<PostVO> selectPostList(@Param("orgId") Integer orgId, @Param("userId") Integer userId);
}
