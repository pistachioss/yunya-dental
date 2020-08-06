package com.yunya.modules.tariff.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.tariff.domain.form.BaseOralTariffForm;
import com.yunya.feign.tariff.domain.form.ClinicItemPriceForm;
import com.yunya.feign.tariff.domain.model.BaseOralTariffImportModel;
import com.yunya.feign.tariff.domain.model.BaseOralTariffModel;
import com.yunya.feign.tariff.domain.model.ClinicItemPriceModel;
import com.yunya.feign.tariff.domain.query.BaseOralTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffInfoVO;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffVO;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffExportVO;
import com.yunya.feign.tariff.domain.vo.ClinicItemPriceVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.tariff.*;
import com.yunya.modules.tariff.mapper.BaseOralTariffMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 描述: 商品项目业务层
 *
 * @author GaoLuding
 * @create 2020-05-20 11:00
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseOralTariffBiz extends BaseBiz<BaseOralTariffMapper, BaseOralTariff> {

  /** 系统服务远程调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 商品分类 */
  @Autowired private BaseOralTariffCategoryBiz baseOralTariffCategoryBiz;
  /** 商品项目操作记录 */
  @Autowired private BaseOralTariffHistoryBiz baseOralTariffHistoryBiz;
  /** 门诊商品项目会员价 */
  @Autowired private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;
  /** 门诊商品项目 */
  @Autowired private ClinicOralTariffBiz clinicOralTariffBiz;

  /**
   * 根据ID查询商品项目信息（包含门诊商品项目价格信息）
   *
   * @param id 商品项目ID
   * @return
   */
  public BaseOralTariffInfoVO findBaseOralTariffInfoById(Integer id) {
    BaseOralTariffInfoVO resultData = mapper.selectBaseOralTariffInfoById(id);
    return resultData;
  }

  /**
   * 根据条件查询商品项目列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public List<BaseOralTariffVO> findList(BaseOralTariffQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseOralTariffVO> resultList = mapper.selectBaseOralTariffList(queryForm);
    return resultList;
  }

  /**
   * 新增商品项目
   *
   * @param model 新增参数
   */
  public void add(BaseOralTariffModel model) {
    Integer categoryId = model.getOralTariffCategoryId();
    BaseOralTariffCategory oralTariffCategory = baseOralTariffCategoryBiz.selectById(categoryId);
    if (null == oralTariffCategory) {
      throw new ClientServiceException(
          "新增失败，ID为'" + categoryId + "'的商品分类不存在，请选择正确的商品分类！",
          OperationCodeConstants.QUERY_RESULT_INVALID);
    }

    BaseOralTariff entity = new BaseOralTariff();
    String name = model.getName();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，商品项目名称'" + name + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
    }

    entity = new BaseOralTariff();
    String number = model.getItemNumber();
    entity.setItemNumber(number);
    count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，商品项目编号'" + number + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
    }

    String categoryNumber = oralTariffCategory.getNumber().substring(0, 2);
    String itemNumber = number.substring(0, 2);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException(
          "新增失败，商品项目编号前3位与商品分类编号前3位不同！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    BeanUtils.copyProperties(model, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);

    // 添加门诊商品项目
    Integer id = entity.getId();
    List<ClinicItemPriceModel> clinicItemPriceModels = model.getClinicItemPriceModels();
    if (clinicItemPriceModels.size() > 0) {
      ClinicOralTariff clinicOralTariff;
      for (ClinicItemPriceModel itemPriceModel : clinicItemPriceModels) {
        clinicOralTariff = new ClinicOralTariff();
        clinicOralTariff.setClinicId(itemPriceModel.getOrgId());
        clinicOralTariff.setOralTariffId(id);
        clinicOralTariff.setPrice(itemPriceModel.getItemPrice());
        clinicOralTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        clinicOralTariff.setCrtName(BaseContextHandler.getName());
        clinicOralTariffBiz.insertSelective(clinicOralTariff);
      }
    }

    // 保存商品项目新增历史记录
    BaseOralTariffHistory baseOralTariffHistory = new BaseOralTariffHistory();
    baseOralTariffHistory.setOralTariffCategoryId(categoryId);
    baseOralTariffHistory.setOralTariffId(id);
    baseOralTariffHistory.setName(name);
    baseOralTariffHistory.setItemNumber(number);
    baseOralTariffHistory.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    baseOralTariffHistory.setCrtName(BaseContextHandler.getName());
    baseOralTariffHistoryBiz.insertSelective(baseOralTariffHistory);
  }

  /**
   * 修改商品项目
   *
   * @param id 商品项目ID
   * @param form 修改参数
   */
  public void modify(Integer id, BaseOralTariffForm form) {
    BaseOralTariffInfoVO resultData = mapper.selectBaseOralTariffInfoById(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "的商品项目不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }

    BaseOralTariff entity = new BaseOralTariff();
    String name = form.getName();
    if (!resultData.getName().equals(name)) {
      entity = new BaseOralTariff();
      entity.setName(name);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，名称为'" + name + "'的商品项目已存在！", OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
    }

    String number = form.getItemNumber();
    if (!resultData.getItemNumber().equals(number)) {
      entity = new BaseOralTariff();
      entity.setItemNumber(number);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，商品项目编号'" + number + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }

    String categoryNumber = resultData.getOralTariffCategoryNumber().substring(0, 2);
    String itemNumber = number.substring(0, 2);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException(
          "修改失败，商品项目编号前3位与商品分类编号前3位不同！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    BeanUtils.copyProperties(form, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setUpdName(BaseContextHandler.getName());
    entity.setUpdTime(new Date(System.currentTimeMillis()));
    entity.setId(id);
    mapper.updateByPrimaryKeySelective(entity);

    // 更新门诊商品项目价格
    List<ClinicItemPriceVO> clinicItemInfos = resultData.getClinicItemInfos();
    List<ClinicItemPriceForm> clinicItemPriceForms = form.getClinicItemPriceForms();
    if (clinicItemInfos.size() > 0) {
      ClinicOralTariff clinicEntity;
      for (ClinicItemPriceVO itemInfo : clinicItemInfos) {
        for (ClinicItemPriceForm itemPriceForm : clinicItemPriceForms) {
          if (itemInfo.getClinicItemId().equals(itemPriceForm.getClinicItemId())
              && !itemInfo.getItemInservice().equals(itemPriceForm.getItemInservice())) {
            clinicEntity = new ClinicOralTariff();
            clinicEntity.setId(itemInfo.getClinicItemId());
            clinicEntity.setPrice(itemPriceForm.getItemPrice());
            clinicEntity.setInservice(itemPriceForm.getItemInservice());
            clinicEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicEntity.setUpdName(BaseContextHandler.getName());
            clinicEntity.setUpdTime(new Date(System.currentTimeMillis()));
            clinicOralTariffBiz.updateSelectiveById(clinicEntity);
          }
        }
      }
    }

    // 保存商品项目变更记录
    if (!resultData.getName().equals(name) || !resultData.getItemNumber().equals(number)) {
      BaseOralTariffHistory baseOralTariffHistory = new BaseOralTariffHistory();
      baseOralTariffHistory.setName(name);
      baseOralTariffHistory.setItemNumber(number);
      baseOralTariffHistory.setOralTariffCategoryId(form.getOralTariffCategoryId());
      baseOralTariffHistory.setOralTariffId(id);
      baseOralTariffHistory.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      baseOralTariffHistory.setCrtName(BaseContextHandler.getName());
      baseOralTariffHistoryBiz.insertSelective(baseOralTariffHistory);
    }
  }

  /**
   * 根据ID删除商品项目
   *
   * @param oralTariffId 商品项目ID
   */
  public void delete(Integer oralTariffId) {
    ClinicOralTariffMemberPrice entity = new ClinicOralTariffMemberPrice();
    entity.setOralTariffId(oralTariffId);
    Long count = clinicOralTariffMemberPriceBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "商品失败，ID为" + oralTariffId + "'的商品项目已被关联！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(oralTariffId);
    BaseOralTariffHistory historyEntity = new BaseOralTariffHistory();
    historyEntity.setOralTariffId(oralTariffId);
    baseOralTariffHistoryBiz.delete(historyEntity);
  }

  /**
   * 导入商品项目列表
   *
   * @param excelFile 导入文件
   * @return
   */
  public String importExcel(MultipartFile excelFile) throws Exception {
    ExcelUtil<BaseOralTariffImportModel> excelUtil =
        new ExcelUtil<>(BaseOralTariffImportModel.class);
    List<BaseOralTariffImportModel> models = excelUtil.importExcel(excelFile.getInputStream());
    if (null == models || models.size() == 0) {
      throw new ClientServiceException(
          "导入失败,导入的商品项目数据不能为空！", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }

    // 初始化参数、常量
    int dataNum = 0;
    StringBuilder successMsg = new StringBuilder();
    StringBuilder failureMsg = new StringBuilder();
    String itemName;
    String itemNumber;
    String englishName;
    String categoryNumber;
    String categoryName;
    String unit;
    BigDecimal price;
    OrganizationModel orgModel = new OrganizationModel();
    // 获取全部门诊信息
    orgModel.setTypes(new Byte[] {2});
    orgModel.setWhetherPage(false);
    // todo 同步门诊商品项目
    List<OrganizationInfoDetail> infoList = systemServiceFeign.findOrgInfoList(orgModel);

    for (BaseOralTariffImportModel model : models) {
      dataNum++;
      itemName = model.getName();
      itemNumber = model.getItemNumber();
      englishName = model.getEnglishName();
      categoryNumber = model.getOralTariffCategoryNumber();
      categoryName = model.getOralTariffCategoryName();
      unit = model.getUnit();
      price = model.getPrice();

      // 校验商品分类参数合法性
      checkCategoryParams(dataNum, failureMsg, itemNumber, categoryName, categoryNumber);

      // 获取商品分类ID
      Integer categoryId = getCategoryId(categoryName, categoryNumber);

      // 校验商品项目参数合法性
      checkItemParams(dataNum, failureMsg, itemName, itemNumber, categoryId);

      // 新增或更新商品项目信息
      addOrUpdItem(itemName, itemNumber, englishName, unit, price, categoryId);
    }
    return successMsg.append("导入成功，共计").append(dataNum).append("'条数据！").toString();
  }

  /**
   * 新增或更新商品项目信息
   *
   * @param itemName 商品项目名称
   * @param itemNumber 商品项目编号
   * @param englishName 商品项目英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 商品分类ID
   */
  private void addOrUpdItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId) {
    BaseOralTariff itemEntity;
    BaseOralTariff itemResult;
    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    itemResult = mapper.selectOne(itemEntity);

    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
    itemEntity.setItemNumber(itemNumber);
    itemEntity.setEnglishName(englishName);
    itemEntity.setPrice(price);
    itemEntity.setUnit(unit);

    if (null == itemResult) {
      // 新增商品项目
      itemEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      itemEntity.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(itemEntity);
    } else {
      // 更新商品项目
      itemEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      itemEntity.setUpdName(BaseContextHandler.getName());
      itemEntity.setUpdTime(new Date(System.currentTimeMillis()));
      mapper.updateByPrimaryKeySelective(itemEntity);
    }
  }

  /**
   * 校验商品项目参数合法性
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemName 商品项目名称
   * @param itemNumber 商品项目编号
   * @param categoryId 商品分类ID
   */
  private void checkItemParams(
      int dataNum,
      StringBuilder failureMsg,
      String itemName,
      String itemNumber,
      Integer categoryId) {
    BaseOralTariff itemEntity;
    BaseOralTariff itemResult;
    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemResult = mapper.selectOne(itemEntity);
    if (itemResult != null) {
      String resultItemNumber = itemResult.getItemNumber();
      if (!resultItemNumber.equals(itemNumber)) {
        failureMsg
            .append("导入失败，Excel表中第")
            .append(dataNum)
            .append("'条数据的商品项目编号'")
            .append(itemNumber)
            .append("'与系统中该商品项目编号'")
            .append(resultItemNumber)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }

    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setItemNumber(itemNumber);
    itemResult = mapper.selectOne(itemEntity);
    if (itemResult != null) {
      String itemResultName = itemResult.getName();
      if (!itemResultName.equals(itemName)) {
        failureMsg
            .append("导入失败，Excel表中第")
            .append(dataNum)
            .append("'条数据的商品项目名称'")
            .append(itemName)
            .append("'与系统中该商品项目名称'")
            .append(itemResultName)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
      }
    }
  }

  /**
   * 获取商品分类ID
   *
   * @param categoryName 商品分类名称
   * @param categoryNumber 商品分类编号
   * @return
   */
  private Integer getCategoryId(String categoryName, String categoryNumber) {
    BaseOralTariffCategory categoryEntity;
    BaseOralTariffCategory categoryResult;
    categoryEntity = new BaseOralTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    categoryResult = baseOralTariffCategoryBiz.selectOne(categoryEntity);
    Integer categoryId;
    if (null == categoryResult) {
      // 新增商品分类
      categoryEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      categoryEntity.setCrtName(BaseContextHandler.getName());
      baseOralTariffCategoryBiz.insertSelective(categoryEntity);
      categoryId = categoryEntity.getId();
    } else {
      categoryId = categoryResult.getId();
    }
    return categoryId;
  }

  /**
   * 校验商品项目分类参数
   *
   * @param dataNum 当前行
   * @param failureMsg 提示信息
   * @param itemNumber 商品项目编号
   * @param categoryName 商品分类名称
   * @param categoryNumber 商品分类编号
   */
  private void checkCategoryParams(
      int dataNum,
      StringBuilder failureMsg,
      String itemNumber,
      String categoryName,
      String categoryNumber) {
    String itemStr = itemNumber.substring(0, 2);
    String categoryStr = categoryNumber.substring(0, 2);
    if (!itemStr.equals(categoryStr)) {
      failureMsg
          .append("导入失败，Excel表中第")
          .append(dataNum)
          .append("'条数据的商品项目编号前3位'")
          .append(itemStr)
          .append("'与商品分类编号前3位'")
          .append(categoryStr)
          .append("'不同！");
      throw new ClientServiceException(
          failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    BaseOralTariffCategory categoryEntity;
    BaseOralTariffCategory categoryResult;
    categoryEntity = new BaseOralTariffCategory();
    categoryEntity.setNumber(categoryNumber);
    categoryResult = baseOralTariffCategoryBiz.selectOne(categoryEntity);
    if (categoryResult != null) {
      String categoryResultName = categoryResult.getName();
      if (!categoryName.equals(categoryResultName)) {
        failureMsg
            .append("导入失败，Excel表中第'")
            .append(dataNum)
            .append("'条数据的商品分类名称'")
            .append(categoryName)
            .append("'与系统中该商品分类名称'")
            .append(categoryResultName)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }

    categoryEntity = new BaseOralTariffCategory();
    categoryEntity.setName(categoryName);
    categoryResult = baseOralTariffCategoryBiz.selectOne(categoryEntity);
    if (categoryResult != null) {
      String categoryResultNumber = categoryResult.getNumber();
      if (!categoryNumber.equals(categoryResultNumber)) {
        failureMsg
            .append("导入失败，Excel表中第'")
            .append(dataNum)
            .append("'条数据的商品分类编号'")
            .append(categoryNumber)
            .append("'与系统中该商品分类编号'")
            .append(categoryResultNumber)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }
  }

  /**
   * 导出商品项目列表
   *
   * @param response http响应
   * @param queryForm 查询参数
   */
  public void exportExcel(HttpServletResponse response, BaseOralTariffQueryForm queryForm)
      throws IOException {
    List<BaseOralTariffExportVO> resultList = mapper.selectExportBaseTariffList(queryForm);
    ExcelUtil<BaseOralTariffExportVO> excelUtil = new ExcelUtil<>(BaseOralTariffExportVO.class);
    excelUtil.exportExcel(response, resultList, "基础商品项目信息表");
  }
}
