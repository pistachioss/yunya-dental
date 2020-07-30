package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 * 卡券销售表单
 *
 * @author GaoLuding
 * @create 2020-07-23 16:25
 */
@Data
@ApiModel("卡券售出表单")
public class CardSaleForm implements Serializable {

    @ApiModelProperty("卡号列表")
    @NotNull(message = "卡号列表不能为空")
    private List<Integer> cardIds;

    /**
     * 是否短信通知,0:不通知,1:通知
     */
    @ApiModelProperty("是否短信通知,0:不通知,1:通知")
    @NotNull(message = "是否短信通知不能为空")
    private Boolean smsType;

    /**
     * 出售类型 0:售卖,1:赠送:2:置换
     */
    @ApiModelProperty("出售类型 0:售卖,1:赠送:2:置换")
    @NotNull(message = "出售类型不能为空")
    private Integer sellingType;

    /**
     * 买家姓名
     */
    @ApiModelProperty("买家姓名")
    @NotBlank(message = "")
    private String buyerName;

    /**
     * 买家电话
     */
    @ApiModelProperty("买家电话")
    @NotBlank(message = "")
    private String buyerPhone;

    /**
     * 是否收款 0:未收费,1:收费
     */
    @ApiModelProperty("是否收款 0:未收费,1:收费")
    @NotNull(message = "是否收款选项不能为空")
    private Boolean charge;

    /**
     * 入账方式
     */
    @ApiModelProperty("入账方式ID")
    @NotNull(message = "入账方式ID不能为空")
    private Integer accountingId;
}
