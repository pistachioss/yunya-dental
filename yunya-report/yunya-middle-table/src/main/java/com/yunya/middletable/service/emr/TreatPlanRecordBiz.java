package com.yunya.middletable.service.emr;

import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.middletable.dao.emr.TreatPlanRecordMapper;
import com.yunya.models.emr.TreatPlanRecord;
import org.springframework.stereotype.Service;

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

    public void operate(MessageModel msg) {
        Integer dataId = (Integer) msg.getParamMap().get("id");
//        BaseBill bill = generateBaseBill(dataId);
//        Integer operateType = msg.getOperateType();
//        switch (operateType) {
//            case 0:
//            case 2:
//            case 1:
//                mapper.deleteByPrimaryKey(dataId);
//                if (null != bill) {
//                    mapper.insertSelective(bill);
//                    baseBillDetailMapper.deleteByBillId(dataId);
//                    // 保存账单明细
//                    saveBaseBillDetail(dataId);
//                    // 推荐积分
//                    addPatientIntegral(bill.getPatientId());
//                    // 回调收费增加积分
//                    baseBillPayCallback.baseBillBizHandlerFinish(bill.getBillId());
//                } else {
//                    baseBillDetailMapper.deleteByBillId(dataId);
//                }
//            default:
//                statisticsEmployeeByBillDate(bill, dataId);
//                break;
//        }
    }
}
