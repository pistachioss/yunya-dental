package com.yunya.employee.common.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * @author bruce
 * @date 2020/7/15
 */
@Setter
@Getter
public class BaseBasicEmployeeReq {

    @NotBlank
    @Size(max = 25, message = "员工姓名不能超过25个字")
    private String employeeName;
    @NotBlank
    @Size(max = 25, message = "身份证长度不能超过18")
    private String idCard;
    @NotNull(message = "性别不能为空")
    private Integer sex;
    @NotNull(message = "出生日期不能为空")
    private LocalDate birthday;
    @NotBlank(message = "手机号码不能为空")
    private String mobilePhone;
    /** 0: 全职，1: 兼职 */
    @NotNull
    private Integer workState;
    /** 就职状态 */
    @NotNull(message = "就职状态不能为空")
    private Integer type;
    @NotNull(message = "入职时间不能为空")
    private LocalDate joinTime;
    /*** 合同签署日期 */
    @NotNull(message = "合同签署日期不能为空")
    private LocalDate contractSignDate;
    @NotNull
    private Integer discount;
    @NotBlank(message = "毕业院校不能为空")
    private String graduateSchool;
    /*** 基本工作量 */
    private Double workAmount;
    /*** 学历 */
    @NotBlank(message = "学历不能为空")
    private String education;
    private String emergencyContact;
    /*** 奖金系数 */
    private Double bonusCoefficient;
    private String emergencyContactPhone;
    private LocalDate leaveTime;
    /** 员工可登录组织 */
    private List<Integer> employeeLoginOrgIds;
}
