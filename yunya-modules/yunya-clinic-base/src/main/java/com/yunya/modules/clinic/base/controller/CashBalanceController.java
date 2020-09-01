package com.yunya.modules.clinic.base.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.form.CashBalanceForm;
import com.yunya.feign.cash_balance.model.CashBalanceModel;
import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceByIdVo;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.clinic_base.CashBalance;
import com.yunya.modules.clinic.base.biz.CashBalanceBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * 简介: 现金模块管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "现金模块增删改查")
@RestController
@RequestMapping("cash")
public class CashBalanceController {


    @Resource
    private CashBalanceBiz cashBalanceBiz;

    /**
     * 查询现金结存列表
     *
     * @param
     * @return
     */
    @ApiOperation("查询现金结存列表")
    @PostMapping("/findCycleList")
    public ResponseResult <PageInfo<CashBalanceVo>> findCashBalance(@Valid @RequestBody CashBalanceQuery query){
        PageInfo<CashBalanceVo> page = cashBalanceBiz.findCashListByPage(query);
        return ResponseUtil.success(page);
    }

    /**
     * 添加现金结存
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("添加现金结存")
    @PostMapping("/add")
    public ResponseResult add(@Valid @RequestBody CashBalanceModel model){
        //报表期间存款
        BigDecimal gathering = new BigDecimal(500);
        //总价
        BigDecimal total;
        //获取当前登录人id
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        //通过门诊id获取上一条数据的期末现金并设置为当期期初现金
        BigDecimal lastData = cashBalanceBiz.findLastData(model.getOrgId());
        //获取当天差额调整
        BigDecimal balanceAdjustment = model.getBalanceAdjustment();
        //获取当天存款金额
        BigDecimal amountDeposited = model.getAmountDeposited();
        //创建实体对象
        CashBalance cashBalance = new CashBalance();
        //对期间期初金额赋上个期间期末金额
        cashBalance.setCashFirst(lastData);
        //总价= 期初+报表期间存款-今日存款+差额调整

        total = lastData.add(gathering.subtract(amountDeposited).add(balanceAdjustment));
        //计算总值期末并赋值
        cashBalance.setCashEnd(total);
        //映射两个实体
        BeanUtils.copyProperties(model,cashBalance);
        //赋值当前登录人
        cashBalance.setCrtId(crtId);
        //通过实体添加数据
        cashBalanceBiz.add(cashBalance);
        return ResponseUtil.success();
    }

    /**
     * 修改现金结存
     *
     * @param
     * @return
     */
    @CurrentUser
    @ApiOperation("修改现金结存")
    @PutMapping("/upd")
    public ResponseResult upd(@Valid @RequestBody CashBalanceForm form){

        //报表期间存款
        BigDecimal gathering = new BigDecimal(500);
        //总价
        BigDecimal total;
        //获取当前登录人id
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        //获取当前期间期初金额
        BigDecimal cashFirst = form.getCashFirst();
        //获取当天差额调整
        BigDecimal balanceAdjustment = form.getBalanceAdjustment();
        //获取当天存款金额
        BigDecimal amountDeposited = form.getAmountDeposited();
        //创建实体对象
        CashBalance cashBalance = new CashBalance();
        //总价= 期初+报表期间存款-今日存款+差额调整
        total = cashFirst.add(gathering).subtract(amountDeposited).add(balanceAdjustment);
        //计算总值期末并赋值
        cashBalance.setCashEnd(total);
        //映射两个实体
        BeanUtils.copyProperties(form,cashBalance);
        //赋值当前登录人
        cashBalance.setCrtId(crtId);
        cashBalanceBiz.upd(cashBalance);
        return ResponseUtil.success();
    }
    /**
     * 删除现金结存
     *
     * @param
     * @return
     */
    @ApiOperation("删除现金结存")
    @DeleteMapping("/del")
    public ResponseResult del(Integer id){
        cashBalanceBiz.del(id);
        return ResponseUtil.success();
    }
    /**
     * 回显现金结存记录
     *
     * @param
     * @return
     */
    @ApiOperation("回显现金结存记录")
    @PostMapping("/findDataById")
    public ResponseResult findDataById(Integer id){
        CashBalanceByIdVo cashBalanceByIdVo = cashBalanceBiz.findDataById(id);
        return ResponseUtil.success(cashBalanceByIdVo);
    }


}
