package com.yunya.modules.treatment.biz;

import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillDetailVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.modules.treatment.mapper.OrderDetailPayRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简介: 订单明细支付记录业务层
 *
 * @author: chow
 * @date: 2020/11/6 10:14
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderDetailPayRecordBiz
    extends BaseBiz<OrderDetailPayRecordMapper, OrderDetailPayRecord> {
  /** 系统服务 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 患者服务 */
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;

  /**
   * 批量插入开单明细收费记录
   *
   * @param orderDetailPayRecords 开单明细收费记录列表
   */
  public void batchInsert(List<OrderDetailPayRecord> orderDetailPayRecords) {
    mapper.batchInsert(orderDetailPayRecords);
  }

  /**
   * 查询账单优惠为0的订单金额
   *
   * @param billRecordId 账单记录ID
   * @return BigDecimal
   */
  public BigDecimal selectNoDiscountAmount(Integer billRecordId) {
    return mapper.selectNoDiscountAmount(billRecordId);
  }

  /**
   * 更新订单明细收费的已收金额
   *
   * @param billRecordId 账单记录ID
   * @param receivedAmount 撤销收费金额
   */
  public void updateReceivedAmount(Integer billRecordId, BigDecimal receivedAmount) {
    OrderDetailPayRecord entity = new OrderDetailPayRecord();
    entity.setBillRecordId(billRecordId);
    List<OrderDetailPayRecord> list = mapper.select(entity);
    if (StringHelper.isNotEmpty(list)) {
      list = list.stream().sorted((v1, v2) -> v2.getId() - v1.getId()).collect(Collectors.toList());
      for (OrderDetailPayRecord vo : list) {
        BigDecimal amount = vo.getReceivedAmount();
        receivedAmount = receivedAmount.subtract(amount);
        if (receivedAmount.compareTo(BigDecimal.ZERO) <= 0) {
          // receivedAmount已经用完了
          vo.setReceivedAmount(receivedAmount.abs());
          mapper.updateByPrimaryKeySelective(vo);
          break;
        } else {
          vo.setReceivedAmount(BigDecimal.ZERO);
          mapper.updateByPrimaryKeySelective(vo);
        }
      }
    }
  }

  /**
   * 根据条件导出门诊当月账单明细
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthBillDetail(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    String fileName = query.getCurrentMonth() + "账单明细报表";
    List<CurrentMonthBillDetailVO> resultList = mapper.selectCurrentMonthBillDetail(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (CurrentMonthBillDetailVO vo : resultList) {
        Integer patientId = vo.getPatientId();
        PatientBaseInfo patientBaseInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
        if (patientBaseInfo != null) {
          vo.setPatientName(patientBaseInfo.getName());
          vo.setMedicalNum(patientBaseInfo.getMedicalNumber());
          vo.setPatientMobile(patientBaseInfo.getMobile());
        }
      }
    }
    ExcelUtil<CurrentMonthBillDetailVO> excelUtil = new ExcelUtil<>(CurrentMonthBillDetailVO.class);
    Integer orgId = query.getOrgId();
    OrganizationInfo organization = systemServiceFeign.findOrgInfoByOrgId(orgId);
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "账单明细记录", fileName);
  }

  /**
   * 根据订单id查询订单在billPayId截止之前的项目收费明细
   *
   * @param orderRecordId
   * @param billPayId
   * @return
   */
  public List<BillPayShareDetailVO> findItemPayDetailDeadlineBillPayId(Integer orderRecordId, Integer billPayId) {
    return mapper.selectItemPayDetailDeadlineBillPayId(orderRecordId, billPayId);
  }
}
