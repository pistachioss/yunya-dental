package com.yunya.modules.system.mapper;

import com.yunya.models.system.PostGroup;
import com.yunya.modules.system.domain.query.PostGroupQueryForm;
import com.yunya.modules.system.vo.PostGroupVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PostGroupMapper extends Mapper<PostGroup> {
  /**
   * 根据条件查询岗位组列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  List<PostGroupVO> selectPostGroupByExample(@Param("queryForm") PostGroupQueryForm queryForm);

  /**
   * 递归查询当前ID即子节点ID
   *
   * @param postGroupId 岗位分类ID
   * @return list
   */
  List<Integer> selectChildIdList(@Param("postGroupId") Integer postGroupId);
}
