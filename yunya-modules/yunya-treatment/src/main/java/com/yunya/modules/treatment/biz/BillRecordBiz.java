package com.yunya.modules.treatment.biz;

import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment.BillRecord;
import com.yunya.modules.treatment.mapper.BillRecordMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 简介: 账单记录业务层
 *
 * @author: chow
 * @date: 2020/8/27 20:43
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillRecordBiz extends BaseBiz<BillRecordMapper, BillRecord> {

  /**
   * 生成账单编号
   *
   * @param orgId 组织ID
   * @return
   */
  public String generateBillNumber(Integer orgId) {
    String number = mapper.selectBillNumberByOrgId(orgId, new Date(System.currentTimeMillis()));
    String suffix = String.format("%04d", Integer.parseInt(number) + 1);
    return String.format("ZD%s%s%s", String.format("%04d", orgId), new DateTime().toString("yyMMdd"), suffix);
  }
}
