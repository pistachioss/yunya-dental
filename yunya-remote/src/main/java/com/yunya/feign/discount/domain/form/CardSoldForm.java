package com.yunya.feign.discount.domain.form;

import com.yunya.feign.discount.valid.*;
import io.swagger.annotations.*;
import lombok.*;
import org.hibernate.validator.group.*;

import javax.validation.constraints.*;
import java.io.*;

/**
 * @author xiangyang
 * @date 2020/8/26
 */
@Getter
@Setter
@ApiModel(value = "卡券售出模型")
@GroupSequenceProvider(CardSoldGroupSequenceProvider.class)
public class CardSoldForm implements Serializable {
    @ApiModelProperty(value = "优惠券id", required = true)
    private Integer couponId;
    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "售出对象", required = true)
    @NotBlank
    private String soldTarget;
    @ApiModelProperty(value = "售出对象手机号", required = true)
    @NotBlank
    private String soldPhoneNumber;
    @ApiModelProperty(value = "售出类型（0:售出 1:置换 2:赠送）", required = true)
    @NotNull
    private Integer soldType;
    @ApiModelProperty(value = "发短信", required = true)
    @NotNull
    private Integer sendText;
    @ApiModelProperty(value = "售出并付款")
    @NotNull(groups = SoldAndPayViewGroup.class, message = "售出并付款不能为空")
    @Null(groups = SoldAnPayNotViewsGroup.class, message = "售出并付款必须为空")
    private Integer soldAndPay;
    @ApiModelProperty(value = "入账方式")
    @NotNull(groups = PayViewGroup.class, message = "入账方式不能为空")
    @Null(groups = PayNotViewGroup.class, message = "入账方式必须为空")
    private Integer payId;
    @ApiModelProperty(value = "备注")
    @Size(max = 150)
    private String remark;
    @ApiModelProperty(value = "销售渠道", required = true)
    @NotNull
    private Integer saleChannelId;
    @ApiModelProperty(value = "售出方式（0:线上 1:线下）", required = true)
    @NotNull
    private Integer soldWay;
    @ApiModelProperty(value = "优惠券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）", required = true)
    @NotNull
    private Integer couponType;

    public interface SoldAndPayViewGroup {
    }
    public interface SoldAnPayNotViewsGroup {
    }
    public interface PayViewGroup {
    }
    public interface PayNotViewGroup {
    }
}
