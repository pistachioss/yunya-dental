package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.middletable.dao.report.StatEmpTreatMapper;
import com.yunya.models.report.StatEmpTreat;
import com.yunya.models.treatment.TreatmentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;

import static com.yunya.framework.common.constant.RedisConstants.LOCK_STATISTICS_EMP_TREAT;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/12/13 14:36
 * @since: 1.0.0
 */
@Slf4j
@Service
public class StatEmpTreatBiz extends BaseBiz<StatEmpTreatMapper, StatEmpTreat> {

    @Autowired private RedisLockBiz redisLockBiz;
    /**
     * 增量更新员工就诊统计项
     *
     * @param treatmentRecord
     */
    public void incStatEmpTreat(TreatmentRecord treatmentRecord) {
        Integer orgId = treatmentRecord.getOrgId();
        Integer dentistId = treatmentRecord.getDentistId();
        Integer treatDate = DateUtil.date2Number(treatmentRecord.getTreatEndTime());
        StatEmpTreat entity = new StatEmpTreat();
        entity.setOrgId(orgId);
        entity.setDentistId(dentistId);
        entity.setTreatDate(treatDate);
        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_TREAT, orgId, dentistId);
        String lockVal = String.valueOf(treatDate);
        redisLockBiz.lockedApply(lockKey, lockVal, (t)->{
            StatEmpTreat statEmpTreat = mapper.selectByPrimaryKey(entity);
            if (treatmentRecord.getInservice()) {
                Byte type = treatmentRecord.getType();
                Integer firstVisitCount = 0;
                Integer reVisitCount = 0;
                if (type.intValue() == 0) {// 初诊
                    firstVisitCount += 1;
                } else { // 复诊
                    reVisitCount += 1;
                }
                Integer updId = treatmentRecord.getUpdId();
                if (ObjectUtils.isEmpty(statEmpTreat)) {// 新数据生成
                    entity.setCrtId(updId);
                    entity.setCrtTime(new Date(System.currentTimeMillis()));
                    entity.setFirstVisitCount(firstVisitCount);
                    entity.setReVisitCount(reVisitCount);
                    mapper.insertSelective(statEmpTreat);
                } else {// 增量更新
                    statEmpTreat.setFirstVisitCount(firstVisitCount + statEmpTreat.getFirstVisitCount());
                    statEmpTreat.setReVisitCount(reVisitCount + statEmpTreat.getReVisitCount());
                    mapper.updateByPrimaryKeySelective(statEmpTreat);
                }
            } else {// 删除
                Byte type = treatmentRecord.getType();
                Integer firstVisitCount = 0;
                Integer reVisitCount = 0;
                if (type.intValue() == 0) {// 初诊
                    firstVisitCount -= 1;
                } else { // 复诊
                    reVisitCount -= 1;
                }
                statEmpTreat.setFirstVisitCount(firstVisitCount + statEmpTreat.getFirstVisitCount());
                statEmpTreat.setReVisitCount(reVisitCount + statEmpTreat.getReVisitCount());
                mapper.updateByPrimaryKeySelective(statEmpTreat);
            }
            return statEmpTreat;
        });
    }
}
