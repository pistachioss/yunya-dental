package com.yunya.modules.patient_central.biz;

import com.sun.xml.internal.bind.v2.TODO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.patient_central.domain.model.PatientBaseInfoModel;
import com.yunya.modules.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.modules.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.modules.patient_central.domain.vo.PatientPublicInfoVo;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    /**
     * 通过患者id查询患者共用属性
     * @param id
     * @return PatientPublicInfo
     */
    public PatientPublicInfoVo findPatientPublicInfoById(Integer id) {
        PatientPublicInfoVo patientPublicInfoVo = new PatientPublicInfoVo();
        patientPublicInfoVo =  patientBaseInfoMapper.findPatientPublicInfoById(id);
        patientPublicInfoVo.setMemberCardName(""); //TODO 根据会员卡类型id调用feign
        return patientPublicInfoVo;
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
     * @param patientBaseInfo
     */
    public void addPatient(PatientBaseInfoModel patientBaseInfoModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientBaseInfo.setCrtName(BaseContextHandler.getName());
        mapper.insertSelective(patientBaseInfo);
    }
}
