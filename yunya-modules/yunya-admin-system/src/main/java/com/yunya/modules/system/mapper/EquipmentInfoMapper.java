package com.yunya.modules.system.mapper;

import com.yunya.models.system.EquipmentInfo;
import com.yunya.modules.system.vo.EquipmentInfoVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface EquipmentInfoMapper extends Mapper<EquipmentInfo> {
    /**
     * 通过设备号 和 IP地址 查询设备信息
     * @param equipmentInfo
     * @return EquipmentInfo
     */
    EquipmentInfoVO selectOneBySN(@Param("form") EquipmentInfo equipmentInfo);

    /**
     * 修改信息是查询是否已经存在相同的数据
     * @param equipmentInfo
     * @return EquipmentInfo
     */
    EquipmentInfoVO selectOneBySNAndId(@Param("form") EquipmentInfo equipmentInfo);

    /**
     * 根据门诊id查询门诊设备
     * @param orgId
     * @return List<EquipmentInfoVO>
     */
    List<EquipmentInfoVO> findListByOrgId(@Param("orgId") int orgId);
}