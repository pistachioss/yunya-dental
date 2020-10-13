package com.yunya.middletable.service.impl;

import com.yunya.middletable.dao.patient.PatientBaseInfoMapper;
import com.yunya.middletable.dao.system.DictionaryItemMapper;
import com.yunya.middletable.service.ShardingService;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.DictionaryItem;
import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShardingServiceImpl implements ShardingService {

    @Autowired
    DictionaryItemMapper dictionaryItemMapper;
    @Autowired
    PatientBaseInfoMapper patientBaseInfoMapper;

    @Override
    public void test() {

        List<PatientBaseInfo> patientBaseInfoList = patientBaseInfoMapper.selectAll();
        System.out.println("PatientBaseInfo表返回数量："+ patientBaseInfoList.size());

        List<DictionaryItem> dictionaryItemList = dictionaryItemMapper.selectAll();
        System.out.println("DictionaryItem表返回数量：" + dictionaryItemList.size());
    }
}
