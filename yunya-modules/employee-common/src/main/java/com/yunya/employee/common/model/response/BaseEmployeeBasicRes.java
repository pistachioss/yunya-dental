package com.yunya.employee.common.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Data
@ApiModel("员工基本信息对象")
public class BaseEmployeeBasicRes {

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "身份证")
    private String sex;

    @ApiModelProperty(value = "出生日期")
    private LocalDate birthday;

    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "就职状态")
    private String typeName;

    @ApiModelProperty(value = "全职/兼职")
    private String workState;

    @ApiModelProperty(value = "合同签署日期")
    private LocalDate contractSignDate;

    @ApiModelProperty(value = "入职时间")
    private LocalDate joinTime;

    @ApiModelProperty(value = "是否有授权折扣")
    private String enableDiscount;

    @ApiModelProperty(value = "毕业院校")
    private String graduateSchool;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "基本工作量")
    private Double workAmount;

    @ApiModelProperty(value = "奖金系数")
    private Double bonusCoefficient;

    @ApiModelProperty(value = "紧急联系人")
    private String emergencyContact;

    @ApiModelProperty(value = "紧急联系人电话")
    private String emergencyContactPhone;

    @ApiModelProperty("用户可登录组织")
    private List<BaseEmployeeLoginOrgRes> employeeOrgList;
}
