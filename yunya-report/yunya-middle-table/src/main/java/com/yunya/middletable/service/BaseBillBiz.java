package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BaseBillPayMapper;
import com.yunya.middletable.dao.treatment.BillPayDetailRecordMapper;
import com.yunya.middletable.dao.treatment.BillPayRecordMapper;
import com.yunya.middletable.dao.treatment.BillRecordMapper;
import com.yunya.models.middletable.BaseBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 中间表账单业务层
 *
 * @author: chow
 * @date: 2020/10/21 16:52
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseBillBiz extends BaseBiz<BaseBillMapper, BaseBill> {

  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordMapper;
  /** 账单付款记录 */
  @Autowired private BillPayRecordMapper payRecordMapper;
  /** 账单付款详情 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 中间表账单付款记录 */
  @Autowired private BaseBillPayMapper baseBillPayMapper;
  /** 中间表账单详情 */
  @Autowired private BaseBillDetailMapper baseBillDetailMapper;

  /**
   * 根据消息操作中间表账单
   *
   * @param msg 消息
   */
  public void operateBill(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseBill bill = generateBaseBill(dataId);
    if (null == bill) {
      return;
    }
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.delete(bill);
        mapper.insertSelective(bill);
        break;
      case 1:
        BaseBill result = mapper.selectByPrimaryKey(dataId);
        if (null == result) {
          mapper.insertSelective(bill);
        } else {
          mapper.updateByPrimaryKeySelective(bill);
        }
        break;
      case 2:
        mapper.delete(bill);
        break;
      default:
        break;
    }
  }

  /**
   * 初始化中间表账单
   *
   * @param dataId 账单ID
   * @return
   */
  private BaseBill generateBaseBill(Integer dataId) {
    return null;
  }

  /**
   * 根据条件拉取账单数据并更新中间表
   *
   * @param form 时间段
   */
  public void pullBillData(PullForm form) {}
}
