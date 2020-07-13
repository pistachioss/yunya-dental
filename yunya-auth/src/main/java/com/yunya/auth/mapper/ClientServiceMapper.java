package com.yunya.auth.mapper;

import com.yunya.auth.domain.entity.ClientService;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ClientServiceMapper extends Mapper<ClientService> {

  /**
   * 根据客户端ID删除服务
   *
   * @param id
   */
  void deleteByServiceId(@Param("id") int id);
}
