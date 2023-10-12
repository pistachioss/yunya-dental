package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.base.KeywordDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.QcCollectedAdviceTollVO;
import com.yunya.feign.report.domain.vo.QcUnCollectedAdviceTollVO;
import com.yunya.feign.report.domain.vo.Wait4UploadTreatmentVO;
import com.yunya.models.report.BaseQcylTreatment;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseQcylTreatmentMapper extends Mapper<BaseQcylTreatment> {
    /**
     * 根据条件查询全程医嘱收费表
     *
     * @param query
     * @return
     */
    List<QcCollectedAdviceTollVO> selectQcCollectedAdviceItemList(@Param("query") KeywordDateRangeQueryForm query);

    /**
     * 根据条件查询全程非医嘱收费表
     *
     * @param query
     * @return
     */
    List<QcUnCollectedAdviceTollVO> selectQcUnCollectedAdviceItemList(@Param("query") KeywordDateRangeQueryForm query);

    /**
     * 查询待同步全程就诊账单记录列表
     *
     * @param query
     * @return
     */
    List<Wait4UploadTreatmentVO> selectWait4UploadQcTreatmentList(@Param("query") KeywordDateRangeQueryForm query);
}