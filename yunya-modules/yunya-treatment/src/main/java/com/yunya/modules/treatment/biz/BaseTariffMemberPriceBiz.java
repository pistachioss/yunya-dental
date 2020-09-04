package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.tariff.BaseTariffMemberPrice;
import com.yunya.modules.treatment.mapper.BaseTariffMemberPriceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-09 16:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffMemberPriceBiz extends BaseBiz<BaseTariffMemberPriceMapper, BaseTariffMemberPrice> {
}
