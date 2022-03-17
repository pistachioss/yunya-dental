package com.yunya.modules.patient_central;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.vo.web.PatientExpInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientExtendInfoVo;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.patient_central.biz.CustomerRegistrationBiz;
import com.yunya.modules.patient_central.controller.web.CustomerRegistrationController;
import com.yunya.modules.patient_central.controller.web.PatientBaseInfoController;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
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
    private CustomerRegistrationBiz customerRegistrationBiz;

    @Autowired
    private PatientBaseInfoController patientBaseInfoController;

    @Autowired
    private RemoteOssServiceFeign remoteOssServiceFeign;
    @Autowired
    private PatientExpInfoMapper patientExpInfoMapper;

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
        model.setFillTreatLastDate("2020-09-10");
        model.setHadPeriodontalSurgery(true);
        model.setHadOcclusalAdjust(true);
        model.setHadRestorativeDentures(true);
        model.setRpdPart("上颚");
        model.setRpdDate("2021-02-12");
        model.setLpdPart("颧骨");
        model.setLpdDate("2021-02-13");
        model.setHadPreventiveTreat(true);
        model.setPreventiveTreatCycle((short) 1);
        model.setPreventiveTreatLastMonth((short) 2);
        model.setHadDiffcultTreat(true);
        model.setMissTeethUnrepeatCause("年龄太小");
        model.setHadOrthodontic(true);
        model.setOrthodonticStartDate("2021-08-12");
        model.setOrthodonticEndDate("2021-09-12");
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
        model.setName("楚风俊");
        model.setOrgId(26);
        model.setAge(10);
        model.setGender((byte) 0);
        model.setBirthdate(DateTime.parse("2012-12-15").toDate());
        model.setSchool("学军小学");
        model.setGrade("五年级");
        model.setGuardian("楚霸王");
        model.setGuardianPhone("13612514529");
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
        model.setFatherHasCaries(true);
        model.setMotherHasCaries(false);
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
        String expParam = "{\"country\":\"上城区\",\"patientId\":108462,\"crtName\":\"患者自主登记\",\"eMail\":\"296xxx.163.com\",\"uptId\":-777,\"province\":\"浙江省\",\"updTime\":1646271955000,\"id\":107526,\"state\":211,\"profession\":93,\"updName\":\"患者自主登记\",\"usefulPhone\":\"13852514520\",\"city\":\"杭州市\",\"crtId\":-777,\"crtTime\":1646271955000,\"address\":\"文新街道2061号\",\"inservice\":true,\"emergencyPhone\":\"13329351629\"}";
        PatientExpInfoModel expModel = JSONObject.parseObject(expParam, PatientExpInfoModel.class);
        String listParam = "[{\"patientId\":108462,\"inservice\":true,\"crtId\":-777,\"crtName\":\"患者自主登记\",\"crtTime\":1646271955000,\"description\":\"哮喘,低血糖\",\"updName\":\"患者自主登记\",\"type\":1,\"orgId\":26,\"uptId\":-777,\"updTime\":1646271955000,\"id\":11763},{\"patientId\":108462,\"inservice\":true,\"crtId\":-777,\"crtName\":\"患者自主登记\",\"crtTime\":1646271955000,\"description\":\"青霉素,海鲜\",\"updName\":\"患者自主登记\",\"type\":2,\"orgId\":26,\"uptId\":-777,\"updTime\":1646271955000,\"id\":11764}]";
        List<PatientExtInfoModel> list = JSONArray.parseArray(listParam, PatientExtInfoModel.class);
        String childParam = "{\"motherHasCaries\":false,\"fatherHasCaries\":true,\"usedFluorideToothpaste\":false,\"patientId\":108462,\"toothLastCheck\":\"2022-01-05\",\"motherPregnancy\":\"正常1\",\"usedDentalFloss\":1,\"habitIdStr\":\"11,26,15\",\"habitIds\":[1,6,15],\"brushingTimes\":20,\"school\":\"学军小学1\",\"toothSprouting\":\"上下大白牙刚长出来1\",\"grade\":\"五年级1\",\"diet\":\"一日三餐，荤素搭配1\",\"id\":4,\"guardian\":\"楚霸王2\",\"guardianPhone\":\"13528145211\",\"toothClearliness\":\"每天刷牙1\",\"useFlossTimes\":1}";
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
        ResponseResult<PatientExtendInfoVo> patientData = patientBaseInfoController.findPatientData(78307);
        System.out.println(JSONObject.toJSON(patientData));
    }

    @Test
    public void savePatientSignature() throws Exception {
        Integer patientId = 108462;
        PatientRegistrationModel patientModel = new PatientRegistrationModel();
        patientModel.setName("crazyman");
        patientModel.setSignatureImgUrl("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");
        customerRegistrationBiz.savePatientSignature(patientModel, -666, patientId);
        String fileUrl = "";
        OssUrlForm ossUrlForm = new OssUrlForm();
        ossUrlForm.setCompanyId(0);
        ossUrlForm.setIsThumb(false);
        ossUrlForm.setObjectId(patientId);
        ossUrlForm.setOssCategory(3);
        ossUrlForm.setOssFilename(fileUrl);
        final ResponseResult url = remoteOssServiceFeign.getUrl(Arrays.asList(ossUrlForm));
        System.out.println(JSONObject.toJSON(url.getData()));
    }

    @Test
    public void test() {
        PatientExpInfoVo patientExpInfoVo = patientExpInfoMapper.selectByPatientId(112634);
        System.out.println(JSONObject.toJSON(patientExpInfoVo));
    }
}
