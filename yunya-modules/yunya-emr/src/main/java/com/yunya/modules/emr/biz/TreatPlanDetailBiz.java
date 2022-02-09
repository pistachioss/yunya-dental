package com.yunya.modules.emr.biz;

import com.yunya.feign.emr.domain.model.TreatPlanDetailModel;
import com.yunya.feign.emr.domain.vo.MedicalTreatPlanRecordVO;
import com.yunya.feign.emr.domain.vo.TreatPlanDetailVO;
import com.yunya.feign.emr.domain.vo.TreatPlanStepVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.TreatPlanStatusEnum;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.TreatPlanDetail;
import com.yunya.models.emr.TreatPlanDetailHistory;
import com.yunya.models.emr.TreatPlanDetailWriteoff;
import com.yunya.modules.emr.mapper.TreatPlanDetailHistoryMapper;
import com.yunya.modules.emr.mapper.TreatPlanDetailMapper;
import com.yunya.modules.emr.mapper.TreatPlanDetailWriteoffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 简介：治疗计划明细业务层
 *
 * @author: chenlin
 * @Description: 治疗计划明细业务层
 * @Date: 2022/1/11 14:18
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TreatPlanDetailBiz extends BaseBiz<TreatPlanDetailMapper, TreatPlanDetail> {
    /**
     * 治疗计划明细历史记录
     */
    @Autowired
    private TreatPlanDetailHistoryMapper treatPlanDetailHistoryMapper;
    /**
     * 治疗计划与订单项目核销表
     */
    @Autowired
    private TreatPlanDetailWriteoffMapper treatPlanDetailWriteoffMapper;
    /**
     * 治疗计划
     */
    @Autowired
    private TreatPlanRecordBiz treatPlanRecordBiz;

    /**
     * 保存治疗计划明细
     *  1、查出本次的原数据
     *  2、原数据与新数据对比，找出已删数据
     *  3、删除原数据，增加新数据，追加操作记录
     *
     * @param planId
     * @param stepId
     * @param userId
     * @param details
     */
    public void save(Integer planId, Integer stepId, Integer userId,List<TreatPlanDetailModel> details) {
        TreatPlanDetail query = new TreatPlanDetail();
        query.setTreatPlanId(planId);
        query.setTreatStepId(stepId);
        List<TreatPlanDetail> deleted = mapper.select(query);
        List<TreatPlanDetailHistory> histories = new ArrayList<>();
        mapper.delete(query);
        if (StringHelper.isNotEmpty(details)) {
            if (StringHelper.isNotEmpty(deleted)) {
                Iterator<TreatPlanDetail> it = deleted.iterator();
                details.forEach(vo->{
                    Integer detailId = vo.getDetailId();
                    Byte status = vo.getStatus();
                    treatPlanRecordBiz.checkNotStatus(status, TreatPlanStatusEnum.UNCONFIRM, TreatPlanStatusEnum.CONFIRMED);
                    if (!ObjectUtils.isEmpty(detailId)) {
                        while (it.hasNext()) {
                            TreatPlanDetail next = it.next();
                            if (next.getId().equals(detailId)) {
                                vo.setCrtId(next.getCrtId());
                                vo.setCrtTime(next.getCrtTime());
                                it.remove();
                                break;
                            }
                        }
                    }
                });
            }

            Date now = new Date(System.currentTimeMillis());
            details.forEach(vo->{
                TreatPlanDetail entity = model2Entity(vo, planId, stepId, userId, now);
                mapper.insertSelective(entity);
                histories.add(entity2History(entity, (byte) (ObjectUtils.isEmpty(vo.getDetailId())?0:1)));
            });
        }
        if (StringHelper.isNotEmpty(deleted)) { // 删除
            deleted.forEach(vo-> histories.add(entity2History(vo, (byte) 2)));
        }
        if (StringHelper.isNotEmpty(histories)) {
            treatPlanDetailHistoryMapper.insertBatch(histories);
        }
    }

    /**
     * 实体数据转为历史操作数据
     *
     * @param entity
     * @param operation
     * @return
     */
    private TreatPlanDetailHistory entity2History(TreatPlanDetail entity, Byte operation) {
        TreatPlanDetailHistory history = new TreatPlanDetailHistory();
        history.setStepDetailId(entity.getId());
        history.setTreatPlanId(entity.getTreatPlanId());
        history.setTreatStepId(entity.getTreatStepId());
        history.setToothBit(entity.getToothBit());
        history.setType(entity.getType());
        history.setBillingItemId(entity.getBillingItemId());
        history.setBillingItemName(entity.getBillingItemName());
        history.setUnit(entity.getUnit());
        history.setPrice(entity.getPrice());
        history.setQuantity(entity.getQuantity());
        history.setRemark(entity.getRemark());
        history.setOperation(operation);
        history.setCrtId(entity.getUpdId());
        history.setCrtTime(entity.getUpdTime());
        return history;
    }

    /**
     * 数据模型转换
     *
     * @param vo
     * @param planId
     * @param stepId
     * @param userId
     * @param now
     * @return
     */
    private TreatPlanDetail model2Entity(TreatPlanDetailModel vo, Integer planId, Integer stepId, Integer userId, Date now) {
        TreatPlanDetail entity = new TreatPlanDetail();
        entity.setId(vo.getDetailId());
        entity.setTreatStepId(stepId);
        entity.setTreatPlanId(planId);
        entity.setType(vo.getType());
        entity.setBillingItemId(vo.getBillingItemId());
        entity.setBillingItemName(vo.getBillingItemName());
        entity.setUnit(vo.getUnit());
        entity.setPrice(vo.getPrice());
        entity.setQuantity(vo.getQuantity());
        entity.setToothBit(vo.getToothBit());
        entity.setRemark(vo.getRemark());
        Integer crtId = vo.getCrtId();
        if (ObjectUtils.isEmpty(crtId)) {
            crtId = userId;
        }
        Date crtTime = vo.getCrtTime();
        if (ObjectUtils.isEmpty(crtTime)) {
            crtTime = now;
        }
        entity.setCrtId(crtId);
        entity.setCrtTime(crtTime);
        entity.setUpdId(userId);
        entity.setUpdTime(now);
        return entity;
    }

    /**
     * 根据治疗计划id查询明细列表
     *
     * @param planId
     * @return
     */
    public List<TreatPlanDetailVO> findTreatPlanDetailByPlanId(Integer planId) {
        return mapper.selectTreatPlanDetailByPlanId(planId);
    }

    public List<TreatPlanDetail> sumTreatPlanDetailEnableQuantity(List<Integer> detailIds) {
        return treatPlanDetailWriteoffMapper.sumTreatPlanDetailEnableQuantity(detailIds);
    }

    public void insertBatchOfWriteoffQuantity(List<TreatPlanDetailWriteoff> datas) {
        if (StringHelper.isNotEmpty(datas)) {
            treatPlanDetailWriteoffMapper.insertBatch(datas);
        }
    }

    public void deleteWriteoffByOrderDetailId(List<Integer> detailIds) {
        treatPlanDetailWriteoffMapper.deleteWriteoffByOrderDetailId(detailIds);
    }

    /**
     * 终止or恢复已核销的记录
     *
     * @param treatPlan
     */
    public void terminalOrRenewWriteoffRecord(MedicalTreatPlanRecordVO treatPlan, Byte status) {
        List<TreatPlanStepVO> steps = treatPlan.getTreatPlanSteps();
        if (StringHelper.isNotEmpty(steps)) {
            steps.forEach(step->{
                List<TreatPlanDetailVO> details = step.getTreatPlanDetails();
                details.forEach(vo->{
                    TreatPlanDetailWriteoff entity = new TreatPlanDetailWriteoff();
                    entity.setId(vo.getDetailId());
                    entity.setStatus(status);
                    treatPlanDetailWriteoffMapper.updateByPrimaryKey(entity);
                });
            });
        }
    }
}
