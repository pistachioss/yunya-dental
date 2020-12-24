package com.yunya.modules.sms.utl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.common.io.Files;
import com.yunya.feign.sms.form.SmsSignatureSetForm;
import com.yunya.feign.sms.model.SmsSignatureSetModel;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.sms.SmsTemplateSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.OPERATION_FAIL;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/11 15:25
 * @since: 1.0.0
 */
@Component
public class AliyunSmsUtl {
    private static Logger log = LoggerFactory.getLogger(AliyunSmsUtl.class);

    private static String ALIYUN_DOMAIN;
    private static String ALIYUN_VERSION;

    @Value("${aliyun.sms.aliyunDomain}")
    private String aliyunDomain;
    @Value("${aliyun.sms.aliyunVersion}")
    private String aliyunVersion;
    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.sms.accessSecret}")
    private String accessSecret;

    private static IAcsClient client = null;

    @PostConstruct
    public void init() {
        ALIYUN_DOMAIN = aliyunDomain;
        ALIYUN_VERSION = aliyunVersion;
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        client = new DefaultAcsClient(profile);
    }

    private static CommonRequest commonRequest() {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(ALIYUN_DOMAIN);
        request.setSysVersion(ALIYUN_VERSION);
        request.putQueryParameter("RegionId", "cn-hangzhou");
        return request;
    }

    /**
     * 添加阿里云短信签名（企业用户每天最多可以申请100个签名）
     *
     * @param model
     * @param files
     * @return
     */
    public static JSONObject addSmsSign(SmsSignatureSetModel model, List<MultipartFile> files) {
        CommonRequest request = commonRequest();
        request.setSysAction("AddSmsSign");
        request.putQueryParameter("SignName", model.getSignName());
        request.putQueryParameter("SignSource", model.getSignSource()+"");
        request.putBodyParameter("Remark", model.getRemark());
        JSONObject result = null;
        try {
            if (files!=null && !files.isEmpty()) {
                int i = 1;
                for (MultipartFile file : files) {
                    String encode = BinaryUtil.toBase64String(file.getBytes());
                    String type = Files.getFileExtension(file.getOriginalFilename());
                    request.putQueryParameter("SignFileList." + i + ".FileSuffix", type);
                    request.putBodyParameter("SignFileList." + i + ".FileContents", encode);
                    i++;
                }
            }
            log.info("addSmsSign requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("addSmsSign response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl addSmsSign error", e);
            throw new ClientServiceException("添加短信签名失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 修改阿里云短信签名
     *
     * @param model
     * @return
     */
    public static JSONObject modifySmsSign(SmsSignatureSetForm model, List<MultipartFile> files) {
        CommonRequest request = commonRequest();
        request.setSysAction("ModifySmsSign");
        request.putQueryParameter("SignName", model.getSignName());
        request.putQueryParameter("SignSource", model.getSignSource()+"");
        request.putBodyParameter("Remark", model.getRemark());
        JSONObject result = null;
        try {
            if (files!=null && !files.isEmpty()) {
                int i = 1;
                for (MultipartFile file : files) {
                    String encode = BinaryUtil.toBase64String(file.getBytes());
                    request.putQueryParameter("SignFileList." + i + ".FileSuffix", Files.getFileExtension(file.getOriginalFilename()));
                    request.putBodyParameter("SignFileList." + i + ".FileContents", encode);
                }
            }
            log.info("modifySmsSign requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("modifySmsSign response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl modifySmsSign error", e);
            throw new ClientServiceException("修改短信签名失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 删除阿里云短信签名（不支持删除正在审核中的签名）
     *
     * @param signName 签名名称
     * @return
     */
    public static JSONObject deleteSmsSign(String signName) {
        CommonRequest request = commonRequest();
        request.setSysAction("DeleteSmsSign");
        request.putQueryParameter("SignName", signName);
        JSONObject result = null;
        try {
            log.info("deleteSmsSign requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("deleteSmsSign response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("删除短信签名失败", e);
            throw new ClientServiceException("AliyunSmsUtl deleteSmsSign error", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 查询阿里云短信签名
     * 成功结果：{"Message":"OK","RequestId":"C5309934-13E3-4DA5-9B33-8C6C315421FB","SignStatus":1,"Code":"OK","CreateDate":"2020-12-10 13:25:18","SignName":"ABC商城","Reason":"无审批备注"}
     *
     * @param signName 签名名称
     * @return
     */
    public static JSONObject querySmsSign(String signName) {
        CommonRequest request = commonRequest();
        request.setSysAction("QuerySmsSign");
        request.putQueryParameter("SignName", signName);
        JSONObject result = null;
        try {
            log.info("querySmsSign requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("querySmsSign response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl querySmsSign error", e);
            throw new ClientServiceException("查询短信签名失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 添加阿里云短信模板（每天最多可以申请100个模板，间隔建议您控制在30S以上）
     *
     * @param model
     * @return
     */
    public static JSONObject addSmsTemplate(SmsTemplateSet model) {
        CommonRequest request = commonRequest();
        request.setSysAction("AddSmsTemplate");
        request.putQueryParameter("TemplateType", model.getTemplateType() + "");
        request.putQueryParameter("TemplateName", model.getTemplateName());
        request.putBodyParameter("TemplateContent", model.getTemplateContent());
        request.putBodyParameter("Remark", model.getRemark());
        JSONObject result = null;
        try {
            log.info("addSmsTemplate requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("addSmsTemplate response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl addSmsTemplate error", e);
            throw new ClientServiceException("添加短信模板失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 修改阿里云短信模板（每天最多可以申请100个模板）
     *
     * @param model
     * @return
     */
    public static JSONObject modifySmsTemplate(SmsTemplateSet model) {
        CommonRequest request = commonRequest();
        request.setSysAction("ModifySmsTemplate");
        request.putQueryParameter("TemplateType", model.getTemplateType() + "");
        request.putQueryParameter("TemplateName", model.getTemplateName());
        request.putBodyParameter("TemplateContent", model.getTemplateContent());
        request.putBodyParameter("Remark", model.getRemark());
        request.putQueryParameter("TemplateCode", model.getTemplateCode());
        JSONObject result = null;
        try {
            log.info("modifySmsTemplate requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("modifySmsTemplate response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl modifySmsSign error", e);
            throw new ClientServiceException("修改短信模板失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 删除阿里云短信模板（不支持删除正在审核中的模板）
     *
     * @param templateCode 模板code
     * @return
     */
    public static JSONObject deleteSmsTemplate(String templateCode) {
        CommonRequest request = commonRequest();
        request.setSysAction("DeleteSmsTemplate");
        request.putQueryParameter("TemplateCode", templateCode);
        JSONObject result = null;
        try {
            log.info("deleteSmsTemplate requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("deleteSmsTemplate response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl deleteSmsTemplate error", e);
            throw new ClientServiceException("删除短信模板失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 查询阿里云短信模板
     *
     * @param templateCode 模板code
     * @return
     */
    public static JSONObject querySmsTemplate(String templateCode) {
        CommonRequest request = commonRequest();
        request.setSysAction("QuerySmsTemplate");
        request.putQueryParameter("TemplateCode", templateCode);
        JSONObject result = null;
        try {
            log.info("querySmsTemplate requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("querySmsTemplate response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl querySmsTemplate error", e);
            throw new ClientServiceException("查询短信模板失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 发送短信，向多个手机发送相同模板内容（在一次请求中，最多可以向1000个手机号码发送同样内容的短信）
     *
     * @param mobiles 手机号，多个以逗号隔开
     * @param signName 签名名称
     * @param templateCode 短信模板code
     * @param templateParam 短信模板变量JSON对象，例如 {"code1":"32"}
     * @return BizId 回执id
     */
    public static String sendSms(String mobiles, String signName, String templateCode, JSONObject templateParam) {
        CommonRequest request = commonRequest();
        request.setSysAction("sendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putBodyParameter("PhoneNumbers", mobiles);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putBodyParameter("TemplateParam", templateParam.toJSONString());
        JSONObject result = null;
        try {
            log.info("sendSms requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("sendSms response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl sendSms error", e);
            throw new ClientServiceException("短信发送失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result.getString("BizId");
    }

    /**
     * 批量发送短信，向不同手机发送不同模板内容（在一次请求中，最多可以向100个手机号码分别发送短信）
     * @param mobiles 手机号JSON数组["1590***0000","13500***000"]
     * @param signNameJson 签名JSON数组 ["阿里云","阿里巴巴"]
     * @param templateCode 短信模板code
     * @param templateParamJson 短信模板变量值JSON数组 [{"code1":"32","code2":"张三"},{"code1":"22","code2":"李四"}]，可空，如果有值，则变量值的个数必须与手机号码、签名的个数相同、内容一一对应
     * @return BizId 回执id
     */
    public static String sendBatchSms(JSONArray mobiles, JSONArray signNameJson, String templateCode, JSONArray templateParamJson) {
        CommonRequest request = commonRequest();
        request.setSysAction("SendBatchSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putBodyParameter("PhoneNumberJson", mobiles.toJSONString());
        request.putBodyParameter("SignNameJson", signNameJson.toJSONString());
        request.putQueryParameter("TemplateCode", templateCode);
        request.putBodyParameter("TemplateParamJson", templateParamJson.toJSONString());
        JSONObject result = null;
        try {
            log.info("SendBatchSms requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("SendBatchSms response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl SendBatchSms error", e);
            throw new ClientServiceException("短信发送失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException(result.getString("Message"), OPERATION_FAIL);
        }
        return result.getString("BizId");
    }

    /**
     * 查询阿里云短信发送详情
     *
     * @param mobile 手机号 国内短信：11位手机号码，例如15900000000。国际/港澳台消息：国际区号+号码，例如85200000000。
     * @param sendDate 发送日期：yyyyMMdd，最近30天
     * @param currentPage 当前页
     * @param pageSize 记录数1-50
     * @param bizId 发送回执ID,可空
     * @return
     */
    public static JSONObject querySendDetails(String mobile, String sendDate, String currentPage, String pageSize, String bizId) {
        CommonRequest request = commonRequest();
        request.setSysAction("QuerySendDetails");
        request.putQueryParameter("PhoneNumber", mobile);
        request.putQueryParameter("SendDate", sendDate);
        request.putQueryParameter("CurrentPage", currentPage);
        request.putQueryParameter("PageSize", pageSize);
        if (StringHelper.isEmpty(bizId)) {
            bizId = "";
        }
        request.putQueryParameter("BizId", bizId);
        JSONObject result = null;
        try {
            log.info("querySendDetails requestParam: {}", request.getSysQueryParameters());
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("querySendDetails response: {}", data);
            result = JSONObject.parseObject(data);
        } catch (Exception e) {
            log.error("AliyunSmsUtl querySendDetails error", e);
            throw new ClientServiceException("查询短信发送详情失败", OPERATION_FAIL);
        }
        return result;
    }
}
