package com.yunya.modules.tariff.controller;

import com.yunya.feign.tariff.domain.vo.BaseOralTariffHistoryVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.tariff.BaseOralTariffHistory;
import com.yunya.modules.tariff.biz.BaseOralTariffHistoryBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 简介: 商品项目变更记录控制器
 *
 * @author: chow
 * @date: 2020/8/3 20:13
 * @description:
 * @since: 1.0.0
 */
@Api("商品项目变更记录管理")
@RestController
@RequestMapping("/goods")
public class BaseOralTariffHistoryController {

  /** 注入对象 */
  private final BaseOralTariffHistoryBiz baseOralTariffHistoryBiz;

  public BaseOralTariffHistoryController(BaseOralTariffHistoryBiz baseOralTariffHistoryBiz) {
    this.baseOralTariffHistoryBiz = baseOralTariffHistoryBiz;
  }

  /**
   * 获取商品项目id查询变更历史记录
   *
   * @param oralTariffId 商品项目ID
   * @return
   */
  @ApiOperation("获取商品项目id查询变更历史记录")
  @GetMapping("/history/{oralTariffId}")
  public ResponseResult findList(@PathVariable("oralTariffId") Integer oralTariffId) {
    List<BaseOralTariffHistoryVO> resultList = baseOralTariffHistoryBiz.findList(oralTariffId);
    return ResponseUtil.success(resultList);
  }
}
