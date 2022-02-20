package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicMemberPriceQuery;
import com.yunya.feign.treatment.domain.vo.ClinicItemPriceVO;
import com.yunya.models.tariff.ClinicTariffMemberPrice;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicTariffMemberPriceMapper extends Mapper<ClinicTariffMemberPrice> {
    List<ClinicItemPriceVO> selectClinicTariffMemberPrice(@Param("query") ClinicMemberPriceQuery query);
}