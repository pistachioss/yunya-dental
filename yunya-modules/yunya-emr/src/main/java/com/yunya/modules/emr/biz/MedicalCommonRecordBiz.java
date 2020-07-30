package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONArray;
import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalCommonRecordBiz extends BaseBiz<MedicalCommonRecordMapper, MedicalCommonRecord> {

  public int create(MedicalCommonRecordModel model){
    MedicalCommonRecord medicalCommonRecord = new MedicalCommonRecord();
    BeanUtils.copyProperties(model,medicalCommonRecord);
    JSONArray jsonArray = (JSONArray) JSONArray.toJSON(model.getExaminations());
    if(model.getExaminations()!=null){
      medicalCommonRecord.setExamination(jsonArray.toJSONString());
    }
    if(model.getDiagnosiss()!=null){
      jsonArray = (JSONArray) JSONArray.toJSON(model.getDiagnosiss());
      medicalCommonRecord.setDiagnosis(jsonArray.toJSONString());
    }
    if(model.getPlans()!=null){
      jsonArray = (JSONArray) JSONArray.toJSON(model.getPlans());
      medicalCommonRecord.setPlan(jsonArray.toJSONString());
    }
    if(model.getTreatments()!=null){
      jsonArray = (JSONArray) JSONArray.toJSON(model.getTreatments());
      medicalCommonRecord.setTreatment(jsonArray.toJSONString());
    }
    return mapper.insert(medicalCommonRecord);
  }

}
