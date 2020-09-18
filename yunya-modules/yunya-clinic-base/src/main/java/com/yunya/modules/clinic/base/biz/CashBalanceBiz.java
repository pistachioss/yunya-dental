package com.yunya.modules.clinic.base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.cash_balance.query.CashBalanceQuery;
import com.yunya.feign.cash_balance.vo.CashBalanceByIdVo;
import com.yunya.feign.cash_balance.vo.CashBalanceVo;
import com.yunya.models.clinic_base.CashBalance;
import com.yunya.modules.clinic.base.mapper.CashBalanceMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service
public class CashBalanceBiz {

    @Resource
    private CashBalanceMapper cashBalanceMapper;

    /**
     * 查询现金模块列表
     *
     * @param query 查询现金模块列表模板
     * @return resultList
     */
    public PageInfo<CashBalanceVo> findCashListByPage(CashBalanceQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<CashBalanceVo> resultList = cashBalanceMapper.findCashListByPage(query);
        return new PageInfo<>(resultList);
    }

    /**
     * 添加现金模块列表
     *
     * @param cashBalance 添加现金模块列表模板
     */
    public void  add(CashBalance cashBalance){ cashBalanceMapper.add(cashBalance); }

    /**
     * 修改现金模块列表
     *
     * @param cashBalance 修改现金模块列表模板
     */
    public void  upd(CashBalance cashBalance){
        cashBalanceMapper.upd(cashBalance);
    }

    /**
     * 删除现金模块列表
     *
     * @param id 删除现金模块列表
     */
    public void  del(Integer id){
        cashBalanceMapper.del(id);
    }

    /**
     * 查询上一天期末金额
     *
     * @param orgId 上一条记录的id
     * @return BigDecimal
     */
    public BigDecimal  findLastData(int orgId){
        return cashBalanceMapper.findLastData(orgId);
    }

    /**
     * 查询现金模块列表
     *
     * @param id 根据id查询详情
     * @return CashBalanceByIdVo
     */
    public CashBalanceByIdVo  findDataById(int id) {
        return cashBalanceMapper.findDataById(id);
    }
}
