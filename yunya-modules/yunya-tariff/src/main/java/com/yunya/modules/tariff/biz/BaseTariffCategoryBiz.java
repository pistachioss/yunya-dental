package com.yunya.modules.tariff.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.BaseTariffCategoryForm;
import com.yunya.feign.treatment.domain.model.BaseTariffCategoryModel;
import com.yunya.feign.treatment.domain.query.BaseTariffCategoryQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffCategoryVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.BaseTariffCategory;
import com.yunya.modules.tariff.mapper.BaseTariffCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;

/**
 * 描述: 价目表分类业务层
 *
 * @author GaoLuding
 * @create 2020-05-19 9:18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffCategoryBiz extends BaseBiz<BaseTariffCategoryMapper, BaseTariffCategory> {

  /** 注入对象 */
  private final BaseTariffBiz baseTariffBiz;

  public BaseTariffCategoryBiz(BaseTariffBiz baseTariffBiz) {
    this.baseTariffBiz = baseTariffBiz;
  }

  /**
   * 根据ID查询价目表分类信息
   *
   * @param id 价目表分类ID
   * @return
   */
  public BaseTariffCategoryVO findById(Integer id) {
    BaseTariffCategoryVO resultData = mapper.selectBaseTariffCategoryById(id);
    return resultData;
  }

  /**
   * 根据条件查询价目表分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<BaseTariffCategoryVO> findList(BaseTariffCategoryQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseTariffCategoryVO> resultList = mapper.selectBaseTariffCategoryList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增价目表目录
   *
   * @param model 新增参数
   */
  public void add(BaseTariffCategoryModel model) {
    String name = model.getName();
    BaseTariffCategory entity = new BaseTariffCategory();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，名称为'" + name + "'的价目表分类已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    String number = model.getNumber();
    entity = new BaseTariffCategory();
    entity.setNumber(number);
    count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，编号为'" + number + "'的价目表分类已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    entity.setName(name);
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 修改价目表目录
   *
   * @param id 价目表目录ID
   * @param form 修改参数
   */
  public void modify(Integer id, BaseTariffCategoryForm form) {
    BaseTariffCategory resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "'的数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }

    String resultName = resultData.getName();
    String resultNumber = resultData.getNumber();

    String name = form.getName();
    if (!resultName.equals(form.getName())) {
      resultData = new BaseTariffCategory();
      resultData.setName(name);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException("修改失败，名称为'" + name + "'的价目表分类已存在！", NAME_IS_OCCUPIED);
      }
    }

    String number = form.getNumber();
    if (!resultNumber.equals(form.getNumber())) {
      resultData = new BaseTariffCategory();
      resultData.setNumber(number);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，编号'" + number + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }

    resultData.setName(name);
    resultData.setNumber(number);
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据ID删除价目表分类
   *
   * @param id 价目表分类ID
   */
  public void deleteBaseTariffCategoryById(Integer id) {
    BaseTariff entity = new BaseTariff();
    entity.setTariffCategoryId(id);
    Long count = baseTariffBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "删除失败，ID为'" + id + "'的价目表分类已被管理！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
