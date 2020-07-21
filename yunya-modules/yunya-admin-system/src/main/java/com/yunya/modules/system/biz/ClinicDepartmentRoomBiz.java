package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.modules.system.form.ClinicDepartmentRoomModel;
import com.yunya.modules.system.form.query.ClinicDepartmentRoomQueryForm;
import com.yunya.modules.system.mapper.ClinicDepartmentRoomMapper;
import com.yunya.modules.system.vo.ClinicDepartmentRoomVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简介: 门诊科室控制器
 *
 * @author: chow
 * @date: 2020/7/20 20:30
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicDepartmentRoomBiz
    extends BaseBiz<ClinicDepartmentRoomMapper, ClinicDepartmentRoom> {

  /**
   * 设置科室在门诊不可用或不可用（注:启用表示科室在门诊科室列表中不存在，默认查询科室模版）
   *
   * @param model 参数模型
   */
  public void switchDeptRoomDisable(ClinicDepartmentRoomModel model) {
    ClinicDepartmentRoom entity = new ClinicDepartmentRoom();
    entity.setCompanyId(model.getOrgId());
    entity.setDeptRoomId(model.getDepartmentRoomId());
    if (model.getInservice()) {
      mapper.delete(entity);
    } else {
      entity.setInservice(false);
      mapper.insertSelective(entity);
    }
  }

  /**
   * 新增门诊科室
   *
   * @param model 参数模型
   */
  public void add(ClinicDepartmentRoomModel model) {
    ClinicDepartmentRoom entity = new ClinicDepartmentRoom();
    entity.setCompanyId(model.getOrgId());
    entity.setDeptRoomId(model.getDepartmentRoomId());
    ClinicDepartmentRoom resultData = mapper.selectOne(entity);
    if (null == resultData) {
      mapper.insertSelective(entity);
    }
  }

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询条件
   * @return list
   */
  public PageInfo<ClinicDepartmentRoomVO> findList(ClinicDepartmentRoomQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }

    List<ClinicDepartmentRoomVO> resultList = mapper.selectClinicDepartmentRoomList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据门诊科室ID查询门诊科室
   *
   * @param id 门诊科室ID
   * @return obj
   */
  public ClinicDepartmentRoomVO findByClinicDeptRoomId(Integer id) {
    return mapper.selectByClinicDeptRoomId(id);
  }

  /**
   * 根据门诊科室ID删除门诊科室
   *
   * @param id 门诊科室ID
   */
  public void deleteByClinicDeptRoomId(Integer id) {
    // todo 增加删除判断逻辑，通过feign查询该门诊科室是否被使用
    mapper.deleteByPrimaryKey(id);
  }
}
