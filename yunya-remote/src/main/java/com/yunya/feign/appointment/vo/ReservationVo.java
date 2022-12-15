package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 预约登记列表返回
 **/
@ApiModel(value = "ReservationVo",description = "预约登记列表返回")
@Data
public class ReservationVo implements Serializable {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 预约意向渠道ID
     */
    private Integer reservationSourceId;

    /**
     * 预约意向渠道ID
     */
    private String reservationSourceName;

    /**
     * 预约意向时间
     */
    private String reservationDate;

    /**
     * 预约项目
     */
    private String appointItemName;

    /**
     * 预约意向门诊ID
     */
    private Integer orgId;

    /**
     * 预约意向门诊ID
     */
    private String orgName;

    /**
     * 就诊人名字
     */
    private String patientName;

    /**
     * 就诊人手机号
     */
    private String patientPhone;

    /**
     * 就诊人性别
     */
    private Byte patientGender;

    /**
     * 登记状态(0：新增；1：已预约；2：已放弃)
     */
    private Byte status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    private Boolean inservice;

    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}
