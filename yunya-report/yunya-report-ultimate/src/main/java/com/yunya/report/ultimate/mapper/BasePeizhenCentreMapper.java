package com.yunya.report.ultimate.mapper;

import com.yunya.feign.treatment.domain.vo.BasePeizhenCentreVO;
import com.yunya.report.ultimate.model.BasePeizhenCentre;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BasePeizhenCentreMapper extends Mapper<BasePeizhenCentre> {
    int deleteAll();

    int batchIntert(List<BasePeizhenCentreVO> list);

   List<BasePeizhenCentreVO> findFirstVisitRecordDetail();

}