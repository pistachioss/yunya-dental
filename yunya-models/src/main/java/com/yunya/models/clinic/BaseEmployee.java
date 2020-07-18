package com.yunya.models.clinic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "base_employee")
public class BaseEmployee {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 姓名
     */
    @Column(name = "name")
    private String name;

    /**
     * 手机
     */
    @Column(name = "mobile_phone")
    private String mobilePhone;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 身份证
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 试用: 0, 正式: 1，实习: 2, 离职
     */
    private Integer type;

    /**
     * 入职时间
     */
    @Column(name = "join_time")
    private LocalDate joinTime;

    /**
     * 职位
     */
    @Column(name = "post_level")
    private String postLevel;

    /**
     * 职称
     */
    private String title;

    /**
     * 0: 全职，1: 兼职
     */
    @Column(name = "work_state")
    private Integer workState;

    /**
     * 合同签署日期
     */
    @Column(name = "contract_sign_date")
    private LocalDate contractSignDate;

    /**
     * 毕业院校
     */
    @Column(name = "graduate_school")
    private String graduateSchool;

    /**
     * 学历
     */
    private String education;

    /**
     * 个人照片链接
     */
    private String photo;

    /**
     * 毕业证书照片:多张照片用;隔开
     */
    @Column(name = "diploma_photo")
    private String diplomaPhoto;

    /**
     * 工号
     */
    @Column(name = "work_number")
    private String workNumber;

    @Column(name = "leave_time")
    private LocalDate leaveTime;

    /**
     * 员工编号
     */
    @Column(name = "employee_number")
    private String employeeNumber;

    /**
     * 是否有折扣权限
     */
    @Column(name = "is_discount")
    private Integer discount;

    /**
     * 基本工作量
     */
    @Column(name = "work_amount")
    private Double workAmount;

    /**
     * 奖金系数
     */
    @Column(name = "bonus_coefficient")
    private Double bonusCoefficient;

    /**
     * 紧急联系人
     */
    @Column(name = "emergency_contact")
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;

}