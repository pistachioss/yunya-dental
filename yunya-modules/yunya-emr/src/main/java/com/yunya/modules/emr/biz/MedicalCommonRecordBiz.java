package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.*;
import com.yunya.feign.emr.domain.form.*;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.context.*;
import com.yunya.framework.common.exception.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.emr.*;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.emr.mapper.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import tk.mybatis.mapper.entity.*;

import java.text.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

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
  @Autowired
  private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

  public ResponseResult create(MedicalCommonRecordModel model) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    Date now = new Date();
    if (model.getDeadTime() != null) {
      try {
        date = simpleDateFormat.parse(model.getDeadTime());
        now = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        model.setTime(new Date());
      } catch (Exception e) {
        throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
      }
      if (date.before(now)) {//如果参数有审批时间(代表是就诊24小时后 通过申请来新增病历) 且审批截止时间超过当前时间 不可进行审批
        throw new ClientServiceException("超过审批时间", OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
    }

    Example example = new Example(MedicalCommonRecord.class);
    example.createCriteria().andEqualTo("treatmentId", model.getTreatmentId());
    if (mapper.selectByExample(example).size() > 0) {
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
    //调用figen获取就诊信息 根据就诊id  //是否超过当前24小时
    TreatmentRecord treatmentRecord = remoteTreatmentServiceFeign.findTreatmentRecordById(model.getTreatmentId());
    Instant instant = treatmentRecord.getTreatStartTime().toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
    if (model.getDeadTime() == null && LocalDateTime.now().isAfter(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX))) {//false处为判断当前时间是否超过就诊当天24点
      throw new ClientServiceException("超过就诊当天24点", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }

    int result = mapper.insertMedical(medicalCommonRecord);
    if (result > 0 && medicalCommonRecord.getStatus() == 0) {//主治医生新增病历时，历史表中同步插入一条数据
      medicalRecordHistoryBiz.insertMedicalHistory(medicalCommonRecord);
    }
    if (result > 0 && medicalCommonRecord.getStatus() == 1) {//助手新增病历时，审核表中同步插入一条数据
      DraftMedicalApplyModel draftMedicalApplyModel = new DraftMedicalApplyModel();
      ApplyBaseModel applyBase = new ApplyBaseModel();
      applyBase.setEventId(medicalCommonRecord.getId());
      applyBase.setProposerId(medicalCommonRecord.getCrtId());
      applyBase.setApproverId(medicalCommonRecord.getMajorDentistId());
      draftMedicalApplyModel.setApplyBase(applyBase);
      ResponseResult responseResult = medicalApprovalBiz.applyAddDraftCase(draftMedicalApplyModel);
      if (responseResult.getStatus() != 200) {
        return responseResult;
      }
    }
    remoteTreatmentServiceFeign.updateTreatmentRecord(model.getTreatmentId());//修改就诊记录病历书写状态
    if (model.getMedicalGeneralNumList()!=null&&model.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
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
    return ResponseUtil.success(result);
  }

  public List<MedicalCommonRecord> findList(MedicalCommonRecord model) {
    return mapper.findList(model);
  }

  /**
   * 当天24点内医生进行病历修改 调用的方法
   *
   * @param medicalCommonRecordForm
   * @return
   */
  public int updateMedical(MedicalCommonRecordForm medicalCommonRecordForm) {
    if (!medicalCommonRecordForm.getCrtId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {//判断修改人是否为当前病历的创建人
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

    //调用figen获取就诊信息 根据就诊id  //是否超过当前24小时
    TreatmentRecord treatmentRecord = remoteTreatmentServiceFeign.findTreatmentRecordById(medicalCommonRecordForm.getTreatmentId());
    Instant instant = treatmentRecord.getTreatStartTime().toInstant();
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

    if (LocalDateTime.now().isAfter(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX))) {//判断当前时间是否超过就诊当天24点
      throw new ClientServiceException("已过修改时间，请提交审核", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    re = mapper.updateByPrimaryKey(medicalcopy);
    MedicalRecordHistory medicalRecordHistory = new MedicalRecordHistory();//24小时内 历史表与病历表同步修改
    BeanUtils.copyProperties(medicalcopy, medicalRecordHistory);
    medicalRecordHistory.setId(null);
    medicalRecordHistory.setMedicalRecordId(medicalcopy.getId().toString());
    Example example = new Example(MedicalRecordHistory.class);
    example.createCriteria().andEqualTo("medicalRecordId", medicalcopy.getId());
    medicalRecordHistoryMapper.updateByExampleSelective(medicalRecordHistory, example);

    if (medicalCommonRecordForm.getMedicalGeneralNumList()!=null&&medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
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

  /**
   * 病历变更通过后 调用的修改方法
   *
   * @param medicalCommonRecordForm
   * @return
   */
  public int updateMedicalAfter(MedicalCommonRecordForm medicalCommonRecordForm) {
    if (!medicalCommonRecordForm.getCrtId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {//判断修改人是否为当前病历的创建人
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
    re = mapper.updateByPrimaryKey(medicalcopy);
    //判断 通过才可以添加记录
    if (re > 0 && medicalcopy.getStatus() == 0) {//主治医生修改病历时，历史表中同步插入一条数据
      medicalRecordHistoryBiz.insertMedicalHistory(medicalcopy);
    }
    if (re > 0 && (medicalcopy.getStatus() == 2 || medicalcopy.getStatus() == 3)) {//助手修改病历通过时，审核表中同步插入一条数据
      DraftMedicalApplyModel draftMedicalApplyModel = new DraftMedicalApplyModel();
      ApplyBaseModel applyBase = new ApplyBaseModel();
      applyBase.setEventId(medicalcopy.getId());
      applyBase.setProposerId(medicalcopy.getCrtId());
      applyBase.setApproverId(medicalcopy.getMajorDentistId());
      draftMedicalApplyModel.setApplyBase(applyBase);
      medicalApprovalBiz.applyUpdateDraftCase(draftMedicalApplyModel);
    }

    if (medicalCommonRecordForm.getMedicalGeneralNumList()!=null&&medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
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

  /**
   * 病历审核通过或拒绝后 走的方法（只有助手的病历才会审核通过或拒绝，医生提交的审核直接通过）
   *
   * @param medicalCommonRecordForm
   * @return
   */
  public int updateMedicalApproval(MedicalCommonRecordForm medicalCommonRecordForm) {
    if (!medicalCommonRecordForm.getCrtId().equals(Integer.valueOf(BaseContextHandler.getUserID())) && !medicalCommonRecordForm.getMajorDentistId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {//判断修改人是否为当前病历的创建人
      throw new ClientServiceException("创建者或主治医生才能修改病历", OperationCodeConstants.OBJECT_EDIT_FAIL);
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
    re = mapper.updateByPrimaryKey(medicalcopy);
    if (re > 0 && medicalcopy.getStatus() == 2) {//助手修改病历通过时，审核表中同步插入一条数据
      medicalRecordHistoryBiz.insertMedicalHistory(medicalcopy);
    }
    if (medicalCommonRecordForm.getMedicalGeneralNumList()!=null&&medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
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
