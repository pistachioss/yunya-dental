package com.yunya.modules.tariff.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.tariff.ClinicTariffMemberPrice;
import com.yunya.modules.tariff.mapper.ClinicTariffMemberPriceMapper;
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
public class ClinicTariffMemberPriceBiz extends BaseBiz<ClinicTariffMemberPriceMapper, ClinicTariffMemberPrice> {
}
