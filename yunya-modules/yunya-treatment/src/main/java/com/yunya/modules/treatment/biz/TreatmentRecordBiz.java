package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 就诊记录管理业务层
 *
 * @author: chow
 * @date: 2020/8/12 14:55
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TreatmentRecordBiz extends BaseBiz<TreatmentRecordMapper, TreatmentRecord> {
  /***/
}
