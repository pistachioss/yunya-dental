package com.yunya.report.ultimate.biz;

import com.yunya.feign.patient_central.domain.query.PatientOriginEmployeeQuery;
import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginEmployeeVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedTotalWorkloadVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.feign.report.domain.vo.BillIdVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BasePatientOriginLog;
import com.yunya.report.ultimate.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/19 16:39
 * @description: 公司端-人力资源菜单内-员工推荐
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientOriginRelationsBiz
    extends BaseBiz<BasePatientOriginLogMapper, BasePatientOriginLog> {

  @Resource private BaseBillPayMapper baseBillPayMapper;

  @Resource private BaseBillMapper baseBillMapper;

  @Resource private BaseRefundMapper baseRefundMapper;

  @Resource private BaseBillDetailMapper baseBillDetailMapper;

  @Resource private PatientOriginActivityRelationsBiz activityRelationsBiz;



  /**
   * 员工推荐信息列表
   *
   * @param query 条件
   * @return 员工推荐信息列表返回
   */
  public List<PatientOriginEmployeeVo> finleEmployeeReferral(PatientOriginEmployeeQuery query) {
    return combinationEmployeeReferral(query);
  }


  /**
   * 组装返回集合
   *
   * @param query 条件
   * @return 推荐人推荐信息
   */
  private List<PatientOriginEmployeeVo> combinationEmployeeReferral(
      PatientOriginEmployeeQuery query) {
    // 查询推荐人信息以及推荐数量
    List<PatientOriginEmployeeVo> patientOriginEmployeeVoList = mapper.findEmployeeVoLists(query);
    if (!StringHelper.isEmpty(patientOriginEmployeeVoList)) {
      // 查询所有符合条件的订单号
      List<BillIdVo> billIdList = baseBillMapper.findBillIdList(query, patientOriginEmployeeVoList);
      if (patientOriginEmployeeVoList != null) {
        // 获取已收工作量合计
        getReceivedTotalWorkload(patientOriginEmployeeVoList, query, true, billIdList);
        // 获取其中免单支付工作量合计
        getReceivedTotalWorkload(patientOriginEmployeeVoList, query, false, billIdList);
        // 获取退费金额合计
        getTotalRefundAmount(patientOriginEmployeeVoList, query);
        // 获取补入工作量合计
        getMakeUpWorkload(patientOriginEmployeeVoList, query);
      }
      return patientOriginEmployeeVoList;
    }
    return null;
  }

  /**
   * 获取补入工作量合计
   *
   * @param patientOriginEmployeeVoList 查询推荐人信息以及推荐数量
   * @param query 条件
   */
  private void getMakeUpWorkload(
      List<PatientOriginEmployeeVo> patientOriginEmployeeVoList, PatientOriginEmployeeQuery query) {
    for (PatientOriginEmployeeVo patientOriginEmployee : patientOriginEmployeeVoList) {
      BigDecimal makeUpWorkload =
          baseBillDetailMapper.findMakeUpWorkload(
              patientOriginEmployee.getOriginId(), query.getStartDate(), query.getEndDate(), 1);
      if (makeUpWorkload == null) {
        patientOriginEmployee.setMakeUpWorkload(new BigDecimal(0));
      } else {
        patientOriginEmployee.setMakeUpWorkload(makeUpWorkload);
      }
    }
  }

  /**
   * 获取退费金额合计
   *
   * @param patientOriginEmployeeVoList 查询推荐人信息以及推荐数量
   * @param query 条件
   */
  private void getTotalRefundAmount(
      List<PatientOriginEmployeeVo> patientOriginEmployeeVoList, PatientOriginEmployeeQuery query) {
    if (patientOriginEmployeeVoList != null) {
      for (PatientOriginEmployeeVo patientOriginEmployeeVo : patientOriginEmployeeVoList) {
        BigDecimal totalRefundAmount = baseRefundMapper.findRefundAmount(patientOriginEmployeeVo.getOriginId(), query.getStartDate(), query.getEndDate(), 1);
        if (totalRefundAmount != null) {
          patientOriginEmployeeVo.setTotalRefundAmount(totalRefundAmount);
        }
      }
    }
  }

  /**
   * 获取已收工作量
   *
   * @param patientOriginEmployeeVoList 查询推荐人信息以及推荐数量
   * @param query 条件
   */
  private void getReceivedTotalWorkload(List<PatientOriginEmployeeVo> patientOriginEmployeeVoList, PatientOriginEmployeeQuery query, Boolean type,List<BillIdVo> BillIdVoList) {
    // 获取全部订单记录Map
    Map<Integer, List<BaseBillPay>> baseBillPayInfoMap = getBaseBillPayInfoMap(query, type,BillIdVoList);
    if (patientOriginEmployeeVoList.size() > 0) {
      for (PatientOriginEmployeeVo patientOriginEmployeeVo : patientOriginEmployeeVoList) {
          // 根据推荐人id获取订单号集合
          List<Integer> billIds = getBillIds(patientOriginEmployeeVo.getOriginId(),BillIdVoList);
          if (billIds != null && billIds.size() > 0){
            // 根据订单id获取订单明细
            List<ReceivedTotalWorkloadVo> receivedTotalWorkloadVoList = mapper.findReceivedTotalWorkload(billIds);
            if (receivedTotalWorkloadVoList != null) {
              for (ReceivedTotalWorkloadVo receivedTotalWorkloadVo : receivedTotalWorkloadVoList) {
                // 根据订单号 获取订单支付记录
                List<BaseBillPay> baseBillPays = baseBillPayInfoMap.get(receivedTotalWorkloadVo.getBillId());
                if (baseBillPays != null) {
                  for (BaseBillPay baseBillPay : baseBillPays) {
                    BigDecimal multiply = receivedTotalWorkloadVo.getOrderWorkload().multiply(baseBillPay.getReceivedAmount());
                    if (multiply == null) {
                      multiply = new BigDecimal(0);
                    }
                    // 判断是全部工作量 还是 免单支付工作量
                    if (type) {
                      patientOriginEmployeeVo.setReceivedTotalWorkload(patientOriginEmployeeVo.getReceivedTotalWorkload().add(multiply.setScale(2,BigDecimal.ROUND_HALF_UP)));
                    } else {
                      patientOriginEmployeeVo.setFreeTotalWorkload(patientOriginEmployeeVo.getFreeTotalWorkload().add(multiply.setScale(2,BigDecimal.ROUND_HALF_UP)));
                    }
                  }
                }
              }
            }
          }
      }
    }
  }

  /**
   * 根据推荐人获取所属订单id
   * @param originId 推荐人id
   * @param BillIdVoList 订单id
   * @return id集合
   */
  public List<Integer> getBillIds(Integer originId,List<BillIdVo> BillIdVoList){
    List<Integer> BillIdList = new ArrayList<>();
    if (originId != null && BillIdVoList != null){
      for (BillIdVo billIdVo : BillIdVoList ) {
          if (originId.equals(billIdVo.getOriginId())){
            BillIdList.add(billIdVo.getBillId());
          }
      }
    }
    return BillIdList;
  }

  /**
   * 获取订单支付记录
   *
   * @param query 条件
   * @return 订单支付记录 key订单号 v订单支付记录集合
   */
  private Map<Integer, List<BaseBillPay>> getBaseBillPayInfoMap(PatientOriginEmployeeQuery query, Boolean type,List<BillIdVo> billIdList) {
    List<BaseBillPay> baseBillPayList;
    Map<Integer, List<BaseBillPay>> billPayMap = new HashMap<>();
    if (type) {
      // 根据订单号和支付时间查询订单支付记录集合
      baseBillPayList = baseBillPayMapper.findBaseBillPayInfoList(billIdList, query.getStartDate(), query.getEndDate(), null);
    } else {
      // 其中免单支付工作量合计
      List<Integer> itemIds = new ArrayList<Integer>() {{ add(23);add(26); }};
      baseBillPayList = baseBillPayMapper.findMdBaseBillPayInfoList(billIdList, query.getStartDate(), query.getEndDate(), itemIds);
    }
    // k 订单id v 订单记录
    for (BillIdVo billIdVo : billIdList) {
      List<BaseBillPay> baseBillPayVo = new ArrayList<>();
      for (BaseBillPay baseBillPay : baseBillPayList) {
        if (baseBillPay.getBillId().equals(billIdVo.getBillId())) {
          baseBillPayVo.add(baseBillPay);
        }
      }
      billPayMap.put(billIdVo.getBillId(), baseBillPayVo);
    }
    return billPayMap;
  }

  /**
   * 导出员工推荐记录列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportEmployeeReferralList(
      HttpServletResponse response, PatientOriginEmployeeQuery query) throws IOException {
    List<PatientOriginEmployeeVo> patientOriginEmployeeVoLists = combinationEmployeeReferral(query);
    ExcelUtil<PatientOriginEmployeeVo> excelUtil = new ExcelUtil<>(PatientOriginEmployeeVo.class);
    excelUtil.exportExcel(response, patientOriginEmployeeVoLists, "员工推荐明细", "员工推荐明细");
  }

  /**
   * 已收工作量明细列表分页列表查询
   *
   * @param query 条件
   * @return 已收工作量明细列表分页列表信息
   */
  public List<ReceivedWorkloadDetailsVo> findEreceiverkLoad(ReceiverkLoadQuery query) throws ParseException {
    // 1.已收 2.免单 3.退费 4.补入
    switch (query.getType()) {
      case 1:
        return receivedDetail(query, true,1);
      case 2:
        return receivedDetail(query, false,1);
      case 3:
        return refundDetail(query,1);
      case 4:
        return makeUpDetail(query,1);
      default:
        break;
    }
    return null;
  }

  /**
   * 补入工作量明细
   *
   * @param query 条件
   * @return 补入工作量明细
   */
  private List<ReceivedWorkloadDetailsVo> makeUpDetail(ReceiverkLoadQuery query,Integer originType) throws ParseException {
    List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList =
        baseBillMapper.selectMakeUpDetail(query,originType);
    if (receivedWorkloadDetailsVoList != null) {
      for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo : receivedWorkloadDetailsVoList) {
        // 判断关联时间是否大于初诊时间
        receivedWorkloadDetailsVo.setIsChange(activityRelationsBiz.getIsChange(receivedWorkloadDetailsVo));
      }
      return receivedWorkloadDetailsVoList;
    }
    return null;
  }

  /**
   * 退费金额明细
   *
   * @param query 条件
   * @return 退费金额明细
   */
  private List<ReceivedWorkloadDetailsVo> refundDetail(ReceiverkLoadQuery query,Integer originType) throws ParseException {
    List<ReceivedWorkloadDetailsVo> refundDetailList = new ArrayList<>();
    List<Integer> refundIdList = baseRefundMapper.selectFundBillIdList(query);
    if (refundIdList != null) {
      for (Integer refundId : refundIdList) {
        List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList =
            baseRefundMapper.selectRefundDetail(refundId, query.getOriginId(), originType);
        if (receivedWorkloadDetailsVoList != null) {
          for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo :receivedWorkloadDetailsVoList ) {
            // 判断关联时间是否大于初诊时间
            receivedWorkloadDetailsVo.setIsChange(activityRelationsBiz.getIsChange(receivedWorkloadDetailsVo));
          }
          refundDetailList.addAll(receivedWorkloadDetailsVoList);
        }
      }
    }
    return refundDetailList;
  }

  /**
   * 已收明细 / 免单明细
   *
   * @param query 条件
   * @param isFreePayment 是否免单
   * @return 明细
   */
  public List<ReceivedWorkloadDetailsVo> receivedDetail(
      ReceiverkLoadQuery query, Boolean isFreePayment,Integer originType) throws ParseException {
    List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsListVo = new ArrayList<>();
    List<Integer> baseBillIdList;
    if (isFreePayment) {
      // 订单id List
      baseBillIdList = baseBillMapper.findBaseBillIdList(query, null,originType);
    } else {
      // 其中免单支付 订单id List
      List<Integer> itemIds = new ArrayList<Integer>() {{ add(23);add(26); }};
      baseBillIdList = baseBillMapper.findBaseBillIdList(query, itemIds,originType);
    }
    if (baseBillIdList != null) {
      List<BaseBillPay> baseBillPayList;
      if (isFreePayment) {
        // 查询所有支付方式
        baseBillPayList = baseBillPayMapper.selectBaseBillPayInfoList(baseBillIdList, query, null);
      } else {
        // 查询免单支付方式
        List<Integer> itemIds = new ArrayList<Integer>() {{ add(23);add(26); }};
        // 其中
        baseBillPayList =
            baseBillPayMapper.selectMdBaseBillPayInfoList(baseBillIdList, query, itemIds);
      }
      Map<Integer, List<BaseBillPay>> baseBillPayMap = getBaseBillPayList(baseBillIdList,baseBillPayList);
      // 订单支付明细
      for (Integer billId : baseBillIdList) {
        // 订单项目明细
        List<ReceivedWorkloadDetailsVo> baseBillDetailBizVos = baseBillDetailMapper.selectEreceiverkLoad(billId, query.getOriginId());
        if (baseBillDetailBizVos != null) {
          for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo : baseBillDetailBizVos) {
            // 判断推荐时间是否大于初诊时间
            receivedWorkloadDetailsVo.setIsChange(activityRelationsBiz.getIsChange(receivedWorkloadDetailsVo));
            // 通过项目订单id 获取订单支付记录
            List<BaseBillPay> billPayList = baseBillPayMap.get(receivedWorkloadDetailsVo.getBillId());
            if (StringHelper.isNotNull(billPayList)){
              // 循环乘以订单记录中本次支付金额
              for (BaseBillPay baseBillPay: billPayList) {
                ReceivedWorkloadDetailsVo receivedWorkloadDetails = new ReceivedWorkloadDetailsVo();
                BeanUtils.copyProperties(receivedWorkloadDetailsVo,receivedWorkloadDetails);
                BigDecimal multiply = receivedWorkloadDetailsVo.getWorkload().multiply(baseBillPay.getReceivedAmount());
                receivedWorkloadDetails.setWorkload(multiply.setScale(2,BigDecimal.ROUND_HALF_UP));
                // 加入到结果返回集合中
                receivedWorkloadDetailsListVo.add(receivedWorkloadDetails);
              }
            }
          }
        }
      }
    }
    return receivedWorkloadDetailsListVo;
  }

  /**
   * 封装map
   * @param baseBillIdList 订单idList
   * @param baseBillPayList 订单支付记录List
   * @return map key订单id v订单支付记录
   */
  public Map<Integer,List<BaseBillPay>> getBaseBillPayList(List<Integer> baseBillIdList,List<BaseBillPay> baseBillPayList){

    Map<Integer,List<BaseBillPay>> map = new HashMap();
    if (baseBillIdList != null && baseBillPayList != null){
      for (Integer billId : baseBillIdList) {
        List<BaseBillPay> baseBillPayVoList = new ArrayList<>();
        for (BaseBillPay baseBillPay: baseBillPayList) {
            if (billId.equals(baseBillPay.getBillId())){
              baseBillPayVoList.add(baseBillPay);
            }
        }
        map.put(billId,baseBillPayVoList);
      }
    }
    return map;
  }

  /**
   * 员工推荐-各项明细列表-导出
   * @param response 请求
   * @param query 条件
   */
  public void exportWorkloadBreakdownList(HttpServletResponse response, ReceiverkLoadQuery query) throws ParseException, IOException {
    ExcelUtil<ReceivedWorkloadDetailsVo> excelUtil = null;
    // 1.已收 2.免单 3.退费 4.补入
    switch (query.getType()) {
      case 1:
        List<ReceivedWorkloadDetailsVo> receivedDetailsList = receivedDetail(query, true, 1);
        excelUtil = new ExcelUtil<>(ReceivedWorkloadDetailsVo.class);
        excelUtil.exportExcel(response, receivedDetailsList, "已收工作量明细", "已收工作量明细");
        break;
      case 2:
        List<ReceivedWorkloadDetailsVo> freeOrderDetails = receivedDetail(query, false, 1);
        excelUtil = new ExcelUtil<>(ReceivedWorkloadDetailsVo.class);
        excelUtil.exportExcel(response, freeOrderDetails, "免单工作量明细", "免单工作量明细");
        break;
      case 3:
        List<ReceivedWorkloadDetailsVo> refundDetails = refundDetail(query, 1);
        excelUtil = new ExcelUtil<>(ReceivedWorkloadDetailsVo.class);
        excelUtil.exportExcel(response, refundDetails, "退费明细", "退费明细");
        break;
      case 4:
        List<ReceivedWorkloadDetailsVo> supplementaryDetails = makeUpDetail(query, 1);
        excelUtil = new ExcelUtil<>(ReceivedWorkloadDetailsVo.class);
        excelUtil.exportExcel(response, supplementaryDetails, "补入工作量明细", "补入工作量明细");
        break;
      default:
        break;
    }
  }
}
