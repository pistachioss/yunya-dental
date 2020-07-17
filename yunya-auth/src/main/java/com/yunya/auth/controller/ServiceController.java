package com.yunya.auth.controller;

import com.yunya.models.auth.Client;
import com.yunya.auth.biz.ClientBiz;
import com.yunya.framework.common.controller.BaseController;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户端服务控制层
 *
 * @author ace
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("service")
public class ServiceController extends BaseController<ClientBiz, Client> {

  /**
   * 根据客户端组册信息ID修改客户端服务信息
   *
   * @param clientId 客户端ID
   * @param clients 服务信息
   * @return
   */
  @PutMapping("/modify/{clientId}")
  public ResponseResult modifyUsers(@PathVariable int clientId, String clients) {
    baseBiz.modifyClientServices(clientId, clients);
    return ResponseUtil.success();
  }

  /**
   * 根据客户端注册信息ID获取客户端信息列表
   *
   * @param id 客户端注册信息ID
   * @return
   */
  @GetMapping("/list/{id}")
  public ResponseResult getUsers(@PathVariable int id) {
    List<Client> clients = baseBiz.getClientServices(id);
    return ResponseUtil.success(clients);
  }
}
