package com.yunya.modules.patient_central.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.patient_central.domain.form.PatientOriginForm;
import com.yunya.feign.patient_central.domain.model.PatientOriginModel;
import com.yunya.feign.patient_central.domain.query.OriginTypeQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientAndStaffListInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginTreeVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.patient_central.PatientOriginLog;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginLogMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 简单介绍:</br> 患者来源业务层
 *
 * @author: WY
 * @date 2020/8/5 13:05
 * @description: 患者来源管理业务层
 * @since: 1.0.0
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class PatientOriginBiz extends BaseBiz<PatientOriginMapper, PatientOrigin> {
  private Logger log = LoggerFactory.getLogger(PatientOriginBiz.class);
  /** 注入患者来源Mapper */
  @Autowired private PatientOriginMapper patientOriginMapper;

  /** 注入服务 */
  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入患者信息Mapper */
  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

  /** 注入服务 */
  @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

  /** 获取患者服务端口号 */
  @Value("${codeUrl.url}")
  private String servePrort;

  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

  @Resource
  private PatientOriginLogMapper patientOriginLogMapper;


  /**
   * 患者原来添加
   *
   * @param patientOriginModel 患者来源分类添加模板类
   */
  public ResponseResult add(PatientOriginModel patientOriginModel) {
    PatientOrigin patientOrigin = new PatientOrigin();
    BeanUtils.copyProperties(patientOriginModel, patientOrigin);
    if (patientOrigin.getLimitEndDate() != null){
      patientOrigin.setLimitEndDate(getEndTimeOfDate(patientOrigin.getLimitEndDate()));
    }
    PatientOrigin patientOriginv =
        patientOriginMapper.findPatientOriginByName(patientOrigin.getName());
    if (patientOriginv != null) {
      return ResponseUtil.fail(OperationCodeConstants.DATA_EXIST, "该患者来源已添加", patientOriginv);
    }

    if (patientOrigin.getParentId() == null) {
      Integer maxiType = mapper.selectTypeMaximum();
      if (maxiType >= 0) { // 查询患者来源type字典最大值
        patientOrigin.setOriginType(maxiType + 1);
        patientOrigin.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientOrigin.setCrtName(BaseContextHandler.getName());
        mapper.insertSelective(patientOrigin);
        remoteRabbitMqServiceFeign.sendMessage(
                patientOrigin.getId(), 0, MsgCategoryEnum.BasePatientOrigin);
      }
    } else {
      PatientOrigin patientOrig =
          patientOriginMapper.findPatientOriginByParentId(patientOrigin.getParentId());
      if (patientOrig == null) {
        return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "未找到父级来源", "");
      }
      patientOrigin.setOriginType(patientOrig.getOriginType());
      patientOrigin.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientOrigin.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(patientOrigin);
      remoteRabbitMqServiceFeign.sendMessage(
              patientOrigin.getId(), 0, MsgCategoryEnum.BasePatientOrigin);
    }
    return ResponseUtil.success();
  }


  private Date getEndTimeOfDate(Date endDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(endDate);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 59);
    Date endTime = calendar.getTime();
    return endTime;
    }

  /**
   * 查询患者来源树状结构列表
   *
   * @return List<PatientOriginTreeVo>
   */
  public List<PatientOriginTreeVo> initPatientOriginTree() {
    List<PatientOriginInfoVo> patientOriginInfoVos = patientOriginMapper.findAll();
    if (StringHelper.isNotEmpty(patientOriginInfoVos)){
      patientOriginInfoVos.forEach(
              patientOriginInfoVo -> {
                if (patientOriginInfoVo.getSourceAttribute() != null){
                  DictionaryItem dictionaryItemById = remoteSystemServiceFeign.findDictionaryItemById(patientOriginInfoVo.getSourceAttribute());
                  if (dictionaryItemById != null){
                    patientOriginInfoVo.setSourceAttributeName(dictionaryItemById.getName());
                  }
                }
                if (patientOriginInfoVo.getParentId() != 0){
                    patientOriginInfoVo.setCodeUrl(getCodeUrl(patientOriginInfoVo));
                }
                if (patientOriginInfoVo.getTimeLimit() == 0) {
                    patientOriginInfoVo.setLimitStartDate(null);
                    patientOriginInfoVo.setLimitEndDate(null);
                }
              }
      );
    }
    return initTree(patientOriginInfoVos);
  }

  public String getCodeUrl(PatientOriginInfoVo patientOriginInfoVo){
    String codeUrl = servePrort+"/#/register?"+"originType="+patientOriginInfoVo.getOriginType()+"&originId="+patientOriginInfoVo.getId();
    return codeUrl;
  }

  /**
   * 构建组织树列表
   *
   * @param vos 组织列表
   * @return List<PatientOriginTreeVo>
   */
  private List<PatientOriginTreeVo> initTree(List<PatientOriginInfoVo> vos) {
    List<PatientOriginTreeVo> trees = new ArrayList<>();
    if (vos.size() > 0) {
      PatientOriginTreeVo node;
      for (PatientOriginInfoVo vo : vos) {
        node = new PatientOriginTreeVo();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
  }

  /**
   * 患者来源修改
   *
   * @param patientOriginForm 患者来源修改Form
   * @return ResponseResult
   */
  public ResponseResult update(PatientOriginForm patientOriginForm) {
    PatientOrigin patientOrigin = mapper.selectByPrimaryKey(patientOriginForm.getId());
    if (patientOrigin != null) {
      if (patientOrigin.getAllowOperate() == false) {
        return ResponseUtil.fail(
            OperationCodeConstants.OBJECT_EDIT_FAIL, "该患者来源不可编辑", patientOrigin);
      }
      patientOrigin.setInservice(patientOriginForm.getInservice());
      if (patientOriginForm.getSourceAttribute() != null){
        patientOrigin.setSourceAttribute(patientOriginForm.getSourceAttribute());
      }
      if (patientOriginForm.getTimeLimit() == null){
        patientOrigin.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientOrigin.setUpdName(BaseContextHandler.getName());
        patientOrigin.setUpdTime(new Date());
        mapper.updateByPrimaryKeySelective(patientOrigin);
        return ResponseUtil.success();
      }
      patientOrigin.setTimeLimit(patientOriginForm.getTimeLimit());
      patientOrigin.setName(patientOriginForm.getName());
      patientOrigin.setEnglishName(patientOriginForm.getEnglishName());
      patientOrigin.setLimitStartDate(patientOriginForm.getLimitStartDate());
      if (patientOriginForm.getTimeLimit() != null && patientOriginForm.getTimeLimit() == 1) {
        patientOrigin.setLimitEndDate(getEndTimeOfDate(patientOriginForm.getLimitEndDate()));
      } else {
        patientOrigin.setLimitEndDate(patientOrigin.getLimitEndDate());
      }
      patientOrigin.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientOrigin.setUpdName(BaseContextHandler.getName());
      patientOrigin.setUpdTime(new Date());
      mapper.updateByPrimaryKey(patientOrigin);
      remoteRabbitMqServiceFeign.sendMessage(
              patientOrigin.getId(), 1, MsgCategoryEnum.BasePatientOrigin);
    } else {
      return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "未找到患者来源", patientOrigin);
    }
    return ResponseUtil.success();
  }

  /**
   * 删除患者来源
   *
   * @param id 患者来源id
   * @return ResponseResult
   */
  public ResponseResult deleteOriginById(Integer id) {
    PatientOrigin patientOriginv = mapper.selectByPrimaryKey(id);

    List<PatientBaseInfo> patientBaseInfoByOriginId = patientBaseInfoMapper.findPatientBaseInfoByOriginId(id);
    if (patientOriginv.getAllowOperate() == true && StringHelper.isNotEmpty(patientBaseInfoByOriginId)) {
      return ResponseUtil.fail(
          OperationCodeConstants.DELETE_NOT_ALLOW, "该患者来源已被患者关联，不允许删除！", patientOriginv);
    }
    mapper.deleteByPrimaryKey(id);
    remoteRabbitMqServiceFeign.sendMessage(
            id, 2, MsgCategoryEnum.BasePatientOrigin);
    return ResponseUtil.success();
  }

  /**
   * 模糊查询员工/老患者信息
   *
   * @param form 模糊查询员工和患者信息QueryForm
   * @return ResponseResult
   */
  public ResponseResult findPatientAndStaffListInfo(PatientAndStaffListInfoQueryForm form) {
    switch (form.getOriginType()) {
      case 1:
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setKeyWord(form.getName());
        model.setWhetherPage(false);
        return ResponseUtil.success(remoteSystemServiceFeign.findSysUserEmployeeInfoList(model));
      case 2:
        PatientLikeFinleQueryForm Patientmodel = new PatientLikeFinleQueryForm();
        Patientmodel.setCondition(form.getName());
        return ResponseUtil.success(patientBaseInfoMapper.findPatientByNameAndMobile(Patientmodel));
      default:
        break;
    }
    return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW, "PARAMETERS_IS_ILLEGAL", "");
  }

  /**
   * 获取符合条件的活动集合
   *
   * @param patientOrigin 患者来源
   * @return List<PatientOrigin>
   */
  public List<PatientOrigin> getPatientOriginList(PatientOrigin patientOrigin) throws ParseException {
    List<PatientOrigin> PatientOriginInfoList = mapper.findPatientOriginByTypt(patientOrigin);
    if (!StringHelper.isEmpty(PatientOriginInfoList)) {
      Iterator<PatientOrigin> PatientOriginIterator = PatientOriginInfoList.iterator();
      while (PatientOriginIterator.hasNext()) {
        PatientOrigin origin = PatientOriginIterator.next();
        if (origin.getSourceAttribute() != null){
          DictionaryItem dictionaryItem = remoteSystemServiceFeign.findDictionaryItemById(origin.getSourceAttribute());
          if (dictionaryItem != null){
            origin.setName(dictionaryItem.getName()+"-"+origin.getName());
          }
        }
        if (origin.getTimeLimit() == 1) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          Date parse = sdf.parse(sdf.format(new Date()));
          if (!DateUtil.isEffectiveDate(parse, origin.getLimitStartDate(), origin.getLimitEndDate())) {
            PatientOriginIterator.remove(); // 使用迭代器的删除方法删除
          }
        }
      }
    }
    return PatientOriginInfoList;
  }

  /**
   * 查询患者来源类型
   *
   * @return List<PatientOriginVo>
   */
  public List<PatientOriginVo> originalType() {
    return patientOriginMapper.originalType();
  }

  /**
   * 根据患者类型查询来源
   *
   * @param form 查询患者来源类型
   * @return List<PatientOrigin>
   */
  public List<PatientOrigin> findPatientOriginByTypt(OriginTypeQueryForm form) {
    PatientOrigin patientOrigin = new PatientOrigin();
    // 根据来源类型
    patientOrigin.setOriginType(form.getOriginType());
    // 获取符合条件的活动集合
    try {
      return getPatientOriginList(patientOrigin);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 迁移患者信息来源到患者来源变更日志表
   */
    public void moveOrigin() throws InterruptedException {
      List<PatientOriginLog> insertPatientOriginLogList = new ArrayList<>();
      List<PatientBaseInfo> patientBaseInfoList = patientBaseInfoMapper.selectOriginByOriginIdNotNull();
      for (PatientBaseInfo patientBaseInfo : patientBaseInfoList ) {
        PatientOriginLog patientOriginLog = new PatientOriginLog();
        patientOriginLog.setPatientId(patientBaseInfo.getId());
        patientOriginLog.setOriginType(patientBaseInfo.getOriginType());
        patientOriginLog.setOriginId(patientBaseInfo.getOriginId());
        patientOriginLog.setCrtId(patientBaseInfo.getCrtId());
        patientOriginLog.setCrtName(patientBaseInfo.getCrtName());
        patientOriginLog.setCrtTime(patientBaseInfo.getCrtTime());
        patientOriginLog.setUptId(patientBaseInfo.getUptId());
        patientOriginLog.setUpdName(patientBaseInfo.getUpdName());
        patientOriginLog.setUpdTime(patientBaseInfo.getUpdTime());
        insertPatientOriginLogList.add(patientOriginLog);
      }
      List<List<PatientOriginLog>> partitionLists = Lists.partition(insertPatientOriginLogList, 100);
      CountDownLatch countDownLatch = new CountDownLatch(partitionLists.size());
      long start = System.currentTimeMillis();
      for (List<PatientOriginLog> patientOriginLogList : partitionLists) {
      importExcelThreadPool.execute(
          () -> {
            try {
              patientOriginLogMapper.deleteList(patientOriginLogList);
              patientOriginLogMapper.insertList(patientOriginLogList);
              countDownLatch.countDown();
            } catch (Exception e) {
              log.info("患者来源迁移入库异常",e);
              e.printStackTrace();
            }
          });
      }
      countDownLatch.await();
      long end = System.currentTimeMillis();
      log.info("患者信息患者来源信息迁移入库成功，时长：[{}]秒", (end - start) / 1000);
    }

  /**
   * 查询患者来源类型及其子类型
   *
   * @return
   */
  public List<PatientOriginVo> findoriginalTypeAndChildren() {
    List<PatientOriginVo> result = originalType();
    if (StringHelper.isNotEmpty(result)) {
      result.forEach(vo-> {
        PatientOrigin query = new PatientOrigin();
        // 根据来源类型
        query.setOriginType(vo.getOriginType());
        try {
            vo.setChildren(getPatientOriginList(query));
        } catch (ParseException e) {
          log.error("findoriginalTypeAndChildren error",e);
        }
      });
    }
    return result;
  }
}
