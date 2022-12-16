package com.yunya.modules.sms.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.sms.model.*;
import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.vo.SmsSendRecordVO;
import com.yunya.feign.sms.vo.SmsSendSituationVO;
import com.yunya.feign.sms.vo.SmsSendVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.MedicalOrganizationInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsTemplateItemEnum;
import com.yunya.framework.common.enums.YiLianBaoServicePackageEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.appointment.Appointment;
import com.yunya.models.sms.SmsSendBatch;
import com.yunya.models.sms.SmsSendRecord;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsEnableEnum;
import com.yunya.modules.sms.enums.SmsSendStatusEnum;
import com.yunya.modules.sms.enums.SmsTypeEnum;
import com.yunya.modules.sms.mapper.SmsSendRecordMapper;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import com.yunya.modules.sms.vo.SmsSendReportVO;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.COMPANY_ORGID;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.enums.SmsTemplateItemEnum.*;
import static java.util.stream.Collectors.toMap;

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
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsSendBatchBiz smsSendBatchBiz;
    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;
    @Autowired
    private SmsOrgStatisticsBiz smsOrgStatisticsBiz;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private RemoteAppointmentFeign remoteAppointmentFeign;
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
        SmsSendSituationVO smsSendSituationVO = mapper.sumSmsSendSituation(queryForm);
        List<SmsSendRecordVO> smsSendRecordList = findSmsSendRecordList(queryForm);
        PageInfo<SmsSendRecordVO> result = new PageInfo<>(smsSendRecordList);
        int total = 0;
        int success = 0;
        int failure = 0;
        if (smsSendSituationVO != null) {
            total = smsSendSituationVO.getTotal();
            success = smsSendSituationVO.getSuccess();
            failure = smsSendSituationVO.getFailure();
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
    public SmsTemplateSetVO checkTemplateSetInfo(String eventCode, Integer templateId, Integer orgId) {
        SmsTemplateSetVO smsTemplateSetVO = null;
        if (StringHelper.isNotEmpty(eventCode)) {
            smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateByEventCode(eventCode, orgId);
            if (smsTemplateSetVO == null) {
                throw new ClientServiceException("事件未关联模板", DATA_NOT_EXIST);
            }
            if (smsTemplateSetVO.getSendEnable().equals(SmsEnableEnum.DISABLE.getCode())) {
                throw new ClientServiceException("未开启短信自动发送事件", OPERATION_NOT_ALLOW);//没有开启自动发送事件
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
     * @param smsVerifyCodeModel
     * @return
     */
    public ResponseResult sendVerifyCode(SmsVerifyCodeModel smsVerifyCodeModel) {
        Integer orgId = COMPANY_ORGID;
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(smsVerifyCodeModel.getEventCode(), null, orgId);
        if (StringHelper.isNull(smsTemplateSetVO)) {
            throw new ClientServiceException("短信模板暂不可用！", OPERATION_NOT_ALLOW);
        }
        String signName = smsTemplateSetVO.getSignName();
        if (StringHelper.isEmpty(signName)) {
            throw new ClientServiceException("短信模板不存在，请先添加短信模板或关联短信模板",OPERATION_NOT_ALLOW);
        }
        Integer userId = smsVerifyCodeModel.getUserId();
        String name = smsVerifyCodeModel.getName();
        redisUtils.lockedFunc(RedisConstants.LOCK_SMS_ORG_STATISTICS + orgId, sms->{
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            checkSmsBalance(surplusNum);
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, smsTemplateSetVO.getId(),
                    SmsTypeEnum.VERIFY_CODE.getCode(), 1, userId, name);
            String content = smsTemplateSetVO.getTemplateContent();
            int count = StringHelper.countChild("@", content);
            JSONObject param = new JSONObject();
            String[] items = splitTemplateItem(smsTemplateSetVO.getTemplateItem());
            if (StringHelper.isNotEmpty(items)) {
                param.put(SmsTemplateItemEnum.getAction(items[0]), smsVerifyCodeModel.getVerifyCode());
            }
            if (param.size() != count) {
                throw new ClientServiceException("模板参数值缺失", PARAMETERS_IS_ILLEGAL);
            }
            StringBuilder builder = parseSmsContent(items,
                    content.split("@"), signName, param);
            int len = getContentLength(builder);
            checkSmsBalance(surplusNum - len);
            String mobile = smsVerifyCodeModel.getMobile();
            Integer recordId = insertSelective(orgId, batchId, builder, mobile,
                    userId, name, userId, name);
            String bizId = AliyunSmsUtl.sendSms(mobile, signName, smsTemplateSetVO.getTemplateCode(), param);
            try {
                updateBizIdAndSurplusNum(bizId, surplusNum, len, orgId, batchId, userId, Arrays.asList(recordId));
            } catch (Exception e) {
                log.error("update bizId error: ", e);
                log.error("update bizId={}, surplusNum={}, orgId={}", bizId, surplusNum, orgId);
            }
            return null;
        });
        return ResponseUtil.success();
    }

    /**
     * 通过自动发送事件来发送短信
     *
     * @param model 短信参数模型
     * @return
     */
    public ResponseResult<T> batchSendByEventCode(SmsAutoEventSendRecordModel model) {
        return batchSend(model.getEventCode(),
                null,
                model.getUserId(),
                model.getName(),
                model.getOrgId(),
                model.getModels());
    }

    public ResponseResult<T> batchSendByTemplateId(SmsTemplateIdRecordModel model) {
        return batchSendByTemplateId(model.getTemplateId(),
                model.getUserId(),
                model.getName(),
                model.getOrgId(),
                model.getModels());
    }

    /**
     * 通过模板id来发送短信
     *
     * @param templateId 模板id
     * @param userId 操作人id
     * @param name 操作人姓名
     * @param orgId 组织id
     * @param models 短信参数模型
     * @return
     */
    public ResponseResult<T> batchSendByTemplateId(Integer templateId, Integer userId, String name, Integer orgId, List<? extends SmsCommonSendRecordModel> models) {
        return batchSend(null,
                templateId,
                userId,
                name,
                orgId,
                models);
    }

    /**
     * 批量发送短信（不同短信内容）
     * @param templateId
     * @param models
     * @return
     */
    public ResponseResult batchSend(String eventCode, Integer templateId, Integer userId, String name, Integer orgId, List<? extends SmsCommonSendRecordModel> models) {
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(eventCode, templateId, orgId);
        if (smsTemplateSetVO == null) {
            throw new ClientServiceException("短信模板暂不可用！", OPERATION_NOT_ALLOW);
        }
        String signName = smsTemplateSetVO.getSignName();
        if (StringHelper.isEmpty(signName)) {
            throw new ClientServiceException("短信模板不存在，请先添加短信模板或关联短信模板",OPERATION_NOT_ALLOW);
        }
        redisUtils.lockedFunc(RedisConstants.LOCK_SMS_ORG_STATISTICS + orgId, sms->{
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            checkSmsBalance(surplusNum);
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, templateId, SmsTypeEnum.SMS_NOTIFY.getCode(), models.size(), userId, name);
            String content = smsTemplateSetVO.getTemplateContent();
            int count = StringHelper.countChild("@", content);
            String[] contents = content.split("@");
            String[] items = splitTemplateItem(smsTemplateSetVO.getTemplateItem());
            JSONArray mobiles = new JSONArray();
            JSONArray signNames = new JSONArray();
            JSONArray templateParamJson = new JSONArray();
            List<Integer> recordIds = new ArrayList<>(models.size());
            int len = 0;
            for (SmsCommonSendRecordModel model : models) {
                String mobile = model.getMobile();
                JSONObject param = model.getTemplateParam();
                if (param.size() < count) {
                    throw new ClientServiceException("模板参数值缺失", PARAMETERS_IS_ILLEGAL);
                }
                StringBuilder builder = parseSmsContent(items, contents, signName, param);
                len += getContentLength(builder);
                Integer recordId = insertSelective(orgId, batchId, builder, mobile, model.getReceiverId(), model.getSendObject(), userId, name);
                mobiles.add(mobile);
                signNames.add(signName);
                templateParamJson.add(param);
                recordIds.add(recordId);
            }
            checkSmsBalance(surplusNum - len);
            String bizId = AliyunSmsUtl.sendBatchSms(mobiles, signNames, smsTemplateSetVO.getTemplateCode(), templateParamJson);
            try {
                updateBizIdAndSurplusNum(bizId, surplusNum, len, orgId, batchId, userId, recordIds);
            } catch (Exception e) {
                log.error("update bizId error", e);
                log.error("update bizId={}, surplusNum={}, orgId={}", bizId, surplusNum, orgId);
            }
            return null;
        });
        return ResponseUtil.success();
    }

    /**
     * 分割模板参数
     *
     * @param templateItem
     * @return
     */
    private String[] splitTemplateItem(String templateItem) {
        String[] items = null;
        if (StringHelper.isNotEmpty(templateItem)) {
            items = templateItem.split(",");
        }
        return items;
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
        builder.append("【").append(signName).append("】");
        if (StringHelper.isNotEmpty(contents)) {
            builder.append(contents[0]);
        }
        Map<String, Integer> repeat = new HashMap<>();
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                String key = SmsTemplateItemEnum.getAction(item);
                Integer reNum = repeat.get(item);
                if (reNum == null) {
                    reNum = 0;
                } else {
                    key = "re" + reNum + key;
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
    public ResponseResult batchSend(Integer orgId, Integer userId, String name, SmsBatchSendRecordModel model) {
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(null, model.getTemplateId(), orgId);
        if (StringHelper.isNull(smsTemplateSetVO)) {
            throw new ClientServiceException("短信模板暂不可用！", OPERATION_NOT_ALLOW);
        }
        String signName = smsTemplateSetVO.getSignName();
        if (StringHelper.isEmpty(signName)) {
            throw new ClientServiceException("短信模板不存在，请先添加短信模板或关联短信模板",OPERATION_NOT_ALLOW);
        }
        redisUtils.lockedFunc(RedisConstants.LOCK_SMS_ORG_STATISTICS + orgId, sms->{
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            checkSmsBalance(surplusNum);
            String[] mobiles = model.getMobiles().split(",");
            JSONArray templateParamJson = model.getTemplateParamJson();
            Map<Integer, List<StringBuilder>> res = parseSmsContent(mobiles, smsTemplateSetVO, templateParamJson);
            if (res.containsKey(-1)) {
                throw new ClientServiceException(res.get(-1).get(0).toString(), QUERY_RESULT_INVALID);
            }
            List<StringBuilder> builders = res.get("0");
            List<String> sendObjects = model.getSendObjects();
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, model.getTemplateId(), SmsTypeEnum.SMS_NOTIFY.getCode(), sendObjects.size(), userId, name);
            JSONArray phoneNumberJson = new JSONArray();
            JSONArray signNameJson = new JSONArray();
            List<Integer> userIds = model.getReceiverIds();
            List<Integer> recordIds = new ArrayList<>(mobiles.length);
            int len = 0;
            for (int i = 0; i < mobiles.length; i++) {
                String mobile = mobiles[i];
                Integer optId = null;
                if (userIds != null && !userIds.isEmpty()) {
                    optId = userIds.get(i);
                }
                StringBuilder content = builders.get(i);
                len += getContentLength(content);
                Integer recordId = insertSelective(orgId, batchId, content, mobile, optId, sendObjects.get(i), userId, name);
                signNameJson.add(signName);
                phoneNumberJson.add(mobile);
                recordIds.add(recordId);
            }
            checkSmsBalance(surplusNum - len);
            String bizId = AliyunSmsUtl.sendBatchSms(phoneNumberJson, signNameJson, smsTemplateSetVO.getTemplateCode(), templateParamJson);
            try {
                updateBizIdAndSurplusNum(bizId, surplusNum, len, orgId, batchId, userId, recordIds);
            } catch (Exception e) {
                log.error("update bizId error: ",e);
                log.error("update bizId={}, surplusNum={}, orgId={}",bizId,surplusNum,orgId);
            }
            return null;
        });
        return ResponseUtil.success();
    }

    /**
     * 批量发送同内容的短信
     *
     * @param model
     * @return
     */
    public ResponseResult sendRecord(Integer orgId, Integer userId, String name, SmsSendRecordModel model) {
        SmsTemplateSetVO smsTemplateSetVO = checkTemplateSetInfo(null, model.getTemplateId(), orgId);
        if (smsTemplateSetVO == null) {
            throw new ClientServiceException("短信模板暂不可用！", OPERATION_NOT_ALLOW);
        }
        String signName = smsTemplateSetVO.getSignName();
        if (StringHelper.isEmpty(signName)) {
            throw new ClientServiceException("短信模板不存在，请先添加短信模板或关联短信模板",OPERATION_NOT_ALLOW);
        }
        redisUtils.lockedFunc(RedisConstants.LOCK_SMS_ORG_STATISTICS + orgId, sms->{
            int surplusNum = smsOrgStatisticsBiz.findSmsOrgStatisticsSurplusByOrgId(orgId);
            checkSmsBalance(surplusNum);
            String[] mobiles = model.getMobiles().split(",");
            JSONArray templateParamJson = new JSONArray();
            templateParamJson.add(model.getTemplateParamJson());
            Map<Integer, List<StringBuilder>> res = parseSmsContent(mobiles, smsTemplateSetVO, templateParamJson);
            if (res.containsKey(-1)) {
                throw new ClientServiceException(res.get(-1).get(0).toString(),QUERY_RESULT_INVALID);
            }
            List<StringBuilder> builders = res.get("0");
            List<String> sendObjects = model.getSendObjects();
            Integer batchId = smsSendBatchBiz.insertEntity(orgId, model.getTemplateId(), SmsTypeEnum.SMS_NOTIFY.getCode(), sendObjects.size(), userId, name);
            List<Integer> userIds = model.getReceiverIds();
            List<Integer> recordIds = new ArrayList<>(mobiles.length);
            int len = 0;
            for (int i = 0; i < mobiles.length; i++) {
                String mobile = mobiles[i];
                Integer uptId = null;
                if (StringHelper.isNotEmpty(userIds)) {
                    uptId = userIds.get(i);
                }
                StringBuilder content = builders.get(0);
                len += getContentLength(content);
                Integer recordId = insertSelective(orgId, batchId, content, mobile, uptId, sendObjects.get(i), userId, name);
                recordIds.add(recordId);
            }
            checkSmsBalance(surplusNum - len);
            String bizId = AliyunSmsUtl.sendSms(model.getMobiles(), signName, smsTemplateSetVO.getTemplateCode(), model.getTemplateParamJson());
            try {
                updateBizIdAndSurplusNum(bizId, surplusNum, len, orgId, batchId, userId, recordIds);
            } catch (Exception e) {
                log.error("update bizId error: ",e);
                log.error("update bizId={}, surplusNum={}, orgId={}",bizId,surplusNum,orgId);
            }
            return null;
        });
        return ResponseUtil.success();
    }


    /**
     * 检查短信余额
     *
     * @param surplusNum
     */
    private void checkSmsBalance(int surplusNum) {
        if (surplusNum <= 0) {
            throw new ClientServiceException("短信余额不足！", BALANCE_INSUFFICIENT);
        }
    }

    /**
     * 更新发送记录和统计
     *
     * @param bizId 业务id
     * @param surplusNum 可用数量
     * @param count 本次短信发送总条数
     * @param orgId 组织id
     * @param batchId 发送批次id
     * @param recordIds 发送记录id
     */
    private void updateBizIdAndSurplusNum(String bizId, int surplusNum, int count, Integer orgId, Integer batchId, Integer userId, List<Integer> recordIds) {
        smsOrgStatisticsBiz.decrByOrgId(surplusNum-count, orgId, userId);
        SmsSendBatch smsSendBatch = new SmsSendBatch();
        smsSendBatch.setId(batchId);
        smsSendBatch.setBizId(bizId);
        smsSendBatch.setSendNum(count);
        Date now = new Date(System.currentTimeMillis());
        smsSendBatch.setUptTime(now);
        smsSendBatch.setUptId(userId);
        smsSendBatchBiz.uptSelectiveById(smsSendBatch);
        for (Integer recordId : recordIds) {
            SmsSendRecord smsSendRecord = new SmsSendRecord();
            smsSendRecord.setId(recordId);
            smsSendRecord.setBizId(bizId);
            smsSendRecord.setUptTime(now);
            smsSendRecord.setUptId(userId);
            mapper.updateByPrimaryKeySelective(smsSendRecord);
        }
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
                    .append("】");
            if (StringHelper.isNotEmpty(contents)) {
                builder.append(contents[0]);
            }
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
                    String key = SmsTemplateItemEnum.getAction(code);
                    if (reNum == null) {
                        reNum = 0;
                    } else {
                        key = "re" + reNum + key;
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
     * @param receiverId 接收短信的人员
     * @param sendObject 发送对象
     */
    public Integer insertSelective(Integer orgId, Integer batchId, StringBuilder content, String mobile, Integer receiverId, String sendObject, Integer userId, String name) {
        Date now = new Date(System.currentTimeMillis());
        SmsSendRecord smsSendRecord = new SmsSendRecord();
        smsSendRecord.setOrgId(orgId);
        smsSendRecord.setBatchId(batchId);
        smsSendRecord.setContent(content.toString());
        smsSendRecord.setMobile(mobile);
        smsSendRecord.setReceiverId(receiverId);
        smsSendRecord.setCrtUser(name);
        smsSendRecord.setCrtId(userId);
        smsSendRecord.setCrtTime(now);
        smsSendRecord.setUptId(userId);
        smsSendRecord.setUptTime(now);
        smsSendRecord.setSendObject(sendObject);
        smsSendRecord.setStatus(SmsSendStatusEnum.SENDING.getCode());
        smsSendRecord.setContentNum(getContentLength(content));
        int count = mapper.insert(smsSendRecord);
        if (count != 1) {
            throw new ClientServiceException("插入短信发送记录失败", OperationCodeConstants.INSERT_MODEL);
        }
        return smsSendRecord.getId();
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
            contentNum = length / 67;
            if (length % 67 > 0) {
                ++contentNum;
            }
        }
        return contentNum;
    }

    /**
     * 发送预约短信
     *
     * @param templateId 短信模板id
     * @param models 短信预约提醒列表
     * @return
     */
    public ResponseResult<T> sendAppointmentBatchSms(Integer templateId, List<AppointmentSmsSendRecordModel> models) {
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        SmsTemplateSetVO smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateSetById(templateId);
        if (smsTemplateSetVO == null) {
            throw new ClientServiceException("该短信模板不存在", DATA_NOT_EXIST);
        }
        String templateItem = smsTemplateSetVO.getTemplateItem();
        if (StringHelper.isNotEmpty(templateItem)) {
            String code2 = null;
            String code3 = null;
            String code4 = null;
            if (templateItem.indexOf(SmsTemplateItemEnum.CLINIC_NAME.getCode()+"")!=-1
                    ||templateItem.indexOf(SmsTemplateItemEnum.CLINIC_PHONE.getCode()+"")!=-1
                    ||templateItem.indexOf(SmsTemplateItemEnum.CLINIC_ADDRESS.getCode()+"")!=-1) {
                MedicalOrganizationInfoVO medicalOrganizationInfoVO = remoteSystemServiceFeign.clinicExtInfoByCompanyId(orgId);
                code2 = medicalOrganizationInfoVO.getAbbreviation();
                code3 = medicalOrganizationInfoVO.getTel();
                code4 = medicalOrganizationInfoVO.getAddress();
            }
            Map<Integer, Appointment> appointMap = findAppointment(models);
            String[] items = templateItem.split(",");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (AppointmentSmsSendRecordModel model : models) {
                JSONObject object = new JSONObject();
                Map<String, Integer> repeat = new HashMap<>();
                for (String item : items) {
                    Integer reNum = repeat.get(item);
                    String key = SmsTemplateItemEnum.getAction(item);
                    if (reNum == null) {
                        reNum = 0;
                    } else {
                        key = "re" + reNum + key;
                    }
                    repeat.put(item, ++reNum);
                    Integer code = Integer.parseInt(item);
                    if (PATIENT_NAME.equals(code)) {// 患者姓名
                        object.put(key, model.getSendObject());
                    } else if (CLINIC_NAME.equals(code)) { // 诊所名称
                        object.put(key,code2);
                    } else if (CLINIC_PHONE.equals(code)) {// 诊所电话
                        object.put(key,code3);
                    } else if (CLINIC_ADDRESS.equals(code)) {// 诊所地址
                        object.put(key,code4);
                    } else if (APPOINTMENT_DOCTOR.equals(code)) {// 预约医生姓名
                        object.put(key, model.getDentistName());
                    } else if (APPOINTMENT.getCode().equals(code)) { // 预约时间
                        String code7 = model.getAppointDate() + " " + model.getAppointTime();
                        object.put(key, code7);
                    } else if (SmsTemplateItemEnum.APPELLATION.getCode().equals(code)) { // 先生/女士/小朋友
                        Integer age = model.getAge();
                        String code7 = "先生";
                        Integer gener = model.getGender();
                        if (age != null) {
                            code7 = "小朋友";
                            if (age>15 && gener!=null && gener==1) {
                                code7 = "女生";
                            }
                        } else {
                            if (gener!=null && gener==1) {
                                code7 = "女生";
                            }
                        }
                        object.put(key, code7);
                    } else if (MORNING_AFTERNOON.equals(code)) {// 上午/下午
                        String code7 = model.getAppointDate() + " " + model.getAppointTime() + ":59";
                        String middleStr = model.getAppointDate() + " 12:00:00";
                        String lastStr = model.getAppointDate() + " 00:00:00";
                        try {
                            Date appointDateTime = sdf.parse(code7);
                            Date middle = sdf.parse(middleStr);
                            Date last = sdf.parse(lastStr);
                            String code9 = "上午";
                            if (appointDateTime.after(middle) && appointDateTime.before(last)) {
                                code9 = "下午";
                            }
                            object.put(key, code9);
                        } catch (ParseException e) {
                            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                        }
                    } else if (YILIANBAO_SERVICE_PACKAGE.equals(code)) {
                        Appointment appointment = appointMap.get(model.getAppointId());
                        if (StringHelper.isNotNull(appointment)) {
                            String packageName = YiLianBaoServicePackageEnum.contains(appointment.getAppointContent());
                            object.put(key, packageName);
                        }
                    } else { // 其他
                        throw new ClientServiceException("模板有误，模板参数与模板适用场景不匹配", OPERATION_NOT_ALLOW);
                    }
                }
                model.setTemplateParam(object);
            }
        }
        batchSendByTemplateId(templateId, Integer.parseInt(BaseContextHandler.getUserID()),
                BaseContextHandler.getName(), orgId, models);
        return ResponseUtil.success(null);
    }

    /**
     * 查询预约列表
     *
     * @param models
     * @return
     */
    private Map<Integer, Appointment> findAppointment(List<AppointmentSmsSendRecordModel> models) {
        List<Integer> appointIds = models.stream().map(AppointmentSmsSendRecordModel::getAppointId).collect(Collectors.toList());
        List<Appointment> appointments = remoteAppointmentFeign.findAppointmentListByIds(appointIds);
        return Optional.ofNullable(appointments).orElseGet(ArrayList::new)
                .stream().collect(toMap(Appointment::getId, Function.identity()));
    }

    /**
     * 阿里云短信发送状态推送通知
     *
     * @param
     */
    public void smsReport(List<SmsSendReportVO> smsSendReportVOS) {
        Map<String, List<SmsSendReportVO>> reportMap = new HashMap<>(smsSendReportVOS.size());
        smsSendReportVOS.forEach(reportVO->{
            List<SmsSendReportVO> list = reportMap.get(reportVO.getBiz_id());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(reportVO);
            reportMap.put(reportVO.getBiz_id(), list);
        });
        SmsSendRecordQueryForm queryForm = new SmsSendRecordQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setBizIds(reportMap.keySet());
        queryForm.setStatus(SmsSendStatusEnum.SENDING.getCode());
        List<SmsSendRecordVO> smsSendRecordVOS = findSmsSendRecordList(queryForm);
        Map<Integer, Integer> rebates = new HashMap<>(16);
        for (SmsSendRecordVO smsSendRecordVO : smsSendRecordVOS) {
            String bizId = smsSendRecordVO.getBizId();
            String mobile = smsSendRecordVO.getMobile();
            List<SmsSendReportVO> list = reportMap.get(bizId);
            if (list != null) {
                Date now = new Date(System.currentTimeMillis());
                for (SmsSendReportVO smsSendReportVO : list) {
                    if (mobile.equals(smsSendReportVO.getPhone_number())) {
                        SmsSendRecord smsSendRecord = new SmsSendRecord();
                        smsSendRecord.setId(smsSendRecordVO.getId());
                        Byte status = SmsSendStatusEnum.SEND_SUCC.getCode();
                        if (!smsSendReportVO.getSuccess()) {
                            status = SmsSendStatusEnum.SEND_FAIL.getCode();
                            Integer rebate = rebates.get(smsSendRecord.getOrgId());
                            if (rebate == null) {
                                rebate = 0;
                            }
                            rebates.put(smsSendRecordVO.getOrgId(), rebate+smsSendRecordVO.getContentNum());
                        }
                        smsSendRecord.setBizMsg(smsSendReportVO.getErr_msg());
                        smsSendRecord.setStatus(status);
                        smsSendRecord.setUptTime(now);
                        mapper.updateByPrimaryKeySelective(smsSendRecord);
                        break;
                    }
                }
            }
        }
        //发送失败，返补短信
        if (StringHelper.isNotEmpty(rebates)) {
            rebates.forEach((orgId, rebate)-> smsOrgStatisticsBiz.incrByOrgId(null, rebate,null, orgId));
        }
    }

    public void uptSelectiveById(SmsSendRecord smsSendRecord) {
        mapper.updateByPrimaryKeySelective(smsSendRecord);
    }
}
