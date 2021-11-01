package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.query.*;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.BillExceptionHandleRecord;
import com.yunya.modules.treatment.mapper.BillExceptionHandleRecordMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 账单异常处理业务层
 *
 * @author: chow
 * @date: 2020/9/24 20:34
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillExceptionHandleRecordBiz
    extends BaseBiz<BillExceptionHandleRecordMapper, BillExceptionHandleRecord> {

  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 账单异常处理详情 */
  @Autowired private BillExceptionHandleDetailRecordBiz billExceptionHandleDetailRecordBiz;
  /** 账单退费 */
  @Autowired private BillRefundRecordBiz billRefundRecordBiz;

  /**
   * 根据账单异常处理记录查询处理详情
   *
   * @param billExceptionHandleRecordId 账单异常处理记录ID
   * @return map
   */
  public Map<String, Object> findBillHandleDetail(Integer billExceptionHandleRecordId) {
    BillExceptionHandleRecord handleRecord = mapper.selectByPrimaryKey(billExceptionHandleRecordId);
    if (null == handleRecord) {
      throw new ClientServiceException("请选择正确的账单异常处理记录！", PARAMETERS_IS_ILLEGAL);
    }
    // 被处理数据ID
    Integer handledRecordId = handleRecord.getHandledRecordId();
    // 下一条异常处理记录ID
    Integer nextExceptionHandleRecordId = mapper.selectNextId(billExceptionHandleRecordId);
    // 异常处理类型
    Byte operateType = handleRecord.getOperateType();
    Map<String, Object> resultMap = new HashMap<>(16);
    switch (operateType) {
        // 调整入账方式
      case 0:
        BillPaymentAdjustDetailVO billPaymentAdjustDetail =
            billExceptionHandleDetailRecordBiz.findBillPaymentAdjustDetail(
                handledRecordId, billExceptionHandleRecordId, nextExceptionHandleRecordId);
        resultMap.put("billPaymentAdjustDetail", billPaymentAdjustDetail);
        break;
        // 账单撤销
      case 1:
        BillPayRecordVO revokePayRecord =
            billExceptionHandleDetailRecordBiz.findBillRevokePayRecord(billExceptionHandleRecordId);
        resultMap.put("revokeBillPayRecord", revokePayRecord);
        break;
        // 账单调整
      case 2:
        resultMap =
            billExceptionHandleDetailRecordBiz.findBillOrderDetailAdjustDetails(
                handledRecordId, billExceptionHandleRecordId, nextExceptionHandleRecordId);
        break;
        // 账单退费
      case 3:
        resultMap = billRefundRecordBiz.findBillRefundRecordInfo(billExceptionHandleRecordId);
        break;
      default:
        break;
    }
    return resultMap;
  }

  /**
   * 根据条件查询账单调整记录列表
   *
   * @param query 查询条件
   * @return List<BillOfAdjustRecordVO>
   */
  public List<BillOfAdjustRecordVO> findBillAdjustRecord(BillAdjustRecordQuery query) {
    List<BillOfAdjustRecordVO> resultList = mapper.selectBillAdjustRecord(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            Integer patientId = vo.getPatientId();
            PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
            if (null != patientInfo) {
              vo.setPatientName(patientInfo.getName());
              vo.setPinyinName(patientInfo.getPinyinName());
              vo.setMobile(patientInfo.getMobile());
              vo.setGender(patientInfo.getGender());
            }
            Integer regDentistId = vo.getRegDentistId();
            SysEmployee dentist = systemServiceFeign.findSysEmployeeById(regDentistId);
            if (null != dentist) {
              vo.setRegDentistName(dentist.getName());
            }
            Integer adjustOperatorId = vo.getAdjustOperatorId();
            SysEmployee sysEmployee = systemServiceFeign.findSysEmployeeById(adjustOperatorId);
            if (null != sysEmployee) {
              vo.setAdjustOperatorName(sysEmployee.getName());
            }
          });
      String keyWord = query.getKeyWord();
      if (StringHelper.isNotBlank(keyWord)) {
        List<BillOfAdjustRecordVO> billOfAdjustRecords = Lists.newArrayList();
        Pattern pattern = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
        resultList.forEach(
            vo -> {
              Matcher matcherName = pattern.matcher(vo.getPatientName());
              Matcher matcherPinyinName = pattern.matcher(vo.getPinyinName());
              Matcher matcherMobile = pattern.matcher(vo.getMobile());
              if (matcherName.find() || matcherMobile.find() || matcherPinyinName.find()) {
                billOfAdjustRecords.add(vo);
              }
            });
        return billOfAdjustRecords;
      }
    }
    return resultList;
  }

  /**
   * 根据条件导出账单调整记录列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillAdjustRecord(HttpServletResponse response, BillAdjustRecordQuery query)
      throws IOException {
    List<BillOfAdjustRecordVO> resultList = findBillAdjustRecord(query);
    ExcelUtil<BillOfAdjustRecordVO> excelUtil = new ExcelUtil<>(BillOfAdjustRecordVO.class);
    String fileName = "账单调整记录";
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (null != orgInfo) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              orgInfo.getAbbreviation(),
              query.getAdjustStartDate(),
              query.getAdjustEndDate(),
              fileName);
    }
    excelUtil.exportExcel(response, resultList, "账单调整记录表", fileName);
  }

  /**
   * 根据条件查询账单收费撤销记录
   *
   * @param query 查询条件
   * @return
   */
  public List<BillOfTollRevokeRecordVO> findBillRevokeRecordList(BillTollRevokeRecordQuery query) {
    List<BillOfTollRevokeRecordVO> resultList = mapper.selectBillRevokeRecordList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            Integer patientId = vo.getPatientId();
            PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
            if (null != patientInfo) {
              vo.setPatientName(patientInfo.getName());
              vo.setPinyinName(patientInfo.getPinyinName());
              vo.setMobile(patientInfo.getMobile());
              vo.setGender(patientInfo.getGender());
            }
            Integer regDentistId = vo.getRegDentistId();
            SysEmployee dentist = systemServiceFeign.findSysEmployeeById(regDentistId);
            if (null != dentist) {
              vo.setRegDentistName(dentist.getName());
            }
          });
      String keyWord = query.getKeyWord();
      if (StringHelper.isNotBlank(keyWord)) {
        List<BillOfTollRevokeRecordVO> billOfRevokeRecords = Lists.newArrayList();
        Pattern pattern = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
        resultList.forEach(
            vo -> {
              Matcher matcherName = pattern.matcher(vo.getPatientName());
              Matcher matcherPinyinName = pattern.matcher(vo.getPinyinName());
              Matcher matcherMobile = pattern.matcher(vo.getMobile());
              if (matcherName.find() || matcherMobile.find() || matcherPinyinName.find()) {
                billOfRevokeRecords.add(vo);
              }
            });
        return billOfRevokeRecords;
      }
    }
    return resultList;
  }

  /**
   * 导出账单收费撤销记录
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillTollRevokeRecord(
      HttpServletResponse response, BillTollRevokeRecordQuery query) throws IOException {
    List<BillOfTollRevokeRecordVO> resultList = findBillRevokeRecordList(query);
    ExcelUtil<BillOfTollRevokeRecordVO> excelUtil = new ExcelUtil<>(BillOfTollRevokeRecordVO.class);
    String fileName = "账单收费撤销记录";
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (null != orgInfo) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              orgInfo.getAbbreviation(),
              query.getRevokeStartDate(),
              query.getRevokeEndDate(),
              fileName);
    }
    excelUtil.exportExcel(response, resultList, "账单收费撤销记录表", fileName);
  }

  /**
   * 根据条件查询账单退费记录信息列表
   *
   * @param query 查询条件
   * @return List<BillOfRefundRecordInfoVO>
   */
  public List<BillOfRefundRecordVO> findBillRefundRecordList(BillRefundRecordQuery query) {
    List<BillOfRefundRecordVO> resultList = mapper.selectBillRefundRecordList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            Integer patientId = vo.getPatientId();
            PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
            if (null != patientInfo) {
              vo.setPatientName(patientInfo.getName());
              vo.setPinyinName(patientInfo.getPinyinName());
              vo.setMobile(patientInfo.getMobile());
              vo.setGender(patientInfo.getGender());
            }
            Integer regDentistId = vo.getRegDentistId();
            SysEmployee dentist = systemServiceFeign.findSysEmployeeById(regDentistId);
            if (null != dentist) {
              vo.setRegDentistName(dentist.getName());
            }
          });
      String keyWord = query.getKeyWord();
      if (StringHelper.isNotBlank(keyWord)) {
        List<BillOfRefundRecordVO> billOfRefundRecords = Lists.newArrayList();
        Pattern pattern = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
        resultList.forEach(
            vo -> {
              Matcher matcherName = pattern.matcher(vo.getPatientName());
              Matcher matcherPinyinName = pattern.matcher(vo.getPinyinName());
              Matcher matcherMobile = pattern.matcher(vo.getMobile());
              if (matcherName.find() || matcherMobile.find() || matcherPinyinName.find()) {
                billOfRefundRecords.add(vo);
              }
            });
        return billOfRefundRecords;
      }
    }
    return resultList;
  }

  /**
   * 根据条件导出账单退费记录
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillRefundRecord(HttpServletResponse response, BillRefundRecordQuery query)
      throws IOException {
    List<BillOfRefundRecordVO> resultList = findBillRefundRecordList(query);
    ExcelUtil<BillOfRefundRecordVO> excelUtil = new ExcelUtil<>(BillOfRefundRecordVO.class);
    String fileName = "账单退费记录";
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (null != orgInfo) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              orgInfo.getAbbreviation(),
              query.getRefundStartDate(),
              query.getRefundEndDate(),
              fileName);
    }
    excelUtil.exportExcel(response, resultList, "账单退费记录表", fileName);
  }

  /**
   * 根据条件查询调整入账方式记录
   *
   * @param query 查询条件
   * @return List<BillOfPayRecordAdjustVO>
   */
  public List<BillOfPayRecordAdjustVO> findBillPayAdjustRecordList(BillPayRecordAdjustQuery query) {
    List<BillOfPayRecordAdjustVO> resultList = mapper.selectBillPayAdjustRecordList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            Integer patientId = vo.getPatientId();
            PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
            if (null != patientInfo) {
              vo.setPatientName(patientInfo.getName());
              vo.setPinyinName(patientInfo.getPinyinName());
              vo.setMobile(patientInfo.getMobile());
              vo.setGender(patientInfo.getGender());
            }
            Integer regDentistId = vo.getRegDentistId();
            SysEmployee dentist = systemServiceFeign.findSysEmployeeById(regDentistId);
            if (null != dentist) {
              vo.setRegDentistName(dentist.getName());
            }
          });
      String keyWord = query.getKeyWord();
      if (StringHelper.isNotBlank(keyWord)) {
        List<BillOfPayRecordAdjustVO> billOfRevokeRecords = Lists.newArrayList();
        Pattern pattern = Pattern.compile(keyWord, Pattern.CASE_INSENSITIVE);
        resultList.forEach(
            vo -> {
              Matcher matcherName = pattern.matcher(vo.getPatientName());
              Matcher matcherPinyinName = pattern.matcher(vo.getPinyinName());
              Matcher matcherMobile = pattern.matcher(vo.getMobile());
              if (matcherName.find() || matcherMobile.find() || matcherPinyinName.find()) {
                billOfRevokeRecords.add(vo);
              }
            });
        return billOfRevokeRecords;
      }
    }
    return resultList;
  }

  /**
   * 根据条件导出账单收费调整记录列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillPayAdjustRecord(
      HttpServletResponse response, BillPayRecordAdjustQuery query) throws IOException {
    List<BillOfPayRecordAdjustVO> resultList = findBillPayAdjustRecordList(query);
    ExcelUtil<BillOfPayRecordAdjustVO> excelUtil = new ExcelUtil<>(BillOfPayRecordAdjustVO.class);
    String fileName = "调整入账方式记录";
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (null != orgInfo) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              orgInfo.getAbbreviation(),
              query.getAdjustStartDate(),
              query.getAdjustEndDate(),
              fileName);
    }
    excelUtil.exportExcel(response, resultList, "调整入账方式记录表", fileName);
  }

  /**
   * 根据条件导出门诊当月调整账单列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthAdjustBill(
      HttpServletResponse response, CurrentMonthBillAdjustQuery query) throws IOException {
    ExcelUtil<BillAdjustRecordVO> excelUtil = new ExcelUtil<>(BillAdjustRecordVO.class);
    List<BillAdjustRecordVO> resultList = mapper.selectCurrentMonthAdjustBill(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (BillAdjustRecordVO vo : resultList) {
        Integer patientId = vo.getPatientId();
        PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
        if (null != patientInfo) {
          vo.setPatientName(patientInfo.getName());
          vo.setPatientMobile(patientInfo.getMobile());
        }
        Integer regDentistId = vo.getRegDentistId();
        SysEmployee dentist = systemServiceFeign.findSysEmployeeById(regDentistId);
        if (null != dentist) {
          vo.setRegDentistName(dentist.getName());
        }
        String currentMonth =
            DateUtil.parseDateToStr("yyyy-MM", new Date(System.currentTimeMillis()));
        String billDate = new DateTime(vo.getBillDate()).toString("yyyy-MM");
        vo.setCurrentMonthBill(currentMonth.equals(billDate) ? "当月账单" : "非当月账单");
      }
    }
    String fileName = "门诊当月调整账单记录";
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (null != orgInfo) {
      fileName =
          MessageFormat.format(
              "{0}{1}{3}", orgInfo.getAbbreviation(), query.getCurrentMonth(), fileName);
    }
    excelUtil.exportExcel(response, resultList, "门诊当月调整账单记录", fileName);
  }

  /**
   * 根据条件查询门诊当前月账单收费撤销记录
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthBillRevokePayRecord(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    List<BillRevokePayRecordVO> resultList = mapper.selectCurrentMonthBillRevokePayRecord(query);
    if (StringHelper.isNotEmpty(resultList)) {
      String currentMonth =
          DateUtil.parseObjectToStr("yyyy-MM", new Date(System.currentTimeMillis()));
      for (BillRevokePayRecordVO vo : resultList) {
        Integer patientId = vo.getPatientId();
        PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
        if (null != patientInfo) {
          vo.setPatientName(patientInfo.getName());
          vo.setPatientMobile(patientInfo.getMobile());
        }
        Integer regDentistId = vo.getRegDentistId();
        SysEmployee dentist = systemServiceFeign.findSysEmployeeById(regDentistId);
        if (null != dentist) {
          vo.setRegDentistName(dentist.getName());
        }
        String billDate = new DateTime(vo.getBillDate()).toString("yyyy-MM");
        vo.setCurrentMonthBill(currentMonth.equals(billDate) ? "当前月账单" : "非当前月账单");
      }
    }
    ExcelUtil<BillRevokePayRecordVO> excelUtil = new ExcelUtil<>(BillRevokePayRecordVO.class);
    String fileName = "门诊当月撤销收费记录";
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
    if (null != orgInfo) {
      fileName =
          MessageFormat.format(
              "{0}{1}{3}", orgInfo.getAbbreviation(), query.getCurrentMonth(), fileName);
    }
    excelUtil.exportExcel(response, resultList, "门诊当月撤销收费记录", fileName);
  }
}
