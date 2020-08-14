package com.yunya.feign.patient_central.domain.vo;

import com.yunya.models.patient_central.PatientOrigin;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br>患者原来类型listVo
 *
 * @author: WY
 * @date 2020/8/13 19:52
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class OriginTypeVo implements Serializable {

    /**
     * 患者来源类型：活动来源
     */
    private  List<PatientOrigin>  activityInfoList;

    /**
     * 患者来源类型：活动来源
     */
    private  List<PatientOrigin>  partnerInfoList;

}
