package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

/**
 * @Program: yunya-dental
 * @Description: 已随访列表VO
 * @Author: LHB
 * @Version: v0.0.1
 * @Time: 2022-02-18 09:57
 **/
@ApiModel("已随访列表VO")
@Data
public class VisitFinishedListVO implements Serializable {
    /**
     * 随访记录ID
     */
    @ApiModelProperty(value = "随访记录ID")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 医生ID 默认为末诊医生
     */
    @ApiModelProperty(value = "医生ID 默认为末诊医生")
    private Integer dentistId;

    /**
     * 随访内容 执行随访
     */
    @ApiModelProperty(value = "随访内容 执行随访")
    private String visitingContent;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /** 实际随访执行人名字 */
    @ApiModelProperty(value = "实际随访执行人名字")
    private String executorName;

    /** 实际随访时间 */
    @ApiModelProperty(value = "实际随访时间")
    private Date executeDateTime;

    /******************************* 患者信息 ********************************/
    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 患者手机号 */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;
}
