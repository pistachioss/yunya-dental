package com.yunya.modules.tariff.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.tariff.domain.form.BaseTariffForm;
import com.yunya.feign.tariff.domain.form.ClinicItemPriceForm;
import com.yunya.feign.tariff.domain.model.BaseTariffImportModel;
import com.yunya.feign.tariff.domain.model.BaseTariffModel;
import com.yunya.feign.tariff.domain.model.ClinicItemPriceModel;
import com.yunya.feign.tariff.domain.query.BaseTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.tariff.*;
import com.yunya.modules.tariff.mapper.BaseTariffMapper;
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
  @Autowired private BaseTariffCategoryBiz baseTariffCategoryBiz;
  /** 价目表操作记录 */
  @Autowired private BaseTariffHistoryBiz baseTariffHistoryBiz;
  /** 门诊价目表会员价 */
  @Autowired private ClinicTariffMemberPriceBiz clinicTariffMemberPriceBiz;
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
    return resultData;
  }

  /**
   * 根据条件查询价目表列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public List<BaseTariffVO> findList(BaseTariffQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseTariffVO> resultList = mapper.selectBaseTariffList(queryForm);
    return resultList;
  }

  /**
   * 新增价目表
   *
   * @param model 新增参数
   */
  public void add(BaseTariffModel model) {
    Integer categoryId = model.getTariffCategoryId();
    BaseTariffCategory TariffCategory = baseTariffCategoryBiz.selectById(categoryId);
    if (null == TariffCategory) {
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

    String categoryNumber = TariffCategory.getNumber().substring(0, 2);
    String itemNumber = number.substring(0, 2);
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
    Integer id = entity.getId();
    List<ClinicItemPriceModel> clinicItemPriceModels = model.getClinicItemPriceModels();
    if (clinicItemPriceModels.size() > 0) {
      ClinicTariff clinicTariff;
      for (ClinicItemPriceModel itemPriceModel : clinicItemPriceModels) {
        clinicTariff = new ClinicTariff();
        clinicTariff.setClinicId(itemPriceModel.getOrgId());
        clinicTariff.setTariffId(id);
        clinicTariff.setPrice(itemPriceModel.getItemPrice());
        clinicTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        clinicTariff.setCrtName(BaseContextHandler.getName());
        clinicTariffBiz.insertSelective(clinicTariff);
      }
    }

    // 保存价目表新增历史记录
    BaseTariffHistory baseTariffHistory = new BaseTariffHistory();
    baseTariffHistory.setTariffCategoryId(categoryId);
    baseTariffHistory.setTariffId(id);
    baseTariffHistory.setName(name);
    baseTariffHistory.setItemNumber(number);
    baseTariffHistory.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    baseTariffHistory.setCrtName(BaseContextHandler.getName());
    baseTariffHistoryBiz.insertSelective(baseTariffHistory);
  }

  /**
   * 修改价目表
   *
   * @param id 价目表ID
   * @param form 修改参数
   */
  public void modify(Integer id, BaseTariffForm form) {
    BaseTariffInfoVO resultData = mapper.selectBaseTariffInfoById(id);
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

    String categoryNumber = resultDataTariffCategoryNumber.substring(0, 2);
    String itemNumber = number.substring(0, 2);
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

    // 更新门诊价目表价格
    List<ClinicItemPriceVO> clinicItemInfos = resultData.getClinicItemInfos();
    List<ClinicItemPriceForm> clinicItemPriceForms = form.getClinicItemPriceForms();
    if (clinicItemInfos.size() > 0) {
      ClinicTariff clinicEntity;
      for (ClinicItemPriceVO itemInfo : clinicItemInfos) {
        for (ClinicItemPriceForm itemPriceForm : clinicItemPriceForms) {
          if (itemInfo.getClinicItemId().equals(itemPriceForm.getClinicItemId())
              && !itemInfo.getItemInservice().equals(itemPriceForm.getItemInservice())) {
            clinicEntity = new ClinicTariff();
            clinicEntity.setId(itemInfo.getClinicItemId());
            clinicEntity.setPrice(itemPriceForm.getItemPrice());
            clinicEntity.setInservice(itemPriceForm.getItemInservice());
            clinicEntity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicEntity.setUpdName(BaseContextHandler.getName());
            clinicEntity.setUpdTime(new Date(System.currentTimeMillis()));
            clinicTariffBiz.updateSelectiveById(clinicEntity);
          }
        }
      }
    }

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
          "价目表失败，ID为" + TariffId + "'的价目表已被关联！", OperationCodeConstants.DELETE_NOT_ALLOW);
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
    if (null == models || models.size() == 0) {
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
    // todo 同步门诊价目表
    List<OrganizationInfoDetail> infoList = systemServiceFeign.findOrgInfoList(orgModel);

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
      addOrUpdItem(itemName, itemNumber, englishName, unit, price, categoryId);
    }
    return successMsg.append("导入成功，共计").append(dataNum).append("'条数据！").toString();
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
  private void addOrUpdItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId) {
    BaseTariff itemEntity;
    BaseTariff itemResult;
    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    itemResult = mapper.selectOne(itemEntity);

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
    BaseTariff itemEntity;
    BaseTariff itemResult;
    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemResult = mapper.selectOne(itemEntity);
    if (itemResult != null) {
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
    if (itemResult != null) {
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
    BaseTariffCategory categoryEntity;
    BaseTariffCategory categoryResult;
    categoryEntity = new BaseTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    categoryResult = baseTariffCategoryBiz.selectOne(categoryEntity);
    Integer categoryId;
    if (null == categoryResult) {
      // 新增价目表分类
      categoryEntity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      categoryEntity.setCrtName(BaseContextHandler.getName());
      baseTariffCategoryBiz.insertSelective(categoryEntity);
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
    String itemStr = itemNumber.substring(0, 2);
    String categoryStr = categoryNumber.substring(0, 2);
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
    categoryResult = baseTariffCategoryBiz.selectOne(categoryEntity);
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
    categoryResult = baseTariffCategoryBiz.selectOne(categoryEntity);
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
}
