package com.yunya.modules.emr.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.yunya.feign.emr.domain.form.MedicalCommonRecordForm;
import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.feign.emr.domain.query.MedicalCommonRecordQueryForm;
import com.yunya.feign.emr.domain.vo.ExaminationsVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.modules.emr.biz.MedicalCommonRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.enums.FileSourceTypeEnum.MEDICAL_COMMON;

/**
 * @author 杨柳絮
 * @className medicalCommonRecordController
 * @description
 * @date 2020/7/28 13:26
 */
@Api(tags = "普通电子病历接口")
@RestController
@RequestMapping("/medical_common")
@CrossOrigin
public class MedicalCommonRecordController {

  @Autowired
  private MedicalCommonRecordBiz medicalCommonRecordBiz;
  @Autowired
  private RemoteSystemServiceFeign remoteSystemServiceFeign;
  @Autowired
  private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
  @Autowired
  private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;

  /**
   * 新增普通电子病历
   *
   * @param model
   * @return
   */
  @PostMapping("/create")
  @ApiOperation("新增数据")
  @CurrentUser
  @RepeatSubmit
  public ResponseResult create(@RequestBody @Valid MedicalCommonRecordModel model) {
    model.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    model.setCrtTime(new Date());
    return medicalCommonRecordBiz.create(model);
  }

  /**
   * 查询普通电子病历
   *
   * @param model
   * @return
   */
  @PostMapping("/findList")
  @ApiOperation("查询数据")
  public ResponseResult<List<MedicalCommonRecordModel>> findList(@RequestBody @Valid MedicalCommonRecordQueryForm model) {
    MedicalCommonRecord medicalCommonRecord = new MedicalCommonRecord();
    BeanUtils.copyProperties(model, medicalCommonRecord);
    List<MedicalCommonRecord> list = medicalCommonRecordBiz.findList(medicalCommonRecord);

    Map<Integer, List<XUploadFileVO>> fileMap = findXRayFilmList(list);
    //获取员工信息
    SysUserEmployeeModel sysUserEmployeeModel = new SysUserEmployeeModel();
    //查询总数不分页
    sysUserEmployeeModel.setWhetherPage(false);

    List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(sysUserEmployeeModel);
    Map<String, SysUserInfoDetail> employeeMap = new HashMap(16);
    employees.forEach(z -> employeeMap.put(z.getUserId() + "", z));

    List<MedicalCommonRecordModel> reList = new ArrayList<>();
    List<ExaminationsVO> list1 = null;
    JSONArray jsonArray = null;

    HashSet<Integer> hs=new HashSet();

    //处理和牙位有关字段的转换
    for (MedicalCommonRecord medical : list) {
      MedicalCommonRecordModel medicalCommonRecordModel = new MedicalCommonRecordModel();
      BeanUtils.copyProperties(medical, medicalCommonRecordModel);
      // 照片影像
      medicalCommonRecordModel.setXRayFilms(fileMap.get(medical.getId()));
      medicalCommonRecordModel.setMajorDentistName(employeeMap.get(medicalCommonRecordModel.getMajorDentistId()+"").getName());

      if (!StrUtil.isEmpty(medical.getExamination())) {
        jsonArray = JSONArray.parseArray(medical.getExamination());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setExamination(list1);
      }
      if (!StrUtil.isEmpty(medical.getDiagnosis())) {
        jsonArray = JSONArray.parseArray(medical.getDiagnosis());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setDiagnosis(list1);
      }
      if (!StrUtil.isEmpty(medical.getPlan())) {
        jsonArray = JSONArray.parseArray(medical.getPlan());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setPlan(list1);
      }
      if (!StrUtil.isEmpty(medical.getTreatment())) {
        jsonArray = JSONArray.parseArray(medical.getTreatment());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setTreatment(list1);
      }
      reList.add(medicalCommonRecordModel);
      //获取就诊ID列表
      hs.add(medical.getTreatmentId());
    }
    if(hs.size()>0){
      //获取就诊列表
      List<TreatmentRecordExtendVO> tLists = remoteTreatmentServiceFeign.findTreatmentRecordByIds(hs);
      Map<String, TreatmentRecordExtendVO> tListsMap = new HashMap(16);
      tLists.forEach(z -> tListsMap.put(z.getId() + "", z));
      //获取门诊
      OrganizationModel organizationModel = new OrganizationModel();
      organizationModel.setWhetherPage(false);
      List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
      Map<String, OrganizationInfoDetail> cliListsMap = new HashMap(16);
      clinics.forEach(z -> cliListsMap.put(z.getId() + "", z));

      //赋予就诊时间
      for(MedicalCommonRecordModel medicalModel : reList){
        TreatmentRecordExtendVO treatmentRecordExtendVO = tListsMap.get(medicalModel.getTreatmentId().toString());
        if(treatmentRecordExtendVO!=null){
          medicalModel.setTreatmentTime(treatmentRecordExtendVO.getTreatStartTime());
          medicalModel.setCompanyName(cliListsMap.get(treatmentRecordExtendVO.getOrgId().toString()).getName());
        }
      }
    }

    return ResponseUtil.success(reList);
  }

  private Map<Integer, List<XUploadFileVO>> findXRayFilmList(List<MedicalCommonRecord> medicalCommonRecords) {
    Map<Integer, List<XUploadFileVO>> result = new HashMap<>(16);
    if (StringHelper.isNotEmpty(medicalCommonRecords)) {
      List<Integer> medicalIds = medicalCommonRecords.stream().map(MedicalCommonRecord::getId).collect(Collectors.toList());
      XUploadFileQuery query = new XUploadFileQuery();
      query.setWhetherPage(false);
      query.setSourceIds(medicalIds);
      query.setSourceType(MEDICAL_COMMON.getCode());
      List<XUploadFileVO> files = remoteTreatmentOtherFeign.findXUploadFileList(query);
      if (StringHelper.isNotEmpty(files)) {
        files.forEach(file->{
          Integer sourceId = file.getSourceId();
          List<XUploadFileVO> list = result.get(sourceId);
          if (list == null) {
            list = new ArrayList<>();
          }
          list.add(file);
          result.put(sourceId, list);
        });
      }
    }
    return result;
  }

  /**
   * 修改普通电子病历（
   * 当天24点之前可随意修改，超过24点需要提交审核修改并将记录插入历史表）
   *
   * @param model
   * @return
   */
  @PostMapping("/update")
  @ApiOperation("修改普通电子病历（当天24点之前可随意修改，超过24点需要提交审核修改并将修改前的历史记录插入历史表 不走这个方法）")
  @CurrentUser
  public ResponseResult update(@RequestBody @Valid MedicalCommonRecordForm model) {
    model.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    model.setUpdTime(new Date());
    return ResponseUtil.success(medicalCommonRecordBiz.updateMedical(model));
  }

  @PostMapping("/updateAfter")
  @ApiOperation("修改普通电子病历（超过24点需要提交审核修改,并将修改后的历史记录插入历史表）")
  @CurrentUser
  public ResponseResult updateAfter(@RequestBody @Valid MedicalCommonRecordForm model) {
    model.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    model.setUpdTime(new Date());
    return ResponseUtil.success(medicalCommonRecordBiz.updateMedicalAfter(model));
  }
}
