package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "employee_push_message_record")
public class EmployeePushMessageRecord {
    @Id
    private Integer id;

    /**
     * 推送消息的数据源id
     */
    @Column(name = "source_id")
    private Integer sourceId;

    /**
     * 推送类型：1-考勤打卡，10-请假审批申请，20-加班审批申请，30-外勤审批申请，11-请假审批抄送，21-加班审批抄送，31-外勤审批抄送，12-请假审批通过，22-加班审批通过，32-外勤审批通过，13-请假审批未通过，23-加班审批未通过，33-外勤审批未通过，14-请假审批撤销，24-加班审批撤销，34-外勤审批撤销
     */
    @Column(name = "push_type")
    private Integer pushType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 推送设备：0-安卓，1-ios
     */
    private Integer platform;

    /**
     * 是否已读
     */
    @Column(name = "had_read")
    private Boolean hadRead;

    /**
     * 消息推送时间
     */
    @Column(name = "push_time")
    private Date pushTime;

    /**
     * 创建人id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人id
     */
    @Column(name = "upt_id")
    private Integer uptId;

    /**
     * 修改时间
     */
    @Column(name = "upt_time")
    private Date uptTime;

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
     * 获取推送消息的数据源id
     *
     * @return
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * 设置推送消息的数据源id
     *
     * @param sourceId
     */
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 获取推送类型：1-考勤打卡，10-请假审批申请，20-加班审批申请，30-外勤审批申请，11-请假审批抄送，21-加班审批抄送，31-外勤审批抄送，12-请假审批通过，22-加班审批通过，32-外勤审批通过，13-请假审批未通过，23-加班审批未通过，33-外勤审批未通过，14-请假审批撤销，24-加班审批撤销，34-外勤审批撤销
     *
     * @return push_type - 推送类型：1-考勤打卡，10-请假审批申请，20-加班审批申请，30-外勤审批申请，11-请假审批抄送，21-加班审批抄送，31-外勤审批抄送，12-请假审批通过，22-加班审批通过，32-外勤审批通过，13-请假审批未通过，23-加班审批未通过，33-外勤审批未通过，14-请假审批撤销，24-加班审批撤销，34-外勤审批撤销
     */
    public Integer getPushType() {
        return pushType;
    }

    /**
     * 设置推送类型：1-考勤打卡，10-请假审批申请，20-加班审批申请，30-外勤审批申请，11-请假审批抄送，21-加班审批抄送，31-外勤审批抄送，12-请假审批通过，22-加班审批通过，32-外勤审批通过，13-请假审批未通过，23-加班审批未通过，33-外勤审批未通过，14-请假审批撤销，24-加班审批撤销，34-外勤审批撤销
     *
     * @param pushType 推送类型：1-考勤打卡，10-请假审批申请，20-加班审批申请，30-外勤审批申请，11-请假审批抄送，21-加班审批抄送，31-外勤审批抄送，12-请假审批通过，22-加班审批通过，32-外勤审批通过，13-请假审批未通过，23-加班审批未通过，33-外勤审批未通过，14-请假审批撤销，24-加班审批撤销，34-外勤审批撤销
     */
    public void setPushType(Integer pushType) {
        this.pushType = pushType;
    }

    /**
     * 获取消息标题
     *
     * @return title - 消息标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置消息标题
     *
     * @param title 消息标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取消息内容
     *
     * @return content - 消息内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容
     *
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取推送设备：0-安卓，1-ios
     *
     * @return platform - 推送设备：0-安卓，1-ios
     */
    public Integer getPlatform() {
        return platform;
    }

    /**
     * 设置推送设备：0-安卓，1-ios
     *
     * @param platform 推送设备：0-安卓，1-ios
     */
    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    /**
     * 获取是否已读
     *
     * @return had_read - 是否已读
     */
    public Boolean getHadRead() {
        return hadRead;
    }

    /**
     * 设置是否已读
     *
     * @param hadRead 是否已读
     */
    public void setHadRead(Boolean hadRead) {
        this.hadRead = hadRead;
    }

    /**
     * 获取消息推送时间
     *
     * @return
     */
    public Date getPushTime() {
        return pushTime;
    }

    /**
     * 设置消息推送时间
     *
     * @param pushTime
     */
    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    /**
     * 获取创建人id
     *
     * @return crt_id - 创建人id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人id
     *
     * @param crtId 创建人id
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
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
     * 获取修改人id
     *
     * @return upt_id - 修改人id
     */
    public Integer getUptId() {
        return uptId;
    }

    /**
     * 设置修改人id
     *
     * @param uptId 修改人id
     */
    public void setUptId(Integer uptId) {
        this.uptId = uptId;
    }

    /**
     * 获取修改时间
     *
     * @return upt_time - 修改时间
     */
    public Date getUptTime() {
        return uptTime;
    }

    /**
     * 设置修改时间
     *
     * @param uptTime 修改时间
     */
    public void setUptTime(Date uptTime) {
        this.uptTime = uptTime;
    }
}