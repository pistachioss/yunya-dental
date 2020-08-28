package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 账单支付记录业务层
 *
 * @author: chow
 * @date: 2020/8/27 20:44
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillPayRecordBiz extends BaseBiz<BillPayRecordMapper, BillPayRecord> {
  /***/
}
