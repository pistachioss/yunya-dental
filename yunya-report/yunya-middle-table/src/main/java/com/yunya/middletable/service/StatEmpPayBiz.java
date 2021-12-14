package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.StatEmpPayMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.models.report.StatEmpPay;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_PAY;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/13 14:42
 * @since: 1.0.0
 */
@Service
public class StatEmpPayBiz extends BaseBiz<StatEmpPayMapper, StatEmpPay> {
    @Autowired private RedisLockBiz redisLockBiz;

    @Autowired private OrderDetailMapper orderDetailMapper;

    public void incStatEmpPay(BillPayRecord billPayRecord) {
        Integer crtId = billPayRecord.getCrtId();
        List<OrderDetail> details = findOrderDetailByOrderRecordId(billPayRecord.getOrderRecordId(),
                billPayRecord.getReceivedAmount());
        if (StringHelper.isNotEmpty(details)) {
            details.forEach(detail-> {
                Integer orgId = detail.getOrgId();
                Integer executorId = detail.getExecutorId();
                Integer payDate = DateUtil.date2Number(billPayRecord.getCrtTime());
                StatEmpPay entity = new StatEmpPay();
                entity.setOrgId(orgId);
                entity.setDentistId(executorId);
                entity.setPayDate(payDate);
                entity.setItemType(detail.getType());
                entity.setItemId(detail.getBillingItemId());
                String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_PAY, orgId, executorId);
                String lockVal = String.valueOf(payDate);
                redisLockBiz.lockedApply(lockKey, lockVal, (t) -> {
                    StatEmpPay statEmpPay = mapper.selectByPrimaryKey(entity);
                    if (billPayRecord.getInservice()) {
                        if (ObjectUtils.isEmpty(statEmpPay)) { // 生成新数据
                            entity.setReceivedWorkload(detail.getReceivableAmount());
                            entity.setCrtId(crtId);
                            entity.setCrtTime(new Date(System.currentTimeMillis()));
                            mapper.insertSelective(entity);
                        } else { // 增量更新
                            entity.setReceivedWorkload(entity.getReceivedWorkload().add(detail.getReceivableAmount()));
                            mapper.updateByPrimaryKeySelective(entity);
                        }
                    } else {// 减量更新
                        entity.setReceivedWorkload(entity.getReceivedWorkload().subtract(detail.getReceivableAmount()));
                        mapper.updateByPrimaryKeySelective(entity);
                    }
                    return null;
                });
            });
        }
    }

    public List<OrderDetail> findOrderDetailByOrderRecordId(Integer orderRecordId, BigDecimal amount) {
        OrderDetail query = new OrderDetail();
        query.setOrderRecordId(orderRecordId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(query);
        // 项目应收转项目实收
        if (StringHelper.isNotEmpty(orderDetails)) {
            // 分摊: 项目应收/账单应收 * 总金额（实收/应收）
            BigDecimal totalWorkload = new BigDecimal("0.00");
            for (OrderDetail vo : orderDetails) {
                if (vo.getInservice()) {
                    totalWorkload = totalWorkload.add(vo.getReceivableAmount());
                }
            }
            BigDecimal percentotal = new BigDecimal("0.00");
            for (int i = 0; i < orderDetails.size(); i++) {
                OrderDetail detail = orderDetails.get(i);
                if (detail.getInservice()) {
                    BigDecimal receivableAmount = detail.getReceivableAmount();
                    BigDecimal percentage = BigDecimal.ZERO;
                    if (totalWorkload.compareTo(BigDecimal.ZERO) != 0) {
                        percentage = receivableAmount.divide(totalWorkload, 2, BigDecimal.ROUND_HALF_UP);
                        percentotal = percentotal.add(percentage);
                    }
                    if (i == orderDetails.size() - 1) {// 最后一个需要补差值，避免比例总和不为1
                        BigDecimal subtract = BigDecimal.ONE.subtract(percentage);
                        if (subtract != BigDecimal.ZERO) {// 补上最后的差值
                            percentage = percentage.add(subtract);
                        }
                    }
                    detail.setReceivableAmount(percentage.multiply(amount));
                }
            }
        }
        return orderDetails;
    }
}
