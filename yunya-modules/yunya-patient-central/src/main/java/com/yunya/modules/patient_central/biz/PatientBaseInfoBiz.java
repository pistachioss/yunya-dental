package com.yunya.modules.patient_central.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.patient_central.domain.form.PatientPhotoForm;
import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientArchivesVo;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.constant.WoPlatformHeartbeat;
import com.yunya.modules.patient_central.mapper.*;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 简单介绍:</br> 患者基本信息业务层
 *
 * @author: WY
 * @date 2020/7/25 11:25
 * @description: 患者基本信息业务层（增删查改）
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientBaseInfoBiz extends BaseBiz<PatientBaseInfoMapper, PatientBaseInfo> {

  /** 注入redis */
  @Autowired private RedisUtils redisUtils;

  /** 注入患者信息Mapper */
  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

  /** 注入患者其他信息Mapper */
  @Autowired private PatientExtInfoMapper patientExtInfoMapper;

  /** 注入患者扩展信息Mapper */
  @Autowired private PatientExpInfoMapper patientExpInfoMapper;

  /** 注入系统服务 */
  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入wo平台对象 */
  @Autowired private WoPersonBiz woPersonBiz;

  /** 注入预付款Mapper */
  @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入会员卡对象 */
  @Autowired private PatientMemberInfoBiz patientMemberInfoBiz;

  /** 注入患者来源Mapper */
  @Autowired private PatientOriginMapper patientOriginMapper;

  /** 注入会员卡Mapper */
  @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

  /**
   * 通过患者id查询患者共用属性
   *
   * @param id 患者id
   * @return PatientPublicInfo
   */
  public PatientPublicInfoVo findPatientPublicInfoById(Integer id) {
    PatientPublicInfoVo patientPublicInfoVo =
        this.patientBaseInfoMapper.findPatientPublicInfoById(id);
    if (patientPublicInfoVo != null && patientPublicInfoVo.getMemberTypeId() != null) {
      MemberType memberType =
          this.remoteSystemServiceFeign.findMemberTypeById(patientPublicInfoVo.getMemberTypeId());
      if (memberType.getName() != null) {
        patientPublicInfoVo.setMemberCardName(memberType.getName());
      }
    }
    return patientPublicInfoVo;
  }

  /**
   * 查询患者是否存在
   *
   * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
   */
  public ResponseResult findUserExists(PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
    PatientBaseInfoVo patientBaseInfoVo;
    patientBaseInfoVo = patientBaseInfoMapper.findUserExists(patientBaseInfoQueryForm);
    if (patientBaseInfoVo != null) {
      return ResponseUtil.fail(OperationCodeConstants.DATA_EXIST, "添加失败,该用户已存在", patientBaseInfoVo);
    }
    PatientBaseInfoVo userExistsByMobile =
        patientBaseInfoMapper.findUserExistsByMobile(patientBaseInfoQueryForm.getMobile());
    if (userExistsByMobile != null) {
      return ResponseUtil.fail(OperationCodeConstants.DATA_EXIST, "该手机号已存在", userExistsByMobile);
    }
    return ResponseUtil.success();
  }

  /**
   * 添加患者信息
   *
   * @param patientBaseInfoModel 新增患者基本信息参数模型
   */
  public PatientBaseInfoVo addPatient(PatientBaseInfoModel patientBaseInfoModel) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);

    PatientOrigin patientOrigin =
        this.patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
    if (patientOrigin != null) {
      patientBaseInfo.setOriginType(patientOrigin.getOriginType());
    }

    patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
    patientBaseInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientBaseInfo.setCrtName(BaseContextHandler.getName());
    patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    // patientBaseInfo.setWoGuid(woPersonBiz.addWoPersonInput(patientBaseInfo.getName())); //
    // wo平台创建对应人员 返回人员Guid添加到数据库

    JSONObject object = new JSONObject();
    object.put("taskNo", "");
    object.put("interfaceName", "person/create");
    object.put("result", true);
    PersonModel person = new PersonModel();
    person.setName(patientBaseInfo.getName());
    object.put("person", person);
    redisUtils.set("object", object);
    /*String person = object.toJSONString();
    NameValuePair[] data = {
      new NameValuePair("pass", redisUtils.get("PASS")), new NameValuePair("person", person)
    };
    // 调用心跳接口创建人员信息
    JSONObject jsonObject =
        WoPlatformHeartbeat.httpPostHeartbeatAccess(redisUtils.get("URL") + "/person/create", data);
    JSONObject jsonData = jsonObject.getJSONObject("data");
    patientBaseInfo.setPersonId((String) jsonData.get("id"));*/
    mapper.insertSelective(patientBaseInfo);
    // 添加患者时,创建预付款账户
    this.addPatientPrepaymentsInfo(patientBaseInfo);
    return this.patientBaseInfoMapper.selectPatientInfoByNameAndMobileAndOrgId(patientBaseInfo);
  }

  /**
   * 添加患者时,创建预付款账户
   *
   * @param patientBaseInfo 患者信息
   */
  private void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo) {
    PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
    patientPrepaymentsInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientPrepaymentsInfo.setPatientId(patientBaseInfo.getId());
    // 预付款卡号生成规则 开通Y
    patientPrepaymentsInfo.setPrepaymentNumber(
        this.patientMemberInfoBiz.generateCardNumber(
            "Y", "patient_prepayments_info", "prepayment_number"));
    patientPrepaymentsInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientPrepaymentsInfo.setCrtName(BaseContextHandler.getName());
    this.patientPrepaymentsInfoMapper.insertSelective(patientPrepaymentsInfo);
  }

  /**
   * 添加完善患者扩展信息、其他信息
   *
   * @param patientExtendInfoModel 患者基本信息+扩展信息+其他信息 参数模板
   */
  public void addPatientInfo(PatientExtendInfoModel patientExtendInfoModel) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientExtendInfoModel.getPatientBaseInfoModel(), patientBaseInfo);
    patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
    patientBaseInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientBaseInfo.setUpdName(BaseContextHandler.getName());
    patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientBaseInfo.setUpdTime(new Date());
    // 完善患者基本信息  对补全信息进行更新
    this.mapper.updateByPrimaryKey(patientBaseInfo);
    PatientExpInfo patientExpInfo = new PatientExpInfo();
    // 完善患者扩展信息
    BeanUtils.copyProperties(patientExtendInfoModel.getPatientExpInfoModel(), patientExpInfo);
    // 如果用户没有扩展信息就添加扩展信息 如果有就修改
    if (patientExpInfo.getId() == null) {
      patientExpInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientExpInfo.setCrtName(BaseContextHandler.getName());
      // 添加扩展信息
      this.patientExpInfoMapper.insertSelective(patientExpInfo);
    } else {
      patientExpInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientExpInfo.setUpdName(BaseContextHandler.getName());
      patientExpInfo.setUpdTime(new Date());
      // 修改扩展信息
      this.patientExpInfoMapper.updateByPrimaryKey(patientExpInfo);
    }

    // 完善患者其他信息（标签、疾病史、过敏原）
    List<PatientExtInfoModel> patientExtInfoList =
        patientExtendInfoModel.getPatientExtInfoModelList();
    List<PatientExtInfoVo> patientExtInfos =
        this.patientExtInfoMapper.patientExtInfoListByid(patientBaseInfo.getId());
    // 判断是否已存在信息，若存在就删除
    if (!StringHelper.isEmpty(patientExtInfos)) {
      this.patientExtInfoMapper.deletePatientExtInfoByPatientId(patientBaseInfo.getId());
    }
    if (!StringHelper.isEmpty(patientExtInfoList)) {
      List<PatientExtInfoModel> addPatientExtInfoList = new ArrayList<>();
      Iterator patientExtInfoModelIterator = patientExtInfoList.iterator();
      // 循环添加 标签、疾病史、过敏原 集合
      while (patientExtInfoModelIterator.hasNext()) {
        PatientExtInfoModel patientExtInfoModel =
            (PatientExtInfoModel) patientExtInfoModelIterator.next();
        patientExtInfoModel.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientExtInfoModel.setCrtName(BaseContextHandler.getName());
        patientExtInfoModel.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientExtInfoModel.setUpdName(BaseContextHandler.getName());
        patientExtInfoModel.setUpdTime(new Date());
        addPatientExtInfoList.add(patientExtInfoModel);
      }
      this.patientExtInfoMapper.insertPatientExtInfoList(addPatientExtInfoList);
    }
  }

  /**
   * 根据患者id查询患者资料
   *
   * @param id 患者id
   * @return PatientExtendInfoModel
   */
  public ResponseResult<PatientExtendInfoVo> findPatientData(Integer id) {
    PatientExtendInfoVo patientExtendInfoVo = new PatientExtendInfoVo();
    PatientBaseInfo patientBaseInfo = this.mapper.selectByPrimaryKey(id);
    if (patientBaseInfo == null) {
      return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "未查询到患者信息", "");
    }
    PatientBaseInfoVo patientBaseInfoVo = new PatientBaseInfoVo();
    BeanUtils.copyProperties(patientBaseInfo, patientBaseInfoVo);
    PatientOrigin patientOrigin =
        patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
    if (patientOrigin == null) {
      return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "未查询到患者来源信息", "");
    }
    // 获取患者来源的父级id
    patientBaseInfoVo.setSourceParentId(patientOrigin.getParentId());
    // 获取患者来源name
    // 基本信息
    patientExtendInfoVo.setPatientBaseInfoVo(getTypeName(patientBaseInfoVo));
    // 扩展信息
    PatientExpInfoVo patientExpInfoVo = patientExpInfoMapper.selectIdByPatientId(id);
    if (patientExpInfoVo != null) {
      patientExtendInfoVo.setPatientExpInfoVo(patientExpInfoVo);
    }
    // 标签
    List<PatientExtInfoVo> patientExtInfoVos = patientExtInfoMapper.patientExtInfoListByid(id);
    if (!StringHelper.isEmpty(patientExtInfoVos)) {
      for (PatientExtInfoVo patientExtInfoVo : patientExtInfoVos) {
        if (patientExtInfoVo.getDictItemId() != null) {
          DictionaryItem dictionaryItem =
              remoteSystemServiceFeign.findDictionaryItemById(patientExtInfoVo.getDictItemId());
          if (dictionaryItem != null) {
            patientExtInfoVo.setDictItemName(dictionaryItem.getName());
          }
        }
      }
    }
    patientExtendInfoVo.setPatientExtInfoListVo(patientExtInfoVos);
    return ResponseUtil.success(patientExtendInfoVo);
  }

  /**
   * 获取 来源名称 推荐人名称 推荐来源名称
   *
   * @param patientBaseInfoVo
   * @return
   */
  private PatientBaseInfoVo getTypeName(PatientBaseInfoVo patientBaseInfoVo) {
    PatientOrigin patientOrigin =
        patientOriginMapper.getTypeName(patientBaseInfoVo.getOriginType());
    if (patientOrigin != null) {
      patientBaseInfoVo.setOriginTypeName(patientOrigin.getName());
    }
    if (patientBaseInfoVo.getOriginId() != null) {
      switch (patientBaseInfoVo.getOriginId()) {
        case 1:
          SysUserEmployeeModel model = new SysUserEmployeeModel();
          model.setUserId(patientBaseInfoVo.getSourceId());
          List<SysUserInfoDetail> list =
              remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
          if (!StringHelper.isEmpty(list)) {
            patientBaseInfoVo.setOriginName(list.get(0).getName());
          }
          break;
        case 2:
          PatientBaseInfo patientBaseInfo =
              patientBaseInfoMapper.selectByPrimaryKey(patientBaseInfoVo.getSourceId());
          if (patientBaseInfo != null) {
            patientBaseInfoVo.setOriginName(patientBaseInfo.getName());
          }
          break;
        case 3:
        case 4:
          PatientOrigin activity =
              patientOriginMapper.selectByPrimaryKey(patientBaseInfoVo.getOriginId());
          if (activity != null) {
            patientBaseInfoVo.setOriginName(activity.getName());
          }
          break;
        default:
          break;
      }
    }
    DictionaryItem dictionaryItem =
        remoteSystemServiceFeign.findDictionaryItemById(patientBaseInfoVo.getMobileOwner());
    if (dictionaryItem != null) {
      patientBaseInfoVo.setMobileOwnerName(dictionaryItem.getName());
    }
    return patientBaseInfoVo;
  }

  /**
   * 根据姓名/手机号/姓名拼音模糊查询患者
   *
   * @param form 患者模糊查询模板
   * @return List<PatientBaseInfoVo>
   */
  public List<PatientBaseInfoVo> findPatientByNameAndMobile(PatientLikeFinleQueryForm form) {
    List<PatientBaseInfoVo> patientByNameAndMobile =
        patientBaseInfoMapper.findPatientByNameAndMobile(form);
    return patientByNameAndMobile;
  }

  /**
   * 授权人脸识别结果 测试！！
   *
   * @param patientWoPlatformInfoModel 测试
   */
  public void renlianshibie(PatientWoPlatformInfoModel patientWoPlatformInfoModel) {
    if (!"STRANGERBABY".equals(patientWoPlatformInfoModel.getPersonId())
        && !"IDCARD".equals(patientWoPlatformInfoModel.getPersonId())) {
      PatientBaseInfoVo patientBaseInfoVo =
          patientBaseInfoMapper.selectOneByPersonId(patientWoPlatformInfoModel.getPersonId());
      if (patientBaseInfoVo != null) {
        System.out.println(
            "***************************************************************************");
        System.out.println("认证成功！");
        System.out.println(
            "***************************************************************************");
        System.out.println(patientWoPlatformInfoModel.toString());
      }
    }
  }

  /**
   * 根据患者id集合查询患者list
   *
   * @param ids 患者id集合
   * @return List<PatientBaseInfoVo>
   */
  public List<PatientBaseInfoVo> findPatientInfoByIds(List<Integer> ids) {
    return patientBaseInfoMapper.selectPatientInfoByIdList(ids);
  }

  /**
   * 拍照
   *
   * @param id 患者id
   */
  public void takeAPhoto(Integer id) {
    PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectPatientById(id);
    woPersonBiz.takeAPhoto(patientBaseInfo);
  }

  /**
   * 删除照片并查询
   *
   * @param pictureForm Wo平台照片删除Form
   */
  public List<PhotoInformationVo> deleteThePhoto(PictureForm pictureForm) {
    return woPersonBiz.deleteThePhoto(pictureForm);
  }

  /**
   * 设备人员认证授权
   *
   * @param pictureModel 人员认证授权Model
   */
  public void equipmenAuthorization(PictureModel pictureModel) {
    woPersonBiz.equipmenAuthorization(pictureModel);
  }

  /**
   * 获取人员照片
   *
   * @param patientId 患者id
   * @return List<PictureVo>
   */
  public ArrayList<PhotoInformationVo> getFaceUrl(Integer patientId) {
    PatientBaseInfo patientBaseInfo = this.patientBaseInfoMapper.selectPatientById(patientId);
    // List<PictureVo> woPersonnelFaceUrl =
    // this.woPersonBiz.findWoPersonnelFaceUrl(patientBaseInfo.getWoGuid());
    // return woPersonnelFaceUrl.size() >= 0 && woPersonnelFaceUrl != null ?
    // ResponseUtil.success(this.woPersonBiz.findWoPersonnelFaceUrl(patientBaseInfo.getWoGuid())) :
    ArrayList<PhotoInformationVo> photoInformationVos = new ArrayList<>();
    NameValuePair[] data = {
      new NameValuePair("pass", redisUtils.get("PASS")),
      new NameValuePair("personId", patientBaseInfo.getPersonId())
    };
    // 调用心跳接口创建人员信息
    JSONObject jsonObject =
        WoPlatformHeartbeat.httpPostHeartbeatAccess(redisUtils.get("URL") + "/face/find", data);
    JSONArray jsonData = jsonObject.getJSONArray("data");
    if (jsonData != null) {
      for (Object jsonDatum : jsonData) {
        photoInformationVos.add(
            JSONObject.parseObject(jsonDatum.toString(), PhotoInformationVo.class));
      }
    }
    return photoInformationVos;
  }

  /**
   * 根据患者id查询患者信息
   *
   * @param id 患者id
   * @return PatientBaseInfo
   */
  public PatientBaseInfo findPatientInfoById(Integer id) {
    return this.patientBaseInfoMapper.selectPatientById(id);
  }

  /**
   * 根据患者id查询患者全部信息
   *
   * @param id 患者id
   * @return PatientTotalInfoVo
   */
  public PatientTotalInfoVo findPatientTotalInfo(Integer id) {
    PatientTotalInfoVo patientData = mapper.selectPatientDataById(id);
    if (null != patientData) {
      PatientExtInfo patientExtInfo = new PatientExtInfo();
      patientExtInfo.setPatientId(id);
      List<PatientExtInfo> extInfos = patientExtInfoMapper.select(patientExtInfo);
      if (StringHelper.isNotEmpty(extInfos)) {
        StringBuilder labels = new StringBuilder(16);
        StringBuilder diseases = new StringBuilder(16);
        StringBuilder allergens = new StringBuilder(16);
        StringBuilder allergensDescriptions = new StringBuilder(16);
        for (PatientExtInfo extInfo : extInfos) {
          Byte type = extInfo.getType();
          if (type == 2 && extInfo.getDescription() != null) {
            allergensDescriptions.append(extInfo.getDescription());
            allergensDescriptions.append(",");
          }
          if (extInfo.getDictItemId() != null) {
            DictionaryItem item =
                remoteSystemServiceFeign.findDictionaryItemById(extInfo.getDictItemId());
            switch (type) {
              case 0:
                if (null != item) {
                  labels.append(item.getName());
                }
                break;
              case 1:
                if (null != item) {
                  diseases.append(item.getName());
                }
                break;
              case 2:
                if (null != item) {
                  allergens.append(item.getName());
                  allergens.append(",");
                }
                break;
              default:
                break;
            }
          }
        }
        patientData.setLabels(labels.toString());
        patientData.setDiseases(diseases.toString());
        // 去掉最后的逗号
        if (allergens.length() > 0) {
          allergens.deleteCharAt(allergens.length() - 1);
          patientData.setAllergens(allergens.toString());
        }
        if (allergensDescriptions.length() > 0) {
          allergensDescriptions.deleteCharAt(allergensDescriptions.length() - 1);
          patientData.setAllergensDescriptions(allergensDescriptions.toString());
        }
      }
    }
    return patientData;
  }

  /**
   * 根据患者id查询患者来访信息
   *
   * @param id 患者id
   * @return PatientVisitInfoVo
   */
  public PatientVisitInfoVo findPatientVisitInfo(Integer id) {
    PatientVisitInfoVo patientVisitInfoVo = patientBaseInfoMapper.findPatientVisitInfo(id);
    // 查询患者是否开通会员卡
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectOneByPatientId(patientVisitInfoVo.getPatientId());
    // 获取会员卡号
    if (patientMemberInfo != null) {
      patientVisitInfoVo.setCardNumber(patientMemberInfo.getCardNumber());
      patientVisitInfoVo.setMemberTypeId(patientMemberInfo.getMemberTypeId());
      // 获取会员卡名称
      MemberType memberType =
          remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId());
      if (memberType != null && memberType.getName() != null) {
        patientVisitInfoVo.setMemberCardName(memberType.getName());
      }
    }
    if (patientVisitInfoVo.getPatientKind() != null) {
      // 查询患者类型字典名称
      DictionaryItem dictionaryItemById =
          remoteSystemServiceFeign.findDictionaryItemById(patientVisitInfoVo.getPatientKind());
      if (dictionaryItemById != null) {
        patientVisitInfoVo.setPatientKindName(dictionaryItemById.getName());
      }
    }
    // 获取标签
    patientVisitInfoVo.setLabels(getLabels(patientVisitInfoVo.getPatientId()));
    return patientVisitInfoVo;
  }

  /**
   * 根据患者id获取患者标签
   *
   * @param patientId 患者id
   * @return String
   */
  public String getLabels(Integer patientId) {
    PatientExtInfo patientExtInfo = new PatientExtInfo();
    StringBuilder labels = new StringBuilder(16);
    patientExtInfo.setPatientId(patientId);
    List<PatientExtInfo> extInfos = patientExtInfoMapper.select(patientExtInfo);
    if (StringHelper.isNotEmpty(extInfos)) {
      for (PatientExtInfo extInfo : extInfos) {
        Byte type = extInfo.getType();
        // 查询标签字典名称
        DictionaryItem item =
            remoteSystemServiceFeign.findDictionaryItemById(extInfo.getDictItemId());
        if (type == 0 && null != item) {
          labels.append(item.getName());
          labels.append("、");
        }
      }
    }
    return labels.toString();
  }

  /**
   * 根据门诊id获取病历号后六位
   *
   * @param orgId 门诊id
   * @return String
   */
  public String findMedicalNumberByOrgId(Integer orgId) {
    return mapper.findMedicalNumberByOrgId(orgId);
  }

  /**
   * 修改头像
   *
   * @param patientPhotoForm 编辑患者头像
   */
  public void uptPhoto(PatientPhotoForm patientPhotoForm) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientPhotoForm, patientBaseInfo);
    this.patientBaseInfoMapper.updatePhoto(patientBaseInfo);
  }

  /**
   * 修改设备密码
   *
   * @param form 修改密码Form
   */
  public void updPass(UpdPassForm form) {
    NameValuePair[] data = {
      new NameValuePair("oldPass", form.getOldPass()),
      new NameValuePair("newPass", form.getNewPass())
    };
    // 调用心跳接口修改设备密码
    JSONObject jsonObject =
        WoPlatformHeartbeat.httpPostHeartbeatAccess(redisUtils.get("URL") + "/setPassWord", data);
  }

  /** @param picturesCallbackInfoModel 接收回调Model */
  public void takePictures(PicturesCallbackInfoModel picturesCallbackInfoModel) {
    System.out.println(
        "**************************************************************************************");
    System.out.println(picturesCallbackInfoModel.toString());
    System.out.println("拍照回调成功！");
    System.out.println(
        "**************************************************************************************");
  }

  /**
   * 根据姓名/病例编号/手机号/姓名拼音模糊查询患者
   *
   * @param form 患者模糊查询模板
   * @return List<PatientBaseInfoVo>
   */
  public List<AppPatientBaseInfoVo> appFindPatientByNameAndMobile(PatientLikeFinleQueryForm form) {
    List<AppPatientBaseInfoVo> appPatientBaseInfoVos =
        patientBaseInfoMapper.appFindPatientByNameAndMobile(form);
    if (!StringHelper.isEmpty(appPatientBaseInfoVos)) {
      Byte[] str = {0, 1};
      int age = 14;
      for (AppPatientBaseInfoVo appPatientBaseInfoVo : appPatientBaseInfoVos) {
        // 设置患者的类型(未成年男性0，成年男性1，未成年女性2，成年女性3)
        Integer integer = appPatientBaseInfoVo.getAge();
        if (integer != null) {
          // 未成年男性0
          Byte gender = appPatientBaseInfoVo.getGender();
          if (integer < age && str[0].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(0);
          }
          // 成年男性1
          if (integer >= age && str[0].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(1);
          }
          // 未成年女性2
          if (integer < age && str[1].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(2);
          }
          // 成年女性3
          if (integer >= age && str[1].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(3);
          }
        } else {
          // 年龄为空，性别为男 4
          if (str[0].equals(appPatientBaseInfoVo.getGender())) {
            appPatientBaseInfoVo.setPatientKind(4);
          } else {
            // 年龄为空，性别为女 5
            appPatientBaseInfoVo.setPatientKind(5);
          }
        }
      }
    }
    return appPatientBaseInfoVos;
  }

  /**
   * app端患者档案查询
   * @param patientId 患者id
   * @return AppPatientArchivesVo
   */
  public AppPatientArchivesVo patientArchives(Integer patientId) {
    AppPatientArchivesVo appPatientArchivesVo = patientBaseInfoMapper.appPatientArchives(patientId);
    if (appPatientArchivesVo != null){
      // 获取会员卡名称
      if (appPatientArchivesVo.getMemberTypeId() != null){
        MemberType memberType = this.remoteSystemServiceFeign.findMemberTypeById(appPatientArchivesVo.getMemberTypeId());
        if (memberType != null && memberType.getName() != null) {
          appPatientArchivesVo.setMemberCardName(memberType.getName());
        }
        Byte[] str = {0, 1};
        int age = 14;
          // 设置患者的类型(未成年男性0，成年男性1，未成年女性2，成年女性3)
          Integer integer = appPatientArchivesVo.getAge();
          if (integer != null) {
            // 未成年男性0
            Byte gender = appPatientArchivesVo.getGender();
            if (integer < age && str[0].equals(gender)) {
              appPatientArchivesVo.setPatientKind(0);
            }
            // 成年男性1
            if (integer >= age && str[0].equals(gender)) {
              appPatientArchivesVo.setPatientKind(1);
            }
            // 未成年女性2
            if (integer < age && str[1].equals(gender)) {
              appPatientArchivesVo.setPatientKind(2);
            }
            // 成年女性3
            if (integer >= age && str[1].equals(gender)) {
              appPatientArchivesVo.setPatientKind(3);
            }
          } else {
            // 年龄为空，性别为男 4
            if (str[0].equals(appPatientArchivesVo.getGender())) {
              appPatientArchivesVo.setPatientKind(4);
            } else {
              // 年龄为空，性别为女 5
              appPatientArchivesVo.setPatientKind(5);
          }
        }
      }
    }
    return appPatientArchivesVo;
  }

  /**
   * 查询患者是否存在
   * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
   * @return ResponseResult
   */
  public ResponseResult appFindUserExists(PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
    List<PatientBaseInfoVo> patientBaseInfoVos;
    patientBaseInfoVos = patientBaseInfoMapper.findUserExistsList(patientBaseInfoQueryForm);
    if (!StringHelper.isEmpty(patientBaseInfoVos)) {
      return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"添加失败,该用户已存在", patientBaseInfoVos);
    }
    List<PatientBaseInfoVo> patientBaseInfoVoList =
            patientBaseInfoMapper.findUserExistsByMobileList(patientBaseInfoQueryForm.getMobile());
    if (!StringHelper.isEmpty(patientBaseInfoVoList)) {
      return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "该手机号已存在", patientBaseInfoVoList);
    }
    return ResponseUtil.success();
  }
}
