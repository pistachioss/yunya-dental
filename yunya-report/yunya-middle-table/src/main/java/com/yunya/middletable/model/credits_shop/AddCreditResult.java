package com.yunya.middletable.model.credits_shop;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 用户积分增加
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@ApiModel(value = "AddCreditResult1", description = "用户积分增加")
@Data
public class AddCreditResult implements Serializable {
    private boolean success;
    private String errorMessage="";
    private String bizId="";
    /** 用户积分余额 */
    private Long credits=-1L;

    public AddCreditResult(boolean success){
        this.success=success;
    }


    @Override
    public String toString(){
        if(success){
            return "{'status':'ok','errorMessage':'','bizId':'"+bizId+"','credits':'"+credits+"'}";
        }else{
            return "{'status':'fail','errorMessage':'"+errorMessage+"','credits':'"+credits+"'}";
        }
    }
}
