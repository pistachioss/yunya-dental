package com.yunya.modules.patient_central.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.patient_central.PatientExtInfo;
import com.yunya.modules.patient_central.mapper.PatientExtInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

/**
 * 简单介绍:</br> 患者其他信息（标签,疾病史,过敏原） 业务成
 *
 * @author: WY
 * @date 2020/7/28 13:33
 * @description: 患者其他信息（标签,疾病史,过敏原） 增删查改
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientExtInfoBiz extends BaseBiz<PatientExtInfoMapper, PatientExtInfo> {

    @Autowired
    private PatientExtInfoMapper patientExtInfoMapper;

    public List<PatientExtInfo> listByTagType(Collection<Integer> type) {
        Example example = new Example(PatientExtInfo.class);
        example.createCriteria()
                .andIn("dictItemId", type);
        return patientExtInfoMapper.selectByExample(example);
    }
}
