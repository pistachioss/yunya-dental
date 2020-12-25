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
     * 患者年龄
     */
    @ApiModelProperty("患者年龄")
    private Integer age;

    /**
     * 患者名字
     */
    @ApiModelProperty("患者名字")
    private String patientName;

    /**
     * 患者性别
     */
    @ApiModelProperty("患者性别")
    private Byte gender;
    /**
     * 可挂号医生名字
     */
    @ApiModelProperty("可挂号医生名字")
    private String dentistName;

    /**
     * 助手名字
     */
    @ApiModelProperty("助手名字")
    private String assistantName;

    /**
     * 科室名称
     */
    @ApiModelProperty("科室名称")
    private String deptRoomName;
}
