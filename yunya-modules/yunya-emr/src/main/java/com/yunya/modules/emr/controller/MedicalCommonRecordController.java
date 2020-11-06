package com.yunya.modules.emr.controller;

import com.alibaba.fastjson.JSONArray;

import com.yunya.feign.emr.domain.form.MedicalCommonRecordForm;
import com.yunya.feign.emr.domain.query.MedicalCommonRecordQueryForm;
import com.yunya.feign.emr.domain.model.MedicalCommonRecordModel;
import com.yunya.feign.emr.domain.vo.ExaminationsVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.emr.MedicalCommonRecord;

import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.emr.biz.MedicalCommonRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.*;

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
  /**
   * 新增普通电子病历
   *
   * @param model
   * @return
   */
  @PostMapping("/create")
  @ApiOperation("新增数据")
  @CurrentUser
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
  public ResponseResult findList(@RequestBody @Valid MedicalCommonRecordQueryForm model) {
    MedicalCommonRecord medicalCommonRecord = new MedicalCommonRecord();
    BeanUtils.copyProperties(model, medicalCommonRecord);
    List<MedicalCommonRecord> list = medicalCommonRecordBiz.findList(medicalCommonRecord);

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
      medicalCommonRecordModel.setMajorDentistName(employeeMap.get(medicalCommonRecordModel.getMajorDentistId()+"").getName());

      if (medical.getExamination() != null) {
        jsonArray = JSONArray.parseArray(medical.getExamination());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setExamination(list1);
      }
      if (medical.getDiagnosis() != null) {
        jsonArray = JSONArray.parseArray(medical.getDiagnosis());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setDiagnosis(list1);
      }
      if (medical.getPlan() != null) {
        jsonArray = JSONArray.parseArray(medical.getPlan());
        list1 = jsonArray.toJavaList(ExaminationsVO.class);
        medicalCommonRecordModel.setPlan(list1);
      }
      if (medical.getTreatment() != null) {
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
      List<TreatmentRecord> tLists = remoteTreatmentServiceFeign.findTreatmentRecordByIds(hs);
      Map<String, TreatmentRecord> tListsMap = new HashMap(16);
      tLists.forEach(z -> tListsMap.put(z.getId() + "", z));
      //获取门诊
      OrganizationModel organizationModel = new OrganizationModel();
      organizationModel.setWhetherPage(false);
      List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
      Map<String, OrganizationInfoDetail> cliListsMap = new HashMap(16);
      clinics.forEach(z -> cliListsMap.put(z.getId() + "", z));

      //赋予就诊时间
      for(MedicalCommonRecordModel medicalModel : reList){
        medicalModel.setTreatmentTime(tListsMap.get(medicalModel.getTreatmentId().toString()).getTreatStartTime());
        medicalModel.setCompanyName(cliListsMap.get(tListsMap.get(medicalModel.getTreatmentId().toString()).getOrgId().toString()).getName());
      }
    }
    return ResponseUtil.success(reList);
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
