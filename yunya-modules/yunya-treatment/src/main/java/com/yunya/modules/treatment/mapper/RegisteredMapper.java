package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.models.treatment.Registered;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RegisteredMapper extends Mapper<Registered> {

  /**
   * 根据条件查询患者挂号信息列表
   *
   * @param status 挂号状态
   * @param queryForm 查询条件
   * @return
   */
  List<WaitingPatientInfoVO> selectRegisteredList(
      @Param("status") Byte status, @Param("queryForm") RegisteredQueryForm queryForm);
}
