package com.yunya.feign.patient_central.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@ApiModel(value = "WxFansQueryForm",description = "公司微信公众号粉丝参数模型")
public class WxFansQueryForm extends PageQuery implements Serializable {

    @ApiModelProperty("开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty("结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    @ApiModelProperty("客户的姓名/昵称/手机号(包括被绑定的客户)")
    private String name;
    @ApiModelProperty("绑定状态 0:未绑定患者 1:已绑定患者")
    private Integer isBind;
}
