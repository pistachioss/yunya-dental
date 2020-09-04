package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.tariff.ClinicOralTariffMemberPrice;
import com.yunya.modules.treatment.mapper.ClinicOralTariffMemberPriceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述: 门诊商品项目会员价信息业务层
 *
 * @author GaoLuding
 * @create 2020-06-09 16:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicOralTariffMemberPriceBiz
    extends BaseBiz<ClinicOralTariffMemberPriceMapper, ClinicOralTariffMemberPrice> {}
