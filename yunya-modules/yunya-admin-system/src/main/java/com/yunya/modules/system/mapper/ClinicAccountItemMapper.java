package com.yunya.modules.system.mapper;

import com.yunya.models.system.ClinicAccountItem;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import com.yunya.modules.system.vo.ClinicAccountItemVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicAccountItemMapper extends Mapper<ClinicAccountItem> {

  /**
   * 根据ID查询门诊入账方式信息
   *
   * @param id 门诊入账方式ID
   * @return
   */
  ClinicAccountItemVO selectById(@Param("id") Integer id);

  /**
   * 根据条件查询组织入账方式列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<ClinicAccountItemVO> selectClinicAccountItemList(
      @Param("queryForm") ClinicAccountItemQueryForm queryForm);
}
