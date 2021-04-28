package com.yunya.middletable.model.credits_shop;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 积分结果反馈参数
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@ApiModel(value = "AddCreditResult1", description = "积分结果反馈参数")
@Data
public class CreditResult implements Serializable {
    /** 扣积分结果状态，回复ok或者fail （不要使用0和1） */
    private String status;
    /** 出错原因 */
    private String errorMessage="";
    /** 开发者的订单号(唯一且不重复，如果失败情况，该值可以不传) */
    private String bizId="";
    /** 用户积分余额 */
    private String credits;
}
