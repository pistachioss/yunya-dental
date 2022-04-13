package com.yunya.modules.employeeattend.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.BaseResult;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yunya.modules.employeeattend.biz.EmployeePushMessageRecordBiz;
import com.yunya.modules.employeeattend.config.JPushConfig;
import com.yunya.modules.employeeattend.form.EmployeePushData;
import com.yunya.modules.employeeattend.form.EmployeePushForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.Collection;

@Slf4j
public class JpushManager {

  private static JpushManager instance;

  private static String appKey;
  private static String appMasterSecret;
  private static Boolean production;

  private JPushClient jpushClient = null;

  @Autowired private static JPushConfig jPushConfig;

  @Autowired private EmployeePushMessageRecordBiz employeePushMessageRecordBiz;

  /** @return */
  public static JpushManager getInstance() {
    if (instance == null) {
      production = jPushConfig.getProduction();
      instance = new JpushManager(jPushConfig.getAppKey(), jPushConfig.getAppMasterSecret());
    }
    return instance;
  }

  /**
   * @param key
   * @param secret
   */
  private JpushManager(String key, String secret) {
    try {
      appKey = key;
      appMasterSecret = secret;
      jpushClient = new JPushClient(appMasterSecret, appKey, null, ClientConfig.getInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param alias 别名
   * @param
   * @param title 标题
   * @param content 内容
   * @param
   * @param
   * @param platform
   * @throws Exception
   */
  public BaseResult send(
      Collection<String> alias,
      String title,
      String content,
      String type,
      int platform,
      boolean schedule,
      String scheTime)
      throws Exception {
    log.info("platform: " + platform);
    BaseResult result = null;
    switch (platform) {
      case 1:
        try {
          // ios
          IosNotification iosNotification =
              IosNotification.newBuilder()
                  .setAlert(content)
                  .addExtra("type", type)
//                  .setBadge(5)
//                  .setSound("Alarm.aif")
                  .build();
          PushPayload payloadIos =
              PushPayload.newBuilder()
                  .setPlatform(Platform.ios())
                  .setAudience(
                      Audience.newBuilder().addAudienceTarget(AudienceTarget.registrationId(alias)).build())
                  .setOptions(
                      Options.newBuilder()
                          .setApnsProduction(production)
                          .build()) // true-推送生产环境 false-推送开发环境（测试使用参数）
                  .setNotification(
                      Notification.newBuilder().addPlatformNotification(iosNotification).build())
                  .build();
          if (schedule) {
            result = jpushClient.createSingleSchedule(
                "Attend_ios", scheTime, payloadIos, appMasterSecret, appKey);
          } else {
            result = jpushClient.sendPush(payloadIos);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        break;
      case 2:
        try {
          // 推送安卓
          AndroidNotification androidNotification =
              AndroidNotification.newBuilder().setAlert(content).addExtra("type", type).build();

          PushPayload payload =
              PushPayload.newBuilder()
                  .setPlatform(Platform.android())
                  .setAudience(
                      Audience.newBuilder()
                          .addAudienceTarget(AudienceTarget.registrationId(alias))
                          .build())
                  .setNotification(
                      Notification.newBuilder()
                          .addPlatformNotification(androidNotification)
                          .build())
                  .setMessage(Message.newBuilder().setTitle(title).setMsgContent(content).build())
                  .build();
          if (schedule) {
            result = jpushClient.createSingleSchedule(
                "Attend_android", scheTime, payload, appMasterSecret, appKey);
          } else {
            result = jpushClient.sendPush(payload);
            System.err.println(result);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        break;
    }
    return result;
  }

  private void crtPushMessageRecord(BaseResult result, EmployeePushForm employeePushForm, int type) {
    if (!ObjectUtils.isEmpty(result) && result.getResponseCode()==200) {
      employeePushMessageRecordBiz.asyncGeneratMsgRecord(employeePushForm, type);
    }
  }

  public String makeSendData(Object Data) {
    String res =
        JSONObject.toJSONString(
            Data,
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullNumberAsZero);
    return res;
  }

  public Boolean pushBase(EmployeePushForm employeePushForm, EmployeePushData pushData) {
    try {
      String data = JpushManager.getInstance().makeSendData(pushData);
      if (employeePushForm.getTitle().isEmpty()) {
        employeePushForm.setTitle("提示");
      }
      if (employeePushForm.getContent().isEmpty()) {
        employeePushForm.setContent("欢迎您成为平台用户，我们将热忱为您服务。");
      }
      if (employeePushForm.getIsSchedule() == null) {
        employeePushForm.setIsSchedule(false);
      }
      System.out.println("===============Jpush Send Param-employeePushForm： "+JSONObject.toJSON(employeePushForm));
      System.out.println("===============Jpush Send Param-data: "+data);
      BaseResult result = JpushManager.getInstance()
          .send(
              employeePushForm.getUserList(),
              employeePushForm.getTitle(),
              employeePushForm.getContent(),
              data,
              employeePushForm.getPlatform(),
              employeePushForm.getIsSchedule(),
              employeePushForm.getScheTime());
      if (!ObjectUtils.isEmpty(result) && result.getResponseCode()==200) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public void pushTest(EmployeePushForm employeePushTest) {
    pushBase(employeePushTest, new EmployeePushData());
  }

  private String getTypeName(int type) {
    switch (type) {
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
        return "请假";
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
        return "加班";
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
        return "外勤";
    }
    return "";
  }

  public Boolean pushLeaveApproval(EmployeePushForm employeePushForm, int type) {
    employeePushForm.setTitle(String.format("%s审批提示", getTypeName(type)));
    employeePushForm.setContent(
        String.format("【OA审批】%s提交的%s", employeePushForm.getShowName(), getTypeName(type)));
    EmployeePushData pushData = new EmployeePushData();
    pushData.setType(type);
    pushData.setId(employeePushForm.getIds().get(0));
    return pushBase(employeePushForm, pushData);
  }

  public Boolean pushLeaveCope(EmployeePushForm employeePushForm, int type) {
    employeePushForm.setTitle(String.format("%s审批提示", getTypeName(type)));
    employeePushForm.setContent(
        String.format("【OA审批】%s提交的%s，抄送给你，请知晓", employeePushForm.getShowName(), getTypeName(type)));
    EmployeePushData pushData = new EmployeePushData();
    pushData.setType(type);
    pushData.setId(employeePushForm.getIds().get(0));
    return pushBase(employeePushForm, pushData);
  }

  public Boolean pushLeaveYes(EmployeePushForm employeePushForm, int type) {
    employeePushForm.setTitle(String.format("%s审批提示", getTypeName(type)));
    employeePushForm.setContent(String.format("【OA审批】%s审批已通过", getTypeName(type)));
    EmployeePushData pushData = new EmployeePushData();
    pushData.setType(type);
    pushData.setId(employeePushForm.getIds().get(0));
    return pushBase(employeePushForm, pushData);
  }

  public Boolean pushLeaveNo(EmployeePushForm employeePushForm, int type) {
    employeePushForm.setTitle(String.format("%s审批提示", getTypeName(type)));
    employeePushForm.setContent(String.format("【OA审批】%s审批未通过，请知晓", getTypeName(type)));
    EmployeePushData pushData = new EmployeePushData();
    pushData.setType(type);
    pushData.setId(employeePushForm.getIds().get(0));
    return pushBase(employeePushForm, pushData);
  }

  public Boolean pushLeaveCancel(EmployeePushForm employeePushForm, int type) {
    employeePushForm.setTitle(String.format("%s审批提示", getTypeName(type)));
    employeePushForm.setContent(
        String.format("【OA审批】%s申请的%s已撤销", employeePushForm.getShowName(), getTypeName(type)));
    EmployeePushData pushData = new EmployeePushData();
    pushData.setType(type);
    pushData.setId(employeePushForm.getIds().get(0));
    return pushBase(employeePushForm, pushData);
  }

  public Boolean pushAttend(EmployeePushForm employeePushForm, int type) {
    employeePushForm.setTitle("考勤打卡提示");
    employeePushForm.setContent("【考勤打卡】还有10分钟就要上班啦，快来一键打卡，已打卡请忽略");
    employeePushForm.setIsSchedule(true);
    EmployeePushData pushData = new EmployeePushData();
    pushData.setType(type);
    return pushBase(employeePushForm, pushData);
  }
}
