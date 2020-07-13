package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.modules.system.entity.Brand;
import com.yunya.modules.system.entity.ClinicExtInfo;
import com.yunya.modules.system.form.base.BaseForm;
import com.yunya.modules.system.form.query.BrandQueryForm;
import com.yunya.modules.system.mapper.BrandMapper;
import com.yunya.modules.system.mapper.ClinicExtInfoMapper;
import com.yunya.modules.system.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍:</br> 品牌业务层
 *
 * @author: chow
 * @date: 2020/5/28 14:24
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BrandBiz extends BaseBiz<BrandMapper, Brand> {

  /** 注入对象 */
  @Autowired private ClinicExtInfoMapper clinicExtInfoMapper;

  /**
   * 根据条件查询品牌列表（可分页）
   *
   * @param form 查询参数封装
   * @return list
   */
  public PageInfo<BrandVO> findAll(BrandQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BrandVO> resultList = mapper.selectBrandByExample(form);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增品牌
   *
   * @param resource 品牌参数封装
   * @return int
   */
  public void add(Brand resource) {
    String brandName = resource.getName();
    Brand brand = new Brand();
    brand.setName(brandName);
    Brand result = mapper.selectOne(brand);
    if (null != result) {
      throw new ClientServiceException(
          "新增品牌'" + brandName + "'失败，该品牌名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    // resource.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    // resource.setCrtName(BaseContextHandler.getUsername());
    mapper.insertSelective(resource);
  }

  /**
   * 编辑品牌信息
   *
   * @param brandId 品牌ID
   * @param form 封装参数
   * @return
   */
  public void modifyBrand(Integer brandId, BaseForm form) {
    Brand brand = mapper.selectByPrimaryKey(brandId);
    if (null == brand) {
      throw new ClientServiceException(
          "修改品牌，品牌ID为'" + brandId + "'的数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }

    // 名称有修改，校验名称是否重复
    if (!form.getName().equals(brand.getName())) {
      String brandName = form.getName();
      Brand entity = new Brand();
      entity.setName(brandName);
      Brand result = mapper.selectOne(entity);
      if (null != result) {
        throw new ClientServiceException(
            "修改品牌'" + brandName + "'失败，该品牌名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
      brand.setName(brandName);
    }
    brand.setOrderNum(form.getOrderNum());
    if (null != form.getInservice()) {
      brand.setInservice(form.getInservice());
    }
    // brand.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    // brand.setUpdName(BaseContextHandler.getUsername());
    // brand.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(brand);
  }

  /**
   * 根据品牌ID删除品牌
   *
   * @param brandId 品牌ID
   */
  public void deleteBrand(Integer brandId) {
    List<ClinicExtInfo> clinicInfos = clinicExtInfoMapper.selectClinicExtInfoByBrandId(brandId);
    if (clinicInfos.size() > 0) {
      throw new ClientServiceException(
          "删除ID为'" + brandId + "'的品牌失败，该品牌已被使用", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(brandId);
  }
}
