package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.models.treatment_other.VisitingRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 后续随访视图模型
 * @author: LHB
 * @create: 2020-12-09 14:41
 **/
@Data
@ApiModel(value = "NextVisitingRecordVo", description = "后续随访视图模型")
public class NextVisitingRecordVo extends VisitingRecord implements Serializable {
    @ApiModelProperty("后续随访数量")
    private Integer count;
}
