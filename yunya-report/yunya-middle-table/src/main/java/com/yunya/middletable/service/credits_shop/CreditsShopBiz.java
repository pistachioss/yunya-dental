package com.yunya.middletable.service.credits_shop;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.credits_shop.CreditsShopMapper;
import com.yunya.models.credits_shop.CreditsShop;
import com.yunya.models.report.BasePatient;
import com.yunya.models.report.BasePatientConsumptionCountVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 积分商城
 * @author: LHB
 * @create: 2021-04-22 16:01
 **/
@Service
public class CreditsShopBiz extends BaseBiz<CreditsShopMapper, CreditsShop> {

    /**
     * 初始患者化积分
     */
    public void initialization() {
        List<CreditsShop> creditsShopList = new ArrayList<>();
        List<BasePatientConsumptionCountVo> basePatientConsumptionCountVos = mapper.selectPatientConsumptionCount();
        if (basePatientConsumptionCountVos != null){
            for (BasePatientConsumptionCountVo basePatientConsumptionCountVo : basePatientConsumptionCountVos) {
                CreditsShop creditsShop = new CreditsShop();
                creditsShop.setPatientId(basePatientConsumptionCountVo.getPatientId());
                creditsShop.setType("offlineConsume");
                creditsShop.setChannel((byte)0);
                creditsShop.setOrderNum(null);



            }
        }
    }
}
