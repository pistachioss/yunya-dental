package com.yunya.modules.treatment.other.rpc;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.treatment_other.domain.model.MedicalRayFilmModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.FindAllRemindRecordVO;
import com.yunya.feign.treatment_other.domain.vo.NextVisitingRecordVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.VisitingRemind;
import com.yunya.models.treatment_other.XRayFilm;
import com.yunya.modules.treatment.other.biz.VisitingRecordBiz;
import com.yunya.modules.treatment.other.biz.XRayFilmBiz;
import com.yunya.modules.treatment.other.biz.XUploadFileBiz;
import com.yunya.modules.treatment.other.mapper.VisitingRecordMapper;
import com.yunya.modules.treatment.other.mapper.VisitingRemindMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 就诊扩展外部服务调用接口
 * @author: LHB
 * @create: 2020-08-25 19:48
 */
@Api(tags = "就诊扩展外部服务调用接口")
@RestController
@Slf4j
@RequestMapping("api/treatment/other")
public class TreatmentOtherServiceRest {
  /** 随访管理服务 */
  @Autowired private VisitingRecordBiz visitingRecordBiz;
  /** 随访 */
  @Autowired private VisitingRecordMapper visitingRecordMapper;
  /** 提醒 */
  @Autowired private VisitingRemindMapper visitingRemindMapper;

  @Autowired private XRayFilmBiz xRayFilmBiz;

  @Autowired private XUploadFileBiz xUploadFileBiz;

  /**
   * 插入随访记录
   *
   * @param visitingRecords 表单
   */
  @ApiOperation(value = "插入随访记录")
  @RequestMapping(value = "/visiting/record/add", method = RequestMethod.POST)
  public void insertVisitingRecordRest(@RequestBody List<VisitingRecord> visitingRecords) {
    if (StringHelper.isNotEmpty(visitingRecords)) {
      log.info("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓插入随访记录Feign调用↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
      log.info("==> visitingRecords:{}", visitingRecords);
      log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
      visitingRecordBiz.insertEntity(visitingRecords);
    }
  }

  /**
   * 根据条件查询随访记录
   *
   * @param query 查询条件
   * @return List<VisitingRecordVo>
   */
  @ApiOperation(value = "根据条件查询随访记录")
  @RequestMapping(value = "/visiting/record/find", method = RequestMethod.POST)
  public List<VisitingRecordVo> findVisitingRecordByConditionRest(
      @RequestBody VisitingRecordQuery query) {
    return visitingRecordBiz.findVisitingRecordByConditionRest(query);
  }

  /**
   * 根据就诊记录ID删除随访
   *
   * @param treatmentId 就诊记录ID
   */
  @ApiOperation(value = "根据就诊记录ID删除随访")
  @RequestMapping(value = "/visiting/record/delete/{treatmentId}", method = RequestMethod.DELETE)
  public void deleteVisitingRecordByTreatmentIdRest(
      @PathVariable(value = "treatmentId") Integer treatmentId) {
    visitingRecordBiz.deleteVisitingRecordByTreatmentId(treatmentId);
  }

  /**
   * 统计后续随访个数
   *
   * @param patientId 患者ID
   * @return 返回统计个数
   */
  @ApiOperation(value = "统计后续随访个数")
  @RequestMapping(value = "/visiting/count/{patientId}/{regDate}", method = RequestMethod.GET)
  Integer countNextVisiting(
      @PathVariable(value = "patientId") Integer patientId,
      @PathVariable(value = "regDate") String regDate) {
    return this.visitingRecordMapper.countNextVisiting(patientId, regDate);
  }

  /**
   * 根据患者ID集合和当前时间查询患者照片集合
   *
   * @param patientIds 患者ID列表
   * @param currentDate 当前日期
   * @return 返回图片信息
   */
  @ApiOperation(value = "根据患者ID集合和当前时间查询患者照片集合")
  @RequestMapping(value = "/xray/film/list/{currentDate}", method = RequestMethod.POST)
  List<XRayFilm> findXRayFilmListByPatientIds(
      @RequestBody List<Integer> patientIds, @PathVariable("currentDate") String currentDate) {
    return xRayFilmBiz.findXRayFilmListByPatientIds(patientIds, currentDate);
  }

  /**
   * 根据患者ID查询后续随访、后续提醒集合列表
   *
   * @param patientIds 患者ID
   * @return 返回数据列表
   */
  @ApiOperation(value = "根据患者ID查询后续随访集合列表")
  @RequestMapping(value = "/visiting/count/{regDate}", method = RequestMethod.POST)
  public List<NextVisitingRecordVo> countNextVisitingListByIds(
      @RequestBody List<Integer> patientIds, @PathVariable(value = "regDate") String regDate) {
    List<NextVisitingRecordVo> resultList = new ArrayList<>();
    if (StringHelper.isNotEmpty(patientIds)) {
      for (Integer patientId : patientIds) {
        NextVisitingRecordVo vo = new NextVisitingRecordVo();
        vo.setPatientId(patientId);
        vo.setVisitRecordCount(0);
        vo.setVisitRemindCount(0);
        resultList.add(vo);
      }
      List<NextVisitingRecordVo> visitRecordResult =
          visitingRecordMapper.countNextVisitingListByIds(patientIds, regDate);
      if (StringHelper.isNotEmpty(visitRecordResult)) {
        for (NextVisitingRecordVo vo : visitRecordResult) {
          for (NextVisitingRecordVo visitingRecordVo : resultList) {
            if (vo.getPatientId().equals(visitingRecordVo.getPatientId())) {
              visitingRecordVo.setVisitRecordCount(vo.getVisitRecordCount());
            }
          }
        }
      }
      List<NextVisitingRecordVo> visitRemindResult =
          visitingRemindMapper.countNextVisitingListByIds(patientIds, regDate);
      if (StringHelper.isNotEmpty(visitRemindResult)) {
        for (NextVisitingRecordVo vo : visitRemindResult) {
          for (NextVisitingRecordVo visitingRecordVo : resultList) {
            if (vo.getPatientId().equals(visitingRecordVo.getPatientId())) {
              visitingRecordVo.setVisitRemindCount(vo.getVisitRemindCount());
            }
          }
        }
      }
    }
    return resultList;
  }

  /**
   * 查询所有随访提醒记录
   * @return
   */
  @ApiOperation(value = "查询所有随访提醒记录")
  @RequestMapping(value = "/visiting/findAllRecord", method = RequestMethod.POST)
  public FindAllRemindRecordVO findAllRecord(@RequestBody PullForm pullForm){
    List<VisitingRecord>list1 = visitingRecordMapper.selectList(pullForm);
    List<VisitingRemind>list2 = visitingRemindMapper.selectList(pullForm);
    FindAllRemindRecordVO findAllRemindRecordVO = new FindAllRemindRecordVO();
    findAllRemindRecordVO.setRecordList(list1);
    findAllRemindRecordVO.setRemindList(list2);
    return findAllRemindRecordVO;
  }

  /**
   * 将指定照片影像复制到x_upload_file中
   *
   */
  @ApiOperation(value = "查询全部随访提醒记录")
  @PostMapping("/xUploadFile/saveMedicalRayToUploadFile")
  public void saveXRayFile2XUploadFile(@RequestBody @Validated MedicalRayFilmModel model) {
    xUploadFileBiz.saveXRayFile2XUploadFile(model);
  }

  /**
   * 条件查询上传文件
   *
   */
  @ApiOperation(value = "条件查询上传文件")
  @PostMapping(value = "/xUploadFile/findList")
  public List<XUploadFileVO> findXUploadFileList(@RequestBody @Validated XUploadFileQuery query) {
    return xUploadFileBiz.findXUploadFileList(query);
  }

  /**
   * 根据条件逻辑删除上传文件
   *
   * @param model
   */
  @ApiOperation(value = "根据条件逻辑删除上传文件")
  @PutMapping(value = "api/treatment/other/xUploadFile/tombstone")
  public void tombstoneUploadFile(@RequestBody @Validated MedicalRayFilmModel model) {
    xUploadFileBiz.tombstone(model);
  }
}
