package com.yunya.feign.treatment_other;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.treatment.domain.form.QcTreatmentImportForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.QcRecommondOrderVO;
import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import com.yunya.feign.treatment.domain.vo.TreatOrderRecordVO;
import com.yunya.feign.treatment_other.domain.model.MedicalRayFilmModel;
import com.yunya.feign.treatment_other.domain.query.TariffPackageDetailQuery;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.*;
import com.yunya.feign.treatment_other.factory.RemoteTreatmentOtherFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.XRayFilm;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 就诊扩展外部调用接口
 *
 * @author A
 */
@FeignClient(
    value = YunyaServiceNameConstants.YUNYA_TREATMENT_OTHER,
    fallbackFactory = RemoteTreatmentOtherFactory.class)
public interface RemoteTreatmentOtherFeign {

  /**
   * 查询图片影像列表
   *
   */
  @RequestMapping(value = "api/treatment/other/photo/findCycleList", method = RequestMethod.POST)
          List<XRayFilmVO>findPhotoListInfo(@RequestBody XRayFilmQuery query);

  /**
   * 插入随访记录
   *
   * @param visitingRecords 表单
   * @return 是否成功
   */
  @RequestMapping(value = "api/treatment/other/visiting/record/add", method = RequestMethod.POST)
  void insertVisitingRecord(@RequestBody List<VisitingRecord> visitingRecords);

  /**
   * 根据条件查询随访记录
   *
   * @param query 查询条件
   * @return List<VisitingRecordVo>
   */
  @ApiOperation(value = "根据条件查询随访记录")
  @RequestMapping(value = "api/treatment/other/visiting/record/find", method = RequestMethod.POST)
  List<VisitingRecordVo> findVisitingRecordByConditionRest(@RequestBody VisitingRecordQuery query);

  /**
   * 根据就诊记录ID删除随访
   *
   * @param treatmentId 就诊记录ID
   */
  @ApiOperation(value = "根据就诊记录ID删除随访")
  @RequestMapping(
      value = "api/treatment/other/visiting/record/delete/{treatmentId}",
      method = RequestMethod.DELETE)
  void deleteVisitingRecordByTreatmentIdRest(
      @PathVariable(value = "treatmentId") Integer treatmentId);

  /**
   * 统计后续随访个数
   *
   * @param patientId 患者ID
   * @return 返回统计个数
   */
  @ApiOperation(value = "统计后续随访个数")
  @RequestMapping(
      value = "api/treatment/other/visiting/count/{patientId}/{regDate}",
      method = RequestMethod.GET)
  Integer countNextVisiting(
      @PathVariable(value = "patientId") Integer patientId,
      @PathVariable(value = "regDate") String regDate);

  /**
   * 根据患者ID查询后续随访集合列表
   *
   * @param patientIds 患者ID
   * @param regDate 查询日期
   * @return 返回数据列表
   */
  @ApiOperation(value = "根据患者ID查询后续随访集合列表")
  @RequestMapping(
      value = "api/treatment/other/visiting/count/{regDate}",
      method = RequestMethod.POST)
  List<NextVisitingRecordVo> countNextVisitingListByIds(
      @RequestBody List<Integer> patientIds, @PathVariable(value = "regDate") String regDate);

  /**
   * 根据患者ID集合和当前时间查询患者照片集合
   *
   * @param patientIds 患者ID列表
   * @param currentDate 当前日期
   * @return 返回图片信息
   */
  @ApiOperation(value = "根据患者ID集合和当前时间查询患者照片集合")
  @RequestMapping(
      value = "api/treatment/other/xray/film/list/{currentDate}",
      method = RequestMethod.POST)
  List<XRayFilm> findXRayFilmListByPatientIds(
      @RequestBody List<Integer> patientIds, @PathVariable("currentDate") String currentDate);

  /**
   * 查询全部随访提醒记录
   *
   */
  @ApiOperation(value = "查询全部随访提醒记录")
  @RequestMapping(
          value = "api/treatment/other/visiting/findAllRecord",
          method = RequestMethod.POST)
  FindAllRemindRecordVO findAllRecord(PullForm pullForm);


  /**
   * 将指定照片影像复制到x_upload_file中
   *
   */
  @ApiOperation(value = "将指定照片影像复制到x_upload_file中")
  @PostMapping("api/treatment/other/xUploadFile/saveMedicalRayToUploadFile")
  void saveXRayFile2XUploadFile(@RequestBody @Validated MedicalRayFilmModel model);

  /**
   * 条件查询上传文件
   *
   */
  @ApiOperation(value = "条件查询上传文件")
  @PostMapping(value = "api/treatment/other/xUploadFile/findList")
  List<XUploadFileVO> findXUploadFileList(@RequestBody @Validated XUploadFileQuery query);

  /**
   * 根据条件逻辑删除上传文件
   *
   * @param model
   */
  @ApiOperation(value = "根据条件逻辑删除上传文件")
  @PutMapping(value = "api/treatment/other/xUploadFile/tombstone")
  void tombstoneUploadFile(@RequestBody @Validated MedicalRayFilmModel model);

  /**
   * 条件查询项目组合明细
   *
   * @param query
   * @return
   */
  @ApiOperation("条件查询项目组合明细")
  @PostMapping("api/treatment/other/package/detail")
  PageInfo<TariffPackageDetailVO> findPackageList(@RequestBody @Validated TariffPackageDetailQuery query);

  /**
   * 全程医疗登记单导入
   *
   * @param form
   * @return
   */
  @PostMapping("api/treatment/other/qc/treatment/match")
  TreatOrderRecordVO orderMatchQcTreatmentList(@RequestBody @Validated QcTreatmentImportForm form);

  /**
   * 获取已绑定账单的全程医疗登记单列表
   *
   * @param orderRecordId
   * @return
   */
  @ApiOperation("获取已绑定账单的全程医疗登记单列表")
  @GetMapping("api/treatment/other/qc/treatment/binding/{orderRecordId}")
  List<QcTreatmentVO> findBindingQcTreatmentList(@PathVariable(value = "orderRecordId") Integer orderRecordId);

  /**
   * 用账单信息更新全程医疗就诊及其明细（绑定或更新实收）
   *
   * @param form
   */
  @ApiOperation("用账单信息更新全程医疗就诊及其明细（绑定或更新实收）")
  @PutMapping("api/treatment/other/qc/treatment/items/update")
  List<OrderDetailChargeVO> updateQcTreatmentAndItems(@RequestBody @Validated QcTreatmentImportForm form);
}
