package com.yunya.auth.biz;

import com.yunya.auth.domain.entity.ClientService;
import com.yunya.auth.mapper.ClientServiceMapper;
import com.yunya.framework.common.biz.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户端服务业务层
 *
 * @author ace
 * @create 2017/12/30.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClientServiceBiz extends BaseBiz<ClientServiceMapper, ClientService> {

  /**
   * 根据客户端ID删除服务信息
   *
   * @param id 客户端ID
   */
  public void deleteByServiceId(int id) {
    mapper.deleteByServiceId(id);
  }
}
