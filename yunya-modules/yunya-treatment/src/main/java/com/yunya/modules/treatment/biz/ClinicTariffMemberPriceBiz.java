package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.query.ClinicMemberPriceQuery;
import com.yunya.feign.treatment.domain.vo.ClinicItemPriceVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.tariff.ClinicTariffMemberPrice;
import com.yunya.modules.treatment.mapper.ClinicTariffMemberPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-09 16:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicTariffMemberPriceBiz extends BaseBiz<ClinicTariffMemberPriceMapper, ClinicTariffMemberPrice> {

    @Autowired
    private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;
    public List<ClinicItemPriceVO> findClinicItemMemberPrice(ClinicMemberPriceQuery query) {
        List<ClinicItemPriceVO> result = new ArrayList<>();
        if (StringHelper.isNotEmpty(query.getTariffIds())) {
            result.addAll(mapper.selectClinicTariffMemberPrice(query));
        }
        if (StringHelper.isNotEmpty(query.getOralIds())) {
            List<ClinicItemPriceVO> oralMemberPrices = clinicOralTariffMemberPriceBiz.findClinicOralTariffItemMemberPrice(query);
            if (StringHelper.isNotEmpty(oralMemberPrices)) {
                result.addAll(oralMemberPrices);
            }
        }
        return result;
    }
}
