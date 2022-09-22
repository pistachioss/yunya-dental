package com.yunya.modules.system.biz;

import com.google.common.collect.Maps;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.TechnicalTitleEnum;
import com.yunya.models.system.QztDoctor;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.mapper.QztDoctorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

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
        Map<String, String> technicalTitle = Maps.newHashMapWithExpectedSize(16);
        Arrays.stream(TechnicalTitleEnum.values()).forEach(enu -> technicalTitle.put(enu.getCode(), enu.getValue()));
//        Arrays.stream(TechnicalTitleEnum.values()).reduce(Ma)
        //职业范围
        Map<String, String> scopePractice = Maps.newHashMapWithExpectedSize(16);
        //抗菌药物处方权
        Map<String, String> antibiosisAuthority = Maps.newHashMapWithExpectedSize(16);
    }

    public void add(QztAddDoctorModel model) {

    }

    public void select() {
    }
}
