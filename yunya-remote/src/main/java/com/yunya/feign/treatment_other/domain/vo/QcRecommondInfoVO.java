package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:22
 * @description: 全程医疗-mall推荐信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-mall推荐信息数据模型")
public class QcRecommondInfoVO implements Serializable {
    @ApiModelProperty("全程就诊记录id")
    private Integer id;

    /** 登记号 */
    @ApiModelProperty("登记号")
    private String admNo;
    
    /** 姓名 */
    @ApiModelProperty("姓名")
    private String customerName;

    /** 身份证号 */
    @ApiModelProperty("身份证号")
    private String idCard;

    /** 手机号 */
    @ApiModelProperty("手机号")
    private String mobile;

    /** 推荐类型：O-医嘱单，L-引导单 */
    @ApiModelProperty("推荐类型：O-医嘱单，L-引导单")
    private String type;

    /** 全程收费时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty("全程收费时间")
    private Date admDate;

    /** 医嘱备注 */
    @ApiModelProperty("医嘱备注")
    private String admRemark;
    
    /** 绑定患者id */
    @ApiModelProperty("绑定患者id")
    private Integer patientId;
    
    /** 绑定患者姓名 */
    @ApiModelProperty("绑定患者姓名")
    private String patientName;
    
    /** 账单id */
    @ApiModelProperty("账单id")
    private Integer billId;

    /** 关联账单编号 */
    @ApiModelProperty("关联账单编号")
    private String billNum;

    /** 状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传） */
    @ApiModelProperty("状态：1-未核销（已下载），2-未开单（已核销并绑定患者），3-已开单（已绑定账单），4-已同步（已上传）")
    private Integer status;
}
