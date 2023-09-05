package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/5 17:38
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱状态变更")
public class QcDoctorAdviceStatusForm implements Serializable {

    private List<QcAdviceItemStatusForm> order_infos;
}
