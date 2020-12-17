package com.yunya.modules.sms.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.model.SmsSendRecordModel;
import com.yunya.feign.sms.query.SmsSendRecordQueryForm;
import com.yunya.feign.sms.vo.SmsOrgStatisticsVO;
import com.yunya.feign.sms.vo.SmsSendRecordVO;
import com.yunya.feign.sms.vo.SmsSendVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.sms.SmsSendRecord;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsSendStatusEnum;
import com.yunya.modules.sms.mapper.SmsSendRecordMapper;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 简介：短信发送记录业务层
 *
 * @author: chenlin
 * @Description: 短信发送记录业务层
 * @Date: 2020/12/14 14:32
 * @since: 1.0.0
 */
@Service
@Transactional
public class SmsSendRecordBiz extends BaseBiz<SmsSendRecordMapper, SmsSendRecord> {

    @Autowired
    private SmsSendBatchBiz smsSendBatchBiz;
    @Autowired
    private SmsTemplateSetBiz smsTemplateSetBiz;
    @Autowired
    private SmsOrgStatisticsBiz smsOrgStatisticsBiz;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

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
     * 批量发送短信
     *
     * @param model 短信发送添加模型
     */
    public ResponseResult<T> batchSend(SmsSendRecordModel model) {
        Integer orgId = model.getOrgId();
        Integer templateId = model.getTemplateId();
        SmsTemplateSetVO smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateSetById(templateId);
        if (smsTemplateSetVO == null) {
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"短信模板不存在",null);
        }
        String templateCode = smsTemplateSetVO.getTemplateCode();
        Byte templateStatus = smsTemplateSetVO.getTemplateStatus();
        if (!SmsApprovalStatusEnum.APPROVAL_PASS.getCode().equals(templateStatus)
                || StringHelper.isEmpty(templateCode)) {
            return ResponseUtil.fail(OperationCodeConstants.OPERATION_NOT_ALLOW,"短信模板暂不可用！",null);
        }
        SmsOrgStatisticsVO smsOrgStatisticsVO = smsOrgStatisticsBiz.findSmsOrgStatisticsByOrgId(orgId);
        if (smsOrgStatisticsVO == null) {
            return ResponseUtil.fail(OperationCodeConstants.OPERATION_NOT_ALLOW,"短信余额不足！",null);
        }
        Integer surplusNum = smsOrgStatisticsVO.getSurplusNum();
        if (surplusNum==null || surplusNum==0) {
            return ResponseUtil.fail(OperationCodeConstants.OPERATION_NOT_ALLOW,"短信余额不足！",null);
        }
        String[]  mobiles = model.getMobiles().split(",");
        JSONArray templateParamJson = model.getTemplateParamJson();
        Map<Integer, List<StringBuilder>> res = parseSmsContent(mobiles, smsTemplateSetVO, templateParamJson, surplusNum);
        if (res.containsKey(-1)) {
            return ResponseUtil.fail(OperationCodeConstants.QUERY_RESULT_INVALID,res.get(-1).get(0).toString(),null);
        }
        List<StringBuilder> builders = res.get("0");
        Integer sendUserId = model.getSendUserId();
        String crtUser = getSendUserNameById(sendUserId);
        Date sendTime = model.getSendTime();
        List<String> sendObjects = model.getSendObjects();
        Integer batchId = smsSendBatchBiz.insertEntity(orgId, templateId,
                sendUserId, sendTime, crtUser, model.getType(), sendObjects.size());
        String signName = smsTemplateSetVO.getSignName();
        JSONArray phoneNumberJson = new JSONArray();
        JSONArray signNameJson = new JSONArray();
        List<Integer> userIds = model.getReceiverIds();
        for (int i = 0; i < mobiles.length; i++) {
            String mobile = mobiles[i];
            Integer userId = null;
            if (userIds!=null && !userIds.isEmpty()) {
                userId = userIds.get(i);
            }
            insertSelective(orgId, batchId, builders.get(i), mobile, crtUser,
                    sendUserId, sendTime, userId, sendObjects.get(i));
            signNameJson.add(signName);
            phoneNumberJson.add(mobile);
        }
        JSONObject smsResponse = AliyunSmsUtl.SendBatchSms(phoneNumberJson,signNameJson,templateCode,templateParamJson);
        return ResponseUtil.success();
    }

    /**
     * 发送短信验证码
     *
     * @param model
     * @return
     */
    public ResponseResult<T> sendVerfyCode(SmsSendRecordModel model) {
        Integer orgId = model.getOrgId();
        Integer templateId = model.getTemplateId();
        SmsTemplateSetVO smsTemplateSetVO = smsTemplateSetBiz.findSmsTemplateSetById(templateId);
        if (smsTemplateSetVO == null) {
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"短信模板不存在",null);
        }
        String templateCode = smsTemplateSetVO.getTemplateCode();
        Byte templateStatus = smsTemplateSetVO.getTemplateStatus();
        if (!SmsApprovalStatusEnum.APPROVAL_PASS.getCode().equals(templateStatus)
                || StringHelper.isEmpty(templateCode)) {
            return ResponseUtil.fail(OperationCodeConstants.QUERY_RESULT_INVALID,"短信模板暂不可用！",null);
        }
        SmsOrgStatisticsVO smsOrgStatisticsVO = smsOrgStatisticsBiz.findSmsOrgStatisticsByOrgId(orgId);
        if (smsOrgStatisticsVO == null) {
            return ResponseUtil.fail(OperationCodeConstants.OPERATION_NOT_ALLOW,"短信余额不足！",null);
        }
        Integer surplusNum = smsOrgStatisticsVO.getSurplusNum();
        if (surplusNum==null || surplusNum==0) {
            return ResponseUtil.fail(OperationCodeConstants.OPERATION_NOT_ALLOW,"短信余额不足！",null);
        }
        String[]  mobiles = model.getMobiles().split(",");
        JSONArray templateParamJson = model.getTemplateParamJson();
        Map<Integer, List<StringBuilder>> res = parseSmsContent(mobiles, smsTemplateSetVO, templateParamJson, surplusNum);
        if (res.containsKey(-1)) {
            return ResponseUtil.fail(OperationCodeConstants.QUERY_RESULT_INVALID,res.get(-1).get(0).toString(),null);
        }
        List<StringBuilder> builders = res.get("0");
        Integer sendUserId = model.getSendUserId();
        String crtUser = getSendUserNameById(sendUserId);
        Date sendTime = model.getSendTime();
        List<String> sendObjects = model.getSendObjects();
        Integer batchId = smsSendBatchBiz.insertEntity(orgId, templateId,
                sendUserId, sendTime, crtUser, model.getType(), sendObjects.size());
        String signName = smsTemplateSetVO.getSignName();
        List<Integer> userIds = model.getReceiverIds();
        for (int i = 0; i < mobiles.length; i++) {
            String mobile = mobiles[i];
            Integer userId = null;
            if (userIds!=null && !userIds.isEmpty()) {
                userId = userIds.get(i);
            }
            insertSelective(orgId, batchId, builders.get(0), mobile, crtUser,
                    sendUserId, sendTime, userId, sendObjects.get(i));
        }
        JSONObject smsResponse = AliyunSmsUtl.sendSms(model.getMobiles(), signName, templateCode, templateParamJson.getJSONObject(0));
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
    private Map<Integer, List<StringBuilder>> parseSmsContent(String[] mobiles, SmsTemplateSetVO smsTemplateSetVO, JSONArray templateParamJson, Integer surplusNum) {
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
                for (int j = 0; j < codes.length; j++) {
                    String code = codes[j];
                    String value = object.getString("code" + code);
                    if (StringHelper.isEmpty(value)) {
                        sb.append("短信模板第").append(i).append("个参数对象的值code").append(code).append("缺失！");
                    }
                    content.append(value);
                    if (j < contents.length-1) {
                        content.append(contents[j+1]);
                    }
                }
                surplusNum -= getContentLength(content);
            }
            if (sb.length() > 0) {
                result.put(-1, Arrays.asList(sb));
                return result;
            }
        }
        if (surplusNum <= 0) {
            result.put(-1, Arrays.asList(new StringBuilder("短信余额不足！")));
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
     * @param crtUser 短信发送人
     * @param sendUserId 发送人id
     * @param sendTime 发送时间
     * @param userId 接收短信的人员
     * @param sendObject 发送对象
     */
    public void insertSelective(Integer orgId, Integer batchId, StringBuilder content, String mobile, String crtUser, Integer sendUserId, Date sendTime, Integer userId, String sendObject) {
        SmsSendRecord smsSendRecord = new SmsSendRecord();
        smsSendRecord.setOrgId(orgId);
        smsSendRecord.setBatchId(batchId);
        smsSendRecord.setContent(content.toString());
        smsSendRecord.setMobile(mobile);
        smsSendRecord.setReceiverId(userId);
        smsSendRecord.setCrtUser(crtUser);
        smsSendRecord.setCrtId(sendUserId);
        smsSendRecord.setCrtTime(sendTime);
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
