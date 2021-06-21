package com.yunya.modules.appointment;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.util.pageUtil.model.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-06-15 15:25
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class JolObjTest {
    @Test
    public void appointSettingObj() {
        ClassLayout appointSettingObj = ClassLayout.parseClass(AppointSettingVo.class);
        System.out.println("预约设置对象AppointSettingVo ===>\n" + appointSettingObj.toPrintable());

        AppointmentDimensionVo appointmentDimensionVo = new AppointmentDimensionVo();
        PageInfo<AppointmentDimensionVo> objectPageInfo = new PageInfo<>();
        List<AppointmentDimensionVo> list = new ArrayList<>();
        AppointmentDimensionVo e = new AppointmentDimensionVo();
        List<EmpScheduleVo> emplSchedule = new ArrayList<>();
        e.setDentistScheduleVos(emplSchedule);
        List<AppointmentPatientCardVo> cards = new ArrayList<>();
        e.setAppointmentPatientCardVos(cards);
        List<AppointmentDimensionVo> em = new ArrayList<>();
        e.setAppointmentAssistants(em);
        list.add(e);
        objectPageInfo.setList(list);
        ResponseResult<PageInfo<AppointmentDimensionVo>> responseResult = ResponseUtil.success(objectPageInfo);
        ClassLayout responseObjAppointmentDimensionVo = ClassLayout.parseInstance(responseResult);
        System.out.println("预约可视图（患者维度）AppointmentDimensionVo ===>\n" + responseObjAppointmentDimensionVo.toPrintable());
    }
}
