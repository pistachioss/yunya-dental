package com.yunya.feign.appointment.vo;

import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-03-25 13:45
 **/
@ApiModel(value = "EmpScheduleVo",description = "排班")
@Data
public class EmpScheduleVo implements Serializable {
    @ApiModelProperty("员工ID")
    private Integer userId;
    @ApiModelProperty("员工名称")
    private String userName;
    @ApiModelProperty("0-无排班 1-有排班 2-请假 3-加班 4-外勤")
    private Integer type;
    @ApiModelProperty("门诊ID")
    private Integer companyId;
    @ApiModelProperty("门诊名称")
    private String comName;
    @ApiModelProperty("排班（上班/休息）; 假期类型(班次/天)")
    private String status;
    @ApiModelProperty("排班/请假 开始日期")
    private String startDate;
    @ApiModelProperty("排班/请假 结束日期")
    private String endDate;
    @ApiModelProperty("排班班次/加班班次/请假详情")
    private String name;













//    @ApiModelProperty("排班明细表")
//    private List<EmpSchedultDetailVo> empSchedultDetailVos;
//
//    public void add(EmpSchedultDetailVo empSchedultDetailVo) {
//        synchronized (this) {
//            if (StringHelper.isEmpty(empSchedultDetailVos)) {
//                empSchedultDetailVos = new ArrayList<>();
//            }
//            empSchedultDetailVos.add(empSchedultDetailVo);
//        }
//    }
}
