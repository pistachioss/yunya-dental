package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.BillPayShareDetail;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillPayShareDetailMapper extends Mapper<BillPayShareDetail> {
    void batchSave(List<BillPayShareDetail> shareDetails);
}