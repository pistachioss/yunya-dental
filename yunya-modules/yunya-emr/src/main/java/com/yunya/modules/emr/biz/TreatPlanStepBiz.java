package com.yunya.modules.emr.biz;

import com.yunya.feign.emr.domain.model.TreatPlanStepModel;
import com.yunya.feign.emr.domain.vo.TreatPlanDetailVO;
import com.yunya.feign.emr.domain.vo.TreatPlanStepVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.TreatPlanStep;
import com.yunya.models.emr.TreatPlanStepHistory;
import com.yunya.modules.emr.mapper.TreatPlanStepHistoryMapper;
import com.yunya.modules.emr.mapper.TreatPlanStepMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 简介：治疗计划步骤业务层
 *
 * @author: chenlin
 * @Description: 治疗计划步骤业务层
 * @Date: 2022/1/11 10:02
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TreatPlanStepBiz extends BaseBiz<TreatPlanStepMapper, TreatPlanStep> {
    /**
     * 治疗计划步骤历史记录
     */
    @Autowired
    private TreatPlanStepHistoryMapper treatPlanStepHistoryMapper;

    /**
     * 治疗计划明细
     */
    @Autowired
    private TreatPlanDetailBiz treatPlanDetailBiz;

    /**
     * 保持治疗计划步骤
     *
     * @param planId
     * @param treatPlanSteps
     */
    public void save(Integer planId, List<TreatPlanStepModel> treatPlanSteps) {
        List<TreatPlanStepHistory> histories = new ArrayList<>();
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = new Date(System.currentTimeMillis());
        TreatPlanStep query = new TreatPlanStep();
        query.setPlanId(planId);
        List<TreatPlanStep> deleted = mapper.select(query);
        mapper.delete(query);
        if (StringHelper.isNotEmpty(treatPlanSteps)) {
            if (StringHelper.isNotEmpty(deleted)) {
                Iterator<TreatPlanStep> it = deleted.iterator();
                treatPlanSteps.forEach(vo->{
                    Integer treatPlanStepId = vo.getTreatPlanStepId();
                    if (!ObjectUtils.isEmpty(treatPlanStepId)) {
                        while (it.hasNext()) {
                            TreatPlanStep next = it.next();
                            if (next.getId().equals(treatPlanStepId)) {
                                vo.setCrtId(next.getCrtId());
                                vo.setCrtTime(next.getCrtTime());
                                it.remove();
                                break;
                            }
                        }
                    }
                });
            }
            treatPlanSteps.forEach(vo->{
                TreatPlanStep entity = model2Entity(vo, planId, userId, now);
                mapper.insertSelective(entity);
                treatPlanDetailBiz.save(planId, entity.getId(), vo.getTreatPlanDetails());
                histories.add(entity2History(entity,(byte) (ObjectUtils.isEmpty(vo.getTreatPlanStepId())?0:1)));
            });
        }
        if (StringHelper.isNotEmpty(deleted)) { // 删除
            deleted.forEach(vo->{
                treatPlanDetailBiz.save(planId, vo.getId(), null);
                histories.add(entity2History(vo, (byte) 2));
            });
        }
        if (StringHelper.isNotEmpty(histories)) {
            treatPlanStepHistoryMapper.insertBatch(histories);
        }
    }

    /**
     * 数据模型转换
     * 
     * @param vo
     * @param planId
     * @param userId
     * @param now
     * @return
     */
    private TreatPlanStep model2Entity(TreatPlanStepModel vo, Integer planId, Integer userId, Date now) {
        TreatPlanStep entity = new TreatPlanStep();
        entity.setId(vo.getTreatPlanStepId());
        entity.setPlanId(planId);
        entity.setStepName(vo.getStepName());
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
        entity.setUptId(userId);
        entity.setUptTime(now);
        return entity;
    }

    /**
     * 实体数据转换成历史数据
     *
     * @param entity
     * @param operation
     * @return
     */
    private TreatPlanStepHistory entity2History(TreatPlanStep entity, byte operation) {
        TreatPlanStepHistory history = new TreatPlanStepHistory();
        history.setStepId(entity.getId());
        history.setStepName(entity.getStepName());
        history.setPlanId(entity.getPlanId());
        history.setOperation(operation);
        history.setCrtId(entity.getUptId());
        history.setCrtTime(entity.getUptTime());
        return history;
    }

    /**
     * 根据治疗计划id查询步骤列表
     *
     * @param planId
     * @return
     */
    public List<TreatPlanStepVO> findTreatPlanStepByPlanId(Integer planId) {
        List<TreatPlanStepVO> steps = mapper.selectTreatPlanStepByPlanId(Collections.singleton(planId));
        if (StringHelper.isNotEmpty(steps)) {
            List<TreatPlanDetailVO> details = treatPlanDetailBiz.findTreatPlanDetailByPlanId(planId);
            if (StringHelper.isNotEmpty(details)) {
                putDetail2Step(steps, details);
            }
        }
        return steps;
    }


    /**
     * 根据治疗计划id查询步骤列表
     *
     * @param planId
     * @return
     */
    public List<TreatPlanStepVO> findTreatPlanStepPreByPlanId(Integer planId, Byte status) {
        List<TreatPlanStepVO> steps = treatPlanStepHistoryMapper.selectTreatPlanStepPreByPlanId(planId, status);
        if (StringHelper.isNotEmpty(steps)) {
            List<TreatPlanDetailVO> details = treatPlanDetailBiz.findTreatPlanDetailPreByPlanId(planId, status);
            if (StringHelper.isNotEmpty(details)) {
                putDetail2Step(steps, details);
            }
        }
        return steps;
    }

    private void putDetail2Step(List<TreatPlanStepVO> steps, List<TreatPlanDetailVO> details) {
        Map<Integer, List<TreatPlanDetailVO>> detailMap = new HashMap<>(16);
        for (TreatPlanDetailVO detail : details) {
            Integer stepId = detail.getStepId();
            List<TreatPlanDetailVO> list = detailMap.get(stepId);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(detail);
            detailMap.put(stepId, list);
        }
        steps.forEach(step->{
            Integer stepId = step.getTreatPlanStepId();
            List<TreatPlanDetailVO> detailList = detailMap.get(stepId);
            int quantity = 0;
            BigDecimal amount = new BigDecimal("0.00");
            if (StringHelper.isNotEmpty(detailList)) {
                for (TreatPlanDetailVO detail : detailList) {
                    Integer num = detail.getQuantity();
                    quantity += num;
                    amount = amount.add(new BigDecimal(num).multiply(detail.getPrice()));
                }
            }
            step.setQuanity(quantity);
            step.setAmount(amount);
            step.setTreatPlanDetails(detailList);
        });
    }
}
