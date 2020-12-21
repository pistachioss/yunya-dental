package com.yunya.modules.sms.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.model.SmsBatchSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.vo.SmsSendRecordVO;
import com.yunya.feign.sms.vo.SmsSendVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.sms.SmsSendRecord;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsSendStatusEnum;
import com.yunya.modules.sms.enums.SmsTypeEnum;
import com.yunya.modules.sms.mapper.SmsSendRecordMapper;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介：短信发送记录业务层
 *
 * @author: chenlin
 * @Description: 短信发送记录业务层
 * @Date: 2020/12/14 14:32
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsSendRecordBiz extends BaseBiz<SmsSendRecordMapper, SmsSendRecord> {

    @Autowired
    private SmsSendBatchBiz smsSendBatchBiz;
    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;
    @Autowired
    private SmsOrgStatisticsBiz smsOrgStatisticsBiz;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 分页查询短信发送记录列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<SmsSendRecordVO> findSmsSendRecordList(SmsSendRecordQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findSmsSendRecordList(queryForm);
    }

    /**
     * 发送记录查询
     *
     * @param queryForm
     * @return
     */
    public SmsSendVO findSmsSendRecordPageList(SmsSendRecordQueryForm queryForm) {
        List<SmsSendRecordVO> smsSendRecordList = findSmsSendRecordList(queryForm);
        PageInfo<SmsSendRecordVO> result = new PageInfo<>(smsSendRecordList);
        int total = 0;
        int success = 0;
        int failure = 0;
        if (smsSendRecordList!=null && !smsSendRecordList.isEmpty()) {
            total = smsSendRecordList.size();
            for (SmsSendRecordVO smsSendRecordVO : smsSendRecordList) {
                Byte status = smsSendRecordVO.getStatus();
                if (SmsSendStatusEnum.SEND_SUCC.getCode().equals(status)) {
                    success++;
                }
                if (SmsSendStatusEnum.SEND_FAIL.getCode().equals(status)) {
                    failure++;
                }
            }
        }
        SmsSendVO smsSendVO = new SmsSendVO();
        smsSendVO.setSendTotal(total);
        smsSendVO.setSendSuccess(success);
        smsSendVO.setSendFailure(failure);
        smsSendVO.setSmsSendRecordPageInfo(result);
        return smsSendVO;
    }

    /**
     * 查询短信模板
     *
     * @param eventCode
     * @param templateId
     * @return
     */
    public SmsTemplateSetVO checkTemplateSetInfo(String eventCode, Integer templateId) {
        SmsTemplateSetVO smsTemplateSetVO = null;
        if (StringHelper.isNotEmpty(eventCode)) {
            smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateByEventCode(eventCode);
            if (smsTemplateSetVO == null) {
                throw new ClientServiceException("事件未关联模板", DATA_NOT_EXIST);
            }
        } else {
            smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateSetById(templateId);
            if (smsTemplateSetVO == null) {
                throw new ClientServiceException("短信模板不存在！", DATA_NOT_EXIST);
            }
        }
        String templateCode = smsTemplateSetVO.getTemplateCode();
        Byte templateStatus = smsTemplateSetVO.getTemplateStatus();
        if (!SmsApprovalStatusEnum.APPROVAL_PASS.getCode().equals(templateStatus)
                || StringHelper.isEmpty(templateCode)) {
            throw new ClientServiceException("短信模板暂不可用！", OPERATION_NOT_ALLOW);
        }
        return smsTemplateSetVO;
    }

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @param verifyCode 验证码
     * @param eventCode 事件编码
     * @return
     */
    public ResponseResult<T> sendVerifyCode(String mobile, String verifyCode, String eventCode) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(eventCode, null);
        try {
            redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS,String.valueOf(orgId), RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS);
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            if (surplusNum <= 0) {
                return ResponseUtil.fail(OPERATION_NOT_ALLOW,"短信余额不足！",null);
            }
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, smsTemplateSetVO.getId(), SmsTypeEnum.SMS_NOTIFY.getCode(), 1);
            String content = smsTemplateSetVO.getTemplateContent();
            int count = StringHelper.countChild("@", content);
            String signName = smsTemplateSetVO.getSignName();
            String templateItem = smsTemplateSetVO.getTemplateItem();
            JSONObject param = new JSONObject();
            if (param.size() != count) {
                throw new ClientServiceException("模板参数值缺失", PARAMETERS_IS_ILLEGAL);
            }
            String[] items = null;
            if (StringHelper.isNotEmpty(templateItem)) {
                items = templateItem.split(",");
                param.put("code0", verifyCode);
            }
            StringBuilder builder = parseSmsContent(items,
                    content.split("@"), signName, param);
            surplusNum -= getContentLength(builder);
            if (surplusNum <= 0) {
                throw new ClientServiceException("短信余额不足！", OPERATION_NOT_ALLOW);
            }
            insertSelective(orgId, batchId, builder, mobile,
                    Integer.parseInt(BaseContextHandler.getUserID()),
                    BaseContextHandler.getName());
            redisUtils.set(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId, surplusNum);
            AliyunSmsUtl.sendSms(mobile, signName, smsTemplateSetVO.getTemplateCode(), param);
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
        return ResponseUtil.success();
    }

    /**
     * 批量发送短信（不同短信内容）
     * @param templateId
     * @param models
     * @return
     */
    public ResponseResult<T> batchSend(Integer templateId, List<? extends SmsCommonSendRecordModel> models) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(null, templateId);
        try {
            redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS,String.valueOf(orgId), RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS);
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            if (surplusNum <= 0) {
                return ResponseUtil.fail(OPERATION_NOT_ALLOW,"短信余额不足！",null);
            }
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, templateId, SmsTypeEnum.SMS_NOTIFY.getCode(), models.size());
            String content = smsTemplateSetVO.getTemplateContent();
            int count = StringHelper.countChild("@", content);
            String[] contents = content.split("@");
            String[] items = smsTemplateSetVO.getTemplateItem().split(",");
            JSONArray mobiles = new JSONArray();
            JSONArray signNames = new JSONArray();
            JSONArray templateParamJson = new JSONArray();
            String signName = smsTemplateSetVO.getSignName();
            for (SmsCommonSendRecordModel model : models) {
                String mobile = model.getMobile();
                JSONObject param = model.getTemplateParam();
                if (param.size() != count) {
                    throw new ClientServiceException("模板参数值缺失", PARAMETERS_IS_ILLEGAL);
                }
                StringBuilder builder = parseSmsContent(items, contents, signName, param);
                surplusNum -= getContentLength(builder);
                insertSelective(orgId, batchId, builder, mobile, model.getReceiverId(), model.getSendObject());
                mobiles.add(mobile);
                signNames.add(signName);
                templateParamJson.add(param);
            }
            if (surplusNum <= 0) {
                throw new ClientServiceException("短信余额不足！", OPERATION_NOT_ALLOW);
            }
            redisUtils.set(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId, surplusNum);
            AliyunSmsUtl.sendBatchSms(mobiles, signNames, smsTemplateSetVO.getTemplateCode(), templateParamJson);
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
        return ResponseUtil.success();
    }

    /**
     * 解析短信内容
     * @param items 参数项列表
     * @param contents 短信模板内容
     * @param signName 短信签名
     * @param param 参数值列表
     * @return
     */
    public StringBuilder parseSmsContent(String[] items, String[] contents, String signName, JSONObject param) {
        StringBuilder builder = new StringBuilder();
        builder.append("【").append(signName).append("】").append(contents[0]);
        Map<String, Integer> repeat = new HashMap<>();
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                String key = "";
                Integer reNum = repeat.get(item);
                if (reNum == null) {
                    reNum = 0;
                    key = "code" + item;
                } else {
                    key = "re" + reNum + "code" + item;
                }
                repeat.put(item, ++reNum);
                String value = param.getString(key);
                if (StringHelper.isNotEmpty(value)) {
                    builder.append(value);
                    if (i < contents.length - 1) {
                        builder.append(contents[i + 1]);
                    }
                }
            }
        }
        return builder;
    }

    /**
     * 批量发送短信
     *
     * @param model 短信发送添加模型
     */
    public ResponseResult<T> batchSend(SmsBatchSendRecordModel model) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(null, model.getTemplateId());
        try {
            redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId), RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS);
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            if (surplusNum <= 0) {
                return ResponseUtil.fail(OPERATION_NOT_ALLOW, "短信余额不足！", null);
            }
            String[] mobiles = model.getMobiles().split(",");
            JSONArray templateParamJson = model.getTemplateParamJson();
            Map<Integer, List<StringBuilder>> res = parseSmsContent(mobiles, smsTemplateSetVO, templateParamJson);
            if (res.containsKey(-1)) {
                return ResponseUtil.fail(OperationCodeConstants.QUERY_RESULT_INVALID, res.get(-1).get(0).toString(), null);
            }
            List<StringBuilder> builders = res.get("0");
            List<String> sendObjects = model.getSendObjects();
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, model.getTemplateId(), SmsTypeEnum.SMS_NOTIFY.getCode(), sendObjects.size());
            String signName = smsTemplateSetVO.getSignName();
            JSONArray phoneNumberJson = new JSONArray();
            JSONArray signNameJson = new JSONArray();
            List<Integer> userIds = model.getReceiverIds();
            for (int i = 0; i < mobiles.length; i++) {
                String mobile = mobiles[i];
                Integer userId = null;
                if (userIds != null && !userIds.isEmpty()) {
                    userId = userIds.get(i);
                }
                StringBuilder content = builders.get(i);
                surplusNum -= getContentLength(content);
                insertSelective(orgId, batchId, content, mobile, userId, sendObjects.get(i));
                signNameJson.add(signName);
                phoneNumberJson.add(mobile);
            }
            if (surplusNum <=0) {
                throw new ClientServiceException("短信余额不足！", OPERATION_NOT_ALLOW);
            }
            redisUtils.set(RedisConstants.SMS_STATISTICS_SURPLUS_ORG + orgId, surplusNum);
            AliyunSmsUtl.sendBatchSms(phoneNumberJson, signNameJson, smsTemplateSetVO.getTemplateCode(), templateParamJson);
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
        return ResponseUtil.success();
    }

    /**
     * 批量发送同内容的短信
     *
     * @param model
     * @return
     */
    public ResponseResult<T> sendRecord(SmsSendRecordModel model) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(null, model.getTemplateId());
        try {
            redisUtils.setLock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId), RedisConstants.SMS_STATISTICS_LOCK_SEC, TimeUnit.SECONDS);
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            if (surplusNum <= 0) {
                return ResponseUtil.fail(OPERATION_NOT_ALLOW, "短信余额不足！", null);
            }
            String[] mobiles = model.getMobiles().split(",");
            JSONArray templateParamJson = new JSONArray();
            templateParamJson.add(model.getTemplateParamJson());
            Map<Integer, List<StringBuilder>> res = parseSmsContent(mobiles, smsTemplateSetVO, templateParamJson);
            if (res.containsKey(-1)) {
                return ResponseUtil.fail(OperationCodeConstants.QUERY_RESULT_INVALID, res.get(-1).get(0).toString(), null);
            }
            List<StringBuilder> builders = res.get("0");
            List<String> sendObjects = model.getSendObjects();
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, model.getTemplateId(), SmsTypeEnum.VERIFY_CODE.getCode(), sendObjects.size());
            String signName = smsTemplateSetVO.getSignName();
            List<Integer> userIds = model.getReceiverIds();
            for (int i = 0; i < mobiles.length; i++) {
                String mobile = mobiles[i];
                Integer userId = null;
                if (userIds != null && !userIds.isEmpty()) {
                    userId = userIds.get(i);
                }
                StringBuilder content = builders.get(0);
                surplusNum -= getContentLength(content);
                insertSelective(orgId, batchId, content, mobile, userId, sendObjects.get(i));
            }
            if (surplusNum <=0) {
                throw new ClientServiceException("短信余额不足！", OPERATION_NOT_ALLOW);
            }
            AliyunSmsUtl.sendSms(model.getMobiles(), signName, smsTemplateSetVO.getTemplateCode(), model.getTemplateParamJson());
        } finally {
            redisUtils.unlock(RedisConstants.LOCK_SMS_ORG_STATISTICS, String.valueOf(orgId));
        }
        return ResponseUtil.success();
    }

    /**
     * 拼接短信真实内容
     *
     * @param mobiles 手机号
     * @param smsTemplateSetVO
     * @param templateParamJson 模板变量值列表
     * @return
     */
    private Map<Integer, List<StringBuilder>> parseSmsContent(String[] mobiles, SmsTemplateSetVO smsTemplateSetVO, JSONArray templateParamJson) {
        Map<Integer, List<StringBuilder>> result = new HashMap<>();
        String[] contents = smsTemplateSetVO.getTemplateContent().split("@");
        List<StringBuilder> builders = new ArrayList<>(mobiles.length);
        for (int i = 0; i < mobiles.length; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append("【").append(smsTemplateSetVO.getSignName())
                    .append("】").append(contents[0]);
            builders.add(builder);
        }
        String templateItem = smsTemplateSetVO.getTemplateItem();
        if (StringHelper.isNotEmpty(templateItem)) {
            String[] codes = templateItem.split(",");
            if (templateParamJson==null || templateParamJson.isEmpty()) {
                StringBuilder sb = new StringBuilder("短信模板参数列表缺失！");
                result.put(-1, Arrays.asList(sb));
                return result;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < templateParamJson.size(); i++) {
                JSONObject object = templateParamJson.getJSONObject(i);
                StringBuilder content = builders.get(i);
                Map<String, Integer> repeat = new HashMap<>();
                for (int j = 0; j < codes.length; j++) {
                    String code = codes[j];
                    Integer reNum = repeat.get(code);
                    String key = "";
                    if (reNum == null) {
                        reNum = 0;
                        key = "code" + code;
                    } else {
                        key = "re" + reNum + "code" + code;
                    }
                    repeat.put(code, ++reNum);
                    String value = object.getString(key);
                    if (StringHelper.isEmpty(value)) {
                        sb.append("短信模板第").append(i).append("个参数对象的值code").append(code).append("缺失！");
                    }
                    content.append(value);
                    if (j < contents.length-1) {
                        content.append(contents[j+1]);
                    }
                }
            }
            if (sb.length() > 0) {
                result.put(-1, Arrays.asList(sb));
                return result;
            }
        }
        result.put(0, builders);
        return result;
    }

    /**
     * 添加短信发送记录
     *
     * @param orgId 门诊id
     * @param batchId 批次id
     * @param content 短信内容
     * @param mobile 接收短信的手机号
     * @param userId 接收短信的人员
     * @param sendObject 发送对象
     */
    public void insertSelective(Integer orgId, Integer batchId, StringBuilder content, String mobile, Integer userId, String sendObject) {
        SmsSendRecord smsSendRecord = new SmsSendRecord();
        smsSendRecord.setOrgId(orgId);
        smsSendRecord.setBatchId(batchId);
        smsSendRecord.setContent(content.toString());
        smsSendRecord.setMobile(mobile);
        smsSendRecord.setReceiverId(userId);
        smsSendRecord.setCrtUser(BaseContextHandler.getName());
        smsSendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        smsSendRecord.setCrtTime(new Date(System.currentTimeMillis()));
        smsSendRecord.setSendObject(sendObject);
        smsSendRecord.setStatus(SmsSendStatusEnum.SENDING.getCode());
        smsSendRecord.setContentNum(getContentLength(content));
        mapper.insertSelective(smsSendRecord);
    }

    /**
     * 计算短信条数
     *
     * @param content
     * @return
     */
    private int getContentLength(StringBuilder content) {
        int contentNum = 1;
        int length = content.length();
        if (length > 70) {
            contentNum = length % 67;
        }
        return contentNum;
    }

    /**
     * 获取操作人姓名
     *
     * @param userId
     * @return
     */
    private String getSendUserNameById(Integer userId) {
        SysUserInfoDetail sysUserInfoDetail = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(userId);
        String employeeName = "";
        if (sysUserInfoDetail == null) {
            employeeName = sysUserInfoDetail.getName();
        }
        return employeeName;
    }
}
