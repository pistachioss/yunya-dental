package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import com.yunya.modules.treatment.other.mapper.QcTreatmentRecordMapper;
import org.springframework.stereotype.Service;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:42
 * @description:
 * @since: 1.0.0
 */
@Service
public class QcTreatmentRecordBiz extends BaseBiz<QcTreatmentRecordMapper, QcTreatmentRecord> {
    public PageInfo<QcRecommondInfoVO> findMallRecommondList() {
        return null;
    }
}
