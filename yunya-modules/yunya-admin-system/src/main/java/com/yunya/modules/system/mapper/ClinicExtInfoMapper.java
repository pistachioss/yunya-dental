package com.yunya.modules.system.mapper;

import com.yunya.modules.system.entity.ClinicExtInfo;
import com.yunya.modules.system.vo.MedicalOrganizationInfoVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicExtInfoMapper extends Mapper<ClinicExtInfo> {

  /**
   * 根据组织ID获取医疗机构详细信息
   *
   * @param companyId 组织ID
   * @return MedicalOrganizationInfoVO
   */
  MedicalOrganizationInfoVO selectClinicExtInfoByCompanyId(@Param("companyId") Integer companyId);

  /**
   * 根据品牌ID查询关联该品牌的诊所信息
   *
   * @param brandId 品牌ID
   * @return list
   */
  List<ClinicExtInfo> selectClinicExtInfoByBrandId(@Param("brandId") Integer brandId);
}
