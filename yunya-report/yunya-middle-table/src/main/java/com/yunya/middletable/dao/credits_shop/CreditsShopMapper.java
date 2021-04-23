package com.yunya.middletable.dao.credits_shop;

import com.yunya.models.credits_shop.CreditsShop;
import com.yunya.models.report.BasePatientConsumptionCountVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CreditsShopMapper extends Mapper<CreditsShop> {

    /**
     * 查询患者消费总额
     * @return
     */
    List<BasePatientConsumptionCountVo> selectPatientConsumptionCount();
}