package com.yunya.modules.tariff.controller;

import com.yunya.feign.tariff.domain.vo.BaseOralTariffHistoryVO;
import com.yunya.feign.tariff.domain.vo.BaseTariffHistoryVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.tariff.biz.BaseTariffHistoryBiz;
import com.yunya.modules.tariff.biz.BaseTariffHistoryBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 简介: 价目表项目变更记录控制器
 *
 * @author: chow
 * @date: 2020/8/5 15:38
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "价目表变更记录管理")
@RestController
@RequestMapping("base")
public class BaseTariffHistoryController {

  /** 注入对象 */
  private final BaseTariffHistoryBiz baseTariffHistoryBiz;

  public BaseTariffHistoryController(BaseTariffHistoryBiz baseTariffHistoryBiz) {
    this.baseTariffHistoryBiz = baseTariffHistoryBiz;
  }

  /**
   * 获取商品项目id查询变更历史记录
   *
   * @param tariffId 商品项目ID
   * @return
   */
  @ApiOperation("获取商品项目id查询变更历史记录")
  @GetMapping("/history/{tariffId}")
  public ResponseResult findList(@PathVariable(value = "tariffId") Integer tariffId) {
    List<BaseTariffHistoryVO> resultList = baseTariffHistoryBiz.findList(tariffId);
    return ResponseUtil.success(resultList);
  }
}
