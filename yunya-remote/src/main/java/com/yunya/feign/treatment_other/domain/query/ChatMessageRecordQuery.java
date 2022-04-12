package com.yunya.feign.treatment_other.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：聊天消息记录查询模型
 *
 * @author: chenlin
 * @Description: 聊天消息记录查询模型
 * @Date: 2022/4/11 17:54
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("聊天消息记录查询模型")
public class ChatMessageRecordQuery extends PageQuery implements Serializable {
    @ApiModelProperty("发送者id")
    private Integer sendId;
}
