package com.yunya.modules.emr.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.emr.MedicalRecordHistory;
import com.yunya.modules.emr.mapper.MedicalRecordHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalRecordHistoryBiz extends BaseBiz<MedicalRecordHistoryMapper, MedicalRecordHistory> {
}
