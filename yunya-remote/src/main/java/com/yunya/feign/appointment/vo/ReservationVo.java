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
     * 创建时间
     */
    @Excel(name = "登记时间", dateFormat = "yyyy-MM-dd HH:mm:ss", width = 30)
    private Date crtTime;

    /**
     * 就诊人名字
     */
    @Excel(name = "就诊人姓名", width = 20)
    private String patientName;

    /**
     * 就诊人性别
     */
    @Excel(name = "就诊人性别", readConverterExp = "0=男,1=女")
    private Byte patientGender;

    /**
     * 就诊人手机号
     */
    @Excel(name = "联系电话")
    private String patientPhone;

    /**
     * 预约项目
     */
    @Excel(name = "预约项目", width = 20)
    private String appointItemName;

    /**
     * 权益码
     */
    @Excel(name = "权益码", width = 30)
    private String code;

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
    @Excel(name = "意向预约时间")
    private String reservationDate;

    /**
     * 预约意向门诊ID
     */
    private Integer orgId;

    /**
     * 预约意向门诊ID
     */
    @Excel(name = "就近门诊", width = 40)
    private String orgName;

    /**
     * 登记状态(0：新增；1：已预约；2：已放弃)
     */
    @Excel(name = "状态", readConverterExp = "0=新增,1=已预约,2=已放弃")
    private Byte status;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 30)
    private String remarks;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    private Boolean inservice;

    /**
     * 创建人名称
     */
    private String crtName;

    /**
     * 更新人名称
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;
}
