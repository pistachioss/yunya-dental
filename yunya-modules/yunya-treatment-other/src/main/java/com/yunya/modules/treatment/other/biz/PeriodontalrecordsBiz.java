package com.yunya.modules.treatment.other.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.modules.treatment.other.mapper.PeriodontalrecordsMapper;
import com.yunya.models.treatment_other.Periodontalrecords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PeriodontalrecordsBiz extends BaseBiz<PeriodontalrecordsMapper, Periodontalrecords> {
}
