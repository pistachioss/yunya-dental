package com.yunya.auth.mapper;

import com.yunya.models.auth.Client;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 客户端mapper
 *
 * @author chow
 */
public interface ClientMapper extends Mapper<Client> {

  /**
   * 根据服务ID获取客户端编码
   *
   * @param serviceId 服务注册ID
   * @return
   */
  List<String> selectAllowedClient(String serviceId);

  /**
   * 根据客户端ID获取所有服务
   *
   * @param clientId 服务ID
   * @return
   */
  List<Client> selectAuthorityServiceInfo(int clientId);
}
