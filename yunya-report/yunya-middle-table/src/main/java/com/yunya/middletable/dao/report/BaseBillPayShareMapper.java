package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBillPayShare;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillPayShareMapper extends Mapper<BaseBillPayShare> {

    void batchSave(List<BaseBillPayShare> data);
}