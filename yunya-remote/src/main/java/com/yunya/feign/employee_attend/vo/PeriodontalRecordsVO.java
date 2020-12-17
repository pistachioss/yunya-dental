package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
@ApiModel
public class PeriodontalRecordsVO {
    @ApiModelProperty(value = "ID")
    private Integer id;

    /**
     * 门诊id
     */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;

    /**
     * 门诊名称
     */
    @ApiModelProperty(value = "门诊名称")
    private String companyName;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 患者名称
     */
    @ApiModelProperty(value = "患者名称")
    private String patientName;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    private Integer doctorId;

    /**
     * 医生名称
     */
    @ApiModelProperty(value = "医生名称")
    private String doctorName;

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

    /**
     * 0:否 1：是
     */
    @ApiModelProperty(value = " 是否删除 0:否 1：是")
    private Integer isDelete;

    @ApiModelProperty(value = "创建者ID")
    private Integer crtId;

    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    @ApiModelProperty(value = "修改者ID")
    private Integer updId;

    @ApiModelProperty(value = "修改时间")
    private Date updTime;
}
