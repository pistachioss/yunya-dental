package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.PatientKinRelationForm;
import com.yunya.feign.patient_central.domain.model.PatientKinRelationModel;
import com.yunya.feign.patient_central.domain.query.PatientKinRelationQueryForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientKinRelation;
import com.yunya.feign.patient_central.domain.vo.PatientKinRelationVo;
import com.yunya.modules.patient_central.mapper.PatientKinRelationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 患者亲属关系 业务层
 *
 * @author: WY
 * @date 2020/7/28 20:56
 * @description: 患者亲属关系 增删查改
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientKinRelationBiz extends BaseBiz<PatientKinRelationMapper, PatientKinRelation> {

    @Autowired private PatientKinRelationMapper patientKinRelationMapper;

    /**
     * 根据患者id 查询患者亲属列表
     * @param form
     * @return List<PatientKinRelation>
     */
    public PageInfo<PatientKinRelationVo> findList(PatientKinRelationQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<PatientKinRelationVo> resultList = patientKinRelationMapper.selectListByPatientId(form.getPatientId());
        return new PageInfo<>(resultList);
    }

    /**
     * 添加患者亲属关系
     * @param patientKinRelationModel
     * @return
     */
    public ResponseResult add(PatientKinRelationModel patientKinRelationModel) {
        PatientKinRelation patientKinRelation = new PatientKinRelation();
        BeanUtils.copyProperties(patientKinRelationModel,patientKinRelation);
        PatientKinRelation patientKinRelationvo = patientKinRelationMapper.findPatientKinRelation(patientKinRelation);
        if(patientKinRelationvo!=null){
            return ResponseUtil.success("患者关系已存在",patientKinRelationvo);
        }
        if(patientKinRelationModel.getId() == null){
            patientKinRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientKinRelation.setCrtName(BaseContextHandler.getName());
            patientKinRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            mapper.insert(patientKinRelation);
        }
        return ResponseUtil.success();
    }

    /**
     * 修改患者亲属关系
     * @param patientKinRelationForm
     * @return ResponseResult
     */
    public void update(PatientKinRelationForm patientKinRelationForm) {
        PatientKinRelation patientKinRelation = new PatientKinRelation();
        BeanUtils.copyProperties(patientKinRelationForm,patientKinRelation);
        patientKinRelation.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientKinRelation.setUpdName(BaseContextHandler.getName());
        patientKinRelation.setUpdTime(new Date());
        patientKinRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        mapper.updateByPrimaryKeySelective(patientKinRelation);
    }
}
