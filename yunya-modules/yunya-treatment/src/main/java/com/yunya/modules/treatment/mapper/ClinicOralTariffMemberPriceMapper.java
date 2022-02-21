package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicMemberPriceQuery;
import com.yunya.feign.treatment.domain.vo.ClinicItemPriceVO;
import com.yunya.models.tariff.ClinicOralTariffMemberPrice;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicOralTariffMemberPriceMapper extends Mapper<ClinicOralTariffMemberPrice> {
    List<ClinicItemPriceVO> selectClinicOralTariffItemMemberPrice(@Param("query") ClinicMemberPriceQuery query);
}