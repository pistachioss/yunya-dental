package com.yunya.middletable.service.emr;

import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.emr.domain.vo.TreatPlanDetailVO;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.emr.TreatPlanDetailMapper;
import com.yunya.middletable.dao.emr.TreatPlanDetailWriteoffMapper;
import com.yunya.middletable.dao.emr.TreatPlanRecordMapper;
import com.yunya.middletable.dao.treatment.BillPayRecordMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.middletable.dao.treatment.OrderRecordMapper;
import com.yunya.models.emr.TreatPlanDetailWriteoff;
import com.yunya.models.emr.TreatPlanRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/15 14:55
 * @since: 1.0.0
 */
@Service
public class TreatPlanRecordBiz extends BaseBiz<TreatPlanRecordMapper, TreatPlanRecord> {

    @Autowired
    private OrderRecordMapper orderRecordMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private TreatPlanDetailMapper treatPlanDetailMapper;
    @Autowired
    private BillPayRecordMapper billPayRecordMapper;
    @Autowired
    private TreatPlanDetailWriteoffMapper treatPlanDetailWriteoffMapper;
    @Autowired
    private RemoteEmrServiceFeign remoteEmrServiceFeign;

    /**
     * 操作
     *
     * @param msg
     */
    public void operate(MessageModel msg) {
        Map<String, Object> dataMap = msg.getParamMap();
        Integer treatmentId = (Integer) dataMap.get("id");

        // 1、找出订单所有项目明细并合一起
        OrderRecord order = findOrderRecordByTreatmentId(treatmentId);
        if (!ObjectUtils.isEmpty(order)) {
            Integer userId = order.getCrtId();
            List<BillPayRecord> billPayRecords = findBillPayRecordByOrderId(order.getId());
            Byte status = 1; // 首次收费
            if (StringHelper.isEmpty(billPayRecords)) {// 末次撤销收费（即账单所有收费全部撤销了）
                status = 0;
            } else if (billPayRecords.size() > 1) {
                return;
            }
            List<OrderDetail> orderDetails = findOrderDetailByOrderId(order.getId());
            List<Integer> orderDetailIds = orderDetails.stream().map(OrderDetail::getId).collect(Collectors.toList());

            // 2、找出对应核销记录及其计划明细
            List<TreatPlanDetailVO> details = treatPlanDetailMapper.selectTreatPlanDetailAndWriteoffList(orderDetailIds);

            // 3、更新治疗计划项目数量与核销记录的状态：
            List<Integer> planIds = updTreatPlanDetailWriteoffStatus(details, status, userId);

            // 4、重新统计给定治疗计划的状态
            remoteEmrServiceFeign.recalculatePlanStatusById(userId, planIds);
        }
    }

    private List<Integer> updTreatPlanDetailWriteoffStatus(List<TreatPlanDetailVO> details, Byte status, Integer userId) {
        Date now = new Date(System.currentTimeMillis());
        List<Integer> planIds = new ArrayList<>();
        details.forEach(vo->{
            TreatPlanDetailWriteoff entity = new TreatPlanDetailWriteoff();
            entity.setId(vo.getOrderDetailId());
            entity.setStatus(status);
            entity.setUptId(userId);
            entity.setUptTime(now);
            treatPlanDetailWriteoffMapper.updateByPrimaryKeySelective(entity);
            Integer planId = vo.getPlanId();
            if (!planIds.contains(planId)) {
                planIds.add(planId);
            }
        });
        return planIds;
    }

    private List<BillPayRecord> findBillPayRecordByOrderId(Integer orderRecordId) {
        BillPayRecord query = new BillPayRecord();
        query.setOrderRecordId(orderRecordId);
        query.setInservice(true);
        return billPayRecordMapper.select(query);
    }

    private OrderRecord findOrderRecordByTreatmentId(Integer treatmentId) {
        OrderRecord query = new OrderRecord();
        query.setTreatmentRecordId(treatmentId);
        query.setInservice(true);
        return orderRecordMapper.selectOne(query);
    }

    private List<OrderDetail> findOrderDetailByOrderId(Integer orderRecordId) {
        OrderDetail query = new OrderDetail();
        query.setOrderRecordId(orderRecordId);
        query.setInservice(true);
        return orderDetailMapper.select(query);
    }
}
