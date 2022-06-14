package com.yunya.modules.treatment.biz;

import com.github.pagehelper.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.vo.GoodsVO;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.domain.form.*;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.query.BaseOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.tariff.*;
import com.yunya.modules.treatment.mapper.BaseOralTariffCategoryMapper;
import com.yunya.modules.treatment.mapper.BaseOralTariffMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.*;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;

/**
 * 描述: 商品商品业务层
 *
 * @author GaoLuding
 * @create 2020-05-20 11:00
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseOralTariffBiz extends BaseBiz<BaseOralTariffMapper, BaseOralTariff> {

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 消息对列 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 系统服务远程调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 商品分类 */
  @Autowired private BaseOralTariffCategoryMapper baseOralTariffCategoryMapper;
  /** 商品商品操作记录 */
  @Autowired private BaseOralTariffHistoryBiz baseOralTariffHistoryBiz;
  /** 门诊商品商品 */
  @Autowired private ClinicOralTariffBiz clinicOralTariffBiz;
  /** 线程池 */
  @Resource(name = "treatmentThreadPool")
  private ExecutorService importExcelThreadPool;

  /**
   * 根据ID查询商品商品信息（包含门诊商品商品价格信息）
   *
   * @param id 商品商品ID
   * @return
   */
  public BaseOralTariffInfoVO findBaseOralTariffInfoById(Integer id) {
    BaseOralTariffInfoVO resultData = mapper.selectBaseOralTariffInfoById(id);
    if (null != resultData) {
      ClinicOralTariff clinicOralTariff = new ClinicOralTariff();
      clinicOralTariff.setOralTariffId(id);
      List<ClinicOralTariff> clinicTariffs = clinicOralTariffBiz.selectList(clinicOralTariff);
      List<ClinicItemPriceVO> itemInfos = new ArrayList<>();
      if (StringHelper.isNotEmpty(clinicTariffs)) {
        clinicTariffs.forEach(
            oralTariff -> {
              ClinicItemPriceVO clinicItem = new ClinicItemPriceVO();
              clinicItem.setClinicItemId(oralTariff.getId());
              Integer clinicId = oralTariff.getClinicId();
              clinicItem.setOrgId(clinicId);
              OrganizationInfo organizationInfo = systemServiceFeign.findOrgInfoByOrgId(clinicId);
              if (organizationInfo != null) {
                clinicItem.setOrgName(organizationInfo.getAbbreviation());
              }
              clinicItem.setItemId(oralTariff.getOralTariffId());
              clinicItem.setClinicItemPrice(oralTariff.getPrice());
              clinicItem.setItemInservice(oralTariff.getInservice());
              itemInfos.add(clinicItem);
            });
      }
      resultData.setClinicItemInfos(itemInfos);
    }
    return resultData;
  }

  /**
   * 根据条件查询商品商品列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<BaseOralTariffVO> findList(BaseOralTariffQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<BaseOralTariffVO> resultList = mapper.selectBaseOralTariffList(queryForm);
    resultList.forEach(
        baseOralTariffVO -> {
          BigDecimal price = baseOralTariffVO.getPrice();
          BigDecimal bigDecimal = price.setScale(2, BigDecimal.ROUND_HALF_UP);
          baseOralTariffVO.setPrice(bigDecimal);
        });
    return new PageInfo<>(resultList);
  }

  /**
   * 根据商品表分类ID生成商品表编号
   *
   * @param oralTariffCategoryId 商品表分类ID
   * @return String - 商品表编号
   */
  public String generateBaseOralTariffNumber(Integer oralTariffCategoryId) {
    BaseOralTariffCategory oralTariffCategory =
        baseOralTariffCategoryMapper.selectByPrimaryKey(oralTariffCategoryId);
    if (oralTariffCategory == null) {
      throw new ClientServiceException("请选择正确的商品表分类进行新增！", PARAMETERS_IS_ILLEGAL);
    }
    String categoryNumber = oralTariffCategory.getNumber().substring(0, 3);
    String number = mapper.selectMaxBaseOralTariffNumber(oralTariffCategoryId, categoryNumber);
    return categoryNumber + String.format("%03d", Integer.parseInt(number) + 1);
  }

  /**
   * 新增商品商品
   *
   * @param model 新增参数
   */
  public void add(BaseOralTariffModel model) {
    Integer categoryId = model.getOralTariffCategoryId();
    BaseOralTariffCategory oralTariffCategory =
        baseOralTariffCategoryMapper.selectByPrimaryKey(categoryId);
    if (null == oralTariffCategory) {
      throw new ClientServiceException(
          "新增失败，ID为'" + categoryId + "'的商品分类不存在，请选择正确的商品分类！", QUERY_RESULT_INVALID);
    }

    BaseOralTariff entity = new BaseOralTariff();
    String name = model.getName();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，商品商品名称'" + name + "'已存在！", NAME_IS_OCCUPIED);
    }

    entity = new BaseOralTariff();
    String number = model.getItemNumber();
    entity.setItemNumber(number);
    count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，商品商品编号'" + number + "'已存在！", NAME_IS_OCCUPIED);
    }

    String categoryNumber = oralTariffCategory.getNumber().substring(0, 3);
    String itemNumber = number.substring(0, 3);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException("新增失败，商品商品编号前3位与商品分类编号前3位不同！", PARAMETERS_IS_ILLEGAL);
    }

    Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
    String crtName = BaseContextHandler.getName();

    BeanUtils.copyProperties(model, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setCrtId(crtId);
    entity.setCrtName(crtName);
    int i = mapper.insertSelective(entity);

    // 添加门诊商品商品
    Integer itemId = entity.getId();
    List<ClinicItemPriceModel> clinicItemPriceModels = model.getClinicItemPriceModels();

    // 保存门诊商品商品
    if (StringHelper.isNotEmpty(clinicItemPriceModels)) {
      saveClinicOralTariff(model.getPrice(), itemId, clinicItemPriceModels);
    }

    // 保存商品商品新增历史记录
    BaseOralTariffHistory baseOralTariffHistory = new BaseOralTariffHistory();
    baseOralTariffHistory.setOralTariffCategoryId(categoryId);
    baseOralTariffHistory.setOralTariffId(itemId);
    baseOralTariffHistory.setName(name);
    baseOralTariffHistory.setItemNumber(number);
    baseOralTariffHistory.setCrtId(crtId);
    baseOralTariffHistory.setCrtName(crtName);
    baseOralTariffHistoryBiz.insertSelective(baseOralTariffHistory);
    // 发送消息同步商品表
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(itemId, 1, 0, BaseTariffInfo);
    }
  }

  /**
   * 保存门诊商品商品
   *
   * @param price 原价
   * @param itemId 商品商品ID
   * @param clinicItemPriceModels 门诊商品商品价格列表
   */
  private void saveClinicOralTariff(
      BigDecimal price, Integer itemId, List<ClinicItemPriceModel> clinicItemPriceModels) {
    ClinicOralTariff clinicOralTariff;
    if (StringHelper.isNotEmpty(clinicItemPriceModels)) {
      for (ClinicItemPriceModel itemPriceModel : clinicItemPriceModels) {
        clinicOralTariff = new ClinicOralTariff();
        clinicOralTariff.setClinicId(itemPriceModel.getOrgId());
        clinicOralTariff.setOralTariffId(itemId);
        clinicOralTariff.setPrice(itemPriceModel.getItemPrice());
        clinicOralTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        clinicOralTariff.setCrtName(BaseContextHandler.getName());
        clinicOralTariffBiz.insertSelective(clinicOralTariff);
      }
    } else {
      OrganizationModel orgModel = new OrganizationModel();
      orgModel.setTypes(new Byte[] {2});
      orgModel.setWhetherPage(false);
      List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoList(orgModel);
      if (StringHelper.isNotEmpty(orgInfos)) {
        for (OrganizationInfoDetail orgInfo : orgInfos) {
          clinicOralTariff = new ClinicOralTariff();
          clinicOralTariff.setClinicId(orgInfo.getId());
          clinicOralTariff.setOralTariffId(itemId);
          clinicOralTariff.setPrice(price);
          clinicOralTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
          clinicOralTariff.setCrtName(BaseContextHandler.getName());
          clinicOralTariffBiz.insertSelective(clinicOralTariff);
        }
      }
    }
  }

  /**
   * 修改商品商品
   *
   * @param id 商品商品ID
   * @param form 修改参数
   */
  public void modify(Integer id, BaseOralTariffForm form) {
    BaseOralTariffInfoVO resultData = findBaseOralTariffInfoById(id);
    if (null == resultData) {
      throw new ClientServiceException("修改失败，ID为'" + id + "的商品商品不存在！", QUERY_RESULT_INVALID);
    }
    String resultDataName = resultData.getName();
    String resultDataItemNumber = resultData.getItemNumber();
    String resultDataOralTariffCategoryNumber = resultData.getOralTariffCategoryNumber();

    BaseOralTariff entity = new BaseOralTariff();
    String name = form.getName();
    if (!resultDataName.equals(name)) {
      entity = new BaseOralTariff();
      entity.setName(name);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException("修改失败，名称为'" + name + "'的商品商品已存在！", OBJECT_EDIT_FAIL);
      }
    }

    String number = form.getItemNumber();
    if (!resultDataItemNumber.equals(number)) {
      entity = new BaseOralTariff();
      entity.setItemNumber(number);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException("修改失败，商品商品编号'" + number + "'已存在！", NAME_IS_OCCUPIED);
      }
    }

    String categoryNumber = resultDataOralTariffCategoryNumber.substring(0, 3);
    String itemNumber = number.substring(0, 3);
    if (!categoryNumber.equals(itemNumber)) {
      throw new ClientServiceException("修改失败，商品商品编号前3位与商品分类编号前3位不同！", PARAMETERS_IS_ILLEGAL);
    }

    BeanUtils.copyProperties(form, entity);
    entity.setPinyin(HanyuPinyinHelper.getFirstLettersLo(name));
    entity.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setUpdName(BaseContextHandler.getName());
    entity.setId(id);
    int i = mapper.updateByPrimaryKeySelective(entity);

    List<ClinicItemPriceForm> clinicItemPriceForms = form.getClinicItemPriceForms();

    // 更新门诊商品商品
    updateClinicOralTariff(id, clinicItemPriceForms);

    // 保存商品商品变更记录
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
    // 发送消息同步中间表商品信息
    if (i > 0) {
      redisUtils.delete(1 + REDIS_KEY_ITEM_INFO + id);
      rabbitMqServiceFeign.sendMessage(id, 1, 1, BaseTariffInfo);
    }
  }

  /**
   * 更新门诊商品商品
   *
   * @param itemId 商品商品ID
   * @param clinicItemPriceForms 修改门诊商品商品
   */
  private void updateClinicOralTariff(
      Integer itemId, List<ClinicItemPriceForm> clinicItemPriceForms) {
    ClinicOralTariff clinicOralTariff = new ClinicOralTariff();
    clinicOralTariff.setOralTariffId(itemId);
    clinicOralTariffBiz.delete(clinicOralTariff);
    if (StringHelper.isNotEmpty(clinicItemPriceForms)) {
      clinicItemPriceForms.forEach(
          form -> {
            clinicOralTariff.setClinicId(form.getOrgId());
            clinicOralTariff.setPrice(form.getItemPrice());
            clinicOralTariff.setInservice(form.getItemInservice());
            clinicOralTariff.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicOralTariff.setCrtName(BaseContextHandler.getName());
            clinicOralTariffBiz.insertSelective(clinicOralTariff);
          });
    }
  }

  /**
   * 根据ID删除商品商品
   *
   * @param oralTariffId 商品商品ID
   */
  public void delete(Integer oralTariffId) {
    ClinicOralTariff entity = new ClinicOralTariff();
    entity.setOralTariffId(oralTariffId);
    Long count = clinicOralTariffBiz.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "商品商品删除失败，ID为" + oralTariffId + "'的商品商品已被关联！", DELETE_NOT_ALLOW);
    }
    int i = mapper.deleteByPrimaryKey(oralTariffId);
    BaseOralTariffHistory historyEntity = new BaseOralTariffHistory();
    historyEntity.setOralTariffId(oralTariffId);
    baseOralTariffHistoryBiz.delete(historyEntity);
    // 发送消息同步中间表商品表信息
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(oralTariffId, 1, 2, BaseTariffInfo);
    }
  }

  /**
   * 一键启用禁用基础商品表
   *
   * @param id 商品表ID
   * @param switchType 开关状态
   */
  public void operateBaseOralTariffStatus(Integer id, Boolean switchType) {
    BaseOralTariff oralTariff = mapper.selectByPrimaryKey(id);
    if (oralTariff == null) {
      throw new ClientServiceException("操作失败，商品表不存在！", PARAMETERS_IS_ILLEGAL);
    }
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String userName = BaseContextHandler.getName();
    if (switchType) {
      clinicOralTariffBiz.enableClinicOralTariffByTariffId(id, userId, userName);
    } else {
      clinicOralTariffBiz.disableClinicOralTariffByTariffId(id, userId, userName);
    }
    oralTariff.setInservice(switchType);
    oralTariff.setUpdId(userId);
    oralTariff.setUpdName(userName);
    mapper.updateByPrimaryKeySelective(oralTariff);
  }

  /**
   * 导入商品商品列表
   *
   * @param excelFile 导入文件
   * @return string
   */
  public String importExcel(MultipartFile excelFile) throws Exception {
    ExcelUtil<BaseOralTariffImportModel> excelUtil =
        new ExcelUtil<>(BaseOralTariffImportModel.class);
    List<BaseOralTariffImportModel> models =
        Collections.synchronizedList(excelUtil.importExcel(excelFile.getInputStream()));
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException("导入失败，导入的商品商品数据不能为空！", PARAM_NOT_ALLOW_EMPTY);
    }

    // 初始化参数、常量
    int size = models.size();
    AtomicInteger dataNum = new AtomicInteger(0);
    StringBuilder failureMsg = new StringBuilder();
    CountDownLatch stepLatch_1 = new CountDownLatch(size);
    // 1异步校验商品表分类参数合法性
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

    // 2.添加基础商品表分类
    // 2-1.异步构建基础商品表目录实体集合
    String userId = BaseContextHandler.getUserID();
    String name = BaseContextHandler.getName();
    CountDownLatch stepLatch_2 = new CountDownLatch(size);
    List<Future> futures = syncBaseOralTariffCategoryBuilder(models, userId, name, stepLatch_2);
    stepLatch_2.await();
    // 2-2.将List<Future<BaseOralTariffCategory>>转化为List<BaseOralTariffCategory>
    List<BaseOralTariffCategory> list = future2List(futures);
    // 根据分类编号去重
    Set<BaseOralTariffCategory> insertBaseOralTariffCategories =
        new TreeSet<>(Comparator.comparing(BaseOralTariffCategory::getNumber));
    Set<BaseOralTariffCategory> updateBaseOralTariffCategories =
        new TreeSet<>(Comparator.comparing(BaseOralTariffCategory::getNumber));
    list.forEach(
        baseOralTariffCategory -> {
          if (baseOralTariffCategory.getId() == null) {
            insertBaseOralTariffCategories.add(baseOralTariffCategory);
          } else {
            updateBaseOralTariffCategories.add(baseOralTariffCategory);
          }
        });

    // 2-3.将基础商品表分类数据异步分片的方式插入数据库
    List<BaseOralTariffCategory> insertCategoryList =
        new ArrayList<>(insertBaseOralTariffCategories);
    List<List<BaseOralTariffCategory>> insertCategories = Lists.partition(insertCategoryList, 1000);
    if (StringHelper.isNotEmpty(insertCategories)) {
      CountDownLatch stepLatch_3 = new CountDownLatch(insertCategories.size());
      insertBaseOralTariffCategoryBySplices(insertCategories, stepLatch_3);
      stepLatch_3.await();
    }

    // 2-4.将基础商品表分类数据异步分片的方式更新到数据库
    List<BaseOralTariffCategory> updateCategoryList =
        new ArrayList<>(updateBaseOralTariffCategories);
    // 2-3.将基础商品表分类数据异步分片的方式插入数据库
    List<List<BaseOralTariffCategory>> updateCategories = Lists.partition(updateCategoryList, 1000);
    if (StringHelper.isNotEmpty(updateCategories)) {
      CountDownLatch stepLatch_4 = new CountDownLatch(updateCategories.size());
      updateBaseOralTariffCategoryBySplices(updateCategories, stepLatch_4);
      stepLatch_4.await();
    }

    // 3.异步校验商品表参数合法性
    CountDownLatch stepLatch_5 = new CountDownLatch(size);
    synCheckItemParams(models, dataNum, failureMsg, stepLatch_5);
    stepLatch_5.await();

    // 4.异步更新基础商品表
    List<BaseOralTariff> baseOralTariffsUpdate =
        this.updateBaseOralTariffList(models, userId, name);
    // 4-1.向mq推送更新消息
    pushMsg2RabbitMq(baseOralTariffsUpdate, 1);

    // 4-2. 异步新增基础商品表
    List<BaseOralTariff> baseOralTariffsInsert =
        this.insertBaseOralTariffList(models, userId, name);
    // 4-3.向mq推送新增消息
    pushMsg2RabbitMq(baseOralTariffsInsert, 0);
    return "导入成功，本次共计导入:" + size + "条基础商品表数据！";
  }

  /**
   * 同步校验excel数据
   *
   * @param models 数据列表
   * @param dataNum 数据序号
   * @param failureMsg 失败信息
   */
  private List<Future> synCheckExcelData(
      List<BaseOralTariffImportModel> models,
      AtomicInteger dataNum,
      StringBuilder failureMsg,
      CountDownLatch latch) {
    dataNum.set(0);
    return models.stream()
        .map(
            model ->
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        dataNum.getAndIncrement();
                        String categoryNumber = model.getOralTariffCategoryNumber();
                        String categoryName = model.getOralTariffCategoryName();
                        String itemNumber = model.getItemNumber();
                        String itemName = model.getName();
                        String itemNumStr = itemNumber.substring(0, 3);
                        String categoryNumStr = categoryNumber.substring(0, 3);
                        // 校验分类编号与商品表编号前三位
                        checkItemNumTopThree(dataNum.get(), failureMsg, categoryNumStr, itemNumStr);
                        // 校验导入商品分类与数据库分类信息的编号、名称
                        checkItemCategoryWithDataBase(
                            dataNum.get(), failureMsg, categoryNumber, categoryName);
                        // 校验导入商品与数据库商品信息的编号、名称
                        checkItemNumAndNameWithDataBase(
                            dataNum.get(), failureMsg, itemNumber, itemName);
                      } finally {
                        latch.countDown();
                      }
                    }))
        .collect(Collectors.toList());
  }

  /**
   * 校验基础商品表编号与前三位
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param categoryStr 分类编号前三位
   * @param itemStr 商品编号前三位
   */
  private void checkItemNumTopThree(
      int dataNum, StringBuilder failureMsg, String categoryStr, String itemStr) {
    if (!categoryStr.equals(itemStr)) {
      failureMsg
          .append("数据错误，Excel表中第")
          .append(dataNum)
          .append("'条数据的商品编号前3位'")
          .append(itemStr)
          .append("'与商品分类编号前3位'")
          .append(categoryStr)
          .append("'不同！");
      // todo 将该行数据加入到错误数据集合中
      throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 校验导入商品分类与数据库分类信息的编号、名称
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param categoryNumber 商品分类编号
   * @param categoryName 商品分类名称
   */
  private void checkItemCategoryWithDataBase(
      int dataNum, StringBuilder failureMsg, String categoryNumber, String categoryName) {
    List<BaseOralTariffCategory> categoryView =
        baseOralTariffCategoryMapper.selectBaseOralTariffCategoryView();
    if (StringHelper.isNotEmpty(categoryView)) {
      categoryView.forEach(
          oralTariffCategory -> {
            String number = oralTariffCategory.getNumber();
            String name = oralTariffCategory.getName();
            if (number.equals(categoryNumber)) {
              if (!name.equals(categoryName)) {
                failureMsg
                    .append("数据错误，Excel表中第")
                    .append(dataNum)
                    .append("'条数据的商品分类编号与数据库一致但商品分类名称不一致");
                // todo 将该行数据加入到错误数据集合中
                throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
              }
            } else {
              if (name.equals(categoryName)) {
                failureMsg
                    .append("数据错误，Excel表中第")
                    .append(dataNum)
                    .append("'条数据的商品分类编号与数据库不一致但商品分类名称一致");
                // todo 将该行数据加入到错误数据集合中
                throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
              }
            }
          });
    }
  }

  /**
   * 异步构建基础商品表实体集合
   *
   * @param models 基础商品表集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回结果集合List<Future<BaseOralTariffCategory>>
   */
  private List<Future> syncBaseOralTariffCategoryBuilder(
      List<BaseOralTariffImportModel> models, String userId, String name, CountDownLatch latch) {
    return models.stream()
        .map(
            model ->
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        return baseOralTariffCategoryBuilder(
                            model.getOralTariffCategoryName(),
                            model.getOralTariffCategoryNumber(),
                            userId,
                            name);
                      } finally {
                        latch.countDown();
                      }
                    }))
        .collect(Collectors.toList());
  }

  /**
   * 校验导入商品与数据库商品信息的编号、名称
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemNumber 商品编号
   * @param itemName 商品名称
   */
  private void checkItemNumAndNameWithDataBase(
      int dataNum, StringBuilder failureMsg, String itemNumber, String itemName) {
    List<BaseOralTariff> oralTariffView = mapper.selectBaseOralTariffView();
    if (StringHelper.isNotEmpty(oralTariffView)) {
      if (StringHelper.isNotEmpty(oralTariffView)) {
        oralTariffView.forEach(
            oralTariff -> {
              String number = oralTariff.getItemNumber();
              String name = oralTariff.getName();
              if (number.equals(itemNumber)) {
                if (!name.equals(itemName)) {
                  failureMsg
                      .append("数据错误，Excel表中第")
                      .append(dataNum)
                      .append("'条数据的商品编号与数据库一致但商品名称不一致");
                  // 将该行数据加入到错误数据集合中
                  throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
                }
              } else {
                if (name.equals(itemName)) {
                  failureMsg
                      .append("数据错误，Excel表中第")
                      .append(dataNum)
                      .append("'条数据的商品编号与数据库不一致但商品名称一致");
                  // 将该行数据加入到错误数据集合中
                  throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
                }
              }
            });
      }
    }
  }

  /**
   * 分片插入基础商品表目录
   *
   * @param datas 集合
   */
  private void insertBaseOralTariffCategoryBySplices(
      List<List<BaseOralTariffCategory>> datas, CountDownLatch latch) {
    datas.forEach(
        data ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    baseOralTariffCategoryMapper.insertBaseOralTariffCategoryList(data);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 分片更新基础商品表分类
   *
   * @param datas 待更新商品表分类列表
   * @param latch 计数器
   */
  private void updateBaseOralTariffCategoryBySplices(
      List<List<BaseOralTariffCategory>> datas, CountDownLatch latch) {
    datas.forEach(
        list ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    baseOralTariffCategoryMapper.updateBaseOralTariffCategoryList(list);
                  } finally {
                    latch.countDown();
                  }
                }));
  }

  /**
   * 异步校验商品表参数合法性
   *
   * @param models 导入数据集合
   */
  private void synCheckItemParams(
      List<BaseOralTariffImportModel> models,
      AtomicInteger dataNum,
      StringBuilder failureMsg,
      CountDownLatch stepLatch) {
    dataNum.set(0);
    models.forEach(
        model ->
            importExcelThreadPool.submit(
                () -> {
                  try {
                    // 校验商品表参数合法性
                    checkItemParams(
                        model.getOralTariffCategoryName(),
                        model.getOralTariffCategoryNumber(),
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
   * 异步新增或更新基础商品表
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @throws InterruptedException 异常
   * @return 返回插入的数据集合
   */
  private List<BaseOralTariff> updateBaseOralTariffList(
      List<BaseOralTariffImportModel> models, String userId, String name)
      throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(models.size());
    // 1. 先执行更新操作,构建更新实体集合
    List<Future> futures = this.syncUpdateBaseOralTariffBuilder(models, userId, name, latch);
    latch.await();
    if (StringHelper.isNotEmpty(futures)) {
      // 2.将List<Future>转化为List<BaseOralTariff>
      List<BaseOralTariff> list = future2List(futures);
      // 3.分片异步更新基础商品表
      List<List<BaseOralTariff>> partition = Lists.partition(list, 1000);
      CountDownLatch updateLatch = new CountDownLatch(partition.size());
      updateBaseOralTariffBySplices(partition, updateLatch);
      updateLatch.await();
      return list;
    }
    return new ArrayList<>();
  }

  /**
   * 异步新增基础商品表
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @throws InterruptedException 异常
   * @return 返回插入的数据集合
   */
  private List<BaseOralTariff> insertBaseOralTariffList(
      List<BaseOralTariffImportModel> models, String userId, String name)
      throws InterruptedException {
    // 1.构建更新实体集合
    CountDownLatch builderLatch = new CountDownLatch(models.size());
    List<Future> futuresInsert =
        this.syncInsertBaseOralTariffBuilder(models, userId, name, builderLatch);
    builderLatch.await();
    if (StringHelper.isNotEmpty(futuresInsert)) {
      // 2.将List<Future>转化为List<BaseOralTariff>
      List<BaseOralTariff> listInsert = future2List(futuresInsert);
      // 3.分片异步更新基础商品表
      List<List<BaseOralTariff>> partition = Lists.partition(listInsert, 1000);
      CountDownLatch insertLatch = new CountDownLatch(partition.size());
      insertBaseOralTariffBySplices(partition, insertLatch);
      insertLatch.await();
      return listInsert;
    }
    return new ArrayList<>();
  }

  /**
   * 分片异步插入基础商品表
   *
   * @param datas 基础商品表集合
   */
  private void insertBaseOralTariffBySplices(
      List<List<BaseOralTariff>> datas, CountDownLatch latch) {
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
   * 异步构建新增基础商品标集合
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回实体集合
   */
  private List<Future> syncInsertBaseOralTariffBuilder(
      List<BaseOralTariffImportModel> models, String userId, String name, CountDownLatch latch) {
    List<Future> future =
        models.stream()
            .map(
                model ->
                    importExcelThreadPool.submit(
                        () -> {
                          try {
                            Integer categoryId =
                                BaseOralTariffBiz.this.getOralCategoryId(
                                    model.getOralTariffCategoryName(),
                                    model.getOralTariffCategoryNumber());
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
   * @param baseOralTariffs 对应更新表的枚举，决定调用哪个中间表更新业务
   * @param operateType 操作类型：0-新增；1-更新；2-删除
   */
  private void pushMsg2RabbitMq(List<BaseOralTariff> baseOralTariffs, Integer operateType) {
    if (StringHelper.isNotEmpty(baseOralTariffs)) {
      switch (operateType) {
        case 0:
          baseOralTariffs.forEach(
              baseOralTariff -> {
                Integer itemId = baseOralTariff.getId();
                rabbitMqServiceFeign.sendMessage(itemId, 1, 0, BaseTariffInfo);
              });
          break;
        case 1:
          baseOralTariffs.forEach(
              baseOralTariff -> {
                Integer itemId = baseOralTariff.getId();
                rabbitMqServiceFeign.sendMessage(itemId, 1, 1, BaseTariffInfo);
              });
          break;
        default:
          break;
      }
    }
  }

  /**
   * 异步构建更新基础商品标集合
   *
   * @param models 导入数据集合
   * @param userId 用户ID
   * @param name 用户名
   * @return 返回实体集合
   */
  private List<Future> syncUpdateBaseOralTariffBuilder(
      List<BaseOralTariffImportModel> models, String userId, String name, CountDownLatch latch) {
    return models.stream()
        .map(
            model ->
                importExcelThreadPool.submit(
                    () -> {
                      try {
                        Integer categoryId =
                            getOralCategoryId(
                                model.getOralTariffCategoryName(),
                                model.getOralTariffCategoryNumber());
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
   * 分片异步更新基础商品表
   *
   * @param datas 基础商品表集合
   */
  private void updateBaseOralTariffBySplices(
      List<List<BaseOralTariff>> datas, CountDownLatch latch) {
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
   * 新增基础商品表或同步门诊商品表
   *
   * @param itemName 商品表名称
   * @param itemNumber 商品表编号
   * @param englishName 商品表英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 商品表分类ID
   */
  private BaseOralTariff insertItemAndClinicItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId,
      String userId,
      String name) {
    BaseOralTariff itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    BaseOralTariff itemResult = mapper.selectOne(itemEntity);
    if (null == itemResult) {
      // 新增商品表
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
   * 更新基础商品表或同步门诊商品表
   *
   * @param itemName 商品表名称
   * @param itemNumber 商品表编号
   * @param englishName 商品表英文名
   * @param unit 单位
   * @param price 单价
   * @param categoryId 商品表分类ID
   */
  private BaseOralTariff updateItemAndClinicItem(
      String itemName,
      String itemNumber,
      String englishName,
      String unit,
      BigDecimal price,
      Integer categoryId,
      String userId,
      String name) {
    BaseOralTariff itemEntity;
    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemEntity.setItemNumber(itemNumber);
    BaseOralTariff itemResult = mapper.selectOne(itemEntity);
    if (null != itemResult) {
      // 更新商品表
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
   * 校验商品表参数合法性
   *
   * @param categoryName 商品分类名称
   * @param categoryNumber 商品分类编号
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemName 商品名称
   * @param itemNumber 商品编号
   */
  private void checkItemParams(
      String categoryName,
      String categoryNumber,
      AtomicInteger dataNum,
      StringBuilder failureMsg,
      String itemName,
      String itemNumber) {
    // 获取商品表分类ID
    Integer categoryId = getOralCategoryId(categoryName, categoryNumber);
    // 校验商品表参数合法性
    checkOralItemParams(dataNum.get(), failureMsg, itemName, itemNumber, categoryId);
  }

  /**
   * 校验商品表参数合法性
   *
   * @param dataNum 当前行
   * @param failureMsg 错误信息
   * @param itemName 商品表名称
   * @param itemNumber 商品表编号
   * @param categoryId 商品表分类ID
   */
  private void checkOralItemParams(
      int dataNum,
      StringBuilder failureMsg,
      String itemName,
      String itemNumber,
      Integer categoryId) {
    // 初始化参数
    BaseOralTariff itemEntity;
    BaseOralTariff itemResult;

    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setName(itemName);
    itemResult = mapper.selectOne(itemEntity);
    if (null != itemResult) {
      String resultItemNumber = itemResult.getItemNumber();
      if (!resultItemNumber.equals(itemNumber)) {
        failureMsg
            .append("导入失败，Excel表中第")
            .append(dataNum)
            .append("'条数据的商品表编号'")
            .append(itemNumber)
            .append("'与系统中该商品表编号'")
            .append(resultItemNumber)
            .append("'不一致！");
        throw new ClientServiceException(failureMsg.toString(), PARAMETERS_IS_ILLEGAL);
      }
    }

    itemEntity = new BaseOralTariff();
    itemEntity.setOralTariffCategoryId(categoryId);
    itemEntity.setItemNumber(itemNumber);
    itemResult = mapper.selectOne(itemEntity);
    if (null != itemResult) {
      String itemResultName = itemResult.getName();
      if (!itemResultName.equals(itemName)) {
        failureMsg
            .append("导入失败，Excel表中第")
            .append(dataNum)
            .append("'条数据的商品表名称'")
            .append(itemName)
            .append("'与系统中该商品表名称'")
            .append(itemResultName)
            .append("'不一致！");
        throw new ClientServiceException(failureMsg.toString(), PARAM_NOT_ALLOW_EMPTY);
      }
    }
  }

  /**
   * 获取商品表分类ID
   *
   * @param categoryName 商品表分类名称
   * @param categoryNumber 商品表分类编号
   * @return
   */
  private Integer getOralCategoryId(String categoryName, String categoryNumber) {
    BaseOralTariffCategory categoryEntity = new BaseOralTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    BaseOralTariffCategory categoryResult = baseOralTariffCategoryMapper.selectOne(categoryEntity);
    return categoryResult.getId();
  }

  /**
   * 基础商品表目录构建起
   *
   * @param categoryName 目录名称
   * @param categoryNumber 目录编号
   * @param userId 用户名ID
   * @param name 用户名
   * @return 返回基础商品表目录
   */
  private BaseOralTariffCategory baseOralTariffCategoryBuilder(
      String categoryName, String categoryNumber, String userId, String name) {
    BaseOralTariffCategory categoryEntity = new BaseOralTariffCategory();
    categoryEntity.setName(categoryName);
    categoryEntity.setNumber(categoryNumber);
    BaseOralTariffCategory categoryResult = baseOralTariffCategoryMapper.selectOne(categoryEntity);
    if (null == categoryResult) {
      // 新增商品表分类
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
   * 导出商品商品列表
   *
   * @param response http响应
   * @param queryForm 查询参数
   */
  public void exportExcel(HttpServletResponse response, BaseOralTariffQueryForm queryForm)
      throws IOException {
    List<BaseOralTariffExportVO> resultList = mapper.selectExportBaseOralTariffList(queryForm);
    ExcelUtil<BaseOralTariffExportVO> excelUtil = new ExcelUtil<>(BaseOralTariffExportVO.class);
    excelUtil.exportExcel(response, resultList, "基础商品商品信息表");
  }

  /**
   * 根据多个商品表ID查询商品表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  public String findBaseOralNamesByIds(String[] ids) {
    if (StringHelper.isNotEmpty(ids)) {
      Joiner joiner = Joiner.on(",");
      return mapper.selectBaseOralNamesByIds(joiner.join(ids));
    }
    return null;
  }

  /**
   * 统一设置商品单价（多门诊）
   *
   * @param form 价格参数
   */
  public void uniteOralTariffPrice(TariffUnitePriceForm form) {
    Set<Integer> orgIds = form.getOrgIds();
    Set<TariffUniteModel> tariffUniteModels = form.getTariffUniteModels();
    if (CollectionUtils.isEmpty(orgIds)) {
      throw new ClientServiceException("请至少选择一个门诊", PARAM_NOT_ALLOW_EMPTY);
    }
    if (CollectionUtils.isEmpty(tariffUniteModels)) {
      throw new ClientServiceException("请至少选择一个商品项目", PARAM_NOT_ALLOW_EMPTY);
    }
    if (!CollectionUtils.isEmpty(orgIds) && !CollectionUtils.isEmpty(tariffUniteModels)) {
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      List<ClinicOralTariff> clinicOralTariffs = Lists.newArrayList();
      for (TariffUniteModel model : tariffUniteModels) {
        for (Integer orgId : orgIds) {
          ClinicOralTariff oralTariff = new ClinicOralTariff();
          oralTariff.setClinicId(orgId);
          oralTariff.setOralTariffId(model.getId());
          clinicOralTariffBiz.delete(oralTariff);
          oralTariff.setPrice(model.getPrice());
          oralTariff.setCrtId(userId);
          oralTariff.setCrtName(name);
          oralTariff.setUpdId(userId);
          oralTariff.setUpdName(name);
          oralTariff.setInservice(true);
          clinicOralTariffs.add(oralTariff);
        }
      }
      if (!CollectionUtils.isEmpty(clinicOralTariffs)) {
        clinicOralTariffBiz.batchInsert(clinicOralTariffs);
      }
    }
  }

  /**
   * 小程序查询商品列表
   * @param query: query
   * @return PageInfo<GoodsVO>
   */
  public PageInfo<GoodsVO> pageGoods(GoodsQuery query) {
    Page<BaseOralTariff> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
    Example example = new Example(BaseOralTariff.class);
    Example.Criteria criteria = example.createCriteria()
            .andEqualTo("isOnlineSale", true);
    Example.Criteria criteria1 = example.createCriteria();
    if (StringUtils.isNotBlank(query.getKeyword())) {
      criteria1.orLike("itemNumber", "%" + query.getKeyword() + "%");
      criteria1.orLike("name", "%" + query.getKeyword() + "%");
      criteria1.orLike("pinyin", "%" + query.getKeyword() + "%");
    }
    Example.Criteria criteria2 = example.createCriteria();
    if (Objects.nonNull(query.getProductCategoryId())) {
      criteria2.andEqualTo("oralTariffCategoryId", query.getProductCategoryId());
    }
    example.and(criteria1);
    example.and(criteria2);
    mapper.selectByExample(example);
    List<GoodsVO> collect = page.getResult().stream().map(t -> {
      String itemPic = t.getItemPic();
      GoodsVO goodsVO = new GoodsVO();
      goodsVO.setProductId(t.getId());
      goodsVO.setProductName(t.getName());
      goodsVO.setProductPic(StringUtils.isNotBlank(itemPic) ? itemPic.substring(0, itemPic.indexOf(",")) : null);
      goodsVO.setProductPrice(t.getPrice());
      goodsVO.setProductType(0);
      return goodsVO;
    }).collect(Collectors.toList());
    PageInfo<GoodsVO> pageInfo = new PageInfo<>(collect);
    pageInfo.setTotal(page.getTotal());
    pageInfo.setPageNum(page.getPageNum());
    return pageInfo;
  }

    public List<BaseOralTariff> listOnSaleOral(Collection<Integer> ids) {
      Example example = new Example(BaseOralTariff.class);
      example.selectProperties("id","itemNumber","name","unit","price","stock","itemPic","sale");
      Example.Criteria criteria = example.createCriteria().andIn("id", ids)
              .andEqualTo("isOnlineSale", true)
              .andEqualTo("inservice", true);
      return mapper.selectByExample(example);
    }
}
