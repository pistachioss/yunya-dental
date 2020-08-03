package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.MedicalGeneralNumVO;
import com.yunya.models.emr.MedicalGeneralNum;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MedicalGeneralNumMapper extends Mapper<MedicalGeneralNum> {

  int saveList(List<MedicalGeneralNum>list);
}