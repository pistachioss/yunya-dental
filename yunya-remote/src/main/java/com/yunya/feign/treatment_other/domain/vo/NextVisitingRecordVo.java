package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.models.treatment_other.VisitingRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 后续随访视图模型
 * @author: LHB
 * @create: 2020-12-09 14:41
 **/
@Data
@ApiModel(value = "NextVisitingRecordVo", description = "后续随访视图模型")
public class NextVisitingRecordVo implements Serializable {
    @ApiModelProperty("后续随访数量")
    private Integer count;
    /**
     * 患者ID
     */
    @ApiModelProperty("患者ID")
    private Integer patientId;
}
