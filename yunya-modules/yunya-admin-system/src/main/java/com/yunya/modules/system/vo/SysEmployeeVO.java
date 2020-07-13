package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br>
 *
 * @author: lihuibin
 * @date: 2020/7/9 16:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class SysEmployeeVO implements Serializable {
    /** 用户名 */
    private String username;
    /** 生日 */
    private Date birthday;
    /** 地址 */
    private String address;
    /** 邮箱地址 */
    private String email;
    /**  */
    private String description;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 县/区 */
    private String country;
    /** 籍贯 */
    private String origin;
    /** 是否启用 */
    private Byte inservice;

    /** 用户ID */
    private Integer userId;
    /** 员工姓名 */
    private String name;
    /** 性别  0:男  1:女 */
    private Integer gender;
    /** 手机 */
    private String mobilePhone;
    /** 固定电话 */
    private String telePhone;
    /** 身份证 */
    private String identity;
    /** 试用：0  正式：1  实习：2 离职 */
    private Integer workStatus;
    /** 工作类型(兼职-0; 全职-1) */
    private Integer workType;
    /** 合同签署日期 */
    private Date contractSigningDate;
    /** 入职时间 */
    private Date entryDate;
    /** 毕业院校 */
    private String graduatedSchool;
    /** 学历 */
    private Integer education;
    /** 是否有员工折扣 */
    private Byte discount;
    /** 离职时间 */
    private Date leaveTime;
    /** 职称 */
    private String title;
    /** 职级 */
    private String postLevel;
    /** 紧急联系人姓名 */
    private String emergencyContact;
    /** 紧急联系人电话 */
    private String emergencyContactPhone;
    /** 个人照片链接 */
    private String photo;
    /** 毕业证书照片:多张照片用;隔开 */
    private String diplomaPhoto;
    /** 工号 */
    private Integer crtId;
    /** 组织ID */
    private Integer orgId;
    /** 组织部门ID */
    private Integer orgDeptId;
    /** 岗位ID */
    private Integer postId;
}
