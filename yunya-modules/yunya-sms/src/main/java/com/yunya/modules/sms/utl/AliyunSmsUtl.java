package com.yunya.modules.sms.utl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> queryParams = new HashMap<>(16);
        Map<String, Object> bodyParams = new HashMap<>(1);
        queryParams.put("SignName", model.getSignName());
        queryParams.put("SignSource", model.getSignSource()+"");
        bodyParams.put("Remark", model.getRemark());
        JSONObject result = null;
        try {
            if (StringHelper.isNotEmpty(files)) {
                for (int i = 0; i < files.size(); i++) {
                    MultipartFile file = files.get(i);
                    queryParams.put("SignFileList." + (i+1) + ".FileSuffix", Files.getFileExtension(file.getOriginalFilename()));
                    bodyParams.put("SignFileList." + (i+1) + ".FileContents", BinaryUtil.toBase64String(file.getBytes()));
                }
            }
            result = aliyunSmsClient("AddSmsSign", queryParams, bodyParams);
        } catch (Exception e) {
            log.error("AliyunSmsUtl addSmsSign error", e);
            throw new ClientServiceException("添加短信签名失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("添加短信签名失败", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(16);
        Map<String, Object> bodyParams = new HashMap<>(1);
        queryParams.put("SignName", model.getSignName());
        queryParams.put("SignSource", model.getSignSource()+"");
        bodyParams.put("Remark", model.getRemark());
        JSONObject result = null;
        try {
            if (StringHelper.isNotEmpty(files)) {
                for (int i = 0; i < files.size(); i++) {
                    MultipartFile file = files.get(i);
                    queryParams.put("SignFileList." + (i+1) + ".FileSuffix", Files.getFileExtension(file.getOriginalFilename()));
                    bodyParams.put("SignFileList." + (i+1) + ".FileContents", BinaryUtil.toBase64String(file.getBytes()));
                }
            }
            result = aliyunSmsClient("ModifySmsSign", queryParams, bodyParams);
        } catch (Exception e) {
            log.error("AliyunSmsUtl modifySmsSign error", e);
            throw new ClientServiceException("修改短信签名失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("修改短信签名失败", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(1);
        queryParams.put("SignName", signName);
        JSONObject result = null;
        try {
            result = aliyunSmsClient("DeleteSmsSign", queryParams, null);
        } catch (Exception e) {
            log.error("删除短信签名失败", e);
            throw new ClientServiceException("AliyunSmsUtl deleteSmsSign error", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("删除短信签名失败", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(1);
        queryParams.put("SignName", signName);
        JSONObject result = null;
        try {
            result = aliyunSmsClient("QuerySmsSign", queryParams, null);
        } catch (Exception e) {
            log.error("AliyunSmsUtl querySmsSign error", e);
            throw new ClientServiceException("查询短信签名失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("查询短信签名失败", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(2);
        Map<String, Object> bodyParams = new HashMap<>(2);
        queryParams.put("TemplateType", model.getTemplateType() + "");
        queryParams.put("TemplateName", model.getTemplateName());
        bodyParams.put("TemplateContent", model.getTemplateContent());
        bodyParams.put("Remark", model.getRemark());
        JSONObject result = null;
        try {
            result = aliyunSmsClient("AddSmsTemplate", queryParams, bodyParams);
        } catch (Exception e) {
            log.error("AliyunSmsUtl addSmsTemplate error", e);
            throw new ClientServiceException("添加短信模板失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("添加短信模板失败", OPERATION_FAIL);
        }
        return result;
    }

    /**
     * 调用阿里云短信接口
     *
     * @param action
     * @param queryParams
     * @param bodyParams
     * @return
     * @throws ClientException
     */
    public static JSONObject aliyunSmsClient(String action, Map<String, String> queryParams, Map<String, Object> bodyParams) throws ClientException {
        return aliyunSmsClient(action, null, null, queryParams, bodyParams);
    }

    /**
     * 调用阿里云短信接口
     *
     * @param action
     * @param pathParams
     * @param headParams
     * @param queryParams
     * @param bodyParams
     * @return
     * @throws ClientException
     */
    public static JSONObject aliyunSmsClient(String action, Map<String, String> pathParams, Map<String, String> headParams, Map<String, String> queryParams, Map<String, Object> bodyParams) throws ClientException {
        CommonRequest request = commonRequest();
        request.setSysAction(action);
        if (StringHelper.isNotEmpty(pathParams)) {
            pathParams.forEach((key, value) -> request.putHeadParameter(key, value));
        }
        if (StringHelper.isNotEmpty(headParams)) {
            headParams.forEach((key, value) -> request.putHeadParameter(key, value));
        }
        if (StringHelper.isNotEmpty(queryParams)) {
            queryParams.forEach((key, value) -> request.putQueryParameter(key, value));
        }
        if (StringHelper.isNotEmpty(bodyParams)) {
            bodyParams.forEach((key, value) -> request.putBodyParameter(key, value));
        }
        log.info("{} queryParam: {}, bodyParam: {}, headParam: {}, pathParam: {}", action,
                request.getSysQueryParameters(), request.getSysBodyParameters(), request.getSysHeadParameters(), request.getSysPathParameters());
        CommonResponse response = client.getCommonResponse(request);
        String data = response.getData();
        log.info("{} response: {}", action, data);
        JSONObject result = JSONObject.parseObject(data);
        return result;
    }

    /**
     * 修改阿里云短信模板（每天最多可以申请100个模板）
     *
     * @param model
     * @return
     */
    public static JSONObject modifySmsTemplate(SmsTemplateSet model) {
        Map<String, String> queryParams = new HashMap<>(3);
        Map<String, Object> bodyParams = new HashMap<>(2);
        queryParams.put("TemplateType", model.getTemplateType() + "");
        queryParams.put("TemplateCode", model.getTemplateCode());
        queryParams.put("TemplateName", model.getTemplateName());
        bodyParams.put("TemplateContent", model.getTemplateContent());
        bodyParams.put("Remark", model.getRemark());
        JSONObject result = null;
        try {
            result = aliyunSmsClient("ModifySmsTemplate", queryParams, bodyParams);
        } catch (Exception e) {
            log.error("AliyunSmsUtl modifySmsSign error", e);
            throw new ClientServiceException("修改短信模板失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("修改短信模板失败", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(1);
        queryParams.put("TemplateCode", templateCode);
        JSONObject result = null;
        try {
            result = aliyunSmsClient("DeleteSmsTemplate", queryParams, null);
        } catch (Exception e) {
            log.error("AliyunSmsUtl deleteSmsTemplate error", e);
            throw new ClientServiceException("删除短信模板失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("删除短信模板失败！", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(1);
        queryParams.put("TemplateCode", templateCode);
        JSONObject result = null;
        try {
            result = aliyunSmsClient("QuerySmsTemplate", queryParams, null);
        } catch (Exception e) {
            log.error("AliyunSmsUtl querySmsTemplate error", e);
            throw new ClientServiceException("查询短信模板失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("查询短信模板失败", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(3);
        Map<String, Object> bodyParams = new HashMap<>(2);
        queryParams.put("SignName", signName);
        queryParams.put("TemplateCode", templateCode);
        bodyParams.put("PhoneNumbers", mobiles);
        bodyParams.put("TemplateParam", templateParam.toJSONString());
        JSONObject result = null;
        try {
            result = aliyunSmsClient("sendSms", queryParams, bodyParams);
        } catch (Exception e) {
            log.error("AliyunSmsUtl sendSms error", e);
            throw new ClientServiceException("短信发送失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("短信发送失败！", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(2);
        Map<String, Object> bodyParams = new HashMap<>(3);
        queryParams.put("TemplateCode", templateCode);
        bodyParams.put("PhoneNumberJson", mobiles.toJSONString());
        bodyParams.put("SignNameJson", signNameJson.toJSONString());
        bodyParams.put("TemplateParamJson", templateParamJson.toJSONString());
        JSONObject result = null;
        try {
            result = aliyunSmsClient("SendBatchSms", queryParams, bodyParams);
        } catch (Exception e) {
            log.error("AliyunSmsUtl SendBatchSms error", e);
            throw new ClientServiceException("短信批量发送失败！", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("短信批量发送失败！", OPERATION_FAIL);
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
        Map<String, String> queryParams = new HashMap<>(5);
        queryParams.put("PhoneNumber", mobile);
        queryParams.put("SendDate", sendDate);
        queryParams.put("CurrentPage", currentPage);
        queryParams.put("PageSize", pageSize);
        queryParams.put("BizId", StringHelper.defaultString(bizId));
        JSONObject result = null;
        try {
            result = aliyunSmsClient("QuerySendDetails", queryParams, null);
        } catch (Exception e) {
            log.error("AliyunSmsUtl querySendDetails error", e);
            throw new ClientServiceException("查询短信发送详情失败", OPERATION_FAIL);
        }
        if (result==null || !"OK".equals(result.getString("Code"))) {
            throw new ClientServiceException("查询短信发送详情失败！", OPERATION_FAIL);
        }
        return result;
    }
}
