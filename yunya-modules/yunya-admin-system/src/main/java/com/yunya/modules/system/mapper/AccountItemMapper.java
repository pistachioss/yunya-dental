package com.yunya.modules.system.mapper;

import com.yunya.feign.system.vo.AccountItemVO;
import com.yunya.models.system.AccountItem;
import com.yunya.modules.system.domain.query.AccountItemQueryForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AccountItemMapper extends Mapper<AccountItem> {

  /**
   * 根据支付方式ID查询入账方式
   *
   * @param id 入账方式ID
   * @return AccountItemVO
   */
  AccountItemVO selectById(@Param("id") Integer id);

  /**
   * 根据条件查询入账方式列表
   *
   * @param queryForm 查询条件
   * @return List<AccountItemVO>
   */
  List<AccountItemVO> selectAccountItemList(@Param("queryForm") AccountItemQueryForm queryForm);
}
