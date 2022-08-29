package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.domain.form.*;
import com.yunya.feign.treatment.domain.model.BaseTariffAssociationImportModel;
import com.yunya.feign.treatment.domain.model.BaseTariffImportModel;
import com.yunya.feign.treatment.domain.model.BaseTariffModel;
import com.yunya.feign.treatment.domain.model.ClinicItemPriceModel;
import com.yunya.feign.treatment.domain.model.TariffUniteModel;
import com.yunya.feign.treatment.domain.query.BaseTariffAssociationQueryForm;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffAssociationExportVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffAssociationVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffExportVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffVO;
import com.yunya.feign.treatment.domain.vo.ClinicItemPriceVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.modules.treatment.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTariffInfo;
import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;
import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;
import static com.yunya.framework.common.constant.OperationCodeConstants.OBJECT_EDIT_FAIL;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY;
import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_ITEM_INFO;

/**
 * 描述: 基础价目表业务层
 *
 * @author GaoLuding
 * @create 2020-05-18 16:28
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffBiz extends BaseBiz<BaseTariffMapper, BaseTariff> {

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 消息中间件调用 */
  @Resource private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 系统服务远程调用 */
  @Resource private RemoteSystemServiceFeign systemServiceFeign;
  /** 价目表分类 */
  @Resource private BaseTariffCategoryMapper baseTariffCategoryMapper;
  /** 价目表操作记录 */
  @Resource private BaseTariffHistoryMapper baseTariffHistoryMapper;
  /** 门诊价目表 */
  @Resource private ClinicTariffBiz clinicTariffBiz;
  /** 开单明细映射 */
  @Resource private OrderDetailMapper orderDetailMapper;
  /** 线程池 */
  @Resource(name = "treatmentThreadPool")
  private ExecutorService importExcelThreadPool;

  @Resource
  private BaseTariffFellowupRelationMapper baseTariffFellowupMapper;


  /**
   * 根据多个价目表ID查询价目表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  public String findBaseTariffNamesByIds(String[] ids) {
    if (StringHelper.isNotEmpty(ids)) {
      Joiner joiner = Joiner.on(",");
      return mapper.selectBaseTariffNamesByIds(joiner.join(ids));
    }
    return null;
  }

  /**
   * 根据ID查询价目表信息（包含门诊价目表价格信息）
   *
   * @param id 价目表ID
   * @return
   */
  public BaseTariffInfoVO findBaseTariffInfoById(Integer id) {
    BaseTariffInfoVO resultData = mapper.selectBaseTariffInfoById(id);
    if (null != resultData) {
      ClinicTariff clinicTariff = new ClinicTariff();
      clinicTariff.setTariffId(id);
      List<ClinicTariff> clinicTariffs = clinicTariffBiz.selectList(clinicTariff);
      List<ClinicItemPriceVO> itemInfos = new ArrayList<>();
      if (StringHelper.isNotEmpty(clinicTariffs)) {
        for (ClinicTariff tariff : clinicTariffs) {
          ClinicItemPriceVO clinicItem = new ClinicItemPriceVO();
          clinicItem.setClinicItemId(tariff.getId());
          Integer clinicId = tariff.getClinicId();
          clinicItem.setOrgId(clinicId);
          OrganizationInfo organizationInfo = systemServiceFeign.findOrgInfoByOrgId(clinicId);
          if (organizationInfo != null) {
            clinicItem.setOrgName(organizationInfo.getAbbreviation());
          }
          clinicItem.setItemId(tariff.getTariffId());
          clinicItem.setClinicItemPrice(tariff.getPrice());
          clinicItem.setItemInservice(tariff.getInservice());
          itemInfos.add(clinicItem);
        }
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
   * 根据价目表分类ID生成价目表编号
   *
   * @param tariffCategoryId 价目表分类ID
   * @return String - 价目表编号
   */
  public String generateBaseTariffNumber(Integer tariffCategoryId) {
    BaseTariffCategory tariffCategory =
        baseTariffCategoryMapper.selectByPrimaryKey(tariffCategoryId);
    if (tariffCategory == null) {
      throw new ClientServiceException("请选择正确的价目表分类进行新增！", PARAMETERS_IS_ILLEGAL);
    }
    String categoryNumber = tariffCategory.getNumber().substring(0, 3);
    String number = mapper.selectMaxBaseTariffNumber(tariffCategoryId, categoryNumber);
    return categoryNumber + String.format("%03d", Integer.parseInt(number) + 1);
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
          "新增失败，ID为'" + categoryId + "'的价目表分类不存在，请选择正确的价目表分类！", QUERY_RESULT_INVALID);
    }

    BaseTariff entity = new BaseTariff();
    String name = model.getName();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，价目表名称'" + name + "'已存在！", NAME_IS_OCCUPIED);
    }

    entity = new BaseTariff();
    String number = model.getItemNumber();
    entity.setItemNumber(number);
    count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，价目表编号'" + number + "'已存在！", NAME_IS_OCCUPIED);
    }

    String categoryNumber = tariffCategory.getNumber().substring(0, 3);
    String itemNumber = number.substring(0, 3);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException("新增失败，价目表编号前3位与价目表分类编号前3位不同！", PARAMETERS_IS_ILLEGAL);
    }

    BeanUtils.copyProperties(model, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    int i = mapper.insertSelective(entity);

    // 添加门诊价目表
    Integer itemId = entity.getId();
    List<ClinicItemPriceModel> clinicItemPriceModels = model.getClinicItemPriceModels();

    // 保存门诊价目表
    if (StringHelper.isNotEmpty(clinicItemPriceModels)) {
      saveClinicTariff(model.getPrice(), itemId, clinicItemPriceModels);
    }

    // 保存价目表新增历史记录
    BaseTariffHistory baseTariffHistory = new BaseTariffHistory();
    baseTariffHistory.setTariffCategoryId(categoryId);
    baseTariffHistory.setTariffId(itemId);
    baseTariffHistory.setName(name);
    baseTariffHistory.setItemNumber(number);
    baseTariffHistory.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    baseTariffHistory.setCrtName(BaseContextHandler.getName());
    baseTariffHistoryMapper.insertSelective(baseTariffHistory);
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(entity.getId(), 0, 0, BaseTariffInfo);
    }
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
      throw new ClientServiceException("修改失败，ID为'" + id + "的价目表不存在！", QUERY_RESULT_INVALID);
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
        throw new ClientServiceException("修改失败，名称为'" + name + "'的价目表已存在！", OBJECT_EDIT_FAIL);
      }
    }
    String number = form.getItemNumber();
    if (!resultDataItemNumber.equals(number)) {
      entity = new BaseTariff();
      entity.setItemNumber(number);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException("修改失败，价目表编号'" + number + "'已存在！", NAME_IS_OCCUPIED);
      }
    }
    String categoryNumber = resultDataTariffCategoryNumber.substring(0, 3);
    String itemNumber = number.substring(0, 3);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException("修改失败，价目表编号前3位与价目表分类编号前3位不同！", PARAMETERS_IS_ILLEGAL);
    }
    BeanUtils.copyProperties(form, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setUpdName(BaseContextHandler.getName());
    entity.setUpdTime(new Date(System.currentTimeMillis()));
    entity.setId(id);
    int i = mapper.updateByPrimaryKeySelective(entity);
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
      baseTariffHistoryMapper.insertSelective(baseTariffHistory);
    }
    // 发送消息同步价目表信息
    if (i > 0) {
      redisUtils.delete(0 + REDIS_KEY_ITEM_INFO + id);
      rabbitMqServiceFeign.sendMessage(id, 0, 1, BaseTariffInfo);
    }
  }

  /**
   * 更新门诊价目表信息
   *
   * @param itemId 基础价目表ID
   * @param clinicItemPriceForms 修改门诊价目表信息
   */
  private void updateClinicTariff(Integer itemId, List<ClinicItemPriceForm> clinicItemPriceForms) {
    ClinicTariff clinicTariff = new ClinicTariff();
    clinicTariff.setTariffId(itemId);
    clinicTariffBiz.delete(clinicTariff);
    if (StringHelper.isNotEmpty(clinicItemPriceForms)) {
      clinicItemPriceForms.forEach(
          form -> {
            clinicTariff.setClinicId(form.getOrgId());
            clinicTariff.setPrice(form.getItemPrice());
            clinicTariff.setInservice(form.getItemInservice());
            clinicTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicTariff.setCrtName(BaseContextHandler.getName());
            clinicTariffBiz.insertSelective(clinicTariff);
          });
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
      throw new ClientServiceException("价目表删除失败，ID为" + TariffId + "'的价目表已被关联！", DELETE_NOT_ALLOW);
    }
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setType((byte) 0);
    orderDetail.setBillingItemId(TariffId);
    int count1 = orderDetailMapper.selectCount(orderDetail);
    if (count1 > 0) {
      throw new ClientServiceException("价目表删除失败，ID为" + TariffId + "'的价目表已被开单！", DELETE_NOT_ALLOW);
    }
    int i = mapper.deleteByPrimaryKey(TariffId);
    BaseTariffHistory historyEntity = new BaseTariffHistory();
    historyEntity.setTariffId(TariffId);
    baseTariffHistoryMapper.delete(historyEntity);
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(TariffId, 0, 2, BaseTariffInfo);
    }
  }

  /**
   * 一键启用禁用基础价目表
   *
   * @param id 价目表ID
   * @param switchType 开关状态
   */
  public void operateBaseTariffStatus(Integer id, Boolean switchType) {
    BaseTariff tariff = mapper.selectByPrimaryKey(id);
    if (tariff == null) {
      throw new ClientServiceException("操作失败，价目表不存在！", PARAMETERS_IS_ILLEGAL);
    }
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String userName = BaseContextHandler.getName();
    if (switchType) {
      clinicTariffBiz.enableClinicTariffByTariffId(id, userId, userName);
    } else {
      clinicTariffBiz.disableClinicTariffByTariffId(id, userId, userName);
    }
    tariff.setInservice(switchType);
    tariff.setUpdId(userId);
    tariff.setUpdName(userName);
    mapper.updateByPrimaryKeySelective(tariff);
  }

  /**
   * 导入价目表列表
   *
   * @param excelFile 导入文件
   * @return
   */
  public String importExcel(MultipartFile excelFile) throws Exception {
    ExcelUtil<BaseTariffImportModel> excelUtil = new ExcelUtil<>(BaseTariffImportModel.class);
    List<BaseTariffImportModel> models =
        Collections.synchronizedList(excelUtil.importExcel(excelFile.getInputStream()));
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException("导入失败,导入的价目表数据不能为空！", PARAM_NOT_ALLOW_EMPTY);
    }

    // 初始化参数、常量
    int size = models.size();
    AtomicInteger dataNum = new AtomicInteger(0);
    StringBuilder failureMsg = new StringBuilder();
    CountDownLatch stepLatch_1 = new CountDownLatch(size);
    // 1异步校验价目表分类参数合法性
    List<Future> futureList = synCheckExcelData(models, dataNum, failureMsg, stepLatch_1);
    stepLatch_1.await();

    // 1.1 检测校验是否通过
    futureList.forEach(
        future -> {
          try {
            Object o = future.get();
            if (o instanceof ClientServiceException) {
              ClientServiceException cexp = (ClientServiceException) o;
              log.info(cexp.getMessage());
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        });

    // 2.添加基础价目表分类
    // 2-1.异步构建基础价目表目录实体集合
    String userId = BaseContextHandler.getUserID();
    String name = BaseContextHandler.getName();
    CountDownLatch stepLatch_2 = new CountDownLatch(size);
    List<Future> futures = syncBaseTariffCategoryBuilder(models, userId, name, stepLatch_2);
    stepLatch_2.await();
    // 2-2.将List<Future<BaseTariffCategory>>转化为List<BaseTariffCategory>
    List<BaseTariffCategory> list = future2List(futures);
    // 根据分类编号去重
    Set<BaseTariffCategory> insertBaseTariffCategories =
        new TreeSet<>(Comparator.comparing(BaseTariffCategory::getNumber));
    Set<BaseTariffCategory> updateBaseTariffCategories =
        new TreeSet<>(Comparator.comparing(BaseTariffCategory::getNumber));
    list.forEach(
        baseTariffCategory -> {
          if (baseTariffCategory.getId() == null) {
            insertBaseTariffCategories.add(baseTariffCategory);
          } else {
            updateBaseTariffCategories.add(baseTariffCategory);
          }
        });

    // 2-3.将基础价目表分类数据异步分片的方式插入数据库
    List<BaseTariffCategory> insertCategoryList = new ArrayList<>(insertBaseTariffCategories);
    List<List<BaseTariffCategory>> insertCategories = Lists.partition(insertCategoryList, 1000);
    if (StringHelper.isNotEmpty(insertCategories)) {
      CountDownLatch stepLatch_3 = new CountDownLatch(insertCategories.size());
      insertBaseTariffCategoryBySplices(insertCategories, stepLatch_3);
      stepLatch_3.await();
    }

    // 2-4.将基础价目表分类数据异步分片的方式更新到数据库
    List<BaseTariffCategory> updateCategoryList = new ArrayList<>(updateBaseTariffCategories);
    // 2-3.将基础价目表分类数据异步分片的方式插入数据库
    List<List<BaseTariffCategory>> updateCategories = Lists.partition(updateCategoryList, 1000);
    if (StringHelper.isNotEmpty(updateCategories)) {
      CountDownLatch stepLatch_4 = new CountDownLatch(updateCategories.size());
      updateBaseTariffCategoryBySplices(updateCategories, stepLatch_4);
      stepLatch_4.await();
    }

    // 3.异步校验价目表参数合法性
    CountDownLatch stepLatch_5 = new CountDownLatch(size);
    synCheckItemParams(models, dataNum, failureMsg, stepLatch_5);
    stepLatch_5.await();

    // 4.异步更新基础价目表
    List<BaseTariff> baseTariffsUpdate = this.updateBaseTariffList(models, userId, name);
    // 4-1.向mq推送更新消息
    pushMsg2RabbitMq(baseTariffsUpdate, 1);

    // 4-2. 异步新增基础价目表
    List<BaseTariff> baseTariffsInsert = this.insertBaseTariffList(models, userId, name);
    // 4-3.向mq推送新增消息
    pushMsg2RabbitMq(baseTariffsInsert, 0);

    // 5-1.合并更新、新增价目表列表
    List<BaseTariff> baseTariffs =
        baseTariffsInsert.stream()
            .sequential()
            .collect(Collectors.toCollection(() -> baseTariffsUpdate));
    // 5-2.保存基础价目表更新历史
    saveBaseTariffHistory(baseTariffs, userId, name);
    return "导入成功，本次共计导入:" + size + "条基础价目表数据！";
  }

  /**
   * 同步校验excel数据
   *
   * @param models 数据列表
   * @param dataNum 数据序号
   * @param failureMsg 失败信息
   */
  private List<Future> synCheckExcelData(
      List<BaseTariffImportModel> models,
      AtomicInteger dataNum,
      StringBuilder failureMsg,
      CountDownLatch latch) {
    dataNum.set(0);
    List<Future> resultFutures = new ArrayList<>();
    models.forEach(
        model ->
            resultFutures.add(
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        dataNum.getAndIncrement();
                        String categoryNumber = model.getTariffCategoryNumber();
                        String categoryName = model.getTariffCategoryName();
                        String itemNumber = model.getItemNumber();
                        String itemName = model.getName();
                        String itemNumStr = itemNumber.substring(0, 3);
                        String categoryNumStr = categoryNumber.substring(0, 3);
                        // 校验分类编号与价目表编号前三位
                        checkItemNumTopThree(dataNum.get(), failureMsg, categoryNumStr, itemNumStr);
                        // 校验导入项目分类与数据库分类信息的编号、名称
                        checkItemCategoryWithDataBase(
                            dataNum.get(), failureMsg, categoryNumber, categoryName);
                        // 校验导入项目与数据库项目信息的编号、名称
                        checkItemNumAndNameWithDataBase(
                            dataNum.get(), failureMsg, itemNumber, itemName);
                      } finally {
                        latch.countDown();
                      }
                    })));
    return resultFutures;
  }

  /**
   * 校验基础价目表编号与前三位
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param categoryStr 分类编号前三位
   * @param itemStr 项目编号前三位
   */
  private void checkItemNumTopThree(
      int dataNum, StringBuilder failureMsg, String categoryStr, String itemStr) {
    if (!categoryStr.equals(itemStr)) {
      failureMsg
          .append("数据错误，Excel表中第")
          .append(dataNum)
          .append("'条数据的项目编号前3位'")
          .append(itemStr)
          .append("'与项目分类编号前3位'")
          .append(categoryStr)
          .append("'不同！");
      // todo 将该行数据加入到错误数据集合中
      throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 校验导入项目分类与数据库分类信息的编号、名称
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param categoryNumber 项目分类编号
   * @param categoryName 项目分类名称
   */
  private void checkItemCategoryWithDataBase(
      int dataNum, StringBuilder failureMsg, String categoryNumber, String categoryName) {
    List<BaseTariffCategory> categoryView = baseTariffCategoryMapper.selectBaseTariffCategoryView();
    if (StringHelper.isNotEmpty(categoryView)) {
      categoryView.forEach(
          tariffCategory -> {
            String number = tariffCategory.getNumber();
            String name = tariffCategory.getName();
            if (number.equals(categoryNumber)) {
              if (!name.equals(categoryName)) {
                failureMsg
                    .append("数据错误，Excel表中第")
                    .append(dataNum)
                    .append("'条数据的项目分类编号与数据库一致但项目分类名称不一致");
                // todo 将该行数据加入到错误数据集合中
                throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
              }
            } else {
              if (name.equals(categoryName)) {
                failureMsg
                    .append("数据错误，Excel表中第")
                    .append(dataNum)
                    .append("'条数据的项目分类编号与数据库不一致但项目分类名称一致");
                // todo 将该行数据加入到错误数据集合中
                throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
              }
            }
          });
    }
  }

  /**
   * 异步构建基础价目表实体集合
   *
   * @param models 基础价目表集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回结果集合List<Future<BaseTariffCategory>>
   */
  private List<Future> syncBaseTariffCategoryBuilder(
      List<BaseTariffImportModel> models, String userId, String name, CountDownLatch latch) {
    return models.stream()
        .map(
            model ->
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        return baseTariffCategoryBuilder(
                            model.getTariffCategoryName(),
                            model.getTariffCategoryNumber(),
                            userId,
                            name);
                      } finally {
                        latch.countDown();
                      }
                    }))
        .collect(Collectors.toList());
  }

  /**
   * 校验导入项目与数据库项目信息的编号、名称
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemNumber 项目编号
   * @param itemName 项目名称
   */
  private void checkItemNumAndNameWithDataBase(
      int dataNum, StringBuilder failureMsg, String itemNumber, String itemName) {
    List<BaseTariff> tariffView = mapper.selectBaseTariffView();
    if (StringHelper.isNotEmpty(tariffView)) {
      if (StringHelper.isNotEmpty(tariffView)) {
        tariffView.forEach(
            tariff -> {
              String number = tariff.getItemNumber();
              String name = tariff.getName();
              if (number.equals(itemNumber)) {
                if (!name.equals(itemName)) {
                  failureMsg
                      .append("数据错误，Excel表中第")
                      .append(dataNum)
                      .append("'条数据的项目编号与数据库一致但项目名称不一致");
                  // todo 将该行数据加入到错误数据集合中
                  throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
                }
              } else {
                if (name.equals(itemName)) {
                  failureMsg
                      .append("数据错误，Excel表中第")
                      .append(dataNum)
                      .append("'条数据的项目编号与数据库不一致但项目名称一致");
                  // todo 将该行数据加入到错误数据集合中
                  throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
                }
              }
            });
      }
    }
  }

  /**
   * 分片插入基础价目表目录
   *
   * @param datas 集合
   */
  private void insertBaseTariffCategoryBySplices(
      List<List<BaseTariffCategory>> datas, CountDownLatch latch) {
    datas.forEach(
        data ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    baseTariffCategoryMapper.insertBaseTariffCategoryList(data);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 分片更新基础价目表分类
   *
   * @param datas 待更新价目表分类列表
   * @param latch 计数器
   */
  private void updateBaseTariffCategoryBySplices(
      List<List<BaseTariffCategory>> datas, CountDownLatch latch) {
    datas.forEach(
        list ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    baseTariffCategoryMapper.updateBaseTariffCategoryList(list);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 异步校验价目表参数合法性
   *
   * @param models 导入数据集合
   */
  private void synCheckItemParams(
      List<BaseTariffImportModel> models,
      AtomicInteger dataNum,
      StringBuilder failureMsg,
      CountDownLatch stepLatch) {
    dataNum.set(0);
    models.forEach(
        model ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    // 校验价目表参数合法性
                    checkItemParams(
                        model.getTariffCategoryName(),
                        model.getTariffCategoryNumber(),
                        dataNum,
                        failureMsg,
                        model.getName(),
                        model.getItemNumber());
                  } finally {
                    stepLatch.countDown();
                  }
                }));
  }

  /**
   * 异步新增或更新基础价目表
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @throws InterruptedException 异常
   * @return 返回插入的数据集合
   */
  private List<BaseTariff> updateBaseTariffList(
      List<BaseTariffImportModel> models, String userId, String name) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(models.size());
    // 1. 先执行更新操作,构建更新实体集合
    List<Future> futures = this.syncUpdateBaseTariffBuilder(models, userId, name, latch);
    latch.await();
    if (StringHelper.isNotEmpty(futures)) {
      // 2.将List<Future>转化为List<BaseTariff>
      List<BaseTariff> list = future2List(futures);
      // 3.分片异步更新基础价目表
      List<List<BaseTariff>> partition = Lists.partition(list, 1000);
      CountDownLatch updateLatch = new CountDownLatch(partition.size());
      updateBaseTariffBySplices(partition, updateLatch);
      updateLatch.await();
      return list;
    }
    return new ArrayList<>();
  }

  /**
   * 异步新增基础价目表
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @throws InterruptedException 异常
   * @return 返回插入的数据集合
   */
  private List<BaseTariff> insertBaseTariffList(
      List<BaseTariffImportModel> models, String userId, String name) throws InterruptedException {
    // 1.构建更新实体集合
    CountDownLatch builderLatch = new CountDownLatch(models.size());
    List<Future> futuresInsert =
        this.syncInsertBaseTariffBuilder(models, userId, name, builderLatch);
    builderLatch.await();
    if (StringHelper.isNotEmpty(futuresInsert)) {
      // 2.将List<Future>转化为List<BaseTariff>
      List<BaseTariff> listInsert = future2List(futuresInsert);
      // 3.分片异步更新基础价目表
      List<List<BaseTariff>> partition = Lists.partition(listInsert, 1000);
      CountDownLatch insertLatch = new CountDownLatch(partition.size());
      insertBaseTariffBySplices(partition, insertLatch);
      insertLatch.await();
      return listInsert;
    }
    return new ArrayList<>();
  }

  /**
   * 分片异步插入基础价目表
   *
   * @param datas 基础价目表集合
   */
  private void insertBaseTariffBySplices(List<List<BaseTariff>> datas, CountDownLatch latch) {
    datas.forEach(
        list ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    mapper.insertBaseItems(list);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 异步构建新增基础价目标集合
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回实体集合
   */
  private List<Future> syncInsertBaseTariffBuilder(
      List<BaseTariffImportModel> models, String userId, String name, CountDownLatch latch) {
    List<Future> future =
        models.stream()
            .map(
                model ->
                    importExcelThreadPool.submit(
                        () -> {
                          try {
                            Integer categoryId =
                                BaseTariffBiz.this.getCategoryId(
                                    model.getTariffCategoryName(), model.getTariffCategoryNumber());
                            return insertItemAndClinicItem(
                                model.getName(),
                                model.getItemNumber(),
                                model.getEnglishName(),
                                model.getUnit(),
                                model.getPrice(),
                                categoryId,
                                userId,
                                name);
                          } finally {
                            latch.countDown();
                          }
                        }))
            .collect(Collectors.toList());
    return future;
  }

  /**
   * 向rabbitMq推送消息
   *
   * @param baseTariffs 对应更新表的枚举，决定调用哪个中间表更新业务
   * @param operateType 操作类型：0-新增；1-更新；2-删除
   */
  private void pushMsg2RabbitMq(List<BaseTariff> baseTariffs, Integer operateType) {
    if (StringHelper.isNotEmpty(baseTariffs)) {
      switch (operateType) {
        case 0:
          baseTariffs.forEach(
              baseTariff -> {
                Integer itemId = baseTariff.getId();
                rabbitMqServiceFeign.sendMessage(itemId, 0, 0, BaseTariffInfo);
              });
          break;
        case 1:
          baseTariffs.forEach(
              baseTariff -> {
                Integer itemId = baseTariff.getId();
                rabbitMqServiceFeign.sendMessage(itemId, 0, 1, BaseTariffInfo);
              });
          break;
        default:
          break;
      }
    }
  }

  /**
   * 保存基础价目表变更历史
   *
   * @param baseTariffs 基础价目表列表
   * @param userId 用户ID
   * @param name 用户姓名
   */
  private void saveBaseTariffHistory(List<BaseTariff> baseTariffs, String userId, String name)
      throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(baseTariffs.size());
    List<Future> futureList = synBaseTariffHistoryBuilder(baseTariffs, userId, name, latch);
    latch.await();
    List<BaseTariffHistory> tariffHistories = future2List(futureList);
    List<List<BaseTariffHistory>> partition = Lists.partition(tariffHistories, 1000);
    // 保存变更记录
    if (StringHelper.isNotEmpty(tariffHistories)) {
      CountDownLatch insertLatch = new CountDownLatch(partition.size());
      insertBaseTariffHistory(partition, insertLatch);
      insertLatch.await();
    }
  }

  /**
   * 批量插入基础价目表变更记录列表
   *
   * @param datas 基础价目表变更记录列表
   * @param latch 计数器
   */
  private void insertBaseTariffHistory(List<List<BaseTariffHistory>> datas, CountDownLatch latch) {
    datas.forEach(
        list ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    baseTariffHistoryMapper.insertBaseTariffHistory(list);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 构建价目表变更记录列表
   *
   * @param baseTariffs 基础价目表列表
   * @param userId 用户ID
   * @param name 用户姓名
   * @param latch 计数器
   * @return
   */
  private List<Future> synBaseTariffHistoryBuilder(
      List<BaseTariff> baseTariffs, String userId, String name, CountDownLatch latch) {
    List<Future> futures = new ArrayList<>();
    baseTariffs.forEach(
        baseTariff ->
            futures.add(
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        BaseTariffHistory tariffHistory =
                            checkBaseTariffHistory(
                                baseTariff.getId(),
                                baseTariff.getName(),
                                baseTariff.getItemNumber(),
                                baseTariff.getTariffCategoryId());
                        if (tariffHistory != null) {
                          tariffHistory.setCrtId(Integer.valueOf(userId));
                          tariffHistory.setCrtName(name);
                          return tariffHistory;
                        }
                        return null;
                      } finally {
                        latch.countDown();
                      }
                    })));
    return futures;
  }

  /**
   * 校验是否存在相同的
   *
   * @param id 价目表ID
   * @param itemName 价目表名称
   * @param itemNumber 价目表编号
   * @param categoryId 价目表分类ID
   * @return
   */
  private BaseTariffHistory checkBaseTariffHistory(
      Integer id, String itemName, String itemNumber, Integer categoryId) {
    BaseTariffHistory tariffHistory = new BaseTariffHistory();
    tariffHistory.setTariffId(id);
    tariffHistory.setTariffCategoryId(categoryId);
    tariffHistory.setItemNumber(itemNumber);
    tariffHistory.setName(itemName);
    BaseTariffHistory tariffHistoryResult = baseTariffHistoryMapper.selectOne(tariffHistory);
    return null == tariffHistoryResult ? tariffHistory : null;
  }

  /**
   * 异步构建更新基础价目标集合
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回实体集合
   */
  private List<Future> syncUpdateBaseTariffBuilder(
      List<BaseTariffImportModel> models, String userId, String name, CountDownLatch latch) {
    return models.stream()
        .map(
            model ->
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        Integer categoryId =
                            getCategoryId(
                                model.getTariffCategoryName(), model.getTariffCategoryNumber());
                        if (categoryId != null) {
                          return updateItemAndClinicItem(
                              model.getName(),
                              model.getItemNumber(),
                              model.getEnglishName(),
                              model.getUnit(),
                              model.getPrice(),
                              categoryId,
                              userId,
                              name);
                        }
                        return null;
                      } finally {
                        latch.countDown();
                      }
                    }))
        .collect(Collectors.toList());
  }

  /**
   * 分片异步更新基础价目表
   *
   * @param datas 基础价目表集合
   */
  private void updateBaseTariffBySplices(List<List<BaseTariff>> datas, CountDownLatch latch) {
    datas.forEach(
        list ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    mapper.updateBaseItems(list);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 将List<Future<T>>转化为List
   *
   * @param list 任务结果集合
   * @return 返回对象集合
   */
  private synchronized List future2List(List<Future> list) {
    List container = Lists.newArrayListWithCapacity(list.size());
    if (StringHelper.isNotEmpty(list)) {
      list.forEach(
          future2List -> {
            try {
              if (future2List != null) {
                Object o = future2List.get();
                if (null != o && !container.contains(o)) {
                  container.add(o);
                }
              }
            } catch (InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
          });
    }
    return container;
  }

  /**
   * 新增基础价目表或同步门诊价目表
   *
   * @param itemName 价目表名称
   * @param itemNumber 价目表编号
   * @param englishName 价目表英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 价目表分类ID
   */
  private BaseTariff insertItemAndClinicItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId,
      String userId,
      String name) {
    BaseTariff itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    BaseTariff itemResult = mapper.selectOne(itemEntity);
    if (null == itemResult) {
      // 新增价目表
      itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
      itemEntity.setEnglishName(englishName);
      itemEntity.setPrice(price);
      itemEntity.setUnit(unit);
      itemEntity.setAdjust(true);
      itemEntity.setAchie(true);
      itemEntity.setInservice(true);
      itemEntity.setCrtId(Integer.valueOf(userId));
      itemEntity.setCrtName(name);
      return itemEntity;
    }
    return null;
  }

  /**
   * 更新基础价目表或同步门诊价目表
   *
   * @param itemName 价目表名称
   * @param itemNumber 价目表编号
   * @param englishName 价目表英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 价目表分类ID
   */
  private BaseTariff updateItemAndClinicItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId,
      String userId,
      String name) {
    BaseTariff itemEntity;
    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    BaseTariff itemResult = mapper.selectOne(itemEntity);
    if (null != itemResult) {
      // 更新价目表
      itemEntity.setId(itemResult.getId());
      itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
      itemEntity.setEnglishName(englishName);
      itemEntity.setPrice(price);
      itemEntity.setUnit(unit);
      itemEntity.setUpdId(Integer.valueOf(userId));
      itemEntity.setUpdName(name);
      return itemEntity;
    }
    return null;
  }

  /**
   * 校验价目表参数合法性
   *
   * @param categoryName 项目分类名称
   * @param categoryNumber 项目分类编号
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemName 项目名称
   * @param itemNumber 项目编号
   */
  private void checkItemParams(
      String categoryName,
      String categoryNumber,
      AtomicInteger dataNum,
      StringBuilder failureMsg,
      String itemName,
      String itemNumber) {
    // 获取价目表分类ID
    Integer categoryId = getCategoryId(categoryName, categoryNumber);
    // 校验价目表参数合法性
    checkItemParams(dataNum.get(), failureMsg, itemName, itemNumber, categoryId);
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
        throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
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
        throw new ClientServiceException(failureMsg.toString(), PARAM_NOT_ALLOW_EMPTY);
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
    return categoryResult.getId();
  }

  /**
   * 基础价目表目录构建起
   *
   * @param categoryName 目录名称
   * @param categoryNumber 目录编号
   * @param userId 用户名ID
   * @param name 用户名
   * @return 返回基础价目表目录
   */
  private BaseTariffCategory baseTariffCategoryBuilder(
      String categoryName, String categoryNumber, String userId, String name) {
    BaseTariffCategory categoryEntity = new BaseTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    BaseTariffCategory categoryResult = baseTariffCategoryMapper.selectOne(categoryEntity);
    if (null == categoryResult) {
      // 新增价目表分类
      categoryEntity.setCrtId(Integer.valueOf(userId));
      categoryEntity.setCrtName(name);
      categoryEntity.setInservice(true);
    } else {
      categoryEntity.setId(categoryResult.getId());
      categoryEntity.setUpdId(Integer.valueOf(userId));
      categoryEntity.setUpdName(name);
      categoryEntity.setInservice(categoryResult.getInservice());
    }
    return categoryEntity;
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
  @Transactional(rollbackFor = Exception.class)
  public void modifyTariffAssociation(Integer id, BaseTariffAssociationForm form) {
    BaseTariff resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException("修改失败，ID为'" + id + "的价目表不存在！", QUERY_RESULT_INVALID);
    }
    String emr = form.getEmr();
    String attention = form.getAttention();
    if (StringHelper.isBlank(emr)) {
      emr = "";
    }
    if (StringHelper.isBlank(attention)) {
      attention = "";
    }
    resultData.setEmr(emr);
    resultData.setAttention(attention);
    mapper.updateByPrimaryKeySelective(resultData);

    List<FellowUpInfoForm> fellowUpInfoFormList = form.getFellowUpInfoForm();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String username = BaseContextHandler.getUsername();
    Integer baseTariffId = resultData.getId();
    List<BaseTariffFellowupRelation> insertList = new ArrayList<>();
    List<BaseTariffFellowupRelation> updateList = new ArrayList<>();

    for (FellowUpInfoForm fellowUpInfoForm : fellowUpInfoFormList) {
      BaseTariffFellowupRelation baseTariffFellowupRelation = new BaseTariffFellowupRelation();
      baseTariffFellowupRelation.setId(fellowUpInfoForm.getId());
      baseTariffFellowupRelation.setBaseTariffId(baseTariffId);
      baseTariffFellowupRelation.setFellowUp(fellowUpInfoForm.getFellowUp());
      baseTariffFellowupRelation.setFellowUpCase(fellowUpInfoForm.getFellowUpCase());
      baseTariffFellowupRelation.setCrtId(userId);
      baseTariffFellowupRelation.setCrtName(username);
      if (ObjectUtils.isEmpty(baseTariffFellowupRelation.getId())) {
        insertList.add(baseTariffFellowupRelation);
      } else {
        updateList.add(baseTariffFellowupRelation);
      }
    }
    if (!ObjectUtils.isEmpty(insertList)) {
      baseTariffFellowupMapper.batchSave(insertList);
    }
    if (!ObjectUtils.isEmpty(updateList)) {
      baseTariffFellowupMapper.batchUpdate(updateList);
    }
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
      throw new ClientServiceException("导入失败,导入的价目表数据不能为空！", PARAM_NOT_ALLOW_EMPTY);
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
        throw new ClientServiceException(failureMsg.toString(), QUERY_RESULT_INVALID);
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
        throw new ClientServiceException(failureMsg.toString(), QUERY_RESULT_INVALID);
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

  /**
   * 统一设置门诊价目表价格
   *
   * @param form
   */
  public void uniteTariffPrice(TariffUnitePriceForm form) {
    Set<Integer> orgIds = form.getOrgIds();
    Set<TariffUniteModel> tariffUniteModels = form.getTariffUniteModels();
    if (CollectionUtils.isEmpty(orgIds)) {
      throw new ClientServiceException("请至少选择一个门诊", PARAM_NOT_ALLOW_EMPTY);
    }
    if (CollectionUtils.isEmpty(tariffUniteModels)) {
      throw new ClientServiceException("请至少选择一个价目表项目", PARAM_NOT_ALLOW_EMPTY);
    }
    if (!CollectionUtils.isEmpty(orgIds) && !CollectionUtils.isEmpty(tariffUniteModels)) {
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      List<ClinicTariff> clinicTariffs = Lists.newArrayList();
      for (TariffUniteModel model : tariffUniteModels) {
        for (Integer orgId : orgIds) {
          ClinicTariff tariff = new ClinicTariff();
          tariff.setClinicId(orgId);
          tariff.setTariffId(model.getId());
          clinicTariffBiz.delete(tariff);
          tariff.setPrice(model.getPrice());
          tariff.setCrtId(userId);
          tariff.setCrtName(name);
          tariff.setUpdId(userId);
          tariff.setUpdName(name);
          tariff.setInservice(true);
          clinicTariffs.add(tariff);
        }
      }
      if (!CollectionUtils.isEmpty(clinicTariffs)) {
        clinicTariffBiz.batchInsert(clinicTariffs);
      }
    }
  }

  /**
   * 查询全部项目表（包含价目表和商品表）
   *
   * @param queryForm
   * @return
   */
  public PageInfo<BaseTariffVO> findAllTariffList(BaseTariffQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseTariffVO> resultList = mapper.selectAllTariffList(queryForm);
    return new PageInfo<>(resultList);
  }
}
