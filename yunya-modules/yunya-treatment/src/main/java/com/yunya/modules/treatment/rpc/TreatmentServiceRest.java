package com.yunya.modules.treatment.rpc;

import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.biz.RegisteredBiz;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/15 15:45
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "诊疗服务接口暴露")
@RestController
@RequestMapping("rpc")
public class TreatmentServiceRest {

  /** 挂号记录 */
  @Autowired private RegisteredBiz registeredBiz;
  /** 就诊记录 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;

  /**
   * 根据挂号记录ID查询挂号记录
   *
   * @param id 挂号记录ID
   * @return
   */
  @RequestMapping(value = "/registered/one/{id}", method = RequestMethod.GET)
  public Registered findRegisteredById(@PathVariable(value = "id") Integer id) {
    return registeredBiz.selectById(id);
  }

  /**
   * 根据条件查询挂号记录列表
   *
   * @param entity 挂号记录
   * @return
   */
  @RequestMapping(value = "/registered/list", method = RequestMethod.POST)
  public List<Registered> findRegisteredList(@RequestBody Registered entity) {
    return registeredBiz.selectList(entity);
  }

  /**
   * 根据就诊记录ID查询就诊记录
   *
   * @param id 就诊记录ID
   * @return
   */
  @RequestMapping(value = "/treatment/one/{id}", method = RequestMethod.GET)
  public TreatmentRecord findTreatmentRecordById(@PathVariable(value = "id") Integer id) {
    return treatmentRecordBiz.selectById(id);
  }

  /**
   * 根据条件查询就诊记录列表
   *
   * @param entity 就诊记录
   * @return
   */
  @RequestMapping(value = "/treatment/list", method = RequestMethod.POST)
  public List<TreatmentRecord> findTreatmentRecordList(@RequestBody TreatmentRecord entity) {
    return treatmentRecordBiz.selectList(entity);
  }

  /**
   * 修改就诊记录病历书写状态
   *
   * @param id 就诊记录ID
   */
  @RequestMapping(value = "/treatment/modify/{id}", method = RequestMethod.GET)
  public void updateTreatmentRecord(@PathVariable(value = "id") Integer id) {
    treatmentRecordBiz.modifyTreatmentRecord(id);
  }
}
