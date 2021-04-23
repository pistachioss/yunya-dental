package com.yunya.middletable.dao.report.credits_shop;

import com.yunya.feign.report.domain.vo.CreditsRecordVO;
import com.yunya.models.credits_shop.CreditsShop;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author A
 */
public interface CreditsShopMapper extends Mapper<CreditsShop> {
    /**
     * 获取患者最新积分信息
     * @param patientId 患者ID
     * @return 积分信息
     */
    CreditsShop selectLastCredits(@Param("patientId") Integer patientId);

    /**
     * 查询患者积分记录表
     * @param patientId 患者ID
     * @return 积分记录列表
     */
    List<CreditsRecordVO> selectPatientCreditsRecord(@Param("patientId") Integer patientId);
}