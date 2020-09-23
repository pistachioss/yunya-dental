package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.form.PatientOriginForm;
import com.yunya.feign.patient_central.domain.model.PatientOriginModel;
import com.yunya.feign.patient_central.domain.query.OriginTypeQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientAndStaffListInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginTreeVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

  /** 注入患者来源Mapper */
  @Autowired private PatientOriginMapper patientOriginMapper;

  /** 注入服务 */
  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入患者信息Mapper */
  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

  /**
   * 患者原来添加
   *
   * @param patientOriginModel 患者来源分类添加模板类
   */
  public ResponseResult add(PatientOriginModel patientOriginModel) {
    PatientOrigin patientOrigin = new PatientOrigin();
    BeanUtils.copyProperties(patientOriginModel, patientOrigin);
    PatientOrigin patientOriginv =
        patientOriginMapper.findPatientOriginByName(patientOrigin.getName());
    if (patientOriginv != null) {
      return ResponseUtil.fail(OperationCodeConstants.DATA_EXIST, "该患者来源已添加", patientOriginv);
    }

    if (patientOrigin.getParentId() == null) {
      Integer maxiType = mapper.selectTypeMaximum();
      if (maxiType >= 0) { // 查询患者来源type字典最大值
        patientOrigin.setOriginType(maxiType+1);
        patientOrigin.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientOrigin.setCrtName(BaseContextHandler.getName());
        mapper.insertSelective(patientOrigin);
      }
    } else {
      PatientOrigin patientOrig =
          patientOriginMapper.findPatientOriginByParentId(patientOrigin.getParentId());
      if (patientOrig == null) {
        return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "未找到父级来源", "");
      }
      patientOrigin.setOriginType(patientOrig.getOriginType());
      patientOrigin.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientOrigin.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(patientOrigin);
    }
    return ResponseUtil.success();
  }

  /**
   * 查询患者来源树状结构列表
   * @return List<PatientOriginTreeVo>
   */
  public List<PatientOriginTreeVo> initPatientOriginTree() {
    List<PatientOriginInfoVo> vos = patientOriginMapper.findAll();
    return initTree(vos);
  }

  /**
   * 构建组织树列表
   *
   * @param vos 组织列表
   * @return List<PatientOriginTreeVo>
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
   * @param patientOriginForm 患者来源修改Form
   * @return ResponseResult
   */
  public ResponseResult update(PatientOriginForm patientOriginForm) {
    PatientOrigin patientOrigin = new PatientOrigin();
    BeanUtils.copyProperties(patientOriginForm, patientOrigin);
    PatientOrigin patientOriginv = mapper.selectByPrimaryKey(patientOrigin.getId());
    if(patientOriginv != null ){
      if (patientOriginv.getAllowOperate() == false) {
        return ResponseUtil.fail(
                OperationCodeConstants.OBJECT_EDIT_FAIL, "该患者来源不可编辑", patientOriginv);
      }
      if (patientOrigin.getTimeLimit() == 0) {
        patientOrigin.setLimitStartDate(null);
        patientOrigin.setLimitEndDate(null);
      }
      patientOrigin.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientOrigin.setUpdName(BaseContextHandler.getName());
      patientOrigin.setUpdTime(new Date());
      mapper.updateByPrimaryKeySelective(patientOrigin);
    }else{
      return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "未找到患者来源", patientOriginv);
    }
    return ResponseUtil.success();
  }

  /**
   * 删除患者来源
   * @param id 患者来源id
   * @return ResponseResult
   */
  public ResponseResult deleteOriginById(Integer id) {
    PatientOrigin patientOriginv = mapper.selectByPrimaryKey(id);
    if (patientOriginv.getAllowOperate() == false) {
      return ResponseUtil.fail(
          OperationCodeConstants.DELETE_NOT_ALLOW, "该患者来源不可删除", patientOriginv);
    }
    mapper.deleteByPrimaryKey(id);
    return ResponseUtil.success();
  }

  /**
   * 模糊查询员工/老患者信息
   * @param form 模糊查询员工和患者信息QueryForm
   * @return ResponseResult
   */
  public ResponseResult findPatientAndStaffListInfo(PatientAndStaffListInfoQueryForm form) {
    switch (form.getOriginType()) {
      case 1:
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setKeyWord(form.getName());
        model.setWhetherPage(false);
        return ResponseUtil.success(remoteSystemServiceFeign.findSysUserEmployeeInfoList(model));
      case 2:
        PatientLikeFinleQueryForm Patientmodel = new PatientLikeFinleQueryForm();
        Patientmodel.setCondition(form.getName());
        return ResponseUtil.success(patientBaseInfoMapper.findPatientByNameAndMobile(Patientmodel));
      default:
        break;
    }
    return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW, "PARAMETERS_IS_ILLEGAL", "");
  }

  /**
   * 获取符合条件的活动集合
   *
   * @param patientOrigin 患者来源
   * @return List<PatientOrigin>
   */
  public List<PatientOrigin> getPatientOriginList(PatientOrigin patientOrigin) {
    List<PatientOrigin> PatientOriginInfoList = mapper.findPatientOriginByTypt(patientOrigin);
    if (!StringHelper.isEmpty(PatientOriginInfoList)) {
      Iterator<PatientOrigin> PatientOriginIterator = PatientOriginInfoList.iterator();
      while (PatientOriginIterator.hasNext()) {
        PatientOrigin origin = PatientOriginIterator.next();
        if (origin.getTimeLimit() == 1) {
          if (DateUtil.isEffectiveDate(
                  new Date(), origin.getLimitStartDate(), origin.getLimitEndDate())
              == false) {
            PatientOriginIterator.remove(); // 使用迭代器的删除方法删除
          }
        }
      }
    }
    return PatientOriginInfoList;
  }

  /**
   * 查询患者来源类型
   *
   * @return List<PatientOriginVo>
   */
  public List<PatientOriginVo> originalType() {
    return patientOriginMapper.originalType();
  }

  /**
   * 根据患者类型查询来源
   *
   * @param form 查询患者来源类型
   * @return List<PatientOrigin>
   */
  public List<PatientOrigin> findPatientOriginByTypt(OriginTypeQueryForm form) {
    PatientOrigin patientOrigin = new PatientOrigin();
    patientOrigin.setOriginType(form.getOriginType()); // 根据来源类型
    return getPatientOriginList(patientOrigin); // 获取符合条件的活动集合
  }
}
