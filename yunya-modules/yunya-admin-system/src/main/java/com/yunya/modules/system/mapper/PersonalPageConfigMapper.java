package com.yunya.modules.system.mapper;

import com.yunya.models.system.PersonalPageConfig;
import com.yunya.modules.system.vo.PersonalPageFieldVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PersonalPageConfigMapper extends Mapper<PersonalPageConfig> {
  /**
   * 根据条件查询用户页面配置字段列表
   *
   * @param userId 用户ID
   * @param pageName 页面名称
   * @return
   */
  List<PersonalPageFieldVO> selectFieldList(
      @Param("userId") Integer userId, @Param("pageName") String pageName);
}
