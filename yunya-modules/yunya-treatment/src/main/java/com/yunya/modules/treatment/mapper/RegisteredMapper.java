package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.RegisteredQueryForm;
import com.yunya.feign.treatment.domain.vo.RegisteredVO;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.models.treatment.Registered;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

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


  /**
   * 根据id集合查询挂号列表信息
   * @param ids 挂号ID集合
   * @return  实例列表
   */
  List<Registered> selectRegisteredListByIds(@Param("ids") List<Integer> ids);


  /**
   * 根据挂号ID查询挂号信息
   * @param id 挂号信息
   * @return
   */
  RegisteredVO registeredInfoDetail(@Param("id") Integer id);
}
