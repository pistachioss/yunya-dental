package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 随访内容视图信息
 * @author: LHB
 * @create: 2020-08-24 12:41
 **/
@ApiModel(value = "随访内容视图信息(随访按钮)")
@Data
@ToString
public class VisitingContentVo implements Serializable {

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 患者手机号 */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /**
     * 就诊日期
     */
    @ApiModelProperty(value = "就诊日期")
    private Date treatmentDate;

    /**
     * 科室名字 默认末诊科室
     */
    @ApiModelProperty(value = "科室名字 默认末诊科室")
    private String deptRoomName;

    /**
     * 随访日期
     */
    @ApiModelProperty(value = "随访日期")
    private Date visitingDate;

    /** 随访时间 */
    @ApiModelProperty(value = "随访时间 HH:mm")
    private String visitingTime;

    /** 初/复诊 */
    @ApiModelProperty(value = "初/复诊")
    private Byte firstVisit;

    /** 末次预约日期 */
    @ApiModelProperty(value = "末次预约日期")
    private Date endAppointDate;

    /**
     * 随访原因 新建随访
     */
    @ApiModelProperty(value = "随访原因 新建随访")
    private String reason;

    /**
     * 随访内容 执行随访
     */
    @ApiModelProperty(value = "随访内容 执行随访")
    private String visitingContent;

    /**
     * 是否启用 0-不启用；1-启用
     */
    @ApiModelProperty(value = "是否启用 0-不启用；1-启用")
    private Boolean inservice;

}
