//package com.yunya.modules.discount.biz;
//
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.yunya.feign.discount.domain.form.ProductTypeForm;
//import com.yunya.feign.discount.domain.model.ProductTypeModel;
//import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
//import com.yunya.feign.discount.domain.vo.ProductTypeVO;
//import com.yunya.framework.common.constant.OperationCodeConstants;
//import com.yunya.framework.common.context.BaseContextHandler;
//import com.yunya.framework.common.biz.BaseBiz;
//import com.yunya.framework.common.exception.ClientServiceException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//
//import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;
//
///**
// * 描述: 产品分类业务层
// *
// * @author GaoLuding
// * @create 2020-07-09 15:40
// */
//@Service
//@Transactional(rollbackFor = Exception.class)
//public class ProductTypeBiz extends BaseBiz<ProductTypeMapper, ProductType> {
//
//  /** 注入对象 */
//  private final CouponCommonInfoBiz couponCommonInfoBiz;
//
//  public ProductTypeBiz(CouponCommonInfoBiz couponCommonInfoBiz) {
//    this.couponCommonInfoBiz = couponCommonInfoBiz;
//  }
//
//  /**
//   * 根据ID查询产品分类信息
//   *
//   * @param id 产品分类ID
//   * @return
//   */
//  public ProductTypeVO findById(Integer id) {
//    ProductTypeVO resultData = mapper.selectProductTypeById(id);
//    return resultData;
//  }
//
//  /**
//   * 根据条件查询产品分类列表
//   *
//   * @param queryForm 查询条件
//   * @return
//   */
//  public PageInfo<ProductTypeVO> findList(ProductTypeQueryForm queryForm) {
//    if (queryForm.getWhetherPage()) {
//      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
//    }
//    List<ProductTypeVO> resultList = mapper.selectProductTypeList(queryForm);
//    return new PageInfo<>(resultList);
//  }
//
//  /**
//   * 新增产品分类
//   *
//   * @param productType 参数封装
//   */
//  public void saveMarketProductType(ProductTypeModel productType) {
//    String name = productType.getName();
//    ProductType entity = new ProductType();
//    entity.setName(name);
//    int count = mapper.selectCount(entity);
//    if (count > 0) {
//      throw new ClientServiceException("新增失败，名称为'" + name + "'的产品类型已存在！", NAME_IS_OCCUPIED);
//    }
//    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
//    entity.setCrtName(BaseContextHandler.getName());
//    mapper.insertSelective(entity);
//  }
//
//  /**
//   * 修改产品分类
//   *
//   * @param id 产品分类ID
//   * @param form 修改参数
//   */
//  public void modify(Integer id, ProductTypeForm form) {
//    ProductType resultData = mapper.selectByPrimaryKey(id);
//    if (null == resultData) {
//      throw new ClientServiceException(
//          "修改失败，ID为'" + id + "'的产品分类不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
//    }
//    String name = form.getName();
//    resultData = new ProductType();
//    resultData.setName(name);
//    if (!resultData.getName().equals(name)) {
//      int count = mapper.selectCount(resultData);
//      if (count > 0) {
//        throw new ClientServiceException(
//            "修改失败，已存在名称为'" + name + "'的产品分类", OperationCodeConstants.SAME_DATA_EXIST);
//      }
//    }
//    resultData.setInservice(form.getInservice());
//    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
//    resultData.setUpdName(BaseContextHandler.getName());
//    resultData.setUpdTime(new Date(System.currentTimeMillis()));
//    resultData.setId(id);
//    mapper.updateByPrimaryKeySelective(resultData);
//  }
//
//  /**
//   * 根据ID删除产品分类
//   *
//   * @param id 产品分类ID
//   */
//  public void deleteProductTypeById(Integer id) {
//    CouponCommonInfo entity = new CouponCommonInfo();
//    entity.setProductTypeId(id);
//    Long count = couponCommonInfoBiz.selectCount(entity);
//    if (count > 0) {
//      throw new ClientServiceException(
//          "删除失败，ID为'" + id + "'的产品分类已被关联，不允许删除！", OperationCodeConstants.DELETE_NOT_ALLOW);
//    }
//    mapper.deleteByPrimaryKey(id);
//  }
//}
