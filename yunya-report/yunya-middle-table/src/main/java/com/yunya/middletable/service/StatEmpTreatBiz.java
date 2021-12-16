package com.yunya.middletable.service;

import com.google.common.base.Joiner;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.middletable.dao.report.BaseTreatmentProcessMapper;
import com.yunya.middletable.dao.report.StatEmpTreatMapper;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.report.StatEmpTreat;
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
    @Autowired private BaseTreatmentProcessMapper baseTreatmentProcessMapper;
    /**
     * 增量更新员工就诊统计项
     *
     * @param treatmentProcess
     */
    public void incStatEmpTreat(BaseTreatmentProcess treatmentProcess) {
        Integer orgId = treatmentProcess.getOrgId();
        Integer dentistId = treatmentProcess.getRegisteredDentistId();
        Integer treatDate = DateUtil.date2Number(treatmentProcess.getTreatEndTime());
        StatEmpTreat entity = new StatEmpTreat();
        entity.setOrgId(orgId);
        entity.setDentistId(dentistId);
        entity.setTreatDate(treatDate);
        String lockKey = Joiner.on(":").join(LOCK_STATISTICS_EMP_TREAT, orgId, dentistId);
        String lockVal = String.valueOf(treatDate);
        redisLockBiz.lockedApply(lockKey, lockVal, (t)->{
            mapper.deleteByPrimaryKey(entity);
            StatEmpTreat statEmpTreat = baseTreatmentProcessMapper.countTreatNumByDateAndDentist(orgId, dentistId, treatDate);
            if (!ObjectUtils.isEmpty(statEmpTreat)) {
                statEmpTreat.setCrtId(treatmentProcess.getAppointDentistId());
                statEmpTreat.setCrtTime(new Date(System.currentTimeMillis()));
                mapper.insertSelective(statEmpTreat);
            }
            return null;
        });
    }
}
