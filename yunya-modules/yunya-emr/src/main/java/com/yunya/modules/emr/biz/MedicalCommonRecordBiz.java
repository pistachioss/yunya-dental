package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONArray;
import com.yunya.feign.emr.domain.form.MedicalCommonRecordForm;
import com.yunya.feign.emr.domain.model.ApplyBaseModel;
import com.yunya.feign.emr.domain.model.DraftMedicalApplyModel;
import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.feign.emr.domain.vo.MedicalGeneralNumVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.emr.MedicalGeneralNum;
import com.yunya.models.emr.MedicalRecordHistory;
import com.yunya.modules.emr.mapper.MedicalCommonRecordMapper;
import com.yunya.modules.emr.mapper.MedicalGeneralNumMapper;
import com.yunya.modules.emr.mapper.MedicalRecordHistoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MedicalCommonRecordBiz extends BaseBiz<MedicalCommonRecordMapper, MedicalCommonRecord> {

  @Autowired
  private MedicalRecordHistoryMapper medicalRecordHistoryMapper;
  @Autowired
  private MedicalGeneralNumMapper medicalGeneralNumMapper;
  @Autowired
  private MedicalApprovalBiz medicalApprovalBiz;
  @Autowired
  private MedicalRecordHistoryBiz medicalRecordHistoryBiz;

  public int create(MedicalCommonRecordModel model) {
    Example example = new Example(MedicalCommonRecord.class);
    example.createCriteria().andEqualTo("treatmentId",model.getTreatmentId());
    if(mapper.selectByExample(example).size()>0){
      throw new ClientServiceException("当前就诊记录已有病历", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    MedicalCommonRecord medicalCommonRecord = new MedicalCommonRecord();
    BeanUtils.copyProperties(model, medicalCommonRecord);
    JSONArray jsonArray = (JSONArray) JSONArray.toJSON(model.getExamination());
    if (model.getExamination() != null) {
      medicalCommonRecord.setExamination(jsonArray.toJSONString());
    }
    if (model.getDiagnosis() != null) {
      jsonArray = (JSONArray) JSONArray.toJSON(model.getDiagnosis());
      medicalCommonRecord.setDiagnosis(jsonArray.toJSONString());
    }
    if (model.getPlan() != null) {
      jsonArray = (JSONArray) JSONArray.toJSON(model.getPlan());
      medicalCommonRecord.setPlan(jsonArray.toJSONString());
    }
    if (model.getTreatment() != null) {
      jsonArray = (JSONArray) JSONArray.toJSON(model.getTreatment());
      medicalCommonRecord.setTreatment(jsonArray.toJSONString());
    }

//    MedicalRecordHistory medicalRecordHistory = new MedicalRecordHistory();
//    BeanUtils.copyProperties(medicalCommonRecord, medicalRecordHistory);
    int result = mapper.insertMedical(medicalCommonRecord);
    if (result > 0 && medicalCommonRecord.getStatus() == 0) {//主治医生新增病历时，历史表中同步插入一条数据
      medicalRecordHistoryBiz.insertMedicalHistory(medicalCommonRecord);
    }
    if(result > 0 && medicalCommonRecord.getStatus() == 1){//助手新增病历时，审核表中同步插入一条数据
      DraftMedicalApplyModel draftMedicalApplyModel = new DraftMedicalApplyModel();
      ApplyBaseModel applyBase = new ApplyBaseModel();
      applyBase.setEventId(medicalCommonRecord.getId());
      applyBase.setProposerId(medicalCommonRecord.getCrtId());
      applyBase.setApproverId(medicalCommonRecord.getMajorDentistId());
      draftMedicalApplyModel.setApplyBase(applyBase);
      medicalApprovalBiz.applyAddDraftCase(draftMedicalApplyModel);
    }
    if (model.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
      List<MedicalGeneralNum> numList = new ArrayList<>();
      for (MedicalGeneralNumVO m : model.getMedicalGeneralNumList()) {
        MedicalGeneralNum medicalGeneralNum = new MedicalGeneralNum();
        medicalGeneralNum.setCrtTime(new Date());
        medicalGeneralNum.setGeneralId(m.getGeneralId());
        medicalGeneralNum.setMedicalId(medicalCommonRecord.getId());
        medicalGeneralNum.setNumber(m.getNumber());
        numList.add(medicalGeneralNum);
      }
      medicalGeneralNumMapper.saveList(numList);
    }
    return result;
  }

  public List<MedicalCommonRecord> findList(MedicalCommonRecord model) {
    return mapper.findList(model);
  }

  public int updateMedical(MedicalCommonRecordForm medicalCommonRecordForm) {
    if (medicalCommonRecordForm.getCrtId() != Integer.valueOf(BaseContextHandler.getUserID())) {//判断修改人是否为当前病历的创建人
      throw new ClientServiceException("创建者才能修改病历", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    MedicalCommonRecord medicalcopy = new MedicalCommonRecord();
    BeanUtils.copyProperties(medicalCommonRecordForm, medicalcopy);

    JSONArray jsonArray = (JSONArray) JSONArray.toJSON(medicalCommonRecordForm.getExamination());//转化四个和牙位有关的字段信息
    if (medicalCommonRecordForm.getExamination() != null) {
      medicalcopy.setExamination(jsonArray.toJSONString());
    }
    if (medicalCommonRecordForm.getDiagnosis() != null) {
      jsonArray = (JSONArray) JSONArray.toJSON(medicalCommonRecordForm.getDiagnosis());
      medicalcopy.setDiagnosis(jsonArray.toJSONString());
    }
    if (medicalCommonRecordForm.getPlan() != null) {
      jsonArray = (JSONArray) JSONArray.toJSON(medicalCommonRecordForm.getPlan());
      medicalcopy.setPlan(jsonArray.toJSONString());
    }
    if (medicalCommonRecordForm.getTreatment() != null) {
      jsonArray = (JSONArray) JSONArray.toJSON(medicalCommonRecordForm.getTreatment());
      medicalcopy.setTreatment(jsonArray.toJSONString());
    }

    int re = 0;

    //调用figen获取就诊信息 medicalCommonRecordForm.getTreatmentId() 就诊id 未对接

    if (false) {//判断当前时间是否超过就诊当天24点
      re = mapper.updateByPrimaryKey(medicalcopy);
      MedicalRecordHistory medicalRecordHistory = new MedicalRecordHistory();
      BeanUtils.copyProperties(medicalcopy,medicalRecordHistory);
      medicalRecordHistory.setId(null);
      medicalRecordHistory.setMedicalRecordId(medicalcopy.getId().toString());
      Example example = new Example(MedicalRecordHistory.class);
      example.createCriteria().andEqualTo("medicalRecordId",medicalcopy.getId());
      medicalRecordHistoryMapper.updateByExampleSelective(medicalRecordHistory,example);
    } else {//超过当天24小时，修改病历需要提价审核 ，通过后在历史表中增加一条记录
      throw new ClientServiceException("已过修改时间，请提交审核", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    if (medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
      List<MedicalGeneralNum> numList = new ArrayList<>();
      for (MedicalGeneralNumVO m : medicalCommonRecordForm.getMedicalGeneralNumList()) {
        MedicalGeneralNum medicalGeneralNum = new MedicalGeneralNum();
        medicalGeneralNum.setCrtTime(new Date());
        medicalGeneralNum.setGeneralId(m.getGeneralId());
        medicalGeneralNum.setMedicalId(medicalCommonRecordForm.getId());
        medicalGeneralNum.setNumber(m.getNumber());
        numList.add(medicalGeneralNum);
      }
      medicalGeneralNumMapper.saveList(numList);
    }
    return re;
  }

}
