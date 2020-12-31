package com.yunya.feign.employee_attend.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 简介：考勤打卡分页响应模型
 *
 * @author: chenlin
 * @Description: 考勤打卡分页响应模型
 * @Date: 2020/12/30 17:13
 * @since: 1.0.0
 */
@ApiModel("考勤打卡分页响应模型")
@Data
@ToString
public class AttendancePunchPageInfoVO<T> extends PageInfo<T> {

    /** 工作时长/加班时长 **/
    @ApiModelProperty("工作时长/加班时长")
    private Long minute;

    public AttendancePunchPageInfoVO() {
        super();
    }

    public AttendancePunchPageInfoVO(List<T> list, Long minute) {
        super(list);
        setMinute(minute);
    }

    public AttendancePunchPageInfoVO(List<T> list) {
        super(list);
    }
}
