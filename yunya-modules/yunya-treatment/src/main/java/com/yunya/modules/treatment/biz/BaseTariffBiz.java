package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.domain.form.BaseTariffAssociationForm;
import com.yunya.feign.treatment.domain.form.BaseTariffForm;
import com.yunya.feign.treatment.domain.form.ClinicItemPriceForm;
import com.yunya.feign.treatment.domain.model.BaseTariffAssociationImportModel;
import com.yunya.feign.treatment.domain.model.BaseTariffImportModel;
import com.yunya.feign.treatment.domain.model.BaseTariffModel;
import com.yunya.feign.treatment.domain.model.ClinicItemPriceModel;
import com.yunya.feign.treatment.domain.query.BaseTariffAssociationQueryForm;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.BaseTariffCategory;
import com.yunya.models.tariff.BaseTariffHistory;
import com.yunya.models.tariff.ClinicTariff;
import com.yunya.modules.treatment.mapper.BaseTariffCategoryMapper;
import com.yunya.modules.treatment.mapper.BaseTariffMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述: 基础价目表业务层
 *
 * @author GaoLuding
 * @create 2020-05-18 16:28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffBiz extends BaseBiz<BaseTariffMapper, BaseTariff> {

  /** 系统服务远程调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 价目表分类 */
  @Autowired private BaseTariffCategoryMapper baseTariffCategoryMapper;
  /** 价目表操作记录 */
  @Autowired private BaseTariffHistoryBiz baseTariffHistoryBiz;
  /** 门诊价目表 */
  @Autowired private ClinicTariffBiz clinicTariffBiz;

  /**
   * 根据ID查询价目表信息（包含门诊价目表价格信息）
   *
   * @param id 价目表ID
   * @return
   */
  public BaseTariffInfoVO findBaseTariffInfoById(Integer id) {
    BaseTariffInfoVO resultData = mapper.selectBaseTariffInfoById(id);
    if (null != resultData) {
      OrganizationModel orgModel = new OrganizationModel();
      orgModel.setTypes(new Byte[] {2});
      orgModel.setWhetherPage(false);
      List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoList(orgModel);
      List<ClinicItemPriceVO> itemInfos = new ArrayList<>();
      if (StringHelper.isNotEmpty(orgInfos)) {
        Integer resultDataId = resultData.getId();
        BigDecimal resultDataPrice = resultData.getPrice();
        ClinicTariff entity = new ClinicTariff();
        entity.setTariffId(resultDataId);
        orgInfos.forEach(
            orgInfo -> {
              entity.setClinicId(orgInfo.getId());
              ClinicTariff resultClinicTariff = clinicTariffBiz.selectOne(entity);
              ClinicItemPriceVO itemPriceVO = new ClinicItemPriceVO();
              if (null != resultClinicTariff) {
                itemPriceVO.setClinicItemId(resultClinicTariff.getId());
                itemPriceVO.setClinicItemPrice(resultClinicTariff.getPrice());
                itemPriceVO.setItemInservice(resultClinicTariff.getInservice());
              } else {
                itemPriceVO.setClinicItemPrice(resultDataPrice);
                itemPriceVO.setItemInservice(true);
              }
              itemPriceVO.setOrgId(orgInfo.getId());
              itemPriceVO.setOrgName(orgInfo.getAbbreviation());
              itemPriceVO.setItemId(resultDataId);
              itemInfos.add(itemPriceVO);
            });
      }
      resultData.setClinicItemInfos(itemInfos);
    }
    return resultData;
  }

  /**
   * 根据条件查询价目表列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<BaseTariffVO> findList(BaseTariffQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseTariffVO> resultList = mapper.selectBaseTariffList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增价目表
   *
   * @param model 新增参数
   */
  public void add(BaseTariffModel model) {
    Integer categoryId = model.getTariffCategoryId();
    BaseTariffCategory tariffCategory = baseTariffCategoryMapper.selectByPrimaryKey(categoryId);
    if (null == tariffCategory) {
      throw new ClientServiceException(
          "新增失败，ID为'" + categoryId + "'的价目表分类不存在，请选择正确的价目表分类！",
          OperationCodeConstants.QUERY_RESULT_INVALID);
    }

    BaseTariff entity = new BaseTariff();
    String name = model.getName();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，价目表名称'" + name + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
    }

    entity = new BaseTariff();
    String number = model.getItemNumber();
    entity.setItemNumber(number);
    count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增失败，价目表编号'" + number + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
    }

    String categoryNumber = tariffCategory.getNumber().substring(0, 3);
    String itemNumber = number.substring(0, 3);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException(
          "新增失败，价目表编号前3位与价目表分类编号前3位不同！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    BeanUtils.copyProperties(model, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);

    // 添加门诊价目表
    Integer itemId = entity.getId();
    List<ClinicItemPriceModel> clinicItemPriceModels = model.getClinicItemPriceModels();

    // 保存门诊价目表
    saveClinicTariff(model.getPrice(), itemId, clinicItemPriceModels);

    // 保存价目表新增历史记录
    BaseTariffHistory baseTariffHistory = new BaseTariffHistory();
    baseTariffHistory.setTariffCategoryId(categoryId);
    baseTariffHistory.setTariffId(itemId);
    baseTariffHistory.setName(name);
    baseTariffHistory.setItemNumber(number);
    baseTariffHistory.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    baseTariffHistory.setCrtName(BaseContextHandler.getName());
    baseTariffHistoryBiz.insertSelective(baseTariffHistory);
  }

  /**
   * 保存门诊商品项目
   *
   * @param price 原价
   * @param itemId 商品项目ID
   * @param clinicItemPriceModels 门诊商品项目价格列表
   */
  private void saveClinicTariff(
      BigDecimal price, Integer itemId, List<ClinicItemPriceModel> clinicItemPriceModels) {
    ClinicTariff clinicTariff = new ClinicTariff();
    clinicTariff.setTariffId(itemId);
    clinicTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    clinicTariff.setCrtName(BaseContextHandler.getName());
    if (StringHelper.isNotEmpty(clinicItemPriceModels)) {
      clinicItemPriceModels.forEach(
          itemPriceModel -> {
            clinicTariff.setClinicId(itemPriceModel.getOrgId());
            clinicTariff.setPrice(itemPriceModel.getItemPrice());
            clinicTariffBiz.insertSelective(clinicTariff);
          });
    } else {
      OrganizationModel orgModel = new OrganizationModel();
      orgModel.setTypes(new Byte[] {2});
      orgModel.setWhetherPage(false);
      List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoList(orgModel);
      if (StringHelper.isNotEmpty(orgInfos)) {
        orgInfos.forEach(
            orgInfo -> {
              clinicTariff.setClinicId(orgInfo.getId());
              clinicTariff.setPrice(price);
              clinicTariffBiz.insertSelective(clinicTariff);
            });
      }
    }
  }

  /**
   * 修改价目表
   *
   * @param id 价目表ID
   * @param form 修改参数
   */
  public void modify(Integer id, BaseTariffForm form) {
    BaseTariffInfoVO resultData = findBaseTariffInfoById(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "的价目表不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    String resultDataName = resultData.getName();
    String resultDataItemNumber = resultData.getItemNumber();
    String resultDataTariffCategoryNumber = resultData.getTariffCategoryNumber();
    BaseTariff entity = new BaseTariff();
    String name = form.getName();
    if (!resultDataName.equals(name)) {
      entity = new BaseTariff();
      entity.setName(name);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，名称为'" + name + "'的价目表已存在！", OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
    }
    String number = form.getItemNumber();
    if (!resultDataItemNumber.equals(number)) {
      entity = new BaseTariff();
      entity.setItemNumber(number);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，价目表编号'" + number + "'已存在！", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }
    String categoryNumber = resultDataTariffCategoryNumber.substring(0, 3);
    String itemNumber = number.substring(0, 3);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException(
          "修改失败，价目表编号前3位与价目表分类编号前3位不同！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    BeanUtils.copyProperties(form, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setUpdName(BaseContextHandler.getName());
    entity.setUpdTime(new Date(System.currentTimeMillis()));
    entity.setId(id);
    mapper.updateByPrimaryKeySelective(entity);

    List<ClinicItemPriceForm> clinicItemPriceForms = form.getClinicItemPriceForms();

    // 更新门诊价目表信息
    updateClinicTariff(id, clinicItemPriceForms);

    // 保存价目表变更记录
    if (!resultData.getName().equals(name) || !resultData.getItemNumber().equals(number)) {
      BaseTariffHistory baseTariffHistory = new BaseTariffHistory();
      baseTariffHistory.setName(name);
      baseTariffHistory.setItemNumber(number);
      baseTariffHistory.setTariffCategoryId(form.getTariffCategoryId());
      baseTariffHistory.setTariffId(id);
      baseTariffHistory.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      baseTariffHistory.setCrtName(BaseContextHandler.getName());
      baseTariffHistoryBiz.insertSelective(baseTariffHistory);
    }
  }

  /**
   * 更新门诊价目表信息
   *
   * @param itemId 基础价目表ID
   * @param clinicItemPriceForms 修改门诊价目表信息
   */
  private void updateClinicTariff(Integer itemId, List<ClinicItemPriceForm> clinicItemPriceForms) {
    if (StringHelper.isNotEmpty(clinicItemPriceForms)) {
      ClinicTariff clinicTariff;
      for (ClinicItemPriceForm form : clinicItemPriceForms) {
        clinicTariff = new ClinicTariff();
        clinicTariff.setClinicId(form.getOrgId());
        clinicTariff.setTariffId(itemId);
        clinicTariff.setPrice(form.getItemPrice());
        clinicTariff.setInservice(form.getItemInservice());
        Integer clinicItemId = form.getClinicItemId();
        if (null == clinicItemId) {
          clinicTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
          clinicTariff.setCrtName(BaseContextHandler.getName());
          clinicTariffBiz.insertSelective(clinicTariff);
        } else {
          clinicTariff.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
          clinicTariff.setUpdName(BaseContextHandler.getName());
          clinicTariff.setUpdTime(new Date(System.currentTimeMillis()));
          clinicTariff.setId(clinicItemId);
          clinicTariffBiz.updateSelectiveById(clinicTariff);
        }
      }
    }
  }

  /**
   * 根据ID删除价目表
   *
   * @param TariffId 价目表ID
   */
  public void delete(Integer TariffId) {
    ClinicTariff entity = new ClinicTariff();
    entity.setTariffId(TariffId);
    Long count = clinicTariffBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "价目表删除失败，ID为" + TariffId + "'的价目表已被关联！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(TariffId);
    BaseTariffHistory historyEntity = new BaseTariffHistory();
    historyEntity.setTariffId(TariffId);
    baseTariffHistoryBiz.delete(historyEntity);
  }

  /**
   * 导入价目表列表
   *
   * @param excelFile 导入文件
   * @return
   */
  public String importExcel(MultipartFile excelFile) throws Exception {
    ExcelUtil<BaseTariffImportModel> excelUtil = new ExcelUtil<>(BaseTariffImportModel.class);
    List<BaseTariffImportModel> models = excelUtil.importExcel(excelFile.getInputStream());
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException(
          "导入失败,导入的价目表数据不能为空！", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
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
    List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoList(orgModel);

    if (StringHelper.isNotEmpty(orgInfos)) {
      for (BaseTariffImportModel model : models) {
        for (OrganizationInfoDetail orgInfo : orgInfos) {
          dataNum++;
          itemName = model.getName();
          itemNumber = model.getItemNumber();
          englishName = model.getEnglishName();
          categoryNumber = model.getTariffCategoryNumber();
          categoryName = model.getTariffCategoryName();
          unit = model.getUnit();
          price = model.getPrice();
          Integer orgId = orgInfo.getId();
          // 校验价目表分类参数合法性
          checkCategoryParams(dataNum, failureMsg, itemNumber, categoryName, categoryNumber);
          // 获取价目表分类ID
          Integer categoryId = getCategoryId(categoryName, categoryNumber);
          // 校验价目表参数合法性
          checkItemParams(dataNum, failureMsg, itemName, itemNumber, categoryId);
          // 新增/更新价目表信息、门诊价目表
          addOrUpdBaseItemAndClinicItem(
              itemName, itemNumber, englishName, unit, price, categoryId, orgId);
        }
      }
    } else {
      for (BaseTariffImportModel model : models) {
        dataNum++;
        itemName = model.getName();
        itemNumber = model.getItemNumber();
        englishName = model.getEnglishName();
        categoryNumber = model.getTariffCategoryNumber();
        categoryName = model.getTariffCategoryName();
        unit = model.getUnit();
        price = model.getPrice();
        // 校验价目表分类参数合法性
        checkCategoryParams(dataNum, failureMsg, itemNumber, categoryName, categoryNumber);
        // 获取价目表分类ID
        Integer categoryId = getCategoryId(categoryName, categoryNumber);
        // 校验价目表参数合法性
        checkItemParams(dataNum, failureMsg, itemName, itemNumber, categoryId);
        // 新增或更新价目表信息
        addOrUpdBaseItem(itemName, itemNumber, englishName, unit, price, categoryId);
      }
    }
    return successMsg
        .append("导入成功，共计:")
        .append(models.size())
        .append("条数据！")
        .append("本次共同步'")
        .append(orgInfos.size())
        .append("个门诊'")
        .append(dataNum)
        .append("'条数据")
        .toString();
  }

  /**
   * 新增/更新基础价目表或同步门诊价目表
   *
   * @param itemName 价目表名称
   * @param itemNumber 价目表编号
   * @param englishName 价目表英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 价目表分类ID
   * @param orgId 门诊ID
   */
  private void addOrUpdBaseItemAndClinicItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId,
      Integer orgId) {
    int itemId;
    BaseTariff itemEntity;
    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    BaseTariff itemResult = mapper.selectOne(itemEntity);

    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
    itemEntity.setItemNumber(itemNumber);
    itemEntity.setEnglishName(englishName);
    itemEntity.setPrice(price);
    itemEntity.setUnit(unit);

    if (null == itemResult) {
      // 新增价目表
      itemEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      itemEntity.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(itemEntity);
      itemId = itemEntity.getId();
    } else {
      // 更新价目表
      itemEntity.setId(itemResult.getId());
      itemEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      itemEntity.setUpdName(BaseContextHandler.getName());
      itemEntity.setUpdTime(new Date(System.currentTimeMillis()));
      mapper.updateByPrimaryKeySelective(itemEntity);
      itemId = itemResult.getId();
    }

    ClinicTariff clinicItem = new ClinicTariff();
    clinicItem.setClinicId(orgId);
    clinicItem.setTariffId(itemId);
    ClinicTariff clinicTariff = clinicTariffBiz.selectOne(clinicItem);
    if (null == clinicTariff) {
      clinicItem.setPrice(price);
      clinicItem.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      clinicItem.setCrtName(BaseContextHandler.getName());
      clinicTariffBiz.insertSelective(clinicItem);
    }
  }

  /**
   * 新增或更新价目表信息
   *
   * @param itemName 价目表名称
   * @param itemNumber 价目表编号
   * @param englishName 价目表英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 价目表分类ID
   */
  private void addOrUpdBaseItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId) {
    BaseTariff itemEntity;
    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    BaseTariff itemResult = mapper.selectOne(itemEntity);

    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
    itemEntity.setItemNumber(itemNumber);
    itemEntity.setEnglishName(englishName);
    itemEntity.setPrice(price);
    itemEntity.setUnit(unit);

    if (null == itemResult) {
      // 新增价目表
      itemEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      itemEntity.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(itemEntity);
    } else {
      // 更新价目表
      itemEntity.setId(itemResult.getId());
      itemEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      itemEntity.setUpdName(BaseContextHandler.getName());
      itemEntity.setUpdTime(new Date(System.currentTimeMillis()));
      mapper.updateByPrimaryKeySelective(itemEntity);
    }
  }

  /**
   * 校验价目表参数合法性
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemName 价目表名称
   * @param itemNumber 价目表编号
   * @param categoryId 价目表分类ID
   */
  private void checkItemParams(
      int dataNum,
      StringBuilder failureMsg,
      String itemName,
      String itemNumber,
      Integer categoryId) {
    // 初始化参数
    BaseTariff itemEntity;
    BaseTariff itemResult;

    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemResult = mapper.selectOne(itemEntity);
    if (null != itemResult) {
      String resultItemNumber = itemResult.getItemNumber();
      if (!resultItemNumber.equals(itemNumber)) {
        failureMsg
            .append("导入失败，Excel表中第")
            .append(dataNum)
            .append("'条数据的价目表编号'")
            .append(itemNumber)
            .append("'与系统中该价目表编号'")
            .append(resultItemNumber)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }

    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setItemNumber(itemNumber);
    itemResult = mapper.selectOne(itemEntity);
    if (null != itemResult) {
      String itemResultName = itemResult.getName();
      if (!itemResultName.equals(itemName)) {
        failureMsg
            .append("导入失败，Excel表中第")
            .append(dataNum)
            .append("'条数据的价目表名称'")
            .append(itemName)
            .append("'与系统中该价目表名称'")
            .append(itemResultName)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
      }
    }
  }

  /**
   * 获取价目表分类ID
   *
   * @param categoryName 价目表分类名称
   * @param categoryNumber 价目表分类编号
   * @return
   */
  private Integer getCategoryId(String categoryName, String categoryNumber) {
    BaseTariffCategory categoryEntity = new BaseTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    BaseTariffCategory categoryResult = baseTariffCategoryMapper.selectOne(categoryEntity);
    Integer categoryId;
    if (null == categoryResult) {
      // 新增价目表分类
      categoryEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      categoryEntity.setCrtName(BaseContextHandler.getName());
      baseTariffCategoryMapper.insertSelective(categoryEntity);
      categoryId = categoryEntity.getId();
    } else {
      categoryId = categoryResult.getId();
    }
    return categoryId;
  }

  /**
   * 校验价目表分类参数
   *
   * @param dataNum 当前行
   * @param failureMsg 提示信息
   * @param itemNumber 价目表编号
   * @param categoryName 价目表分类名称
   * @param categoryNumber 价目表分类编号
   */
  private void checkCategoryParams(
      int dataNum,
      StringBuilder failureMsg,
      String itemNumber,
      String categoryName,
      String categoryNumber) {
    // 校验分类编号与价目表编号前三位
    String itemStr = itemNumber.substring(0, 3);
    String categoryStr = categoryNumber.substring(0, 3);
    if (!itemStr.equals(categoryStr)) {
      failureMsg
          .append("导入失败，Excel表中第")
          .append(dataNum)
          .append("'条数据的价目表编号前3位'")
          .append(itemStr)
          .append("'与价目表分类编号前3位'")
          .append(categoryStr)
          .append("'不同！");
      throw new ClientServiceException(
          failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    BaseTariffCategory categoryEntity;
    BaseTariffCategory categoryResult;
    categoryEntity = new BaseTariffCategory();
    categoryEntity.setNumber(categoryNumber);
    categoryResult = baseTariffCategoryMapper.selectOne(categoryEntity);
    if (categoryResult != null) {
      String categoryResultName = categoryResult.getName();
      if (!categoryName.equals(categoryResultName)) {
        failureMsg
            .append("导入失败，Excel表中第'")
            .append(dataNum)
            .append("'条数据的价目表分类名称'")
            .append(categoryName)
            .append("'与系统中该价目表分类名称'")
            .append(categoryResultName)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }

    categoryEntity = new BaseTariffCategory();
    categoryEntity.setName(categoryName);
    categoryResult = baseTariffCategoryMapper.selectOne(categoryEntity);
    if (categoryResult != null) {
      String categoryResultNumber = categoryResult.getNumber();
      if (!categoryNumber.equals(categoryResultNumber)) {
        failureMsg
            .append("导入失败，Excel表中第'")
            .append(dataNum)
            .append("'条数据的价目表分类编号'")
            .append(categoryNumber)
            .append("'与系统中该价目表分类编号'")
            .append(categoryResultNumber)
            .append("'不一致！");
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }
  }

  /**
   * 导出价目表列表
   *
   * @param response http响应
   * @param queryForm 查询参数
   */
  public void exportExcel(HttpServletResponse response, BaseTariffQueryForm queryForm)
      throws IOException {
    List<BaseTariffExportVO> resultList = mapper.selectExportBaseTariffList(queryForm);
    ExcelUtil<BaseTariffExportVO> excelUtil = new ExcelUtil<>(BaseTariffExportVO.class);
    excelUtil.exportExcel(response, resultList, "基础价目表信息表");
  }

  /**
   * 根据条件查询开单关联信息列表（可分页）
   *
   * @param queryForm 查询条件
   * @return list
   */
  public PageInfo<BaseTariffAssociationVO> findTariffAssociationList(
      BaseTariffAssociationQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseTariffAssociationVO> resultList = mapper.selectBaseTariffAssociationList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 修改价目表开单关联信息
   *
   * @param id 价目表ID
   * @param form 开单关联信息
   */
  public void modifyTariffAssociation(Integer id, BaseTariffAssociationForm form) {
    BaseTariff resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "的价目表不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    String emr = form.getEmr();
    String attention = form.getAttention();
    StringBuilder fellowUp = new StringBuilder();
    List<Integer> fellowUps = form.getFellowUps();
    if (StringHelper.isBlank(emr)) {
      emr = "";
    }
    if (StringHelper.isBlank(attention)) {
      attention = "";
    }
    if (StringHelper.isNotEmpty(fellowUps)) {
      fellowUps.forEach(integer -> fellowUp.append(integer).append(","));
    }
    resultData.setEmr(emr);
    resultData.setAttention(attention);
    resultData.setFellowUp(fellowUp.toString().substring(0, fellowUp.length() - 1));
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 导入价目表开单关联信息
   *
   * @param excelFile excel文件
   */
  public String importTariffAssociation(MultipartFile excelFile) throws Exception {
    ExcelUtil<BaseTariffAssociationImportModel> excelUtil =
        new ExcelUtil<>(BaseTariffAssociationImportModel.class);
    List<BaseTariffAssociationImportModel> models =
        excelUtil.importExcel(excelFile.getInputStream());
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException(
          "导入失败,导入的价目表数据不能为空！", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }

    int dataNum = 0;
    StringBuilder successMsg = new StringBuilder();
    StringBuilder failureMsg = new StringBuilder();

    String name;
    String itemNumber;
    String tariffCategoryNumber;
    String tariffCategoryName;
    String emr;
    String attention;
    String fellowUp;
    BaseTariffCategory tariffCategory;
    BaseTariff tariff;

    for (BaseTariffAssociationImportModel model : models) {
      dataNum++;
      name = model.getName();
      itemNumber = model.getItemNumber();
      tariffCategoryNumber = model.getTariffCategoryNumber();
      tariffCategoryName = model.getTariffCategoryName();
      emr = model.getEmr();
      attention = model.getAttention();
      fellowUp = model.getFellowUp();

      tariffCategory = new BaseTariffCategory();
      tariffCategory.setNumber(tariffCategoryNumber);
      tariffCategory.setName(tariffCategoryName);
      BaseTariffCategory tariffCategoryResult = baseTariffCategoryMapper.selectOne(tariffCategory);
      if (null == tariffCategoryResult) {
        failureMsg
            .append("导入失败！未查询到项目分类编号为:")
            .append(tariffCategoryNumber)
            .append("，")
            .append("项目分类名称为:")
            .append(tariffCategoryName)
            .append("的开单项目分类！")
            .append("数据序号为：")
            .append(dataNum);
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.QUERY_RESULT_INVALID);
      }

      tariff = new BaseTariff();
      tariff.setItemNumber(itemNumber);
      tariff.setName(name);
      tariff.setTariffCategoryId(tariffCategoryResult.getId());
      BaseTariff tariffResult = mapper.selectOne(tariff);
      if (null == tariffResult) {
        failureMsg
            .append("导入失败！未查询到项目编号为:")
            .append(itemNumber)
            .append("，")
            .append("项目名称为:")
            .append(name)
            .append("的开单项目！")
            .append("数据序号为:")
            .append(dataNum);
        throw new ClientServiceException(
            failureMsg.toString(), OperationCodeConstants.QUERY_RESULT_INVALID);
      }

      tariffResult.setEmr(emr);
      tariffResult.setAttention(attention);
      tariffResult.setFellowUp(fellowUp);
      mapper.updateByPrimaryKeySelective(tariffResult);
    }
    return successMsg
        .append("导入成功！")
        .append("本次共导入：")
        .append(dataNum)
        .append("条开单关联数据！")
        .toString();
  }

  /**
   * 导出价目表开单关联列表
   *
   * @param response 响应
   * @param queryForm 查询条件
   * @throws IOException IO异常
   */
  public void exportTariffAssociation(
      HttpServletResponse response, BaseTariffAssociationQueryForm queryForm) throws IOException {
    List<BaseTariffAssociationExportVO> resultList =
        mapper.selectExportBaseTariffAssociationList(queryForm);
    ExcelUtil<BaseTariffAssociationExportVO> excelUtil =
        new ExcelUtil<>(BaseTariffAssociationExportVO.class);
    excelUtil.exportExcel(response, resultList, "价目表开单关联信息");
  }
}
