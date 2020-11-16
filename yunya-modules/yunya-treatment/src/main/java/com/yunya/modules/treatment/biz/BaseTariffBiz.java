package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
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
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.tariff.*;
import com.yunya.modules.treatment.mapper.BaseTariffCategoryMapper;
import com.yunya.modules.treatment.mapper.BaseTariffMapper;
import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.poi.ss.formula.functions.Count;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTariffInfo;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 描述: 基础价目表业务层
 *
 * @author GaoLuding
 * @create 2020-05-18 16:28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffBiz extends BaseBiz<BaseTariffMapper, BaseTariff> {

  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 系统服务远程调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 价目表分类 */
  @Autowired private BaseTariffCategoryMapper baseTariffCategoryMapper;
  /** 价目表操作记录 */
  @Autowired private BaseTariffHistoryBiz baseTariffHistoryBiz;
  /** 门诊价目表 */
  @Autowired private ClinicTariffBiz clinicTariffBiz;
  /** 线程池 */
  @Resource(name = "treatmentThreadPool")
  private ExecutorService importExcelThreadPool;

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
      baseTariffHistoryBiz.insertSelective(baseTariffHistory);
    }
    // 发送消息同步价目表信息
    if (i > 0) {
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
      throw new ClientServiceException("价目表删除失败，ID为" + TariffId + "'的价目表已被关联！", DELETE_NOT_ALLOW);
    }
    int i = mapper.deleteByPrimaryKey(TariffId);
    BaseTariffHistory historyEntity = new BaseTariffHistory();
    historyEntity.setTariffId(TariffId);
    baseTariffHistoryBiz.delete(historyEntity);
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(TariffId, 0, 2, BaseTariffInfo);
    }
  }

  /**
   * 导入价目表列表
   *
   * @param excelFile 导入文件
   * @return
   */
  public String importExcel(MultipartFile excelFile) throws Exception {
    ExcelUtil<BaseTariffImportModel> excelUtil = new ExcelUtil<>(BaseTariffImportModel.class);
    List<BaseTariffImportModel> models = Collections.synchronizedList(excelUtil.importExcel(excelFile.getInputStream()));
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException("导入失败,导入的价目表数据不能为空！", PARAM_NOT_ALLOW_EMPTY);
    }
    // 初始化参数、常量
    AtomicInteger dataNum = new AtomicInteger(0);
    StringBuilder successMsg = new StringBuilder();
    StringBuilder failureMsg = new StringBuilder();
    String userId = BaseContextHandler.getUserID();
    String name = BaseContextHandler.getName();
    OrganizationModel orgModel = new OrganizationModel();
    // 获取全部门诊信息
    orgModel.setTypes(new Byte[] {2});
    orgModel.setWhetherPage(false);
    List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoList(orgModel);
    if (StringHelper.isNotEmpty(orgInfos)) {
      CountDownLatch stepLatch_1 = new CountDownLatch(models.size());
      // 1异步校验价目表分类参数合法性
      SynCheckCategoryParams(models,dataNum,failureMsg,stepLatch_1);
      stepLatch_1.await();

      // 2.添加基础价目表分类
      // 2-1.异步构建基础价目表目录实体集合
      CountDownLatch stepLatch_2 = new CountDownLatch(models.size());
      List<Future> futures = syncBaseTariffCategoryBuilder(models, userId, name,stepLatch_2);
      stepLatch_2.await();
      // 2-2.将List<Future<BaseTariffCategory>>转化为List<BaseTariffCategory>
      List<BaseTariffCategory> list = future2List(futures);
      // 根据分类编号去重
      List<BaseTariffCategory> distictedList = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BaseTariffCategory::getNumber))), ArrayList::new));
      // 2-3.将基础价目表目录数据异步分片的方式插入数据库
      List<List<BaseTariffCategory>> partition = Lists.partition(distictedList,1000);
      CountDownLatch stepLatch_3 = new CountDownLatch(partition.size());
      insertBaseTariffCategoryBySplices(partition,stepLatch_3);
      stepLatch_3.await();

      CountDownLatch stepLatch = new CountDownLatch(models.size());
      // 3.异步校验价目表参数合法性
      synCheckItemParams(models,dataNum,failureMsg,userId,name,stepLatch);
      stepLatch.await();

      // 4.异步更新基础价目表
      List<BaseTariff> baseTariffs = this.updateBaseTariffList(models, userId, name);
      // 4-1.向mq推送更新消息
      pushMsg2RabbitMq(baseTariffs,0,1);
      // 4-2. 异步新增基础价目表
      List<BaseTariff> baseTariffsInsert = this.insertBaseTariffList(models, userId, name);
      // 4-3.向mq推送新增消息
      pushMsg2RabbitMq(baseTariffs,0,0);
      baseTariffsInsert.stream().sequential().collect(Collectors.toCollection(()->baseTariffs));

      // 5.添加门诊价目表
      // 5-1构建门诊价目表实体集合
      List<Future> clinicTariffFutures = clinicTariffBatchBuilder(baseTariffs, orgInfos, userId, name);
      // 5-2将门诊节目表List<Future<ClinicTariff>>集合转化为List<ClinicTariif>
      List<ClinicTariff> clinicTariffList = this.future2List(clinicTariffFutures);
      // 5-3分片插入门诊价目表
      List<List<ClinicTariff>> partitionClinicTariff = Lists.partition(clinicTariffList, 1000);
      CountDownLatch latch = new CountDownLatch(partitionClinicTariff.size());
      this.insertClinicTariffBySplices(partitionClinicTariff,latch);
      latch.await();
    } else {
      CountDownLatch stepLatch_1 = new CountDownLatch(models.size());
      // 1异步校验价目表分类参数合法性
      SynCheckCategoryParams(models,dataNum,failureMsg,stepLatch_1);
      stepLatch_1.await();

      // 2.添加基础价目表分类
      // 2-1.异步构建基础价目表目录实体集合
      CountDownLatch stepLatch_2 = new CountDownLatch(models.size());
      List<Future> futures = syncBaseTariffCategoryBuilder(models, userId, name,stepLatch_2);
      stepLatch_2.await();
      // 2-2.将List<Future<BaseTariffCategory>>转化为List<BaseTariffCategory>
      List<BaseTariffCategory> list = future2List(futures);
      // 2-3.将基础价目表目录数据异步分片的方式插入数据库
      List<List<BaseTariffCategory>> partition = Lists.partition(list,1000);
      CountDownLatch stepLatch_3 = new CountDownLatch(partition.size());
      insertBaseTariffCategoryBySplices(partition,stepLatch_3);
      stepLatch_3.await();

      CountDownLatch stepLatch = new CountDownLatch(models.size());
      // 3.异步校验价目表参数合法性
      synCheckItemParams(models,dataNum,failureMsg,userId,name,stepLatch);
      stepLatch.await();

      // 4.异步更新基础价目表
      List<BaseTariff> baseTariffs = this.updateBaseTariffList(models, userId, name);
      // 4.异步新增基础价目表
      List<BaseTariff> baseTariffsInsert = this.insertBaseTariffList(models, userId, name);
    }
    return successMsg
        .append("导入成功，共计:")
        .append(models.size())
        .append("条数据！")
        .append("本次共同步'")
        .append(orgInfos.size())
        .append("个门诊'")
        .append(models.size()*orgInfos.size())
        .append("'条数据")
        .toString();
  }

  /**
   * 向rabbitMq推送消息
   * @param baseTariffs  对应更新表的枚举，决定调用哪个中间表更新业务
   * @param dateType 数据类型 0-价目表；1-商品表
   * @param operateType  操作类型：0-新增；1-更新；2-删除
   */
  private void pushMsg2RabbitMq(List<BaseTariff> baseTariffs, Integer dateType, Integer operateType) {
    if (StringHelper.isNotEmpty(baseTariffs)) {
      if (operateType == 0) {
        baseTariffs.forEach(baseTariff -> {
          Integer itemId = baseTariff.getId();
          rabbitMqServiceFeign.sendMessage(itemId, 0, 0, BaseTariffInfo);
        });
      } else if (operateType == 1) {
        baseTariffs.forEach(baseTariff -> {
          Integer itemId = baseTariff.getId();
          rabbitMqServiceFeign.sendMessage(itemId, 0, 1, BaseTariffInfo);
        });
      }
    }
  }

  /**
   * 异步校验价目表分类参数合法性
   * @param models 导入数据集合
   * @param dataNum 当前数据行数
   * @param failureMsg 错误信息
   * @throws InterruptedException 异常
   */
  private void SynCheckCategoryParams(List<BaseTariffImportModel> models,AtomicInteger dataNum,StringBuilder failureMsg,CountDownLatch latch) throws InterruptedException {
    dataNum.set(0);
    for (BaseTariffImportModel model : models) {
      importExcelThreadPool.submit(()->{
        try {
          dataNum.getAndIncrement();
          // 校验价目表分类参数合法性
          checkCategoryParams(dataNum.get(), failureMsg, model.getItemNumber(), model.getTariffCategoryName(), model.getTariffCategoryNumber());
        } finally {
          latch.countDown();
        }
      });
    }
  }


  /**
   * 异步新增或更新基础价目表
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @throws InterruptedException 异常
   * @return 返回插入的数据集合
   */
  private List<BaseTariff> updateBaseTariffList(List<BaseTariffImportModel> models,String userId, String name) throws InterruptedException {
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
      updateBaseTariffBySplices(partition,updateLatch);
      updateLatch.await();
      return list;
    }
    return new ArrayList<>();
  }

  /**
   * 异步新增基础价目表
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @throws InterruptedException 异常
   * @return 返回插入的数据集合
   */
  private List<BaseTariff> insertBaseTariffList(List<BaseTariffImportModel> models,String userId, String name) throws InterruptedException {
    // 再执行新增操作
    // 1.构建更新实体集合
    CountDownLatch builderLatch = new CountDownLatch(models.size());
    List<Future> futuresInsert = this.syncInsertBaseTariffBuilder(models, userId, name,builderLatch);
    builderLatch.await();
    if (StringHelper.isNotEmpty(futuresInsert)) {
      // 2.将List<Future>转化为List<BaseTariff>
      List<BaseTariff> listInsert = future2List(futuresInsert);
      // 3.分片异步更新基础价目表
      List<List<BaseTariff>> partition = Lists.partition(listInsert, 1000);
      CountDownLatch insertLatch = new CountDownLatch(partition.size());
      insertBaseTariffBySplices(partition,insertLatch);
      insertLatch.await();
      return listInsert;
    }
    return new ArrayList<>();
  }



  /**
   * 异步构建新增基础价目标集合
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回实体集合
   */
  private List<Future> syncInsertBaseTariffBuilder(List<BaseTariffImportModel> models,String userId, String name,CountDownLatch latch) throws InterruptedException {
    List<Future> future = new ArrayList<>();
    for (BaseTariffImportModel model : models) {
      future.add(importExcelThreadPool.submit(()->{
        try {
          Integer categoryId = getCategoryId(model.getTariffCategoryName(), model.getTariffCategoryNumber(), userId, name);
          BaseTariff baseTariff = insertItemAndClinicItem(model.getName(), model.getItemNumber(), model.getEnglishName(), model.getUnit(),
                  model.getPrice(), categoryId, userId, name);
          return baseTariff;
        } finally {
          latch.countDown();
        }
      }));
    }
    return future;
  }

  /**
   * 异步构建更新基础价目标集合
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回实体集合
   */
  private List<Future> syncUpdateBaseTariffBuilder(List<BaseTariffImportModel> models,String userId, String name, CountDownLatch latch) throws InterruptedException {
    List<Future> future = new ArrayList<>();
    for (BaseTariffImportModel model : models) {
      future.add(importExcelThreadPool.submit(()->{
        try {
          Integer categoryId = getCategoryId(model.getTariffCategoryName(), model.getTariffCategoryNumber(), userId, name);
          if (categoryId != null) {
            BaseTariff baseTariff = updateItemAndClinicItem(model.getName(), model.getItemNumber(), model.getEnglishName(), model.getUnit(),
                    model.getPrice(), categoryId, userId, name);
            return baseTariff;
          }
          return null;
        } finally {
          latch.countDown();
        }
      }));
    }
    return future;
  }
  /**
   * 异步校验价目表参数合法性
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   */
  private void synCheckItemParams(List<BaseTariffImportModel> models,
                                  AtomicInteger dataNum,
                                  StringBuilder failureMsg,
                                  String userId, String name,
                                  CountDownLatch stepLatch) {
    dataNum.set(0);
    for (BaseTariffImportModel model : models) {
      importExcelThreadPool.submit(()->{
        try {
          // 校验价目表参数合法性
          checkItemParams(model.getTariffCategoryName(),
                  model.getTariffCategoryNumber(), dataNum, failureMsg, model.getName(), model.getItemNumber(), userId, name);
        } finally {
          stepLatch.countDown();
        }
      });
    }
  }

  /**
   * 分片异步插入基础价目表
   * @param datas 基础价目表集合
   * @throws InterruptedException 异常
   */
  private void insertBaseTariffBySplices(List<List<BaseTariff>> datas,CountDownLatch latch) throws InterruptedException {

    for (List<BaseTariff> list : datas) {
      importExcelThreadPool.submit(()->{
        try {
          mapper.insertBaseItems(list);
        } finally {
          latch.countDown();
        }
      });
    }
  }

  /**
   * 分片异步更新基础价目表
   * @param datas 基础价目表集合
   * @throws InterruptedException 异常
   */
  private void updateBaseTariffBySplices(List<List<BaseTariff>> datas, CountDownLatch latch) throws InterruptedException {
    for (List<BaseTariff> list : datas) {
      importExcelThreadPool.submit(()->{
        try {
          mapper.updateBaseItems(list);
        } finally {
          latch.countDown();
        }
      });
    }
  }

  /**
   * 分片插入基础价目表目录
   * @param datas 集合
   */
  private void insertBaseTariffCategoryBySplices(List<List<BaseTariffCategory>> datas,CountDownLatch latch) throws InterruptedException {
    for (List<BaseTariffCategory> data : datas) {
      importExcelThreadPool.submit(()->{
        try {
          baseTariffCategoryMapper.insertBaseTariffCategoryList(data);
        } finally {
          latch.countDown();
        }
      });
    }
  }

  /**
   * 分片插入门诊价目表
   * @param datas 门诊价目表集合
   * @throws InterruptedException 异常
   */
  private void insertClinicTariffBySplices(List<List<ClinicTariff>> datas, CountDownLatch latch) throws InterruptedException {
    for (List<ClinicTariff> list : datas) {
      importExcelThreadPool.submit(()->{
        try {
          clinicTariffBiz.insertEntities(list);
        } finally {
          latch.countDown();
        }
      });
    }
  }

  /**
   * 门诊价目表实体构造器
   * @param baseTariffs 基础价目表集合
   * @param orgInfos 门诊信息集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回门诊价目表结果集
   * @throws InterruptedException 异常
   */
  private List<Future> clinicTariffBatchBuilder(
          List<BaseTariff> baseTariffs,
          List<OrganizationInfoDetail> orgInfos,
          String userId,
          String name) throws InterruptedException {
    List<Future> futureClinicTariffs = new ArrayList<>();
    CountDownLatch clinicTariffLatch = new CountDownLatch(baseTariffs.size()*orgInfos.size());
    for (BaseTariff baseTariff : baseTariffs) {
      for (OrganizationInfoDetail organizationInfoDetail : orgInfos) {
        futureClinicTariffs.add(importExcelThreadPool.submit(()->{
          try {
            if (baseTariff != null) {
              // 新增门诊节目表
              ClinicTariff clinicTariff = insertClinicTariff(organizationInfoDetail.getId(), baseTariff.getId(), baseTariff.getPrice(), userId, name);
              return clinicTariff;
            }
            return null;
          } finally {
            clinicTariffLatch.countDown();
          }
        }));
      }
    }
    clinicTariffLatch.await();
    return futureClinicTariffs;
  }

  /**
   * 将List<Future<T>>转化为List
   * @param list 任务结果集合
   * @return 返回对象集合
   */
  private synchronized List future2List(List<Future> list) {
    List container = Lists.newArrayListWithCapacity(list.size());
    if (StringHelper.isNotEmpty(list)) {
      list.forEach(future2List->{
        try {
          if (future2List != null) {
            Object o = future2List.get();
            if (null != o && !container.contains(o)) {
              container.add(o);
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
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
    itemEntity.setAdjust(true);
    itemEntity.setAchie(true);
    itemEntity.setInservice(true);

    if (null == itemResult) {
      // 新增价目表
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

    itemEntity = new BaseTariff();
    itemEntity.setTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
    itemEntity.setItemNumber(itemNumber);
    itemEntity.setEnglishName(englishName);
    itemEntity.setPrice(price);
    itemEntity.setUnit(unit);

    if (null != itemResult) {
      // 更新价目表
      itemEntity.setId(itemResult.getId());
      itemEntity.setUpdId(Integer.valueOf(userId));
      itemEntity.setUpdName(name);
      return itemEntity;
    }
    return null;
  }



  /**
   * 校验价目表参数合法性
   * @param categoryName
   * @param categoryNumber
   * @param dataNum
   * @param failureMsg
   * @param itemName
   * @param itemNumber
   */
  private void checkItemParams(String categoryName,
                               String categoryNumber,
                               AtomicInteger dataNum,
                               StringBuilder failureMsg,
                               String itemName,
                               String itemNumber,
                                  String userId,
                                  String name) {
    // 获取价目表分类ID
    Integer categoryId = getCategoryId(categoryName, categoryNumber,userId,name);
    // 校验价目表参数合法性
    checkItemParams(dataNum.get(), failureMsg, itemName, itemNumber, categoryId);
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
  private BaseTariff addOrUpdBaseItemAndClinicItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId,
      String userId,
      String name) {
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
      itemEntity.setCrtId(Integer.valueOf(userId));
      itemEntity.setCrtName(name);
//      itemId = itemEntity.getId();
//      if (i > 0) {
//        rabbitMqServiceFeign.sendMessage(itemId, 0, 0, BaseTariffInfo);
//      }
    } else {
      // 更新价目表
      itemEntity.setId(itemResult.getId());
      itemEntity.setUpdId(Integer.valueOf(userId));
      itemEntity.setUpdName(name);
      itemEntity.setUpdTime(new Date(System.currentTimeMillis()));
//      int i = mapper.updateByPrimaryKeySelective(itemEntity);
//      itemId = itemResult.getId();
//      if (i > 0) {
////        rabbitMqServiceFeign.sendMessage(itemId, 0, 1, BaseTariffInfo);
//      }
    }
    return itemEntity;
  }

  /**
   * 添加门诊价目表
   * @param orgId
   * @param itemId
   * @param price
   * @param userId
   * @param name
   */
  public ClinicTariff insertClinicTariff(Integer orgId, Integer itemId,BigDecimal price, String userId, String name) {
    ClinicTariff clinicItem = new ClinicTariff();
    clinicItem.setClinicId(orgId);
    clinicItem.setTariffId(itemId);
    ClinicTariff clinicTariff = clinicTariffBiz.selectOne(clinicItem);
    if (null == clinicTariff) {
      clinicItem.setPrice(price);
      clinicItem.setCrtId(Integer.valueOf(userId));
      clinicItem.setCrtName(name);
//      clinicTariffBiz.insertSelective(clinicItem);
      return clinicItem;
    }
    return null;
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
  private BaseTariff addOrUpdBaseItem(
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

    itemEntity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(itemName));
    itemEntity.setEnglishName(englishName);
    itemEntity.setPrice(price);
    itemEntity.setUnit(unit);

    if (null == itemResult) {
      // 新增价目表
      itemEntity.setCrtId(Integer.valueOf(userId));
      itemEntity.setCrtName(name);
//      int i = mapper.insertSelective(itemEntity);
//      if (i > 0) {
//        rabbitMqServiceFeign.sendMessage(itemEntity.getId(), 0, 0, BaseTariffInfo);
//      }
    } else {
      // 更新价目表
      Integer id = itemResult.getId();
      itemEntity.setId(id);
      itemEntity.setUpdId(Integer.valueOf(userId));
      itemEntity.setUpdName(name);
//      int i = mapper.updateByPrimaryKeySelective(itemEntity);
//      if (i > 0) {
//        rabbitMqServiceFeign.sendMessage(id, 0, 1, BaseTariffInfo);
//      }
    }
    return itemEntity;
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
  private Integer getCategoryId(String categoryName, String categoryNumber,String userId,String name) {
    BaseTariffCategory categoryEntity = new BaseTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    BaseTariffCategory categoryResult = baseTariffCategoryMapper.selectOne(categoryEntity);
    Integer categoryId = categoryResult.getId();
    return categoryId;
  }

  /**
   * 基础价目表目录构建起
   * @param categoryName 目录名称
   * @param categoryNumber 目录编号
   * @param userId 用户名ID
   * @param name 用户名
   * @return 返回基础价目表目录
   */
  private BaseTariffCategory baseTariffCategoryBuilder(String categoryName, String categoryNumber,String userId,String name) {
    BaseTariffCategory categoryEntity = new BaseTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    BaseTariffCategory categoryResult = baseTariffCategoryMapper.selectOne(categoryEntity);
    if (null == categoryResult) {
      // 新增价目表分类
      categoryEntity.setCrtId(Integer.valueOf(userId));
      categoryEntity.setCrtName(name);
      categoryEntity.setInservice(true);
      return categoryEntity;
    }
    return null;
  }

  /**
   * 异步构建基础价目表实体集合
   * @param models 基础价目表集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回结果集合List<Future<BaseTariffCategory>>
   * @throws InterruptedException 异常
   */
  private List<Future> syncBaseTariffCategoryBuilder(List<BaseTariffImportModel> models,String userId,String name,CountDownLatch latch) throws InterruptedException {
    List<Future> futureBaseTariffCategory = new ArrayList<>();
    CountDownLatch latch_1 = new CountDownLatch(models.size());
    for (BaseTariffImportModel model : models) {
      futureBaseTariffCategory.add(importExcelThreadPool.submit(()->{
        try {
          BaseTariffCategory baseTariffCategory = baseTariffCategoryBuilder(model.getTariffCategoryName(), model.getTariffCategoryNumber(), userId, name);
          return baseTariffCategory;
        } finally {
          latch.countDown();
        }
      }));
    }
    return futureBaseTariffCategory;
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
      throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
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
        throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
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
        throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
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
      throw new ClientServiceException("修改失败，ID为'" + id + "的价目表不存在！", QUERY_RESULT_INVALID);
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
}
