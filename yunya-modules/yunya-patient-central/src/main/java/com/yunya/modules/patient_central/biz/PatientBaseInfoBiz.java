package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientExpInfo;
import com.yunya.models.patient_central.PatientExtInfo;
import com.yunya.feign.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.feign.patient_central.domain.model.PatientExtendInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientExtendInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientPublicInfoVo;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientExtInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 患者基本信息业务层
 *
 * @author: WY
 * @date 2020/7/25 11:25
 * @description: 患者基本信息业务层（增删查改）
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientBaseInfoBiz extends BaseBiz<PatientBaseInfoMapper, PatientBaseInfo> {

    @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

    @Autowired private PatientExtInfoMapper patientExtInfoMapper;

    @Autowired private PatientExpInfoMapper patientExpInfoMapper;

    @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;


    /**
     * 通过患者id查询患者共用属性
     * @param id
     * @return PatientPublicInfo
     */
    public ResponseResult findPatientPublicInfoById(Integer id) {
        PatientPublicInfoVo patientPublicInfoVo = new PatientPublicInfoVo();
        patientPublicInfoVo =  patientBaseInfoMapper.findPatientPublicInfoById(id);
        if(patientPublicInfoVo.getMemberTypeId()==null){
            return ResponseUtil.success("该患者会员卡类型ID为空","");
        }
        MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(patientPublicInfoVo.getMemberTypeId());
        if(memberType.getName() == null){
            return ResponseUtil.success("根据患者会员卡类型ID未查询到会员卡","");
        }
        patientPublicInfoVo.setMemberCardName(memberType.getName()); // 根据会员卡类型id调用feign 查询会员卡类型名称
        return ResponseUtil.success(patientPublicInfoVo);
    }

    /**
     * 查询患者是否存在
     * @param patientBaseInfoQueryForm
     */
    public ResponseResult findUserExists(PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
        PatientBaseInfoVo patientBaseInfoVo = new PatientBaseInfoVo();
        patientBaseInfoVo = patientBaseInfoMapper.findUserExists(patientBaseInfoQueryForm);
        if (patientBaseInfoVo != null){
           return ResponseUtil.success("添加失败,该用户已存在",patientBaseInfoVo);
        }
        int count = patientBaseInfoMapper.findUserExistsByMobile(patientBaseInfoQueryForm.getMobile());
        if(count > 0){
            return ResponseUtil.success("该手机号已存在");
        }
        return ResponseUtil.success();
    }

    /**
     * 添加患者信息
     * @param patientBaseInfoModel
     */
    public void addPatient(PatientBaseInfoModel patientBaseInfoModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientBaseInfo.setCrtName(BaseContextHandler.getName());
        patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        mapper.insertSelective(patientBaseInfo);
    }

    /**
     * 添加完善患者扩展信息、其他信息
     * @param patientExtendInfoModel
     */
    public void addPatientInfo(PatientExtendInfoModel patientExtendInfoModel) {
        PatientBaseInfo patientBaseInfo = patientExtendInfoModel.getPatientBaseInfo(); //完善患者基本信息  对补全信息进行更新
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientBaseInfo.setUpdName(BaseContextHandler.getName());
        patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientBaseInfo.setUpdTime(new Date());
        mapper.updateByPrimaryKey(patientBaseInfo);
        PatientExpInfo patientExpInfo = patientExtendInfoModel.getPatientExpInfo();    //完善患者扩展信息
        if(patientExpInfo.getId() == null){   //如果用户没有扩展信息就添加扩展信息 如果有就修改
            patientExpInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientExpInfo.setCrtName(BaseContextHandler.getName());
            patientExpInfoMapper.insertSelective(patientExpInfo);
        }else {
            patientExpInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientExpInfo.setUpdName(BaseContextHandler.getName());
            patientExpInfo.setUpdTime(new Date());
            patientExpInfoMapper.updateByPrimaryKey(patientExpInfo);
        }
        List<PatientExtInfo> patientExtInfoList = patientExtendInfoModel.getPatientExtInfoList();//完善患者其他信息（标签、疾病史、过敏原）
        List<PatientExtInfo> patientExtInfos = patientExtInfoMapper.patientExtInfoListByid(patientBaseInfo.getId());
        if(patientExtInfos.size()!= 0 || patientExtInfos != null){ //判断是否已存在id，若存在就删除
            patientExtInfoMapper.deletePatientExtInfoByPatientId(patientBaseInfo.getId());
        }
        List<PatientExtInfo> addPatientExtInfoList = new ArrayList<PatientExtInfo>();            //循环添加 标签、疾病史、过敏原 集合
        for (PatientExtInfo patientExtInfo: patientExtInfoList) {
            patientExtInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientExtInfo.setCrtName(BaseContextHandler.getName());
            patientExtInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientExtInfo.setUpdName(BaseContextHandler.getName());
            patientExtInfo.setUpdTime(new Date());
            addPatientExtInfoList.add(patientExtInfo);
        }
        patientExtInfoMapper.insertPatientExtInfoList(addPatientExtInfoList);

    }

    /**
     * 根据患者id查询患者资料
     * @param id
     * @return PatientExtendInfoModel
     */
    public PatientExtendInfoVo findPatientDate(Integer id) {
        PatientExtendInfoVo patientExtendInfoVo = new PatientExtendInfoVo();
        patientExtendInfoVo.setPatientBaseInfo(mapper.selectByPrimaryKey(id));
        patientExtendInfoVo.setPatientExpInfo(patientExpInfoMapper.selectIdByPatientId(id));
        patientExtendInfoVo.setPatientExtInfoList(patientExtInfoMapper.patientExtInfoListByid(id));
        return patientExtendInfoVo;
    }

    /**
     * 根据姓名/手机号/姓名拼音模糊查询患者
     * @param condition
     * @return List<PatientBaseInfoVo>
     */
    public List<PatientBaseInfoVo> findPatientByNameAndMobile(PatientLikeFinleQueryForm form) {
        return patientBaseInfoMapper.findPatientByNameAndMobile(form);
    }

    /**
     * 根据输入年龄计算患者出生年份
     * @param age 年龄
     */
    public Date birthYear(Integer age) {
        // 获取输入年龄当天日历
        Calendar now = Calendar.getInstance();
        // 计算减去年龄后的年份
        now.add(Calendar.YEAR, -age);
        // 拼装日期字符串
        String birthYear =
                now.get(Calendar.YEAR)
                        + "-"
                        + (now.get(Calendar.MONTH) + 1)
                        + "-"
                        + now.get(Calendar.DAY_OF_MONTH);
        // 设置时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(birthYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
