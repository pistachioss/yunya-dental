package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.MemberType;
import com.yunya.modules.system.domain.form.MemberTypeForm;
import com.yunya.modules.system.domain.model.MemberTypeModel;
import com.yunya.modules.system.domain.query.MemberTypeQueryForm;
import com.yunya.modules.system.mapper.MemberTypeMapper;
import com.yunya.modules.system.vo.MemberTypeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;
import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;

/**
 * 简介: 会员卡类型业务层
 *
 * @author: chow
 * @date: 2020/7/22 15:44
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberTypeBiz extends BaseBiz<MemberTypeMapper, MemberType> {

  /** 患者服务 */
  @Autowired
  private RemotePatientCentralServiceFeign patientCentralServiceFeign;

  /**
   * 根据ID查询会员卡类型信息
   *
   * @param id 会员卡ID
   * @return
   */
  public MemberTypeVO findById(Integer id) {
    MemberTypeVO resultData = mapper.selectMemberTypeById(id);
    return resultData;
  }

  /**
   * 根据条件查询会员卡类型列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<MemberTypeVO> findList(MemberTypeQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<MemberTypeVO> resultList = mapper.selectMemberTypeList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增会员卡类型
   *
   * @param model 参数模型
   */
  public void add(MemberTypeModel model) {
    String name = model.getName();
    MemberType entity = new MemberType();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增会员卡失败，名称为'" + name + "'的会员卡已存在！", NAME_IS_OCCUPIED);
    }
    BeanUtils.copyProperties(model, entity);
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 修改会员卡信息
   *
   * @param id 会员卡类型ID
   * @param form 参数封装
   */
  public void modify(Integer id, MemberTypeForm form) {
    MemberType resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException("修改失败，ID为'" + id + "'的数据不存在！", QUERY_RESULT_INVALID);
    }
    if (!resultData.getName().equals(form.getName())) {
      String name = form.getName();
      resultData = new MemberType();
      resultData.setName(name);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException("修改会员类型失败，名称为'" + name + "'的会员卡已存在", NAME_IS_OCCUPIED);
      }
    }
    BeanUtils.copyProperties(form, resultData);
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据ID删除会员卡类型
   *
   * @param id 会员卡类型ID
   */
  public void deleteMemberTypeById(Integer id) {
    boolean b = patientCentralServiceFeign.memberInfoCount(id);
    if (b) {
      throw new ClientServiceException("该类型会员卡正在使用中,不允许删除", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
