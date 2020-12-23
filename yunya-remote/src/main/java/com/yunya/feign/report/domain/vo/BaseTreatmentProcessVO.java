package com.yunya.feign.report.domain.vo;

import com.yunya.models.report.BaseTreatmentProcess;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 就诊中间表信息
 * @author: LHB
 * @create: 2020-12-23 13:05
 **/
@ApiModel(value = "BaseTreatmentProcessVO",description = "就诊中间表信息")
@Data
public class BaseTreatmentProcessVO extends BaseTreatmentProcess implements Serializable {
    /**
     * 患者名字
     */
    private String patientName;
    /**
     * 患者年龄
     */
    private Integer age;
    /**
     * 管着性别
     */
    private Byte gender;
    /**
     * 预约医生名字
     */
    private String appointDentistName;
    /**
     * 挂号医生名字
     */
    private String regDentistName;
    /**
     * 助手1名字
     */
    private String assistantName1;

    /**
     * 助手2名字
     */
    private String assistantName2;

    /**
     * 助手3名字
     */
    private String assistantName3;

}
