package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.system.SysDistricts;
import com.yunya.modules.system.domain.query.SysDistrictsQueryForm;
import com.yunya.modules.system.mapper.SysDistrictsMapper;
import com.yunya.modules.system.vo.SysDistrictsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍:</br> 地区统一管理业务层
 *
 * @author: chow
 * @date: 2020/6/13 16:29
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysDistrictsBiz extends BaseBiz<SysDistrictsMapper, SysDistricts> {

  /**
   * 根据条件查询地区列表（可分页）
   *
   * @param queryForm 参数封装
   * @return list
   */
  public PageInfo<SysDistrictsVO> findDistrictsList(SysDistrictsQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<SysDistrictsVO> resultList = mapper.selectDistrictsList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增地区
   *
   * @param districts 参数封装
   */
  public void add(SysDistricts districts) {
    districts.setParentId(districts.getId() / 100);
    mapper.insertSelective(districts);
  }

  /**
   * 修改保存地区
   *
   * @param districts 参数封装
   */
  public void edit(SysDistricts districts) {
    districts.setParentId(districts.getId() / 100);
    mapper.updateByPrimaryKeySelective(districts);
  }

  /**
   * 删除地区
   *
   * @param ids 地区编号
   */
  public void remove(String[] ids) {
    mapper.deleteDistrictsByIds(ids);
  }
}
