package com.yunya.models.wechat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_automsg")
public class WxAutomsg {
    /**
     * 记录ID
     */
    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 公司ID
     */
    @Column(name = "CompID")
    private String compid;

    /**
     * 事件关键字
     */
    @Column(name = "EventKey")
    private String eventkey;

    /**
     * 事件名称
     */
    @Column(name = "EventName")
    private String eventname;

    /**
     * 消息内容
     */
    @Column(name = "MsgText")
    private String msgtext;

    /**
     * 是否有效性
     */
    @Column(name = "IsValid")
    private Boolean isvalid;

    /**
     * 更新时间
     */
    @Column(name = "UpdateTime")
    private Date updatetime;

    /**
     * 更新者
     */
    @Column(name = "UpdateUser")
    private String updateuser;

    /**
     * 创建时间
     */
    @Column(name = "CreateTime")
    private Date createtime;

    /**
     * 创建者
     */
    @Column(name = "CreateUser")
    private String createuser;

    @Column(name = "MsgTitle")
    private String msgtitle;

    @Column(name = "MsgUrl")
    private String msgurl;

    @Column(name = "MsgPicUrl")
    private String msgpicurl;

    /**
     * 获取记录ID
     *
     * @return ID - 记录ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置记录ID
     *
     * @param id 记录ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取公司ID
     *
     * @return CompID - 公司ID
     */
    public String getCompid() {
        return compid;
    }

    /**
     * 设置公司ID
     *
     * @param compid 公司ID
     */
    public void setCompid(String compid) {
        this.compid = compid;
    }

    /**
     * 获取事件关键字
     *
     * @return EventKey - 事件关键字
     */
    public String getEventkey() {
        return eventkey;
    }

    /**
     * 设置事件关键字
     *
     * @param eventkey 事件关键字
     */
    public void setEventkey(String eventkey) {
        this.eventkey = eventkey;
    }

    /**
     * 获取事件名称
     *
     * @return EventName - 事件名称
     */
    public String getEventname() {
        return eventname;
    }

    /**
     * 设置事件名称
     *
     * @param eventname 事件名称
     */
    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    /**
     * 获取消息内容
     *
     * @return MsgText - 消息内容
     */
    public String getMsgtext() {
        return msgtext;
    }

    /**
     * 设置消息内容
     *
     * @param msgtext 消息内容
     */
    public void setMsgtext(String msgtext) {
        this.msgtext = msgtext;
    }

    /**
     * 获取是否有效性
     *
     * @return IsValid - 是否有效性
     */
    public Boolean getIsvalid() {
        return isvalid;
    }

    /**
     * 设置是否有效性
     *
     * @param isvalid 是否有效性
     */
    public void setIsvalid(Boolean isvalid) {
        this.isvalid = isvalid;
    }

    /**
     * 获取更新时间
     *
     * @return UpdateTime - 更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置更新时间
     *
     * @param updatetime 更新时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 获取更新者
     *
     * @return UpdateUser - 更新者
     */
    public String getUpdateuser() {
        return updateuser;
    }

    /**
     * 设置更新者
     *
     * @param updateuser 更新者
     */
    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    /**
     * 获取创建时间
     *
     * @return CreateTime - 创建时间
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 设置创建时间
     *
     * @param createtime 创建时间
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * 获取创建者
     *
     * @return CreateUser - 创建者
     */
    public String getCreateuser() {
        return createuser;
    }

    /**
     * 设置创建者
     *
     * @param createuser 创建者
     */
    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    /**
     * @return MsgTitle
     */
    public String getMsgtitle() {
        return msgtitle;
    }

    /**
     * @param msgtitle
     */
    public void setMsgtitle(String msgtitle) {
        this.msgtitle = msgtitle;
    }

    /**
     * @return MsgUrl
     */
    public String getMsgurl() {
        return msgurl;
    }

    /**
     * @param msgurl
     */
    public void setMsgurl(String msgurl) {
        this.msgurl = msgurl;
    }

    /**
     * @return MsgPicUrl
     */
    public String getMsgpicurl() {
        return msgpicurl;
    }

    /**
     * @param msgpicurl
     */
    public void setMsgpicurl(String msgpicurl) {
        this.msgpicurl = msgpicurl;
    }
}