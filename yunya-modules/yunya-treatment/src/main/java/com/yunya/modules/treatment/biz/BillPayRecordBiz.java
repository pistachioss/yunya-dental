package com.yunya.modules.treatment.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.MemberRevocationFeeModel;
import com.yunya.feign.patient_central.domain.model.PrepaidRevocationFeeModel;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillCollectionDebtVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillPayRecordVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.BillExceptionHandleDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillExceptionHandleRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import com.yunya.modules.treatment.mapper.BillRecordMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBillPay;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_BILL_PAY_RECORD;

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

  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 缓存调用 */
  @Autowired private RedisUtils redisUtils;
  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordMapper;
  /** 账单支付明细 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 账单异常处理记录 */
  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;
  /** 账单异常处理详情 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;
  /** 订单明细收费 */
  @Autowired private OrderDetailPayRecordBiz orderDetailPayRecordBiz;

  /**
   * 根据账单收费记录ID撤销账单收费记录
   *
   * @param billPayRecordId 账单收费记录ID
   */
  public void revoke(Integer billPayRecordId) {
    BillPayRecord billPayRecord = checkRevokeBillPayRecordWhetherAllow(billPayRecordId);
    String redisKey = LOCK_BILL_PAY_RECORD + billPayRecordId;
    redisUtils.set(redisKey, billPayRecordId, 5);
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Integer patientId = billPayRecord.getPatientId();
    Integer treatmentRecordId = billPayRecord.getTreatmentRecordId();
    Integer billRecordId = billPayRecord.getBillRecordId();
    BigDecimal receivedAmount = billPayRecord.getReceivedAmount();
    BillRecord billRecord = billRecordMapper.selectByPrimaryKey(billRecordId);
    BigDecimal totalReceivedAmount = billRecord.getReceivedAmount();
    billRecord.setReceivedAmount(totalReceivedAmount.subtract(receivedAmount));
    BigDecimal debtAmount = billRecord.getDebtAmount();
    billRecord.setDebtAmount(debtAmount.add(receivedAmount));
    billRecord.setUpdId(userId);
    billRecord.setUpdName(name);
    billRecordMapper.updateByPrimaryKeySelective(billRecord);
    billPayRecord.setInservice(false);
    billPayRecord.setUpdId(userId);
    billPayRecord.setUpdName(name);
    int result = mapper.updateByPrimaryKeySelective(billPayRecord);
    if (receivedAmount.compareTo(BigDecimal.ZERO) > 0) {
      orderDetailPayRecordBiz.updateReceivedAmount(billRecordId, receivedAmount);
    }
    // 更新账单记录
    rabbitMqServiceFeign.sendMessage(billRecord.getOrderRecordId(), 1, BaseBill);

    BillPayDetailRecord billPayDetail = new BillPayDetailRecord();
    billPayDetail.setBillPayRecordId(billPayRecordId);
    billPayDetail.setInservice(true);
    List<BillPayDetailRecord> payDetailRecords = billPayDetailRecordBiz.selectList(billPayDetail);
    List<Integer> payDetailIds = Lists.newArrayList();
    if (StringHelper.isNotEmpty(payDetailRecords)) {
      //  撤销账单收费方式明细
      revokeBillPaymentDetail(billPayRecordId, userId, name, payDetailRecords, payDetailIds);
    }

    BillExceptionHandleRecord handleRecord = new BillExceptionHandleRecord();
    handleRecord.setOrgId(orgId);
    handleRecord.setPatientId(patientId);
    handleRecord.setTreatmentRecordId(treatmentRecordId);
    handleRecord.setHandledRecordId(billPayRecordId);
    handleRecord.setOperateType((byte) 1);
    handleRecord.setCrtId(userId);
    handleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(handleRecord);

    Joiner joiner = Joiner.on(",");
    Integer handleRecordId = handleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(handleRecordId);
    handleDetailRecord.setAssociateRecordId(billPayRecordId);
    handleDetailRecord.setRemark(joiner.join(payDetailIds));
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);
    // 发送消息同步中间表账单相关数据
    if (result > 0) {
      // 删除账单收费记录
      rabbitMqServiceFeign.sendMessage(billPayRecordId, 2, BaseBillPay);
    }
    redisUtils.delete(redisKey);
  }

  /**
   * 撤销账单收费方式明细
   *
   * @param billPayRecordId 账单收费记录ID
   * @param userId 用户ID
   * @param name 用户姓名
   * @param payDetailRecords 支付方式列表
   * @param payDetailIds 支付方式ID列表
   */
  private void revokeBillPaymentDetail(
      Integer billPayRecordId,
      Integer userId,
      String name,
      List<BillPayDetailRecord> payDetailRecords,
      List<Integer> payDetailIds) {
    // 会员卡、预付款需退还到原先账号
    payDetailRecords.forEach(
        detailRecord -> {
          payDetailIds.add(detailRecord.getId());
          Byte type = detailRecord.getType();
          String cardNum = detailRecord.getRemark();
          switch (type) {
              // 预付款
            case 0:
              PrepaidRevocationFeeModel prepaidRevocationFeeModel = new PrepaidRevocationFeeModel();
              prepaidRevocationFeeModel.setPrepaidCard(cardNum);
              prepaidRevocationFeeModel.setBillPayRecordId(billPayRecordId);
              prepaidRevocationFeeModel.setRemarks("撤销预付款收费");
              patientCentralServiceFeign.revocationFee(prepaidRevocationFeeModel);
              break;
              // 会员卡
            case 1:
              MemberRevocationFeeModel memberRevocationFeeModel = new MemberRevocationFeeModel();
              memberRevocationFeeModel.setMemberId(cardNum);
              memberRevocationFeeModel.setBillPayRecordId(billPayRecordId);
              memberRevocationFeeModel.setRemarks("撤销会员卡收费");
              patientCentralServiceFeign.revocationFee(memberRevocationFeeModel);
              break;
            default:
              break;
          }
          detailRecord.setInservice(false);
          detailRecord.setUpdId(userId);
          detailRecord.setUpdName(name);
          billPayDetailRecordBiz.updateSelectiveById(detailRecord);
        });
  }

  /**
   * 校验是否允许撤销收费记录
   *
   * @param billPayRecordId 收费记录ID
   */
  private BillPayRecord checkRevokeBillPayRecordWhetherAllow(Integer billPayRecordId) {
    String str = redisUtils.get(LOCK_BILL_PAY_RECORD + billPayRecordId);
    if (StringHelper.isNotBlank(str)) {
      throw new ClientServiceException("收费记录撤销失败，当前收费记录正在被操作，请稍后再试！", QUERY_RESULT_INVALID);
    }

    BillPayRecord billPayRecord = mapper.selectByPrimaryKey(billPayRecordId);
    if (null == billPayRecord) {
      throw new ClientServiceException("撤销收费失败，收费记录不存在！", QUERY_RESULT_INVALID);
    }

    Boolean inservice = billPayRecord.getInservice();
    if (!inservice) {
      throw new ClientServiceException("撤销收费失败，该条记录已被撤销！", QUERY_RESULT_INVALID);
    }

    Integer orderRecordId = billPayRecord.getOrderRecordId();
    BillPayRecord entity = new BillPayRecord();
    entity.setOrderRecordId(orderRecordId);
    entity.setInservice(true);
    List<BillPayRecord> billPayRecords = mapper.select(entity);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      Date billPayRecordCrtTime = billPayRecord.getCrtTime();
      billPayRecords.forEach(
          payRecord -> {
            Integer payRecordId = payRecord.getId();
            if (!billPayRecordId.equals(payRecordId)) {
              Date payRecordCrtTime = payRecord.getCrtTime();
              if (payRecordCrtTime.after(billPayRecordCrtTime)) {
                throw new ClientServiceException("撤销收费失败，请从时间最新的一条收费开始撤销！", PARAMETERS_IS_ILLEGAL);
              }
            }
          });
    }
    return billPayRecord;
  }

  /**
   * 根据条件导出门诊本月收欠费（使用优惠）账单列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillCollectionDebt(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    List<CurrentMonthBillCollectionDebtVO> resultList =
        mapper.selectCurrentMonthBillCollectionDebtList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (CurrentMonthBillCollectionDebtVO vo : resultList) {
        String billDate = vo.getBillDate();
        vo.setCurrentMonthBill(
            query.getCurrentMonth().equals(new DateTime(billDate).toString("yyyy-MM"))
                ? "当月账单"
                : "非当月账单");
        Integer patientId = vo.getPatientId();
        PatientBaseInfo patientBaseInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
        if (patientBaseInfo != null) {
          vo.setPatientName(patientBaseInfo.getName());
          vo.setPatientMobile(patientBaseInfo.getMobile());
        }
        Integer payeeId = vo.getPayeeId();
        SysEmployee payer = systemServiceFeign.findSysEmployeeById(payeeId);
        if (payer != null) {
          vo.setPayeeName(payer.getName());
        }
        Integer regDentistId = vo.getRegDentistId();
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(regDentistId);
        if (employee != null) {
          vo.setRegDentistName(employee.getName());
        }
      }
    }
    String fileName = "当月收欠费（使用优惠）账单记录";
    OrganizationInfo organization = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (organization != null) {
      fileName = organization.getAbbreviation() + query.getCurrentMonth() + fileName;
    }
    ExcelUtil<CurrentMonthBillCollectionDebtVO> excelUtil =
        new ExcelUtil<>(CurrentMonthBillCollectionDebtVO.class);
    excelUtil.exportExcel(response, resultList, "门诊当月收欠费（使用优惠）账单记录", fileName);
  }

  /**
   * 根据条件导出本门诊本月收费记录列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthBillPayRecord(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    String fileName = query.getCurrentMonth() + "账单当月收费记录";
    Integer orgId = query.getOrgId();
    OrganizationInfo organization = systemServiceFeign.findOrgInfoByOrgId(orgId);
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    List<CurrentMonthBillPayRecordVO> resultList = mapper.selectCurrentMonthBillPayRecord(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (CurrentMonthBillPayRecordVO vo : resultList) {
        PatientBaseInfo patientInfo =
            patientCentralServiceFeign.findPatientInfoById(vo.getPatientId());
        if (null != patientInfo) {
          vo.setPatientName(patientInfo.getName());
          vo.setPatientMobile(patientInfo.getMobile());
        }
        Integer payeeId = vo.getPayeeId();
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(payeeId);
        if (null != employee) {
          vo.setPayeeName(employee.getName());
        }
        Integer regDentistId = vo.getRegDentistId();
        SysEmployee employee1 = systemServiceFeign.findSysEmployeeById(regDentistId);
        if (employee1 != null) {
          vo.setRegDentistName(employee1.getName());
        }
      }
    }
    ExcelUtil<CurrentMonthBillPayRecordVO> excelUtil =
        new ExcelUtil<>(CurrentMonthBillPayRecordVO.class);
    excelUtil.exportExcel(response, resultList, "门诊当月收费记录", fileName);
  }


  /**
   * 有效账单中收费在查询日期之前，且不包含查询日期之前的撤销收费的收费记录
   *
   * @param query 查询条件
   * @return list
   */
  public List<BillRestReceivableAmountVO> findFollowUpBillReceivedList(
          BillOfReceivableQuery query) {
    return mapper.selectFollowUpBillReceivedList(query);
  }

  /**
   * 撤销收费记录ID
   *
   * @param query
   * @return
   */
  public List<Integer> selectRevokePayIds(StatementStatisticQuery query) {
    return mapper.selectRevokePayIds(query);
  }

  /**
   * 调整收费方式
   *
   * @param query
   * @return
   */
  public List<Integer> selectAdjustPayIds(StatementStatisticQuery query) {
    return mapper.selectAdjustPayIds(query);
  }
}
