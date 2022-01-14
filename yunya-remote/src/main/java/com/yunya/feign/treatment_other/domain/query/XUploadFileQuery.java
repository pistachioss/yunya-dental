package com.yunya.feign.treatment_other.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：文件上传查询模型
 *
 * @author: chenlin
 * @Description: 文件上传查询模型
 * @Date: 2022/1/10 19:13
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("文件上传查询模型")
public class XUploadFileQuery extends PageQuery implements Serializable {
    @ApiModelProperty(value = "数据来源id列表", required = true)
    @NotNull(message = "数据来源id列表不能为空")
    private List<Integer> sourceIds;

    @ApiModelProperty(value = "数据来源类型", required = true)
    @NotNull(message = "数据来源类型不能为空")
    private Byte sourceType;
}
