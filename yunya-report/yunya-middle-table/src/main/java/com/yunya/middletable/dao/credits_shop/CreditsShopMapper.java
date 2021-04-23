package com.yunya.middletable.dao.credits_shop;

import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.feign.report.domain.vo.CreditsRecordVO;
import com.yunya.models.credits_shop.CreditsShop;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CreditsShopMapper extends Mapper<CreditsShop> {
    /**
     * 获取患者最新积分信息
     * @param patientId 患者ID
     * @return 积分信息
     */
    public CreditsShop selectLastCredits(Integer patientId);

    /**
     * 查询患者积分记录表
     * @param patientId 患者ID
     * @return 积分记录列表
     */
    List<CreditsRecordVO> selectPatientCreditsRecord(Integer patientId);
}