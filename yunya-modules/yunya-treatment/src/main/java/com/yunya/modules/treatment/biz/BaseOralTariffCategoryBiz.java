package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.form.BaseOralTariffCategoryForm;
import com.yunya.feign.treatment.domain.model.BaseOralTariffCategoryModel;
import com.yunya.feign.treatment.domain.query.BaseOralTariffCategoryQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffCategoryVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya.modules.treatment.mapper.BaseOralTariffCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 描述: 商品分类业务层
 *
 * @author GaoLuding
 * @create 2020-05-20 11:34
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseOralTariffCategoryBiz
    extends BaseBiz<BaseOralTariffCategoryMapper, BaseOralTariffCategory> {

  /** 注入对象 */
  private final BaseOralTariffBiz baseOralTariffBiz;

  public BaseOralTariffCategoryBiz(BaseOralTariffBiz baseOralTariffBiz) {
    this.baseOralTariffBiz = baseOralTariffBiz;
  }

  /**
   * 根据ID查询价目表分类信息
   *
   * @param id 价目表分类ID
   * @return
   */
  public BaseOralTariffCategoryVO findById(Integer id) {
    BaseOralTariffCategoryVO resultData = mapper.selectBaseOralTariffCategoryById(id);
    return resultData;
  }

  /**
   * 根据条件查询价目表分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<BaseOralTariffCategoryVO> findList(BaseOralTariffCategoryQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseOralTariffCategoryVO> resultList = mapper.selectBaseOralTariffCategoryList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增商品分类
   *
   * @param model 新增参数
   */
  public void add(BaseOralTariffCategoryModel model) {
    String name = model.getName();
    BaseOralTariffCategory entity = new BaseOralTariffCategory();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，名称为'" + name + "'的商品分类已存在", NAME_IS_OCCUPIED);
    }
    String number = model.getNumber();
    entity = new BaseOralTariffCategory();
    entity.setNumber(number);
    count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，编号为'" + number + "'的商品分类已存在", NAME_IS_OCCUPIED);
    }
    entity.setName(name);
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 修改商品分类
   *
   * @param id 商品分类ID
   * @param form 修改参数
   */
  public void modify(Integer id, BaseOralTariffCategoryForm form) {
    BaseOralTariffCategory resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException("修改失败，ID为'" + id + "'的数据不存在", QUERY_RESULT_INVALID);
    }

    String resultDataName = resultData.getName();
    String resultDataNumber = resultData.getNumber();
    String name = form.getName();

    if (!resultDataName.equals(name)) {
      resultData = new BaseOralTariffCategory();
      resultData.setName(name);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException("修改失败，名称为'" + name + "'的价目表分类已存在！", NAME_IS_OCCUPIED);
      }
    }

    String number = form.getNumber();
    if (!resultDataNumber.equals(number)) {
      resultData = new BaseOralTariffCategory();
      resultData.setNumber(number);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，编号'" + number + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }
    resultData.setName(name);
    resultData.setNumber(number);
    Boolean inservice = form.getInservice();
    resultData.setInservice(inservice);
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据ID删除商品分类
   *
   * @param id 商品分类ID
   */
  public void deleteBaseOralTariffCategoryById(Integer id) {
    BaseOralTariff entity = new BaseOralTariff();
    entity.setOralTariffCategoryId(id);
    Long count = baseOralTariffBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("删除失败，ID为'" + id + "'的商品分类已被管理！", DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
