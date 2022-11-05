package com.yunya365.mini.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 微信请求记录
 * </p>
 *
 * @author xiangyang
 * @since 2022-11-02
 */
@Data
@TableName("wx_request_record")
public class WxRequestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 类型(0-请求付款 1-请求退款 2-付款通知 3-退款通知)
     */
    @TableField("type")
    private Integer type;

    /**
     * 请求参数json
     */
    @TableField("request_json")
    private String requestJson;

    /**
     * 响应json
     */
    @TableField("response_json")
    private String responseJson;

    @TableField("crt_id")
    private Integer crtId;

    @TableField("upd_id")
    private Integer updId;

    /**
     * 创建时间
     */
    @TableField("crt_time")
    private LocalDateTime crtTime;

    /**
     * 修改时间
     */
    @TableField("upd_time")
    private LocalDateTime updTime;


}
