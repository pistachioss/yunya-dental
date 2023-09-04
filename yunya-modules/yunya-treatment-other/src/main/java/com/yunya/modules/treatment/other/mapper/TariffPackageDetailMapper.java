package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.TariffPackageDetailQuery;
import com.yunya.feign.treatment_other.domain.vo.TariffPackageDetailVO;
import com.yunya.models.treatment_other.TariffPackageDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TariffPackageDetailMapper extends Mapper<TariffPackageDetail> {
    void deleteByPackageId(Integer packageId);

    /**
     * 条件查询项目组合明细
     *
     * @param query
     * @return
     */
    List<TariffPackageDetailVO> selectTariffPackageDetailList(@Param("query") TariffPackageDetailQuery query);
}