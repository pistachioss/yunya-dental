package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.FirstVisitDetailVO;
import com.yunya.models.report.BaseFirstVisitDetail;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseFirstVisitDetailMapper extends Mapper<BaseFirstVisitDetail> {

    int batchIntert(List<FirstVisitDetailVO> list);

    int deleteAll();
}