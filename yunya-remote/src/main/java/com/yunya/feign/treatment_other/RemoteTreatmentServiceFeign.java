package com.yunya.feign.treatment_other;

import com.yunya.feign.treatment_other.factory.RemoteTreatmentServiceFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 简介: 云牙诊疗服务接口调用Feign
 *
 * @author: chow
 * @date: 2020/8/14 20:52
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    value = YunyaServiceNameConstants.YUNYA_TREATMENT_SERVICE,
    fallback = RemoteTreatmentServiceFallBackFactory.class)
public interface RemoteTreatmentServiceFeign {

  /**
   * 根据挂号记录ID查询挂号记录
   *
   * @param id 挂号记录ID
   * @return
   */
  @RequestMapping(value = "/rpc/registered/one/{id}", method = RequestMethod.GET)
  Registered findRegisteredById(@PathVariable(value = "id") Integer id);
  /**
   * 根据条件查询挂号记录列表
   *
   * @param entity 挂号记录
   * @return
   */
  @RequestMapping(value = "/rpc/registered/list", method = RequestMethod.POST)
  List<Registered> findRegisteredList(@RequestBody Registered entity);

  /**
   * 根据就诊记录ID查询就诊记录
   *
   * @param id 就诊记录ID
   * @return
   */
  @RequestMapping(value = "/rpc/treatment/one/{id}", method = RequestMethod.GET)
  TreatmentRecord findTreatmentRecordById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询就诊记录列表
   *
   * @param entity 就诊记录
   * @return
   */
  @RequestMapping(value = "/rpc/treatment/list", method = RequestMethod.POST)
  List<TreatmentRecord> findTreatmentRecordList(@RequestBody TreatmentRecord entity);

  /**
   * 修改就诊记录病历书写状态
   *
   * @param id 就诊记录ID
   */
  @RequestMapping(value = "/rpc/treatment/modify/{id}", method = RequestMethod.GET)
  void updateTreatmentRecord(@PathVariable(value = "id") Integer id);
}
