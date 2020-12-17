package com.yunya.feign.treatment.domain.vo;

import com.yunya.models.treatment.Registered;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 挂号信息视图模型
 * @author: LHB
 * @create: 2020-12-16 13:58
 **/
@Data
@ApiModel(value = "RegisteredVO",description = "挂号信息视图模型")
public class RegisteredVO extends Registered implements Serializable {
    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private Integer id;

    /**
     * 诊所id
     */
    @ApiModelProperty("诊所id")
    private Integer orgId;

    /**
     * 患者id
     */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /**
     * 患者名字
     */
    @ApiModelProperty("患者名字")
    private String patientName;

    /**
     * 预约id
     */
    @ApiModelProperty("预约id")
    private Integer appointmentId;

    /**
     * 可挂号医生id
     */
    @ApiModelProperty("可挂号医生id")
    private Integer dentistId;

    /**
     * 可挂号医生名字
     */
    @ApiModelProperty("可挂号医生名字")
    private String dentistName;

    /**
     * 助手id
     */
    @ApiModelProperty("助手id")
    private Integer assistantId;

    /**
     * 助手名字
     */
    @ApiModelProperty("助手名字")
    private String assistantName;

    /**
     * 科室ID
     */
    @ApiModelProperty("科室ID")
    private Integer deptRoomId;

    /**
     * 科室名称
     */
    @ApiModelProperty("科室名称")
    private String deptRoomName;

    /**
     * 挂号时间
     */
    @ApiModelProperty("挂号时间")
    private Date regTime;

    /**
     * 初复诊（0-初诊；1-复诊）
     */
    @ApiModelProperty("初复诊（0-初诊；1-复诊）")
    private Byte firstVisit;

    /**
     * 接诊状态状态（0-待接诊；1-已接诊）
     */
    @ApiModelProperty("接诊状态状态（0-待接诊；1-已接诊）")
    private Byte status;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 是否启用（有效）
     */
    @ApiModelProperty("是否启用（有效）")
    private Boolean inservice;

    /**
     * 创建人id
     */
    @ApiModelProperty("创建人id")
    private Integer crtId;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String crtName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date crtTime;

    /**
     * 更新人id
     */
    @ApiModelProperty("更新人id")
    private Integer updId;

    /**
     * 最后更新人
     */
    @ApiModelProperty("最后更新人")
    private String updName;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    private Date updTime;
}
