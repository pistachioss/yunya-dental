package com.yunya.modules.system.mapper;

import com.yunya.models.system.MemberType;
import com.yunya.modules.system.domain.query.MemberTypeQueryForm;
import com.yunya.modules.system.vo.MemberTypeVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MemberTypeMapper extends Mapper<MemberType> {

  /**
   * 根据ID查询会员卡类型信息
   *
   * @param id 会员卡类型ID
   * @return
   */
  MemberTypeVO selectMemberTypeById(@Param("id") Integer id);

  /**
   * 根据条件查询会员卡类型列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<MemberTypeVO> selectMemberTypeList(@Param("queryForm") MemberTypeQueryForm queryForm);
}
