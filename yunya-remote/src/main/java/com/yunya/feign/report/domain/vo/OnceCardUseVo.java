package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2020/11/16
 */
@Data
@ApiModel(value = "患者档案产品管理使用记录返回")
public class OnceCardUseVo {
	@ApiModelProperty(value = "项目id")
	private Integer itemId;
	@ApiModelProperty(value = "项目名称")
	private String itemName;
	@ApiModelProperty(value = "项目类型")
	private Integer itemType;
	@ApiModelProperty(value = "数量")
	private Integer useQuantity;
	@ApiModelProperty(value = "使用时间")
	private LocalDate useDate;
	@ApiModelProperty(value = "使用门诊")
	private String usedClinicName;
	@ApiModelProperty(value = "接诊医生")
	private String dentistName;
	@ApiModelProperty(value = "优惠金额")
	private BigDecimal benefitAmount;

}
