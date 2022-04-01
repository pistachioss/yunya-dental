package com.yunya.modules.emr;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import com.yunya.feign.emr.domain.model.MedicalPictureRecordModel;
import com.yunya.feign.emr.domain.query.MedicalPictureRecordExistsQuery;
import com.yunya.feign.emr.domain.query.MedicalPictureRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalPictureRecordVO;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.modules.emr.controller.MedicalPictureRecordController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YunyaEmrApplicationTests {
    @Autowired
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;

    @Autowired
    private MedicalPictureRecordController medicalPictureRecordController;

    @Test
    void contextLoads() {
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setStartDate("2020-09-24");
        employeeScheduleQueryForm.setEndDate("2020-09-24");
        employeeScheduleQueryForm.setClinicId(42);
        EmployeeScheduleResultVO vo = new EmployeeScheduleResultVO();
        vo = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
        System.out.println(vo);
    }

    @Test
    public void testSaveMedicalPicture() {
        BaseContextHandler.setUserID("635");
        String files = "[\n" +
                "        {\n" +
                "            \"fileLocation\":\"e7643100-1496-49ee-af4c-b1233953c60d.png\",\n" +
                "            \"fileName\":\"3.png\",\n" +
                "            \"uploadTime\":\"2022-05-11\",\n" +
                "            \"url\":\"img/ivy2-dev/patient/108355/e7643100-1496-49ee-af4c-b1233953c60d.png?Expires=1648196237&OSSAccessKeyId=LTAI4GL3SpbVMBDnzGRYz6GZ&Signature=H3rSt5jfb7%2FcItJXAkztVwJMUSI%3D&x-oss-process=image%2Fresize%2Cm_lfit%2Ch_100%2Cw_100\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fileLocation\":\"5bfe7e1e-2dc6-4bd7-b26b-1a939d7f8dbc.pdf\",\n" +
                "            \"fileName\":\"《4.pdf\",\n" +
                "            \"uploadTime\":\"2021-11-20\",\n" +
                "            \"url\":\"img/ivy2-dev/patient/108355/5bfe7e1e-2dc6-4bd7-b26b-1a939d7f8dbc.pdf?Expires=1648196237&OSSAccessKeyId=LTAI4GL3SpbVMBDnzGRYz6GZ&Signature=EmKTBhOAP0e6r6SkBuO6SRoXFH4%3D&x-oss-process=image%2Fresize%2Cm_lfit%2Ch_100%2Cw_100\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fileLocation\":\"98b8997c-e2ec-473f-b6f3-016cb21d43be.pdf\",\n" +
                "            \"fileName\":\"5.pdf\",\n" +
                "            \"uploadTime\":\"2021-11-19\",\n" +
                "            \"url\":\"img/ivy2-dev/patient/108355/98b8997c-e2ec-473f-b6f3-016cb21d43be.pdf?Expires=1648196237&OSSAccessKeyId=LTAI4GL3SpbVMBDnzGRYz6GZ&Signature=O5XnoATWzYHcL5HnwiHEz5DAato%3D&x-oss-process=image%2Fresize%2Cm_lfit%2Ch_100%2Cw_100\"\n" +
                "        }\n" +
                "    ]";
        MedicalPictureRecordModel model = new MedicalPictureRecordModel();
        model.setId(2);
        model.setName("2021/12/10");
        model.setPatientId(108355);
        model.setFiles(JSONObject.parseArray(files, XUploadFileVO.class));
        medicalPictureRecordController.save(model);
    }

    @Test
    public void testFindMedicalPictures() {
        MedicalPictureRecordQuery query = new MedicalPictureRecordQuery();
        query.setPatientId(1564);
        query.setWhetherPage(false);
        PageInfo<MedicalPictureRecordVO> data = medicalPictureRecordController.findList(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }

    @Test
    public void testIsExistsMedicalPictureRecord() {
        MedicalPictureRecordExistsQuery query = new MedicalPictureRecordExistsQuery();
//        query.setId(1);
        query.setPatientId(108355);
        query.setName("2021/12/10");
        Boolean data = medicalPictureRecordController.isExistsMedicalPictureRecord(query).getData();
        System.out.println(JSONObject.toJSON(data));
    }

    @Test
    public void testDelete() {
        BaseContextHandler.setUserID("635");
        medicalPictureRecordController.delete(2);
    }

}
