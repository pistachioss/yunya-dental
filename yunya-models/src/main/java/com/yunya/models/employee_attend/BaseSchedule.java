package com.yunya.models.employee_attend;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Table(name = "base_schedule")
@ApiModel(description = "公司排班设置")
public class BaseSchedule {
    /**
     * 主键
     */
    @Id
    @ApiModelProperty(value = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 颜色
     */
    @ApiModelProperty(value = "名称")
    private String color;

    /**
     * 类型 是否工作；
     */
    @ApiModelProperty(value = "名称")
    private String type;

    /**
     * 开始时间点1
     */
    @Column(name = "first_start_time")
    @ApiModelProperty(value = "开始时间点1")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date firstStartTime;

    /**
     * 结束时间点1
     */
    @Column(name = "first_end_time")
    @ApiModelProperty(value = "结束时间点1")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date firstEndTime;

    /**
     * 开始时间点2
     */
    @Column(name = "second_start_time")
    @ApiModelProperty(value = "开始时间点2")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date secondStartTime;

    /**
     * 结束时间点2
     */
    @Column(name = "second_end_time")
    @ApiModelProperty(value = "结束时间点2")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date secondEndTime;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;

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
    private Date crtTime;

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
    private Date updTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取颜色
     *
     * @return color - 颜色
     */
    public String getColor() {
        return color;
    }

    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * 获取类型 是否工作；0：休息，1：工作
     *
     * @return type - 类型 是否工作；0：休息，1：工作
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类型 是否工作；0：休息，1：工作
     *
     * @param type 类型 是否工作；0：休息，1：工作
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取开始时间点1
     *
     * @return first_start_time - 开始时间点1
     */
    public Date getFirstStartTime() {
        return firstStartTime;
    }

    /**
     * 设置开始时间点1
     *
     * @param firstStartTime 开始时间点1
     */
    public void setFirstStartTime(Date firstStartTime) {
        this.firstStartTime = firstStartTime;
    }

    /**
     * 获取结束时间点1
     *
     * @return first_end_time - 结束时间点1
     */
    public Date getFirstEndTime() {
        return firstEndTime;
    }

    /**
     * 设置结束时间点1
     *
     * @param firstEndTime 结束时间点1
     */
    public void setFirstEndTime(Date firstEndTime) {
        this.firstEndTime = firstEndTime;
    }

    /**
     * 获取开始时间点2
     *
     * @return second_start_time - 开始时间点2
     */
    public Date getSecondStartTime() {
        return secondStartTime;
    }

    /**
     * 设置开始时间点2
     *
     * @param secondStartTime 开始时间点2
     */
    public void setSecondStartTime(Date secondStartTime) {
        this.secondStartTime = secondStartTime;
    }

    /**
     * 获取结束时间点2
     *
     * @return second_end_time - 结束时间点2
     */
    public Date getSecondEndTime() {
        return secondEndTime;
    }

    /**
     * 设置结束时间点2
     *
     * @param secondEndTime 结束时间点2
     */
    public void setSecondEndTime(Date secondEndTime) {
        this.secondEndTime = secondEndTime;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取修改人名称
     *
     * @return upd_name - 修改人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置修改人名称
     *
     * @param updName 修改人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}