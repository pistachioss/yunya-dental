package com.yunya.models.emr;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 
 * 审批记录
 */
@Table(name="approval_record")
@Data
public class ApprovalRecord implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 审批事件ID
     */
    @Column(name = "event_id")
    private Integer eventId;

    /**
     * 审批事件类型 0：草稿病历审批，1：申请新增病历审批，2：申请修改病历审批，3：删除审批
     */
    @Column(name = "event_type")
    private Integer eventType;

    /**
     * 申请人Id
     */
    @Column(name = "proposer_id")
    private Integer proposerId;

    /**
     * 申请原因
     */
    @Column(name = "apply_reason")
    private String applyReason;

    /**
     * 审批人Id
     */
    @Column(name = "approver_id")
    private Integer approverId;

    /**
     * 审批时间
     */
    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    /**
     * 审批原因
     */
    @Column(name = "approve_reason")
    private String approveReason;

    /**
     * 申请类型 0：新增，1：修改
     */
    @Column(name = "apply_type")
    private Integer applyType;

    /**
     * 操作截止时间
     */
    @Column(name = "dead_time")
    private LocalDateTime deadTime;

    /**
     * 状态  0：待审批  1：同意 2：拒绝
     */
    private Integer status;

    /**
     * 创建人Id
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private LocalDateTime crtTime;

    /**
     * 更新人Id
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private LocalDateTime updTime;
}