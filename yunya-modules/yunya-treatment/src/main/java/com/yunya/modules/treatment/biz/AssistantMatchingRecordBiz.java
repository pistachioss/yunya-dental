package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment.AssistantMatchingRecord;
import com.yunya.modules.treatment.mapper.AssistantMatchingRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 助手配诊记录业务层
 *
 * @author: chow
 * @date: 2020/8/18 11:11
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AssistantMatchingRecordBiz
    extends BaseBiz<AssistantMatchingRecordMapper, AssistantMatchingRecord> {
  /***/
}
