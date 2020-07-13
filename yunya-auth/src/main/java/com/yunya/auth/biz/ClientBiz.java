package com.yunya.auth.biz;

import com.yunya.auth.domain.entity.Client;
import com.yunya.auth.domain.entity.ClientService;
import com.yunya.auth.mapper.ClientMapper;
import com.yunya.framework.common.biz.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 客户端注册信息业务层
 *
 * @author Mr.AG
 * @date 2017-12-26 19:43:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClientBiz extends BaseBiz<ClientMapper, Client> {

  /** 注入对象 */
  private final ClientServiceBiz clientServiceBiz;

  public ClientBiz(ClientServiceBiz clientServiceBiz) {
    this.clientServiceBiz = clientServiceBiz;
  }

  /**
   * 根据客户端ID获取客户端注册信息列表
   *
   * @param id
   * @return
   */
  public List<Client> getClientServices(int id) {
    return mapper.selectAuthorityServiceInfo(id);
  }

  /**
   * 修改服务信息
   *
   * @param id 客户端注册信息ID
   * @param clients 服务信息
   */
  public void modifyClientServices(int id, String clients) {
    clientServiceBiz.deleteByServiceId(id);
    if (!StringUtils.isEmpty(clients)) {
      String[] mem = clients.split(",");
      for (String m : mem) {
        ClientService clientService = new ClientService();
        clientService.setServiceId(m);
        clientService.setClientId(id + "");
        clientServiceBiz.insertSelective(clientService);
      }
    }
  }
}
