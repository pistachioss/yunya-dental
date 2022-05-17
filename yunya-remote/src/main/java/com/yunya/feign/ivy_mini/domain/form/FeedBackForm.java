package com.yunya.feign.ivy_mini.domain.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Data
@ApiModel(value = "意见反馈")
public class FeedBackForm extends PageQuery implements Serializable {

    @ApiModelProperty(value = "提交时间")
    private String crtTime;
    @ApiModelProperty(value = "提交用户")
    private String name;
    @ApiModelProperty(value = "后端使用")
    private List<Integer> nameList;

}
