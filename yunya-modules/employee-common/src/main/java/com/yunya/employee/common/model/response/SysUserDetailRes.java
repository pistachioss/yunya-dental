package com.yunya.employee.common.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author bruce
 * @date 2020/7/11
 */
@Getter
@Setter
public class SysUserDetailRes {
    private Integer id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 地址
     */
    private String address;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 性别: 0, 男; 1, 女
     */
    private Integer gender;

    private String description;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 县/区
     */
    private String country;

    /**
     * 籍贯
     */
    private String origin;

}
