package com.yunya.modules.patient_central;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.vo.web.PatientExtendInfoVo;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.patient_central.controller.web.CustomerRegistrationController;
import com.yunya.modules.patient_central.controller.web.PatientBaseInfoController;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/3/2 12:53
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PatientRegistrationControllerTest {

    @Autowired
    private CustomerRegistrationController customerRegistrationController;

    @Autowired
    private PatientBaseInfoController patientBaseInfoController;

    @Test
    public void testAdultAddPatient() {
        AdultPatientRegistrationModel model = new AdultPatientRegistrationModel();
        model.setName("楚雨荨");
        model.setOrgId(26);
        model.setAge(23);
        model.setGender((byte) 1);
        model.setBirthdate(DateTime.parse("1999-02-15").toDate());
        model.setMobile("13233351624");
        model.setMobileOwner(10);
        model.setOriginType(1);
        model.setOriginId(79);
        model.setPatientId(25);
        model.setEmployeeId(635);
        model.setState(50);
        model.setProvince("浙江省");
        model.setCity("杭州市");
        model.setCountry("西湖区");
        model.setDetailedAddress("文新街道161号");
        model.setProfession(97);
        model.setEmployer("蚂蚁金服科技有限公司");
        model.setEMail("xxxx.163.com");
        model.setEmergencyPhone("13233351629");
        model.setMedicalHistorys(Arrays.asList("哮喘","低血糖"));
        model.setAllergns(Arrays.asList("青霉素","海鲜"));
        model.setHeredity("家族性地中海贫血");
        model.setPregnancyMonth(3);
        model.setFeedBaby(true);
        model.setOtherHealth("最近牙龈疼");
        model.setHadMissTooth(true);
        model.setMissToothHistory(Arrays.asList(1,5,8));
        model.setHadFillTreat(true);
        model.setFillTreatHistory(Arrays.asList(1,2,3));
        model.setFillTreatLastDate(DateTime.parse("2020-09-10").toDate());
        model.setHadPeriodontalSurgery(true);
        model.setHadOcclusalAdjust(true);
        model.setHadRestorativeDentures(true);
        model.setRpdPart("上颚");
        model.setRpdDate(DateTime.parse("2021-02-12").toDate());
        model.setLpdPart("颧骨");
        model.setLpdDate(DateTime.parse("2021-02-13").toDate());
        model.setHadPreventiveTreat(true);
        model.setPreventiveTreatCycle((short) 1);
        model.setPreventiveTreatLastMonth((short) 2);
        model.setHadDiffcultTreat(true);
        model.setMissTeethUnrepeatCause("年龄太小");
        model.setHadOrthodontic(true);
        model.setOrthodonticStartDate(DateTime.parse("2021-08-12").toDate());
        model.setOrthodonticEndDate(DateTime.parse("2021-09-12").toDate());
        model.setHadHygieneEducation(false);
        model.setUsedPlaqueDna(false);
        model.setBrushingTimes(3);
        model.setBrushingTime((byte) 10);
        model.setBrushHardness((byte) 1);
        model.setUseFloss(false);
        model.setUseCollutory(false);
        model.setBruxism(false);
        model.setSmokingAge((byte) 4);
        model.setSmokingNum(10);
        customerRegistrationController.addPatient(model);
    }

    @Test
    public void testChildrenAddPatient() {
        ChildrenPatientRegistrationModel model = new ChildrenPatientRegistrationModel();
        model.setName("楚风荨");
        model.setOrgId(26);
        model.setAge(10);
        model.setGender((byte) 0);
        model.setBirthdate(DateTime.parse("2012-12-15").toDate());
        model.setSchool("学军小学");
        model.setGrade("五年级");
        model.setGuardian("楚霸王");
        model.setGuardianPhone("13612514529");
        model.setOriginType(3);
        model.setOriginId(79);
        model.setPatientId(25);
        model.setEmployeeId(635);
        model.setState(50);
        model.setProvince("浙江省");
        model.setCity("杭州市");
        model.setCountry("拱墅区");
        model.setDetailedAddress("文新街道161号");
        model.setProfession(97);
        model.setEMail("xxxx.163.com");
        model.setEmergencyPhone("13233351629");
        model.setMedicalHistorys(Arrays.asList("哮喘","低血糖"));
        model.setAllergns(Arrays.asList("青霉素","海鲜"));
        model.setMedicationHistory("贫血服药");
        model.setDiet("一日三餐，荤素搭配");
        model.setToothSprouting("上下大白牙刚长出来");
        model.setToothClearliness("每天刷牙");
        model.setBrushingTimes((short) 3);
        model.setUsedFluorideToothpaste(false);
        model.setUsedDentalFloss((byte) 1);
        model.setUseFlossTimes(1);
        model.setParentHasCaries(Arrays.asList(1,2));
        model.setMotherPregnancy("正常");
        model.setHabitIds(Arrays.asList(1,6,15));
        model.setToothLastCheck(DateTime.parse("2021-12-13").toDate());
        customerRegistrationController.addPatient(model);
    }

    @Test
    public void testAddPatient() {
        BaseContextHandler.setOrgId("26");
        BaseContextHandler.setUserID("635");
        BaseContextHandler.setUsername("测试-chenlin");

        String baseParam = "{\"birthday\":\"2012-12-15\",\"sourceId\":79,\"gender\":0,\"crtId\":-777,\"originType\":1,\"originId\":79,\"name\":\"楚风荨\",\"originTypeName\":\"员工转介绍\",\"attribute\":1,\"id\":108462,\"sourceName\":\"员工转介绍\",\"hasDied\":false,\"age\":9,\"originName\":\"马奕艳\"}";
        PatientBaseInfoModel base = JSONObject.parseObject(baseParam, PatientBaseInfoModel.class);
        String expParam = "{\"country\":\"上城区\",\"patientId\":108462,\"crtName\":\"患者自主登记\",\"eMail\":\"296xxx.163.com\",\"uptId\":-777,\"province\":\"浙江省\",\"updTime\":1646271955000,\"id\":107526,\"state\":\"CN\",\"profession\":93,\"updName\":\"患者自主登记\",\"usefulPhone\":\"13852514520\",\"city\":\"杭州市\",\"crtId\":-777,\"crtTime\":1646271955000,\"address\":\"文新街道2061号\",\"inservice\":true,\"emergencyPhone\":\"13329351629\"}";
        PatientExpInfoModel expModel = JSONObject.parseObject(expParam, PatientExpInfoModel.class);
        String listParam = "[{\"patientId\":108462,\"inservice\":true,\"crtId\":-777,\"crtName\":\"患者自主登记\",\"crtTime\":1646271955000,\"description\":\"哮喘,低血糖\",\"updName\":\"患者自主登记\",\"type\":1,\"orgId\":26,\"uptId\":-777,\"updTime\":1646271955000,\"id\":11763},{\"patientId\":108462,\"inservice\":true,\"crtId\":-777,\"crtName\":\"患者自主登记\",\"crtTime\":1646271955000,\"description\":\"青霉素,海鲜\",\"updName\":\"患者自主登记\",\"type\":2,\"orgId\":26,\"uptId\":-777,\"updTime\":1646271955000,\"id\":11764}]";
        List<PatientExtInfoModel> list = JSONArray.parseArray(listParam, PatientExtInfoModel.class);
        String childParam = "{\"usedFluorideToothpaste\":false,\"patientId\":108462,\"toothLastCheck\":1632004800000,\"motherPregnancy\":\"正常1\",\"usedDentalFloss\":1,\"habitIdStr\":\"11,26,15\",\"habitIds\":[1,6,15],\"brushingTimes\":20,\"parentHasCaries\":[1,3],\"school\":\"学军小学1\",\"toothSprouting\":\"上下大白牙刚长出来1\",\"grade\":\"五年级1\",\"parentHasCarieStr\":\"1,3\",\"diet\":\"一日三餐，荤素搭配1\",\"id\":4,\"guardian\":\"楚霸王1\",\"toothClearliness\":\"每天刷牙1\",\"useFlossTimes\":1}";
        PatientChildInfoModel childModel = JSONObject.parseObject(childParam, PatientChildInfoModel.class);

        PatientExtendInfoModel model = new PatientExtendInfoModel();
        model.setPatientBaseInfoModel(base);
        model.setPatientExpInfoModel(expModel);
        model.setPatientExtInfoModelList(list);
        model.setPatientChildInfoModel(childModel);
        patientBaseInfoController.addPatientInfo(model);
    }

    @Test
    public void testFindPatientData() {
        ResponseResult<PatientExtendInfoVo> patientData = patientBaseInfoController.findPatientData(108462);
        System.out.println(JSONObject.toJSON(patientData));
    }
}
