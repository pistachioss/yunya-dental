package com.yunya.models.employee_attend;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "base_employee")
@ApiModel(description = "员工基础信息")
public class BaseEmployee {
    @Id
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    /**
     * 姓名
     */
    @Column(name = "name")
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 手机
     */
    @Column(name = "mobile_phone")
    @ApiModelProperty(value = "手机")
    private String mobilePhone;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 身份证
     */
    @Column(name = "id_card")
    @ApiModelProperty(value = "身份证")
    private String idCard;

    /**
     * 试用: 0, 正式: 1，实习: 2, 离职
     */
    @ApiModelProperty(value = "试用: 0, 正式: 1，实习: 2, 离职")
    private Integer type;

    /**
     * 入职时间
     */
    @Column(name = "join_time")
    @ApiModelProperty(value = "入职时间")
    private Date joinTime;

    /**
     * 职位
     */
    @Column(name = "post_level")
    @ApiModelProperty(value = "职位")
    private String postLevel;

    /**
     * 职称
     */
    @ApiModelProperty(value = "职称")
    private String title;

    /**
     * 0: 全职，1: 兼职
     */
    @Column(name = "work_state")
    @ApiModelProperty(value = "0: 全职，1: 兼职")
    private Boolean workState;

    /**
     * 合同签署日期
     */
    @Column(name = "contract_sign_date")
    @ApiModelProperty(value = "合同签署日期")
    private Date contractSignDate;

    /**
     * 紧急联系人
     */
    @ApiModelProperty(value = "紧急联系人")
    private String contact;

    /**
     * 联系人电话
     */
    @Column(name = "contact_tel")
    @ApiModelProperty(value = "联系人电话")
    private String contactTel;

    /**
     * 毕业院校
     */
    @Column(name = "graduate_school")
    @ApiModelProperty(value = "毕业院校")
    private String graduateSchool;

    /**
     * 学历
     */
    @ApiModelProperty(value = "学历")
    private String education;

    /**
     * 个人照片链接
     */
    @ApiModelProperty(value = "个人照片链接")
    private String photo;

    /**
     * 毕业证书照片:多张照片用;隔开
     */
    @Column(name = "diploma_photo")
    @ApiModelProperty(value = "毕业证书照片:多张照片用;隔开")
    private String diplomaPhoto;

    /**
     * 工号
     */
    @Column(name = "work_number")
    @ApiModelProperty(value = "工号")
    private String workNumber;

    @Column(name = "leave_time")
    @ApiModelProperty(value = "离职日期")
    private Date leaveTime;

    /**
     * 员工编号
     */
    @Column(name = "employee_number")
    @ApiModelProperty(value = "员工编号")
    private String employeeNumber;

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
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取身份证
     *
     * @return id_card - 身份证
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置身份证
     *
     * @param idCard 身份证
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取试用: 0, 正式: 1，实习: 2, 离职
     *
     * @return type - 试用: 0, 正式: 1，实习: 2, 离职
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置试用: 0, 正式: 1，实习: 2, 离职
     *
     * @param type 试用: 0, 正式: 1，实习: 2, 离职
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取入职时间
     *
     * @return join_time - 入职时间
     */
    public Date getJoinTime() {
        return joinTime;
    }

    /**
     * 设置入职时间
     *
     * @param joinTime 入职时间
     */
    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    /**
     * 获取职位
     *
     * @return post_level - 职位
     */
    public String getPostLevel() {
        return postLevel;
    }

    /**
     * 设置职位
     *
     * @param postLevel 职位
     */
    public void setPostLevel(String postLevel) {
        this.postLevel = postLevel;
    }

    /**
     * 获取职称
     *
     * @return title - 职称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置职称
     *
     * @param title 职称
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取0: 全职，1: 兼职
     *
     * @return work_state - 0: 全职，1: 兼职
     */
    public Boolean getWorkState() {
        return workState;
    }

    /**
     * 设置0: 全职，1: 兼职
     *
     * @param workState 0: 全职，1: 兼职
     */
    public void setWorkState(Boolean workState) {
        this.workState = workState;
    }

    /**
     * 获取合同签署日期
     *
     * @return contract_sign_date - 合同签署日期
     */
    public Date getContractSignDate() {
        return contractSignDate;
    }

    /**
     * 设置合同签署日期
     *
     * @param contractSignDate 合同签署日期
     */
    public void setContractSignDate(Date contractSignDate) {
        this.contractSignDate = contractSignDate;
    }

    /**
     * 获取紧急联系人
     *
     * @return contact - 紧急联系人
     */
    public String getContact() {
        return contact;
    }

    /**
     * 设置紧急联系人
     *
     * @param contact 紧急联系人
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * 获取联系人电话
     *
     * @return contact_tel - 联系人电话
     */
    public String getContactTel() {
        return contactTel;
    }

    /**
     * 设置联系人电话
     *
     * @param contactTel 联系人电话
     */
    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    /**
     * 获取毕业院校
     *
     * @return graduate_school - 毕业院校
     */
    public String getGraduateSchool() {
        return graduateSchool;
    }

    /**
     * 设置毕业院校
     *
     * @param graduateSchool 毕业院校
     */
    public void setGraduateSchool(String graduateSchool) {
        this.graduateSchool = graduateSchool;
    }

    /**
     * 获取学历
     *
     * @return education - 学历
     */
    public String getEducation() {
        return education;
    }

    /**
     * 设置学历
     *
     * @param education 学历
     */
    public void setEducation(String education) {
        this.education = education;
    }

    /**
     * 获取个人照片链接
     *
     * @return photo - 个人照片链接
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * 设置个人照片链接
     *
     * @param photo 个人照片链接
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * 获取毕业证书照片:多张照片用;隔开
     *
     * @return diploma_photo - 毕业证书照片:多张照片用;隔开
     */
    public String getDiplomaPhoto() {
        return diplomaPhoto;
    }

    /**
     * 设置毕业证书照片:多张照片用;隔开
     *
     * @param diplomaPhoto 毕业证书照片:多张照片用;隔开
     */
    public void setDiplomaPhoto(String diplomaPhoto) {
        this.diplomaPhoto = diplomaPhoto;
    }

    /**
     * 获取工号
     *
     * @return work_number - 工号
     */
    public String getWorkNumber() {
        return workNumber;
    }

    /**
     * 设置工号
     *
     * @param workNumber 工号
     */
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    /**
     * @return leave_time
     */
    public Date getLeaveTime() {
        return leaveTime;
    }

    /**
     * @param leaveTime
     */
    public void setLeaveTime(Date leaveTime) {
        this.leaveTime = leaveTime;
    }

    /**
     * 获取员工编号
     *
     * @return employee_number - 员工编号
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /**
     * 设置员工编号
     *
     * @param employeeNumber 员工编号
     */
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
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