package com.yunya.modules.patient_central.biz;

import com.alibaba.fastjson.JSONArray;
import com.yunya.feign.patient_central.domain.form.PatientPhotoForm;
import com.yunya.feign.patient_central.domain.form.PictureForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.constant.WoPlatformConstants;
import com.yunya.modules.patient_central.constant.WoPlatformHeartbeat;
import com.yunya.modules.patient_central.mapper.*;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONObject;

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

  @Autowired private RedisUtils redisUtils;

  @Autowired private PatientBaseInfoMapper patientBaseInfoMapper;

  @Autowired private PatientExtInfoMapper patientExtInfoMapper;

  @Autowired private PatientExpInfoMapper patientExpInfoMapper;

  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

  @Autowired private WoPersonBiz woPersonBiz;

  @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  @Autowired private PatientMemberInfoBiz patientMemberInfoBiz;

  @Autowired private PatientOriginMapper patientOriginMapper;

  @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

  /**
   * 通过患者id查询患者共用属性
   *
   * @param id
   * @return PatientPublicInfo
   */
  public PatientPublicInfoVo findPatientPublicInfoById(Integer id) {
    PatientPublicInfoVo patientPublicInfoVo = this.patientBaseInfoMapper.findPatientPublicInfoById(id);
    if (patientPublicInfoVo.getMemberTypeId() != null) {
      MemberType memberType = this.remoteSystemServiceFeign.findMemberTypeById(patientPublicInfoVo.getMemberTypeId());
      if (memberType.getName() != null) {
        patientPublicInfoVo.setMemberCardName(memberType.getName());
      }
    }
    return patientPublicInfoVo;
  }

  /**
   * 查询患者是否存在
   *
   * @param patientBaseInfoQueryForm
   */
  public ResponseResult findUserExists(PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
    PatientBaseInfoVo patientBaseInfoVo;
    patientBaseInfoVo = patientBaseInfoMapper.findUserExists(patientBaseInfoQueryForm);
    if (patientBaseInfoVo != null) {
      return ResponseUtil.error("添加失败,该用户已存在", patientBaseInfoVo);
    }
    PatientBaseInfoVo userExistsByMobile = patientBaseInfoMapper.findUserExistsByMobile(patientBaseInfoQueryForm.getMobile());
    if (userExistsByMobile != null) {
      return ResponseUtil.error("该手机号已存在", userExistsByMobile);
    }
    return ResponseUtil.success();
  }

  /**
   * 添加患者信息
   *
   * @param patientBaseInfoModel
   */
  public PatientBaseInfoVo addPatient(PatientBaseInfoModel patientBaseInfoModel) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);
    if (patientBaseInfoModel.getFaceUrl() != null) { //判断是否是更新
      this.patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
      return this.patientBaseInfoMapper.selectOneById(patientBaseInfo.getId());
    } else {
      PatientOrigin patientOrigin = this.patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
      if (patientOrigin != null) {
        patientBaseInfo.setOriginType(patientOrigin.getOriginType());
      }

      patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
      patientBaseInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientBaseInfo.setCrtName(BaseContextHandler.getName());
      patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      //patientBaseInfo.setWoGuid(woPersonBiz.addWoPersonInput(patientBaseInfo.getName())); // wo平台创建对应人员 返回人员Guid添加到数据库

      JSONObject object = new JSONObject();
      object.put("name",patientBaseInfo.getName());
      String person = object.toJSONString();
      NameValuePair[] data = {
              new NameValuePair("pass",redisUtils.get("PASS")),
              new NameValuePair("person",person)
      };
      JSONObject jsonObject = WoPlatformHeartbeat.httpPostHeartbeatAccess(redisUtils.get("URL") + "/person/create", data); //调用心跳接口创建人员信息
      JSONObject jsonData = jsonObject.getJSONObject("data");
      patientBaseInfo.setPersonId((String) jsonData.get("id"));
      mapper.insertSelective(patientBaseInfo);

      this.addPatientPrepaymentsInfo(patientBaseInfo); // 添加患者时,创建预付款账户
      return this.patientBaseInfoMapper.selectPatientInfoByNameAndMobileAndOrgId(patientBaseInfo);
    }
  }

  /**
   * 添加患者时,创建预付款账户
   * @param patientBaseInfo
   */
  public void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo) {
    PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
    patientPrepaymentsInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientPrepaymentsInfo.setPatientId(patientBaseInfo.getId());
    patientPrepaymentsInfo.setPrepaymentNumber(this.patientMemberInfoBiz.generateCardNumber("Y", "patient_prepayments_info", "prepayment_number")); //预付款卡号生成规则 开通Y
    patientPrepaymentsInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientPrepaymentsInfo.setCrtName(BaseContextHandler.getName());
    this.patientPrepaymentsInfoMapper.insertSelective(patientPrepaymentsInfo);
  }

  /**
   * 添加完善患者扩展信息、其他信息
   * @param patientExtendInfoModel
   */
  public void addPatientInfo(PatientExtendInfoModel patientExtendInfoModel) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientExtendInfoModel.getPatientBaseInfoModel(), patientBaseInfo);
    patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
    patientBaseInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientBaseInfo.setUpdName(BaseContextHandler.getName());
    patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientBaseInfo.setUpdTime(new Date());
    this.mapper.updateByPrimaryKey(patientBaseInfo); // 完善患者基本信息  对补全信息进行更新
    PatientExpInfo patientExpInfo = new PatientExpInfo();
    BeanUtils.copyProperties(patientExtendInfoModel.getPatientExpInfoModel(), patientExpInfo); // 完善患者扩展信息
    if (patientExpInfo.getId() == null) { // 如果用户没有扩展信息就添加扩展信息 如果有就修改
      patientExpInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientExpInfo.setCrtName(BaseContextHandler.getName());
      this.patientExpInfoMapper.insertSelective(patientExpInfo); // 添加扩展信息
    } else {
      patientExpInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientExpInfo.setUpdName(BaseContextHandler.getName());
      patientExpInfo.setUpdTime(new Date());
      this.patientExpInfoMapper.updateByPrimaryKey(patientExpInfo); // 修改扩展信息
    }

    List<PatientExtInfoModel> patientExtInfoList = patientExtendInfoModel.getPatientExtInfoModelList();  // 完善患者其他信息（标签、疾病史、过敏原）
    List<PatientExtInfoVo> patientExtInfos = this.patientExtInfoMapper.patientExtInfoListByid(patientBaseInfo.getId());
    if (patientExtInfos.size() != 0 || patientExtInfos != null) { // 判断是否已存在信息，若存在就删除
      this.patientExtInfoMapper.deletePatientExtInfoByPatientId(patientBaseInfo.getId());
    }
    if(patientExtInfoList.size() > 0){
      List<PatientExtInfoModel> addPatientExtInfoList = new ArrayList();
      Iterator var7 = patientExtInfoList.iterator();
      while(var7.hasNext()) {  // 循环添加 标签、疾病史、过敏原 集合
        PatientExtInfoModel patientExtInfoModel = (PatientExtInfoModel)var7.next();
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
   * @param id
   * @return PatientExtendInfoModel
   */
  public PatientExtendInfoVo findPatientData(Integer id) {
    PatientExtendInfoVo patientExtendInfoVo = new PatientExtendInfoVo();
    PatientBaseInfo patientBaseInfo = this.mapper.selectByPrimaryKey(id);
    PatientBaseInfoVo patientBaseInfoVo = new PatientBaseInfoVo();
    BeanUtils.copyProperties(patientBaseInfo, patientBaseInfoVo);
    PatientOrigin patientOrigin = patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
    patientBaseInfoVo.setSourceParentId(patientOrigin.getParentId()); //获取患者来源的父级id
    patientExtendInfoVo.setPatientBaseInfoVo(patientBaseInfoVo); //基本信息
    patientExtendInfoVo.setPatientExpInfoVo(patientExpInfoMapper.selectIdByPatientId(id)); //扩展信息
    patientExtendInfoVo.setPatientExtInfoListVo(patientExtInfoMapper.patientExtInfoListByid(id)); //标签
    return patientExtendInfoVo;
  }

  /**
   * 根据姓名/手机号/姓名拼音模糊查询患者
   *
   * @param form
   * @return List<PatientBaseInfoVo>
   */
  public List<PatientBaseInfoVo> findPatientByNameAndMobile(PatientLikeFinleQueryForm form) {
    return patientBaseInfoMapper.findPatientByNameAndMobile(form);
  }


  /**
   * 授权人脸识别结果   测试！！
   *
   * @param patientWoPlatformInfoModel
   */
  public void renlianshibie(PatientWoPlatformInfoModel patientWoPlatformInfoModel) {
    if(!patientWoPlatformInfoModel.getPersonId().equals("STRANGERBABY") && !patientWoPlatformInfoModel.getPersonId().equals("IDCARD")){
      PatientBaseInfoVo patientBaseInfoVo = patientBaseInfoMapper.selectOneByPersonId(patientWoPlatformInfoModel.getPersonId());
      if(patientBaseInfoVo != null){
        System.out.println("***************************************************************************");
        System.out.println("认证成功！");
        System.out.println("***************************************************************************");
        System.out.println(patientWoPlatformInfoModel.toString());
      }
    }
  }

  /**
   * 根据患者id集合查询患者list
   *
   * @param ids
   * @return List<PatientBaseInfoVo>
   */
  public List<PatientBaseInfoVo> findPatientInfoByIds(List<Integer> ids) {
    return patientBaseInfoMapper.selectPatientInfoByIdList(ids);
  }

  /**
   * 拍照
   *
   * @param id
   * @return String
   */
  public void takeAPhoto(Integer id) {
    PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectPatientById(id);
    woPersonBiz.takeAPhoto(patientBaseInfo);
  }

  /**
   * 删除照片并查询
   *
   * @param pictureForm
   */
  public List<PhotoInformationVo> deleteThePhoto(PictureForm pictureForm) {
    return woPersonBiz.deleteThePhoto(pictureForm);
  }

  /**
   * 设备人员认证授权
   *
   * @param pictureModel
   */
  public void equipmenAuthorization(PictureModel pictureModel) {
    woPersonBiz.equipmenAuthorization(pictureModel);
  }

  /**
   * 获取人员照片
   *
   * @param patientId
   * @return List<PictureVo>
   */
  public ArrayList<PhotoInformationVo> getFaceUrl(Integer patientId) {
    PatientBaseInfo patientBaseInfo = this.patientBaseInfoMapper.selectPatientById(patientId);
    //List<PictureVo> woPersonnelFaceUrl = this.woPersonBiz.findWoPersonnelFaceUrl(patientBaseInfo.getWoGuid());
    //return woPersonnelFaceUrl.size() >= 0 && woPersonnelFaceUrl != null ? ResponseUtil.success(this.woPersonBiz.findWoPersonnelFaceUrl(patientBaseInfo.getWoGuid())) : ResponseUtil.error("未查询到照片，请先拍照", "");
    ArrayList<PhotoInformationVo> photoInformationVos = new ArrayList<>();
    NameValuePair[] data = {
            new NameValuePair("pass",redisUtils.get("PASS")),
            new NameValuePair("personId",patientBaseInfo.getPersonId())
    };
    JSONObject jsonObject = WoPlatformHeartbeat.httpPostHeartbeatAccess(redisUtils.get("URL") + "/face/find", data); //调用心跳接口创建人员信息
    JSONArray jsonData = jsonObject.getJSONArray("data");
    if(jsonData != null){
      for (Object jsonDatum : jsonData) {
        photoInformationVos.add(JSONObject.parseObject(jsonDatum.toString(),PhotoInformationVo.class));
      }
    }
    return photoInformationVos;
  }

  /**
   * 根据患者id查询患者信息
   *
   * @param id
   * @return
   */
  public PatientBaseInfo findPatientInfoById(Integer id) {
    return this.patientBaseInfoMapper.selectPatientById(id);
  }

  /**
   * 根据患者id查询患者全部信息
   *
   * @param id
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
        StringBuffer allergensDescriptions = new StringBuffer();
        for (PatientExtInfo extInfo : extInfos) {
          if(extInfo.getDescription() != null){
            allergensDescriptions.append(extInfo.getDescription());
            allergensDescriptions.append(",");
          }
          Byte type = extInfo.getType();
          if(extInfo.getDictItemId() != null){
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
        allergensDescriptions.deleteCharAt(allergensDescriptions.length()-1); //去掉最后的逗号
        patientData.setAllergens(allergens.toString());
        allergensDescriptions.deleteCharAt(allergensDescriptions.length()-1);
        patientData.setAllergensDescriptions(allergensDescriptions.toString());
      }
    }
    return patientData;
  }

  /**
   * 根据患者id查询患者来访信息
   * @param id
   * @return PatientVisitInfoVo
   */
  public PatientVisitInfoVo findPatientVisitInfo(Integer id) {
    PatientVisitInfoVo patientVisitInfoVo = patientBaseInfoMapper.findPatientVisitInfo(id);
    PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOneByPatientId(patientVisitInfoVo.getPatientId()); //查询患者是否开通会员卡
    if(patientMemberInfo != null){
      patientVisitInfoVo.setCardNumber(patientMemberInfo.getCardNumber()); //获取会员卡号
      patientVisitInfoVo.setMemberTypeId(patientMemberInfo.getMemberTypeId());
      MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId()); //获取会员卡名称
      if (memberType.getName() != null) {
        patientVisitInfoVo.setMemberCardName(memberType.getName());
      }
    }
    if(patientVisitInfoVo.getPatientKind() != null){
      DictionaryItem dictionaryItemById = remoteSystemServiceFeign.findDictionaryItemById(patientVisitInfoVo.getPatientKind()); //查询患者类型字典名称
      if(dictionaryItemById!=null){
        patientVisitInfoVo.setPatientKindName(dictionaryItemById.getName());
      }
    }
    patientVisitInfoVo.setLabels(getLabels(patientVisitInfoVo.getPatientId())); //获取标签
    return patientVisitInfoVo;
  }

  /**
   * 根据患者id获取患者标签
   * @param patientId
   * @return
   */
  public String getLabels(Integer patientId){
    PatientExtInfo patientExtInfo = new PatientExtInfo();
    StringBuilder labels = new StringBuilder(16);
    patientExtInfo.setPatientId(patientId);
    List<PatientExtInfo> extInfos = patientExtInfoMapper.select(patientExtInfo);
    if (StringHelper.isNotEmpty(extInfos)) {
      for (PatientExtInfo extInfo : extInfos) {
        Byte type = extInfo.getType();
        DictionaryItem item = remoteSystemServiceFeign.findDictionaryItemById(extInfo.getDictItemId()); //查询标签字典名称
        if(type == 0 && null != item){
          labels.append(item.getName());
          labels.append("、");
        }
      }
    }
    return labels.toString();
  }

  /**
   * 根据门诊id获取病历号后六位
   * @param orgId
   * @return String
   */
  public String findMedicalNumberByOrgId(Integer orgId) {
    return mapper.findMedicalNumberByOrgId(orgId);
  }


  /**
   * 修改头像
   * @param patientPhotoForm
   */
  public void uptPhoto(PatientPhotoForm patientPhotoForm) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientPhotoForm, patientBaseInfo);
    this.patientBaseInfoMapper.updatePhoto(patientBaseInfo);
  }

  /**
   * 设置硬件ip
   * @param model
   */
  public void setIp(IpModel model) {
    redisUtils.set("PASS",model.getPass());
    redisUtils.set("URL","http://" + model.getIp() + ":" + "8090");
  }
}
