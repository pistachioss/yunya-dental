package com.yunya.modules.patient_central.mapper;

import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WxFansBindMapper extends Mapper<WxFansBind> {

    Integer batchInsert(List<WxFansBind> list);

    List<WxFans> listWxUsers(@Param("list") List<Integer> list);
}