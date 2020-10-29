package com.yunya.modules.treatment.biz;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.vo.AssistantInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.AssistantMatchingRecord;
import com.yunya.modules.treatment.mapper.AssistantMatchingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介: 助手配诊记录业务层
 *
 * @author: chow
 * @date: 2020/8/18 11:11
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AssistantMatchingRecordBiz
    extends BaseBiz<AssistantMatchingRecordMapper, AssistantMatchingRecord> {

  /** 系统管理服务 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 根据就诊记录ID查询配诊助手列表信息
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  public List<AssistantInfoVO> findAssistantInfoVOList(Integer treatmentRecordId) {
    List<AssistantInfoVO> resultList = mapper.selectAssistantInfoVOList(treatmentRecordId);
    if (StringHelper.isNotEmpty(resultList)) {
      // todo 从缓存中获取员工信息
      resultList.forEach(
          assistant -> {
            Integer assistantId = assistant.getAssistantId();
            SysUserInfoDetail employeeInfo =
                systemServiceFeign.findSysUserEmployeeInfoByUserId(assistantId);
            if (null != employeeInfo) {
              assistant.setAssistantName(employeeInfo.getName());
            }
          });
    } else {
      resultList = new ArrayList<>();
    }
    return resultList;
  }

  /**
   * 根据就诊记录ID，助手类型查询助手配诊记录
   *
   * @param treatmentRecordId 就诊记录ID
   * @param type 配诊类型 0-助手1；1-助手2；2-助手3
   * @return AssistantMatchingRecord
   */
  public AssistantMatchingRecord selectOneByTreatmentIdAndType(
      Integer treatmentRecordId, int type) {

    return null;
  }
}
