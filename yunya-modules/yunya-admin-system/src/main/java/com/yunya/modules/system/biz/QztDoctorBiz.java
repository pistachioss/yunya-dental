package com.yunya.modules.system.biz;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.*;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.system.QztDoctor;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.mapper.QztDoctorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description: 全诊通
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class QztDoctorBiz extends BaseBiz<QztDoctorMapper, QztDoctor> {
    /**
     * 注入对象
     */
    @Resource
    private QztDoctorMapper doctorMapper;

    public static final Map<String, Map<String, String>> qztSelect = Maps.newHashMap();

    static {
        //职称
        Map<String, String> technicalTitle = Arrays.stream(TechnicalTitleEnum.values()).reduce(Maps.newHashMap(), (u, t) -> {
            u.put(t.getCode(), t.getValue());
            return u;
        }, null);
        //职业范围
        Map<String, String> scopePractice = Arrays.stream(ScopePracticeEnum.values()).reduce(Maps.newHashMap(), (u, t) -> {
            u.put(t.getCode(), t.getValue());
            return u;
        }, null);
        //抗菌药物处方权
        Map<String, String> antibiosisAuthority = Arrays.stream(AntibiosisAuthorityEnum.values()).reduce(Maps.newHashMap(), (u, t) -> {
            u.put(t.getCode(), t.getValue());
            return u;
        }, null);
        qztSelect.put("technicalTitle", technicalTitle);
        qztSelect.put("scopePractice", scopePractice);
        qztSelect.put("antibiosisAuthority", antibiosisAuthority);
    }

    public void add(QztAddDoctorModel model) {
        String idCard = model.getIdCard();
        //医生资格证号
        String qualification = model.getQualification();
        Example example = new Example(QztDoctor.class);
        example.createCriteria().andEqualTo("idCard", idCard)
                .andEqualTo("qualification", qualification);
        QztDoctor qztDoctor = mapper.selectOneByExample(example);
        QztDoctor doctor = BeanCopierUtils.generalCopyBean(model, QztDoctor.class);
        if (Objects.isNull(qztDoctor)) {
            mapper.insert(doctor);
            return;
        }
        if (!Objects.equals(model.getDoctorName(), qztDoctor.getDoctorName())) {
            throw ClientServiceException.wrap(9999, "该医生的资格证号已被关联");
        }
        doctor.setRelateUserIds(Joiner.on(",").join(qztDoctor.getRelateUserIds(), model.getUserId()));
        doctor.setId(qztDoctor.getId());
        mapper.updateByPrimaryKey(doctor);
    }

    public Map<String, Map<String, String>> select() {
        return qztSelect;
    }
}
