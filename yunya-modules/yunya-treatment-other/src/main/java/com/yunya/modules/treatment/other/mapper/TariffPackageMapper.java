package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.TariffPackageQuery;
import com.yunya.feign.treatment_other.domain.vo.TariffPackageVO;
import com.yunya.models.treatment_other.TariffPackage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TariffPackageMapper extends Mapper<TariffPackage> {
    /**
     * 条件查询
     *
     * @param query
     * @return
     */
    List<TariffPackageVO> selectTariffPackageList(@Param("query") TariffPackageQuery query);
}