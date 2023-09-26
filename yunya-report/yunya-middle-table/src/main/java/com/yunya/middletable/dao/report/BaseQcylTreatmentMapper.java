package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseQcylTreatment;
import tk.mybatis.mapper.common.Mapper;

public interface BaseQcylTreatmentMapper extends Mapper<BaseQcylTreatment> {
    /**
     * 不存在则新增，否则更新
     *
     * @param data
     */
    void saveByPrimaryKeySelective(BaseQcylTreatment data);
}