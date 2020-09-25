package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 患者档案中查询预约记录参数封装
 * @author: LHB
 * @create: 2020-09-25 19:01
 **/
@ApiModel(value = "患者档案中查询预约记录参数封装")
@Data
public class AppointPatientRecordQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 患者ID */
    @ApiModelProperty(value = "患者ID", required = true)
    @NotNull(message = "患者ID不能为空")
    private Integer patientId;

    /** 查询开始时间 */
    @ApiModelProperty(value = "查询开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;

    /** 查询结束时间 */
    @ApiModelProperty(value = "查询结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;

    /** 门诊ID */
    @ApiModelProperty(value = "门诊ID")
    private Integer orgId;

    /** 预约医生姓名 */
    @ApiModelProperty(value = "预约医生姓名")
    private String dentistName;

}
