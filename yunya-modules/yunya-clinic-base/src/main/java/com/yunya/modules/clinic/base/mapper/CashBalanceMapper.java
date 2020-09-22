package com.yunya.modules.clinic.base.mapper;


import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceByIdVo;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.models.clinic_base.CashBalance;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 现金模块管理
 *
 * @author: Zkq
 * @date: 2020/8/20 14:53
 * @description:
 * @since: 1.0.0
 */
@org.apache.ibatis.annotations.Mapper
public interface CashBalanceMapper extends Mapper<CashBalance> {

    /**
     * 查询现金模块列表
     *
     * @param query 查询现金模块列表模板
     * @return resultList
     */
    List<CashBalanceVo> findCashListByPage(CashBalanceQuery query);

    /**
     * 添加现金模块列表
     *
     * @param cashBalance 添加现金模块列表模板
     */
    void add(CashBalance cashBalance);

    /**
     * 修改现金模块列表
     *
     * @param cashBalance 修改现金模块列表模板
     */
    void upd(CashBalance cashBalance);

    /**
     * 删除现金模块列表
     *
     * @param id 删除现金模块列表
     */
    void del(Integer id);

    /**
     * 查询上一天期末金额
     *
     * @param orgId 上一条记录的id
     * @return BigDecimal
     */
    BigDecimal findLastData(Integer orgId);

    /**
     * 查询现金模块列表
     *
     * @param id 根据id查询详情
     * @return CashBalanceByIdVo
     */
    CashBalanceByIdVo findDataById(Integer id);
}