package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：产品卡券使用-激活/复购明细VO
 *
 * @author: chenlin
 * @Description: 产品卡券使用-激活/复购明细VO
 * @Date: 2021/12/27 14:41
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("产品卡券使用-激活/复购明细VO")
public class CardCouponUsedDetailVO implements Serializable {
    /** 项目ID*/
    @ApiModelProperty("项目ID")
    private Integer itemId;

    /** 项目类型*/
    @ApiModelProperty("项目类型")
    private Integer itemType;

    /** 患者ID*/
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /** 卡券ID*/
    @ApiModelProperty("卡券ID")
    private Integer cardId;

    /** 患者 */
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;

    /** 病历号 */
    @Excel(name = "病历号")
    @ApiModelProperty("病历号")
    private String medicalNumber;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;

    /** 绑定时间 */
    @Excel(name = "绑定时间")
    @ApiModelProperty("绑定时间")
    private String bindTime;

    /** 绑定礼包名称 */
    @Excel(name = "绑定礼包名称")
    @ApiModelProperty("绑定礼包名称")
    private String bindCouponName;

    /** 账单日期 */
//    @Excel(name = "账单日期", dateFormat = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("账单日期")
    private String billDate;

    /** 开单项目 */
//    @Excel(name = "开单项目")
    @ApiModelProperty("开单项目")
    private String itemName;

    /** 项目类型 */
//    @Excel(name = "项目类型")
    @ApiModelProperty("项目类型")
    private String itemCategoryName;
}
