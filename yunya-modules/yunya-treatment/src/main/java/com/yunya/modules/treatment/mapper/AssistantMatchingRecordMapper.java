package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.AssistantInfoVO;
import com.yunya.models.treatment.AssistantMatchingRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AssistantMatchingRecordMapper extends Mapper<AssistantMatchingRecord> {

  /**
   * 根据就诊记录ID查询配诊助手列表
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  List<AssistantInfoVO> selectAssistantInfoVOList(@Param("treatmentRecordId") Integer treatmentRecordId);
}
