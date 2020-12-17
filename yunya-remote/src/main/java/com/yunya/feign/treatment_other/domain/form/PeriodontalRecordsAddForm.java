package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class PeriodontalRecordsAddForm {
    @ApiModelProperty(value = "id")
    private Integer id;
    /**
     * 门诊id
     */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;
    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;
    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Integer doctorId;
    /**
     * 检查日期
     */
    @ApiModelProperty(value = "检查日期")
    private Date checkDate;

    @ApiModelProperty(value = "Data_PdB1")
    private String dataPdb1;

    @ApiModelProperty(value = "Data_PdL1")
    private String dataPdl1;

    @ApiModelProperty(value = "Data_FI1")
    private String dataFi1;

    @ApiModelProperty(value = "Data_M1")
    private String dataM1;

    @ApiModelProperty(value = "Data_BIB1")
    private String dataBib1;

    @ApiModelProperty(value = "Data_BIL1")
    private String dataBil1;

    @ApiModelProperty(value = "Data_BIL2")
    private String dataBil2;

    @ApiModelProperty(value = "Data_BIB2")
    private String dataBib2;

    @ApiModelProperty(value = "Data_FI2")
    private String dataFi2;

    @ApiModelProperty(value = "Data_M2")
    private String dataM2;

    @ApiModelProperty(value = "Data_PdL2")
    private String dataPdl2;

    @ApiModelProperty(value = "Data_PdB2")
    private String dataPdb2;


}
