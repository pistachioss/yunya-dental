package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class CopyInfoVO {

    private Integer id;

    /**
     * 申请类型对应表的Id
     */
   @ApiModelProperty("申请类型对应表的Id")
    private Integer applyId;

    /**
     * 申请类型 0：请假 1：加班 2：外勤
     */
   @ApiModelProperty("申请类型 0：请假 1：加班 2：外勤")
    private Integer applyType;

    /**
     * 用户id
     */
   @ApiModelProperty("用户id")
    private Integer userId;
    @ApiModelProperty("用户姓名")
    private String userName;

    /**
     * 创建人
     */
   @ApiModelProperty("crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
   @ApiModelProperty("crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
   @ApiModelProperty("upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
   @ApiModelProperty("upd_time")
    private Date updTime;
}
