package com.yunya.modules.appointment.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.appointment.domain.form.AppointmentBaseForm;
import com.yunya.feign.appointment.domain.query.AppointmentCurrentListQuery;
import com.yunya.feign.appointment.vo.AppointmentUnDonePatientInfoVO;
import com.yunya.models.appointment.Appointment;
import com.yunya.modules.appointment.biz.web.AppointmentModifyRecordBiz;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin @Description: @Date: 2021/2/8 10:41
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppointmentControllerTest {
  @Autowired private AppointmentModifyRecordBiz appointmentModifyRecordBiz;
  @Autowired private AppointmentMapper appointmentMapper;

  @Test
  public void test() {
    String param =
        "{\"patientId\":105884,\"patientName\":\"crazyman\",\"appointDate\":\"2021-02-08\",\"dentistId\":175,\"appointTime\":\"10:00\",\"assistantId\":null,\"appointDuration\":10,\"deptRoomId\":null,\"confirmStatus\":true,\"clinicDeviceItemId\":null,\"remarks\":\"\",\"appointContent\":\"#72: \",\"toothBit\":\"\",\"splitList\":[],\"id\":498383,\"gender\":0,\"age\":null,\"patientMobile\":\"13867185423\",\"orgId\":29,\"dentistName\":\"陈林\",\"assistantName\":null,\"deptRoomName\":null,\"clinicDeviceItemName\":null,\"clinicAppointItemId\":null,\"appointStartTime\":\"2021-02-08 10:00:00\",\"appointEndTime\":\"2021-02-08 10:10:00\",\"appointPeriod\":\"10:00-10:10\",\"appointType\":0,\"appointStatus\":0,\"cancelReason\":null,\"inservice\":true,\"crtTime\":\"2021-02-08 09:50:45\",\"crtName\":\"陈林\",\"appointId\":498383}";
    AppointmentBaseForm appointmentForm = JSONObject.parseObject(param, AppointmentBaseForm.class);
    appointmentForm.setOrgId(29);
    Appointment appointment = appointmentMapper.selectByPrimaryKey(appointmentForm.getId());
    appointmentModifyRecordBiz.saveAppointModify(appointmentForm, appointment);
  }

  @Test
  public void find() {
    AppointmentCurrentListQuery qu = new AppointmentCurrentListQuery();
    qu.setOrgId(29);
    qu.setCurrentDate("2021-03-07");
    List<AppointmentUnDonePatientInfoVO> vos =
        appointmentMapper.selectAppointmentUnDonePatientInfoList(qu);
    System.out.println(vos);
  }

  @Test
  public void cancelAppointmentList() {
    String param = "{\"startDate\":\"2021-01-01\",\"endDate\":\"2021-05-01\",\"orgIds\":[26],\"dentistIds\":[]}";
    CancelAppointmentQuery query = JSONObject.parseObject(param, CancelAppointmentQuery.class);
    long t1 = System.currentTimeMillis();
    ResponseResult<PageInfo<CancelAppointmentVO>> result = appointmentController.cancelAppointmentList(query);
    long t2 = System.currentTimeMillis();
    System.out.println("接口耗时：" + (t2 - t1));
    System.out.println(JSONObject.toJSON(result.getData()));
  }
}
