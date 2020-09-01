package com.yunya.feign.emr.valid;

import com.yunya.feign.emr.domain.form.*;
import lombok.extern.slf4j.*;
import org.hibernate.validator.spi.group.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Slf4j
public class MedicalTempGroupSequenceProvider implements DefaultGroupSequenceProvider<MedicalTemplateForm> {
    @Override
    public List<Class<?>> getValidationGroups(MedicalTemplateForm medicalTemplateForm) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(MedicalTemplateForm.class);
        if (medicalTemplateForm != null) {
            Integer type = medicalTemplateForm.getType();
            log.info("病例模板类型为type：[{}]执行校验", type);
            if (type == 0) {
                defaultGroupSequence.add(MedicalTemplateForm.FirstVisitGroupNotView.class);
            } else {
                defaultGroupSequence.add(MedicalTemplateForm.FollowUpGroupNotView.class);
            }
        }
        return defaultGroupSequence;
    }
}
