package com.yunya.modules.system.mapper;

import com.yunya.models.system.AccountType;
import com.yunya.modules.system.domain.query.AccountTypeQueryForm;
import com.yunya.modules.system.vo.AccountTypeVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AccountTypeMapper extends Mapper<AccountType> {
  /**
   * 根据ID查询入账方式分类信息
   *
   * @param id 入账方式分类ID
   * @return
   */
  AccountTypeVO selectAccountTypeVOById(@Param("id") Integer id);

  /**
   * 根据条件查询入账方式分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<AccountTypeVO> selectAccountTypeVOList(@Param("queryForm") AccountTypeQueryForm queryForm);
}
