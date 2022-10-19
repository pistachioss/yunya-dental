package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.emr.domain.form.MedicalCommonRecordForm;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.vo.MedicalGeneralNumVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.model.MedicalRayFilmModel;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.*;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.emr.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya.framework.common.enums.FileSourceTypeEnum.*;

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
    private MedicalCheckRecordBiz medicalCheckRecordBiz;
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Autowired
    private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;
    @Resource
    private RedisUtils redisUtils;

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
            //如果参数有审批时间(代表是就诊24小时后 通过申请来新增病历) 且审批截止时间超过当前时间 不可进行审批
            if (date.before(now)) {
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
        //false处为判断当前时间是否超过就诊当天24点
        if(model.getTime()==null){
            if (model.getDeadTime() == null && LocalDateTime.now().isAfter(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX))) {
                throw new ClientServiceException("超过就诊当天24点", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }
        int result = mapper.insertMedical(medicalCommonRecord);

        //主治医生新增病历时，历史表中同步插入一条数据
        if (result > 0 && medicalCommonRecord.getStatus() == 0) {
            medicalRecordHistoryBiz.insertMedicalHistory(medicalCommonRecord);
            //更新医生的申请变更时间
            medicalApprovalBiz.updateDocApplyChangeTime(medicalCommonRecord.getTreatmentId());
        }

        // 保存照片影像
        saveXRayFile2XUploadFile(medicalCommonRecord.getId(), model.getXrayFilms(), model.getCrtTime());
        // 保存检查记录
        medicalCheckRecordBiz.saveCheckRecord(medicalCommonRecord.getId(), model.getCheckRecords());
        //助手新增病历时，审核表中同步插入一条数据
        if (result > 0 && medicalCommonRecord.getStatus() == 1) {
            DraftMedicalApplyModel draftMedicalApplyModel = new DraftMedicalApplyModel();
            ApplyBaseModel applyBase = new ApplyBaseModel();
            applyBase.setEventId(medicalCommonRecord.getId());
            applyBase.setProposerId(medicalCommonRecord.getCrtId());
            applyBase.setApproverId(medicalCommonRecord.getMajorDentistId());
            draftMedicalApplyModel.setApplyBase(applyBase);
            draftMedicalApplyModel.setId(model.getApprovalId());
            ResponseResult responseResult = medicalApprovalBiz.applyAddDraftCase(draftMedicalApplyModel);
            if (responseResult.getStatus() != 0) {
                throw new ClientServiceException(responseResult.getMsg(), responseResult.getStatus());
            }
        }
        //修改就诊记录病历书写状态
        remoteTreatmentServiceFeign.updateTreatmentRecord(model.getTreatmentId());
        //插入常用词条使用频率
        if (model.getMedicalGeneralNumList() != null && model.getMedicalGeneralNumList().size() > 0) {
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
        return ResponseUtil.success(medicalCommonRecord.getId());
    }

    private void saveXRayFile2XUploadFile(Integer medicalId, List<XUploadFileVO> files, Date crtTime) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        MedicalRayFilmModel model = new MedicalRayFilmModel();
        model.setSourceId(medicalId);
        model.setSourceType(MEDICAL_COMMON.getCode());
        model.setRayFiles(files);
        model.setCrtId(userId);
        if (ObjectUtils.isEmpty(crtTime)) {
            crtTime = new Date(System.currentTimeMillis());
        }
        model.setCrtTime(crtTime);
        remoteTreatmentOtherFeign.saveXRayFile2XUploadFile(model);
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
        //判断修改人是否为当前病历的创建人
        if (!medicalCommonRecordForm.getCrtId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
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

        // 保存照片影像
        saveXRayFile2XUploadFile(medicalcopy.getId(), medicalCommonRecordForm.getXrayFilms(), medicalcopy.getUpdTime());
        // 保存检查记录
        medicalCheckRecordBiz.saveCheckRecord(medicalcopy.getId(), medicalCommonRecordForm.getCheckRecords());

        if (medicalCommonRecordForm.getMedicalGeneralNumList() != null && medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {//插入常用词条使用频率
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
        //判断修改人是否为当前病历的创建人
        if (!medicalCommonRecordForm.getCrtId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
            throw new ClientServiceException("创建者才能修改病历", OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        MedicalCommonRecord medicalcopy = new MedicalCommonRecord();
        BeanUtils.copyProperties(medicalCommonRecordForm, medicalcopy);
        //转化四个和牙位有关的字段信息
        JSONArray jsonArray = (JSONArray) JSONArray.toJSON(medicalCommonRecordForm.getExamination());
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
        if(medicalCommonRecordForm.getStatus() == 2 || medicalCommonRecordForm.getStatus() == 3){
            medicalcopy.setStatus(1);
        }
        re = mapper.updateByPrimaryKey(medicalcopy);
        //判断 通过才可以添加记录
        //主治医生修改病历时，历史表中同步插入一条数据
        if (re > 0 && medicalcopy.getStatus() == 0) {
            medicalRecordHistoryBiz.insertMedicalHistory(medicalcopy);
        }

        // 保存照片影像
        saveXRayFile2XUploadFile(medicalcopy.getId(), medicalCommonRecordForm.getXrayFilms(), medicalcopy.getUpdTime());
        // 保存检查记录
        medicalCheckRecordBiz.saveCheckRecord(medicalcopy.getId(), medicalCommonRecordForm.getCheckRecords());
        //助手修改病历通过时，审核表中同步插入一条数据
        if (re > 0 && (medicalCommonRecordForm.getStatus() == 2 || medicalCommonRecordForm.getStatus() == 3)) {
            DraftMedicalApplyModel draftMedicalApplyModel = new DraftMedicalApplyModel();
            ApplyBaseModel applyBase = new ApplyBaseModel();
            applyBase.setEventId(medicalcopy.getId());
            applyBase.setProposerId(medicalcopy.getCrtId());
            applyBase.setApproverId(medicalcopy.getMajorDentistId());
            draftMedicalApplyModel.setApplyBase(applyBase);
            draftMedicalApplyModel.setId(medicalCommonRecordForm.getApprovalId());
            medicalApprovalBiz.applyUpdateDraftCase(draftMedicalApplyModel);
        }
        //插入常用词条使用频率
        if (medicalCommonRecordForm.getMedicalGeneralNumList() != null && medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {
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
        //判断修改人是否为当前病历的创建人
        if (!medicalCommonRecordForm.getCrtId().equals(Integer.valueOf(BaseContextHandler.getUserID())) && !medicalCommonRecordForm.getMajorDentistId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
            throw new ClientServiceException("创建者或主治医生才能修改病历", OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        MedicalCommonRecord medicalcopy = new MedicalCommonRecord();
        BeanUtils.copyProperties(medicalCommonRecordForm, medicalcopy);
        //转化四个和牙位有关的字段信息
        JSONArray jsonArray = (JSONArray) JSONArray.toJSON(medicalCommonRecordForm.getExamination());
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
        //助手修改病历通过时，审核表中同步插入一条数据
        if (re > 0 && medicalcopy.getStatus() == 2) {
            medicalRecordHistoryBiz.insertMedicalHistory(medicalcopy);
            medicalCheckRecordBiz.saveCheckRecord(medicalCommonRecordForm.getId(), medicalCommonRecordForm.getCheckRecords());
            //修改就诊记录病历书写状态
            remoteTreatmentServiceFeign.updateTreatmentRecord(medicalcopy.getTreatmentId());
        }

        // 保存照片影像
        saveXRayFile2XUploadFile(medicalcopy.getId(), medicalCommonRecordForm.getXrayFilms(), medicalcopy.getUpdTime());
        // 保存检查记录
        medicalCheckRecordBiz.saveCheckRecord(medicalcopy.getId(), medicalCommonRecordForm.getCheckRecords());
        //插入常用词条使用频率
        if (medicalCommonRecordForm.getMedicalGeneralNumList() != null && medicalCommonRecordForm.getMedicalGeneralNumList().size() > 0) {
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


    public MedicalCommonRecord findMedicalIllegaHistoryById(Integer medicalRecordId) {
        return mapper.selectMedicalIllnessHistoryById(medicalRecordId);
    }

    public TreatmentRecord findTreatmentByMedicalId(Integer medicalRecordId) {
        MedicalCommonRecord medical = mapper.selectByPrimaryKey(medicalRecordId);
        return remoteTreatmentServiceFeign.findTreatmentRecordById(medical.getTreatmentId());
    }

    public void draft(MedicalCommonRecordModel model) {
        LocalDate now = LocalDate.now();
        String date = DateUtil.format(now, "yyyyMMdd");
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String key = buildLockCacheKey(DRAFT_TEMP, date);
        String hashKey = loginUserId + "_" + model.getTreatmentId();
        if (redisUtils.hasKey(key)) {
            redisUtils.hput(key, hashKey, model);
        } else {
            long remainSeconds = Duration.between(LocalDateTime.of(now, LocalTime.MAX), java.time.LocalDateTime.now()).getSeconds();
            redisUtils.hPutAndExpire(RedisConstants.buildLockCacheKey(DRAFT_TEMP, date)
                    , loginUserId + "_" + model.getTreatmentId(), JSONObject.toJSONString(model), Math.abs(remainSeconds));
        }
    }

    public MedicalCommonRecordModel draftDetail(Integer treatmentId) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String date = DateUtil.format(LocalDate.now(), "yyyyMMdd");
        String key = buildLockCacheKey(DRAFT_TEMP, date);
        Object hget = redisUtils.hget(key, loginUserId + "_" + treatmentId);
        return JSONObject.parseObject(hget.toString(), MedicalCommonRecordModel.class);
    }

    public void removeDraftTemp(Integer treatmentId) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        String date = DateUtil.format(LocalDate.now(), "yyyyMMdd");
        String key = buildLockCacheKey(DRAFT_TEMP, date);
        redisUtils.hdelete(key, loginUserId + "_" + treatmentId);
    }
}
