package com.yunya.auth.mapper;

import com.yunya.auth.domain.entity.ClientService;
import tk.mybatis.mapper.common.Mapper;

public interface ClientServiceMapper extends Mapper<ClientService> {

  /**
   * 根据客户端ID删除服务
   *
   * @param id
   */
  void deleteByServiceId(int id);
}
