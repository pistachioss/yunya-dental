package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQuery;
import com.yunya.framework.common.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：病历照片记录查询模型
 *
 * @author: chenlin
 * @Description: 病历照片记录查询模型
 * @Date: 2022/3/24 10:56
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("病历照片记录查询模型")
public class MedicalPictureRecordQuery extends PageQuery implements Serializable {

    /** 患者id*/
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 日期*/
    @ApiModelProperty(value = "日期")
    private String name;

    public void setName(String name) {
        this.name = DateUtil.format(DateUtil.parse2Date(name));
    }
}
