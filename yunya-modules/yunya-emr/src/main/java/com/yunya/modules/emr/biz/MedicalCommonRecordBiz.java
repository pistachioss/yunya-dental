package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONArray;
import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.emr.MedicalRecordHistory;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import com.yunya.modules.emr.mapper.MedicalRecordHistoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalCommonRecordBiz extends BaseBiz<MedicalCommonRecordMapper, MedicalCommonRecord> {

  @Autowired private MedicalRecordHistoryMapper medicalRecordHistoryMapper;

  public int create(MedicalCommonRecordModel model){
    MedicalCommonRecord medicalCommonRecord = new MedicalCommonRecord();
    BeanUtils.copyProperties(model,medicalCommonRecord);
    JSONArray jsonArray = (JSONArray) JSONArray.toJSON(model.getExamination());
    if(model.getExamination()!=null){
      medicalCommonRecord.setExamination(jsonArray.toJSONString());
    }
    if(model.getDiagnosis()!=null){
      jsonArray = (JSONArray) JSONArray.toJSON(model.getDiagnosis());
      medicalCommonRecord.setDiagnosis(jsonArray.toJSONString());
    }
    if(model.getPlan()!=null){
      jsonArray = (JSONArray) JSONArray.toJSON(model.getPlan());
      medicalCommonRecord.setPlan(jsonArray.toJSONString());
    }
    if(model.getTreatment()!=null){
      jsonArray = (JSONArray) JSONArray.toJSON(model.getTreatment());
      medicalCommonRecord.setTreatment(jsonArray.toJSONString());
    }

    MedicalRecordHistory medicalRecordHistory = new MedicalRecordHistory();
    BeanUtils.copyProperties(medicalCommonRecord,medicalRecordHistory);
    int result = mapper.insertMedical(medicalCommonRecord);
    if(result>0 && medicalCommonRecord.getStatus()==0){//主治医生新增病历时，历史表中同步插入一条数据
      medicalRecordHistory.setMedicalRecordId(medicalCommonRecord.getId().toString());
      medicalRecordHistoryMapper.insert(medicalRecordHistory);
    }
    return result;
  }

  public List<MedicalCommonRecord> findList(MedicalCommonRecord model){
      return mapper.findList(model);
  }

}
