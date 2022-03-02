package com.yunya.modules.patient_central;

import com.yunya.feign.patient_central.domain.model.AdultPatientRegistrationModel;
import com.yunya.feign.patient_central.domain.model.ChildrenPatientRegistrationModel;
import com.yunya.modules.patient_central.controller.web.CustomerRegistrationController;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

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
        model.setState("CN");
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
        model.setOriginType(1);
        model.setOriginId(79);
        model.setState("CN");
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
}
