package com.yunya.report.ultimate.biz;

import com.yunya.feign.patient_central.domain.query.PatientOriginActivityQuery;
import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.patient_central.domain.vo.web.ActivityVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginActivityVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedTotalWorkloadVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.feign.report.domain.vo.BillIdVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BasePatientOrigin;
import com.yunya.models.report.BasePatientOriginLog;
import com.yunya.report.ultimate.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/19 16:39
 * @description: 公司端-人力资源菜单内-活动推荐
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientOriginActivityRelationsBiz
    extends BaseBiz<BasePatientOriginLogMapper, BasePatientOriginLog> {

  @Resource private BaseBillPayMapper baseBillPayMapper;

  @Resource private BaseBillMapper baseBillMapper;

  @Resource private BaseRefundMapper baseRefundMapper;

  @Resource private BaseBillDetailMapper baseBillDetailMapper;

  @Autowired private BasePatientOriginMapper basePatientOriginMapper;
  /**
   * 员工推荐信息列表
   *
   * @param query 条件
   * @return 员工推荐信息列表返回
   */
  public List<PatientOriginActivityVo> finleActivityReferral(PatientOriginActivityQuery query) {
    return combinationActivityReferral(query);
  }

  /**
   * 组合返回集
   *
   * @param query 条件
   * @return 推荐人推荐信息
   */
  private List<PatientOriginActivityVo> combinationActivityReferral(
          PatientOriginActivityQuery query) {
    // 查询活动信息以及推荐数量
    List<PatientOriginActivityVo> patientOriginActivityVoList = mapper.findActivityVoLists(query);
    // 查询所有符合条件的订单号
    List<BillIdVo> billIdList = baseBillMapper.findActivityBillIdList(query);
    if (patientOriginActivityVoList != null) {
      // 获取已收工作量合计
      getReceivedTotalWorkload(patientOriginActivityVoList, query, true,billIdList);
      // 获取其中免单支付工作量合计
      getReceivedTotalWorkload(patientOriginActivityVoList, query, false,billIdList);
      // 获取退费金额合计
      getTotalRefundAmount(patientOriginActivityVoList, query);
      // 获取补入工作量合计
      getMakeUpWorkload(patientOriginActivityVoList, query);
    }
    return patientOriginActivityVoList;
  }

  /**
   * 获取补入工作量合计
   *
   * @param patientOriginActivityVoList 查询推荐人信息以及推荐数量
   * @param query 条件
   */
  private void getMakeUpWorkload(
      List<PatientOriginActivityVo> patientOriginActivityVoList, PatientOriginActivityQuery query) {
    for (PatientOriginActivityVo patientOriginActivityVo : patientOriginActivityVoList) {
      BigDecimal makeUpWorkload =
          baseBillDetailMapper.findMakeUpWorkload(patientOriginActivityVo.getOriginId(), query.getStartDate(),query.getEndDate());
      if (makeUpWorkload == null) {
        patientOriginActivityVo.setMakeUpWorkload(new BigDecimal(0));
      } else {
        patientOriginActivityVo.setMakeUpWorkload(makeUpWorkload);
      }
    }
  }

  /**
   * 获取退费金额合计
   *
   * @param patientOriginActivityVoList 查询推荐人信息以及推荐数量
   * @param query 条件
   */
  private void getTotalRefundAmount(
      List<PatientOriginActivityVo> patientOriginActivityVoList, PatientOriginActivityQuery query) {
    if (patientOriginActivityVoList != null) {
      for (PatientOriginActivityVo patientOriginActivityVo : patientOriginActivityVoList) {
        BigDecimal totalRefundAmount =
            baseRefundMapper.findRefundAmount(patientOriginActivityVo.getOriginId(), query.getStartDate(),query.getEndDate());
        if (totalRefundAmount == null) {
          patientOriginActivityVo.setTotalRefundAmount(new BigDecimal(0));
        } else {
          patientOriginActivityVo.setTotalRefundAmount(totalRefundAmount);
        }
      }
    }
  }

  /**
   * 获取已收工作量
   *
   * @param patientOriginActivityVoList 查询活动信息以及推荐数量
   * @param query 条件
   */
  private void getReceivedTotalWorkload(List<PatientOriginActivityVo> patientOriginActivityVoList, PatientOriginActivityQuery query, Boolean type, List<BillIdVo> billIdList) {
    // 获取全部订单记录Map
    Map<Integer, List<BaseBillPay>> baseBillPayInfoMap = getBaseBillPayInfoMap(query, type,billIdList);
    if (patientOriginActivityVoList.size() > 0) {
      for (PatientOriginActivityVo patientOriginActivityVo : patientOriginActivityVoList) {
        // 根据推荐人id获取订单号集合
        List<Integer> billIds = getBillId(patientOriginActivityVo.getOriginId(),billIdList);
        if (billIds != null && billIds.size() > 0){
          // 根据订单id获取订单明细
          List<ReceivedTotalWorkloadVo> receivedTotalWorkloadVoList = mapper.findReceivedTotalWorkload(billIds);
          if (receivedTotalWorkloadVoList != null) {
            for (ReceivedTotalWorkloadVo receivedTotalWorkloadVo : receivedTotalWorkloadVoList) {
              // 根据订单号 获取订单支付记录
              List<BaseBillPay> baseBillPays =
                      baseBillPayInfoMap.get(receivedTotalWorkloadVo.getBillId());
              if (baseBillPays != null) {
                for (BaseBillPay baseBillPay : baseBillPays) {
                  BigDecimal multiply = receivedTotalWorkloadVo.getOrderWorkload().multiply(baseBillPay.getReceivedAmount());
                  if (multiply == null) {
                    multiply = new BigDecimal(0);
                  }
                  // 判断是全部工作量 还是 免单支付工作量
                  if (type) {
                    patientOriginActivityVo.setReceivedTotalWorkload(patientOriginActivityVo.getReceivedTotalWorkload().add(multiply));
                  } else {
                    patientOriginActivityVo.setFreeTotalWorkload(patientOriginActivityVo.getFreeTotalWorkload().add(multiply));
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
  public List<Integer> getBillId(Integer originId,List<BillIdVo> BillIdVoList){
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
  private Map<Integer, List<BaseBillPay>> getBaseBillPayInfoMap(
          PatientOriginActivityQuery query, Boolean type,List<BillIdVo> billIdList) {
    List<BaseBillPay> baseBillPayVo = null;
    List<BaseBillPay> baseBillPayList;
    Map<Integer, List<BaseBillPay>> billPayMap = new HashMap<>();
    if (type) {
      // 根据订单号和支付时间查询订单支付记录集合
      baseBillPayList = baseBillPayMapper.findBaseBillPayInfoList(billIdList, query.getStartDate(),query.getEndDate(), null);
    } else {
      // 其中免单支付工作量合计
      List<Integer> typeList = new ArrayList<Integer>() {{ add(23);add(26); }};
      baseBillPayList = baseBillPayMapper.findBaseBillPayInfoList(billIdList, query.getStartDate(),query.getEndDate(), typeList);
    }
    for (BillIdVo billIdVo : billIdList) {
      for (BaseBillPay baseBillPay : baseBillPayList) {
        if (billIdVo.getBillId().equals(baseBillPay.getBillId())) {
          baseBillPayVo = new ArrayList<>();
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
  public void exportActivityReferralList(
      HttpServletResponse response, PatientOriginActivityQuery query) throws IOException {
    if (StringHelper.isNotEmpty(query.getEndDate())) {
      String endDate = new DateTime(query.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      query.setEndDate(endDate);
    }
    List<PatientOriginActivityVo> patientOriginActivityVoList = combinationActivityReferral(query);
    ExcelUtil<PatientOriginActivityVo> excelUtil = new ExcelUtil<>(PatientOriginActivityVo.class);
    excelUtil.exportExcel(response, patientOriginActivityVoList, "活动推荐明细", "活动推荐明细");
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
        return receivedDetail(query, true);
      case 2:
        return receivedDetail(query, false);
      case 3:
        return refundDetail(query);
      case 4:
        return makeUpDetail(query);
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
  private List<ReceivedWorkloadDetailsVo> makeUpDetail(ReceiverkLoadQuery query) throws ParseException {
    List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList = baseBillMapper.selectMakeUpDetail(query);
    if (receivedWorkloadDetailsVoList != null) {
      for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo : receivedWorkloadDetailsVoList) {
        // 判断关联时间是否大于初诊时间
        receivedWorkloadDetailsVo.setIsChange(getIsChange(receivedWorkloadDetailsVo));
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
  private List<ReceivedWorkloadDetailsVo> refundDetail(ReceiverkLoadQuery query) throws ParseException {
    List<ReceivedWorkloadDetailsVo> refundDetailList = new ArrayList<>();
    List<Integer> refundIdList = baseRefundMapper.selectfundBillIdList(query);
    if (refundIdList != null) {
      for (Integer refundId : refundIdList) {
        List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList = baseRefundMapper.selectrefundDetail(refundId, query.getOriginId());
        if (receivedWorkloadDetailsVoList != null) {
          for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo :receivedWorkloadDetailsVoList ) {
            // 判断关联时间是否大于初诊时间
            receivedWorkloadDetailsVo.setIsChange(getIsChange(receivedWorkloadDetailsVo));
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
      ReceiverkLoadQuery query, Boolean isFreePayment) throws ParseException {
    List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsListVo = new ArrayList<>();
    List<Integer> baseBillIdList;
    if (isFreePayment) {
      // 订单id List
      baseBillIdList = baseBillMapper.findBaseBillIdList(query, null);
    } else {
      // 其中免单支付 订单id List
      List<Integer> typeList = new ArrayList<Integer>() {{ add(23);add(26); }};
      baseBillIdList = baseBillMapper.findBaseBillIdList(query, typeList);
    }
    if (baseBillIdList != null) {
      List<BaseBillPay> baseBillPayList;
      if (isFreePayment) {
        // 查询所以支付方式
        baseBillPayList = baseBillPayMapper.selectBaseBillPayInfoList(baseBillIdList, query, null);
      } else {
        // 查询免单支付方式
        List<Integer> typeList = new ArrayList<Integer>() {{ add(23);add(26); }};
        baseBillPayList = baseBillPayMapper.selectBaseBillPayInfoList(baseBillIdList, query, typeList);
      }
      Map<Integer, List<BaseBillPay>> baseBillPayMap = getBaseBillPayList(baseBillIdList,baseBillPayList);
      // 订单支付明细
      for (Integer billId : baseBillIdList) {
        // 订单项目明细
        List<ReceivedWorkloadDetailsVo> baseBillDetailBizVos = baseBillDetailMapper.selectEreceiverkLoad(billId, query.getOriginId());
        if (baseBillDetailBizVos != null) {
          for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo : baseBillDetailBizVos) {
            // 通过项目订单id 获取订单支付记录
            List<BaseBillPay> billPayList = baseBillPayMap.get(receivedWorkloadDetailsVo.getBillId());
            if (StringHelper.isNotNull(billPayList)){
              // 循环乘以订单记录中本次支付金额
              for (BaseBillPay baseBillPay: billPayList) {
                BigDecimal multiply = receivedWorkloadDetailsVo.getWorkload().multiply(baseBillPay.getReceivedAmount());
                receivedWorkloadDetailsVo.setWorkload(multiply.setScale(1, BigDecimal.ROUND_HALF_UP));
                // 加入到结果返回集合中
                receivedWorkloadDetailsListVo.add(receivedWorkloadDetailsVo);
              }
            }
          }
        }
      }
    }
    return receivedWorkloadDetailsListVo;
  }

  /**
   * 判断患者来源是否有变更过
   * @param model 来源信息
   * @return 是否更改过
   */
  private Boolean getIsChange(ReceivedWorkloadDetailsVo model) throws ParseException {
    if (model.getRelatedTime() != null && model.getFirstVisitDate() != null){
      return comparetoTime(model.getRelatedTime(),model.getFirstVisitDate());
    }
    return false;
  }

  /**
   * 比较两个时间大小
   * @param relatedTime 关联时间
   * @param firstVisitDate 初诊时间
   * @return true 大于 false 小于 or 等于
   * @throws ParseException
   */
  public Boolean comparetoTime(String relatedTime, String firstVisitDate) throws ParseException {
    Boolean isChange = false;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    try {
      Date relatedTime1 = format.parse(relatedTime);
      Date firstVisitDate1 = format.parse(firstVisitDate);
      int compareTo = relatedTime1.compareTo(firstVisitDate1);
      if (compareTo > 0){
        isChange = true;
      }
      if (compareTo < 0){
        isChange = false;
      }
      if (compareTo == 0){
        isChange = false;
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return isChange;
  }


  /**
   * 封装map
   * @param baseBillIdList 订单idList
   * @param baseBillPayList 订单支付记录List
   * @return map key订单id v订单支付记录
   */
  public Map<Integer,List<BaseBillPay>> getBaseBillPayList(List<Integer> baseBillIdList,List<BaseBillPay> baseBillPayList){
    Map<Integer,List<BaseBillPay>> map = new HashMap();
    List<BaseBillPay> baseBillPayVoList = null;
    if (baseBillIdList != null && baseBillPayList != null){
      for (Integer billId : baseBillIdList) {
        for (BaseBillPay baseBillPay: baseBillPayList) {
          if (billId.equals(baseBillPay.getBillId())){
            baseBillPayVoList = new ArrayList<>();
            baseBillPayVoList.add(baseBillPay);
          }
        }
        map.put(billId,baseBillPayVoList);
      }
    }
    return map;
  }

  /**
   * 获取活动列表
   * @return 活动列表
   */
  public List<ActivityVo> getActivityList() {
    List<ActivityVo> activityVoList = basePatientOriginMapper.selectActivityList();
    if (!StringHelper.isEmpty(activityVoList)){
      Iterator<ActivityVo> activityVoIterator = activityVoList.iterator();
      while (activityVoIterator.hasNext()){
        ActivityVo activityVo = activityVoIterator.next();
        if (activityVo.getTimeLimit() == 1) {
          if (!DateUtil.isEffectiveDate(new Date(), activityVo.getLimitStartDate(), activityVo.getLimitEndDate())) {
            activityVoIterator.remove(); // 使用迭代器的删除方法删除
          }
        }
      }
    }
    return activityVoList;
  }
}
