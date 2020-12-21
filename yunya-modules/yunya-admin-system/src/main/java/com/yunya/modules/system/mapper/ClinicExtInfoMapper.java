package com.yunya.modules.system.mapper;

import com.yunya.models.system.ClinicExtInfo;
import com.yunya.feign.system.vo.MedicalOrganizationInfoVO;
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

  /**
   * 根据门诊简称查询数量
   *
   * @param abbreviation 门诊简称
   * @param companyId 门诊id
   * @return Integer
   */
  Integer countByAbbreviation(@Param("abbreviation") String abbreviation, @Param("companyId")Integer companyId);
}
