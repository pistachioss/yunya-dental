package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.form.PatientOriginForm;
import com.yunya.feign.patient_central.domain.model.PatientOriginModel;
import com.yunya.feign.patient_central.domain.vo.PatientOriginInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientOriginTreeVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 患者来源业务层
 *
 * @author: WY
 * @date 2020/8/5 13:05
 * @description: 患者来源管理业务层
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientOriginBiz extends BaseBiz<PatientOriginMapper, PatientOrigin> {

    @Autowired private PatientOriginMapper patientOriginMapper;

    /**
     * 患者原来添加
     * @param patientOriginModel
     */
    public ResponseResult add(PatientOriginModel patientOriginModel) {
        PatientOrigin patientOrigin = new PatientOrigin();
        BeanUtils.copyProperties(patientOriginModel,patientOrigin);
        PatientOrigin patientOriginv = patientOriginMapper.findPatientOriginByName(patientOrigin.getName());
        if(patientOriginv!= null){
            return ResponseUtil.error("该患者来源已添加",patientOriginv);
        }

        if(patientOrigin.getParentId() == null){
            patientOrigin.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientOrigin.setCrtName(BaseContextHandler.getName());
            mapper.insertSelective(patientOrigin);
        }else{
            if(patientOriginMapper.findPatientOriginByParentId(patientOrigin.getParentId())==null){
                return ResponseUtil.error("未找到父级来源","");
            }
            patientOrigin.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientOrigin.setCrtName(BaseContextHandler.getName());
            mapper.insertSelective(patientOrigin);
        }
        return ResponseUtil.success();
    }

    /**
     * 查询患者来源树状结构列表
     * @return
     */
    public List<PatientOriginTreeVo> initPatientOriginTree() {
        List<PatientOriginInfoVo> vos = patientOriginMapper.findAll();
        return initTree(vos);
    }


    /**
     * 构建组织树列表
     *
     * @param vos 组织列表
     * @return list
     */
    private List<PatientOriginTreeVo> initTree(List<PatientOriginInfoVo> vos) {
        List<PatientOriginTreeVo> trees = new ArrayList<>();
        if (vos.size() > 0) {
            PatientOriginTreeVo node;
            for (PatientOriginInfoVo vo : vos) {
                node = new PatientOriginTreeVo();
                BeanUtils.copyProperties(vo, node);
                trees.add(node);
            }
        }
        return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
    }

    /**
     * 患者来源修改
     * @param patientOriginForm
     */
    public ResponseResult update(PatientOriginForm patientOriginForm) {
        PatientOrigin patientOrigin = new PatientOrigin();
        BeanUtils.copyProperties(patientOriginForm,patientOrigin);
        PatientOrigin patientOriginv = mapper.selectByPrimaryKey(patientOrigin.getId());
        if(patientOriginv.getAllowOperate() == false){
            return ResponseUtil.error("该患者来源不可编辑",patientOriginv);
        }
        patientOrigin.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientOrigin.setUpdName(BaseContextHandler.getName());
        patientOrigin.setUpdTime(new Date());
        mapper.updateByPrimaryKeySelective(patientOrigin);
        return ResponseUtil.success();
    }

    /**
     * 删除患者来源
     * @param id
     */
    public ResponseResult deleteOriginById(Integer id) {
        PatientOrigin patientOriginv = mapper.selectByPrimaryKey(id);
        if(patientOriginv.getAllowOperate() == false){
            return ResponseUtil.error("该患者来源不可删除",patientOriginv);
        }
        mapper.deleteByPrimaryKey(id);
        return ResponseUtil.success();
    }

    /**
     * 根据患者来源type查询相应信息
     * @param patientOrigin
     * @return List<PatientOrigin>
     */
    public List<PatientOrigin> findPatientOriginByTypt(PatientOrigin patientOrigin) {
        List<PatientOrigin> patientOriginByTypt = mapper.findPatientOriginByTypt(patientOrigin);
        for (PatientOrigin origin : patientOriginByTypt) {
            if(origin.getTimeLimit() == 1){
               if(DateUtil.isEffectiveDate(new Date(),origin.getLimitStartDate(),origin.getLimitEndDate()) == false){
                   patientOriginByTypt.remove(origin);
               }
            }
        }
        return patientOriginByTypt;
    }
}
