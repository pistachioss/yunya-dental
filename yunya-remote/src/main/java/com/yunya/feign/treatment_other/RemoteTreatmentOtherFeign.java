package com.yunya.feign.treatment_other;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.FindAllRemindRecordVO;
import com.yunya.feign.treatment_other.domain.vo.NextVisitingRecordVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.feign.treatment_other.factory.RemoteTreatmentOtherFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.XRayFilm;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
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
}
