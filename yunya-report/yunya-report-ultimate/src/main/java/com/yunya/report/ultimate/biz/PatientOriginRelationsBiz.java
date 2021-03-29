package com.yunya.report.ultimate.biz;

import com.yunya.feign.patient_central.domain.query.PatientOriginEmployeeQuery;
import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.patient_central.domain.vo.web.BaseBillDetailBizVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginEmployeeVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedTotalWorkloadVo;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillPay;
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

  /**
   * 员工推荐信息列表
   *
   * @param query 条件
   * @return 员工推荐信息列表返回
   */
  public List<PatientOriginEmployeeVo> finleEmployeeReferral(PatientOriginEmployeeQuery query) {
    if (StringHelper.isNotEmpty(query.getEndDate())) {
      String endDate = new DateTime(query.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      query.setEndDate(endDate);
    }
    return combinationEmployeeReferral(query);
  }

  /**
   * 组合返回集
   *
   * @param query 条件
   * @return 推荐人推荐信息
   */
  private List<PatientOriginEmployeeVo> combinationEmployeeReferral(
      PatientOriginEmployeeQuery query) {
    // 查询推荐人信息以及推荐数量
    List<PatientOriginEmployeeVo> patientOriginEmployeeVoList = mapper.findEmployeeVoLists(query);
    if (patientOriginEmployeeVoList != null) {
      // 获取已收工作量合计
      getReceivedTotalWorkload(patientOriginEmployeeVoList, query, true);
      // 获取其中免单支付工作量合计
      getReceivedTotalWorkload(patientOriginEmployeeVoList, query, false);
      // 获取退费金额合计
      getTotalRefundAmount(patientOriginEmployeeVoList, query);
      // 获取补入工作量合计
      getMakeUpWorkload(patientOriginEmployeeVoList, query);
    }
    return patientOriginEmployeeVoList;
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
          baseBillDetailMapper.findMakeUpWorkload(patientOriginEmployee.getOriginId(), query);
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
        BigDecimal totalRefundAmount =
            baseRefundMapper.findRefundAmount(patientOriginEmployeeVo.getOriginId(), query);
        if (totalRefundAmount == null) {
          patientOriginEmployeeVo.setTotalRefundAmount(new BigDecimal(0));
        } else {
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
  private void getReceivedTotalWorkload(
      List<PatientOriginEmployeeVo> patientOriginEmployeeVoList,
      PatientOriginEmployeeQuery query,
      Boolean type) {
    // 获取全部订单记录Map
    Map<Integer, List<BaseBillPay>> baseBillPayInfoMap = getBaseBillPayInfoMap(query, type);
    if (patientOriginEmployeeVoList.size() > 0) {
      for (PatientOriginEmployeeVo patientOriginEmployeeVo : patientOriginEmployeeVoList) {
        // 根据推荐人id查询推荐患者订单项目实收计算
        List<ReceivedTotalWorkloadVo> receivedTotalWorkloadVoList =
            mapper.findReceivedTotalWorkload(patientOriginEmployeeVo);
        if (receivedTotalWorkloadVoList != null) {
          for (ReceivedTotalWorkloadVo receivedTotalWorkloadVo : receivedTotalWorkloadVoList) {
            // 根据订单号 获取订单支付记录
            List<BaseBillPay> baseBillPays =
                baseBillPayInfoMap.get(receivedTotalWorkloadVo.getBillId());
            if (baseBillPays != null) {
              for (BaseBillPay baseBillPay : baseBillPays) {
                BigDecimal multiply =
                    receivedTotalWorkloadVo
                        .getOrderWorkload()
                        .multiply(baseBillPay.getReceivedAmount());
                if (multiply == null) {
                  multiply = new BigDecimal(0);
                }
                // 判断是全部工作量 还是 免单支付工作量
                if (type) {
                  if (patientOriginEmployeeVo.getReceivedTotalWorkload() == null) {
                    patientOriginEmployeeVo.setReceivedTotalWorkload(multiply);
                  } else {
                    patientOriginEmployeeVo.setReceivedTotalWorkload(
                        patientOriginEmployeeVo.getReceivedTotalWorkload().add(multiply));
                  }
                } else {
                  if (patientOriginEmployeeVo.getFreeTotalWorkload() == null) {
                    patientOriginEmployeeVo.setFreeTotalWorkload(multiply);
                  } else {
                    patientOriginEmployeeVo.setFreeTotalWorkload(
                        patientOriginEmployeeVo.getFreeTotalWorkload().add(multiply));
                  }
                }
              }
            }
          }
        }
        if (type) {
          // 如果已收工作量为空就天补为0
          if (patientOriginEmployeeVo.getReceivedTotalWorkload() == null) {
            patientOriginEmployeeVo.setReceivedTotalWorkload(new BigDecimal(0));
          }
        } else {
          // 如果免单支付工作量为空就天补为0
          if (patientOriginEmployeeVo.getFreeTotalWorkload() == null) {
            patientOriginEmployeeVo.setFreeTotalWorkload(new BigDecimal(0));
          }
        }
      }
    }
  }

  /**
   * 获取订单支付记录
   *
   * @param query 条件
   * @return 订单支付记录 key订单号 v订单支付记录集合
   */
  private Map<Integer, List<BaseBillPay>> getBaseBillPayInfoMap(
      PatientOriginEmployeeQuery query, Boolean type) {
    List<BaseBillPay> baseBillPayVo = null;
    List<BaseBillPay> baseBillPayList;
    Map<Integer, List<BaseBillPay>> billPayMap = new HashMap<>();
    // 查询所有符合条件的订单号
    List<Integer> billIdList = baseBillMapper.findBillIdList(query);
    if (type) {
      // 根据订单号和支付时间查询订单支付记录集合
      baseBillPayList = baseBillPayMapper.findBaseBillPayInfoList(billIdList, query, null);
    } else {
      // 其中免单支付工作量合计
      List<Integer> typeList =
          new ArrayList<Integer>() {
            {
              add(23);
              add(26);
            }
          };
      baseBillPayList = baseBillPayMapper.findBaseBillPayInfoList(billIdList, query, typeList);
    }
    for (Integer biilId : billIdList) {
      for (BaseBillPay baseBillPay : baseBillPayList) {
        if (biilId.equals(baseBillPay.getBillId())) {
          baseBillPayVo = new ArrayList<>();
          baseBillPayVo.add(baseBillPay);
        }
      }
      billPayMap.put(biilId, baseBillPayVo);
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
    if (StringHelper.isNotEmpty(query.getEndDate())) {
      String endDate = new DateTime(query.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      query.setEndDate(endDate);
    }
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
  public List<ReceivedWorkloadDetailsVo> finlereceiverkLoad(ReceiverkLoadQuery query) {
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
  private List<ReceivedWorkloadDetailsVo> makeUpDetail(ReceiverkLoadQuery query) {
    List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList = baseBillMapper.selectMakeUpDetail(query);
    if (receivedWorkloadDetailsVoList != null) {
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
  private List<ReceivedWorkloadDetailsVo> refundDetail(ReceiverkLoadQuery query) {
    List<ReceivedWorkloadDetailsVo> refundDetailList = new ArrayList<>();
    List<Integer> refundIdList = baseRefundMapper.selectfundBillIdList(query);
    if (refundIdList != null) {
      for (Integer refundId : refundIdList) {
        List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsVoList =
            baseRefundMapper.selectrefundDetail(refundId, query.getOriginId());
        if (receivedWorkloadDetailsVoList != null) {
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
      ReceiverkLoadQuery query, Boolean isFreePayment) {
    List<ReceivedWorkloadDetailsVo> receivedWorkloadDetailsListVo = new ArrayList<>();
    List<Integer> baseBillIdList;
    if (isFreePayment) {
      // 订单id List
      baseBillIdList = baseBillMapper.findBaseBillIdList(query, null);
    } else {
      // 其中免单支付 订单id List
      List<Integer> typeList =
          new ArrayList<Integer>() {
            {
              add(23);
              add(26);
            }
          };
      baseBillIdList = baseBillMapper.findBaseBillIdList(query, typeList);
    }
    if (baseBillIdList != null) {
      List<BaseBillPay> baseBillPayList;
      if (isFreePayment) {
        // 查询所以支付方式
        baseBillPayList = baseBillPayMapper.selectBaseBillPayInfoList(baseBillIdList, query, null);
      } else {
        // 查询免单支付方式
        List<Integer> typeList =
            new ArrayList<Integer>() {
              {
                add(23);
                add(26);
              }
            };
        baseBillPayList =
            baseBillPayMapper.selectBaseBillPayInfoList(baseBillIdList, query, typeList);
      }
      // 订单支付明细
      for (Integer billId : baseBillIdList) {
        // 订单项目明细
        List<ReceivedWorkloadDetailsVo> baseBillDetailBizVos =
            baseBillDetailMapper.selectEreceiverkLoad(billId, query.getOriginId());
        if (baseBillDetailBizVos != null) {
          for (ReceivedWorkloadDetailsVo receivedWorkloadDetailsVo : baseBillDetailBizVos) {
            if (baseBillPayList != null) {
              for (BaseBillPay baseBillPay : baseBillPayList) {
                // 如果项目订单id 和支付记录明细订单id相同,就用项目实收价格乘以支付金额 = 项目工作量
                if (receivedWorkloadDetailsVo.getBillId().equals(baseBillPay.getBillId())) {
                  // 获取单个项目工作量
                  BigDecimal multiply =
                      receivedWorkloadDetailsVo
                          .getWorkload()
                          .multiply(baseBillPay.getReceivedAmount());
                  receivedWorkloadDetailsVo.setWorkload(
                      multiply.setScale(2, BigDecimal.ROUND_DOWN));
                  // 加入到结果返回集合中
                  receivedWorkloadDetailsListVo.add(receivedWorkloadDetailsVo);
                }
              }
            }
          }
        }
      }
    }
    return receivedWorkloadDetailsListVo;
  }
}
