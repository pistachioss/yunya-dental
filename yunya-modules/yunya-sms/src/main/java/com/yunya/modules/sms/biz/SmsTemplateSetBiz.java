package com.yunya.modules.sms.biz;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yunya.feign.sms.form.SmsTemplateSetForm;
import com.yunya.feign.sms.model.SmsTemplateSetModel;
import com.yunya.feign.sms.query.SmsAutosendEventQueryForm;
import com.yunya.feign.sms.query.SmsTemplateSetQueryForm;
import com.yunya.feign.sms.vo.SmsAutosendEventVO;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.feign.sms.vo.SmsTemplateSetVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.SmsTemplateItemEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.sms.SmsTemplateSet;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsSenseEnum;
import com.yunya.modules.sms.enums.SmsTypeEnum;
import com.yunya.modules.sms.mapper.SmsTemplateSetMapper;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import com.yunya.modules.sms.vo.SmsTemplateReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介：短信模板设置业务层
 *
 * @author: chenlin
 * @Description: 短信模板设置业务层
 * @Date: 2020/12/11 17:40
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsTemplateSetBiz extends BaseBiz<SmsTemplateSetMapper, SmsTemplateSet> {

    @Autowired
    private SmsSignatureSetBiz smsSignatureSetBiz;
    @Autowired
    private SmsAutosendEventBiz smsAutosendEventBiz;
    /** 延迟2.5小时 */
    private final int LATER_TIME = 150;

    /**
     * 分页查询短信模板列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<SmsTemplateSetVO> findSmsTemplateSetList(SmsTemplateSetQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<SmsTemplateSetVO> smsTemplateSetVOS = mapper.findSmsTemplateSetList(queryForm);
        if (queryForm.getNeedPreview()) {
            smsTemplateSetVOS.forEach(templateSetVO -> {
                JSONObject template = getTemplate(templateSetVO.getTemplateContent(),
                        templateSetVO.getTemplateItem(), templateSetVO.getSignName());
                templateSetVO.setTemplateContentPreview(template.getString("template"));
            });
        }
        return smsTemplateSetVOS;
    }

    /**
     * 添加短信模板
     *
     * @param smsTemplateSetModel 模板设置添加模型
     */
    public void add(SmsTemplateSetModel smsTemplateSetModel) {
        Integer signatureId = smsTemplateSetModel.getSignatureId();
        SmsSignatureSetVO smsSignatureSetVO = smsSignatureSetBiz.findSmsSignatureSetById(signatureId);
        if (smsSignatureSetVO==null
            || !SmsApprovalStatusEnum.APPROVAL_PASS.getCode().equals(smsSignatureSetVO.getSignStatus())) {
            throw new ClientServiceException("短信签名暂不可用！", PARAMETERS_IS_ILLEGAL);
        }
        byte type = checkSense(smsTemplateSetModel.getSense(), smsTemplateSetModel.getTemplateItem());
        String templateName = smsTemplateSetModel.getTemplateName();
        uniqueTemplateName(templateName, null);
        JSONObject template = getTemplate(smsTemplateSetModel.getTemplateContent(),
                smsTemplateSetModel.getTemplateItem(), smsSignatureSetVO.getSignName());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        SmsTemplateSet smsTemplateSet = new SmsTemplateSet();
        BeanUtil.copyProperties(smsTemplateSetModel, smsTemplateSet);
        smsTemplateSet.setTemplateType(type);
        smsTemplateSet.setTemplateStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        smsTemplateSet.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        smsTemplateSet.setCrtId(userId);
        smsTemplateSet.setUptId(userId);
        smsTemplateSet.setCrtUser(BaseContextHandler.getName());
        smsTemplateSet.setTemplateLength(template.getInteger("length"));
        int count = mapper.insert(smsTemplateSet);
        if (count != 1) {
            throw new ClientServiceException("插入数据失败", INSERT_MODEL);
        }
        smsTemplateSet.setTemplateContent(template.getString("template"));
        JSONObject result = AliyunSmsUtl.addSmsTemplate(smsTemplateSet);
        smsTemplateSet.setTemplateContent(null);
        smsTemplateSet.setTemplateCode(result.getString("TemplateCode"));
        updateSelectiveById(smsTemplateSet);
    }

    /**
     * 检查场景
     *
     * @param sense
     * @param templateItem
     */
    private byte checkSense(Byte sense, String templateItem) {
        byte type = SmsTypeEnum.SMS_NOTIFY.getCode();
        if (SmsSenseEnum.RETRIEVE_PWD_VERIFYCODE.getCode().equals(sense)
                ||SmsSenseEnum.DEVICE_BINDING_VERIFYCODE.getCode().equals(sense)) {
            type = SmsTypeEnum.VERIFY_CODE.getCode();
            if (StringHelper.isNotEmpty(templateItem)) {
                String[] items = templateItem.split(",");
                for (int i = 0; i < items.length; i++) {
                    if (!items[i].equals(SmsTemplateItemEnum.VERIFY_CODE.getCode()+"")) {
                        throw new ClientServiceException(SmsSenseEnum.getValue(sense) + "的模板参数只能是验证码！", PARAMETERS_IS_ILLEGAL);
                    }
                }
            }
        }
        return type;
    }

    /**
     * 组织成阿里云需要的模板
     *
     * @param templateContent
     * @param templateItem
     * @return 其中template-替换占位符@为${code}之后的模板内容；length-模板有效字数（包含头部的签名，不包含模板变量及其占位符）
     */
    private static JSONObject getTemplate(String templateContent, String templateItem, String signName) {
        JSONObject result = new JSONObject();
        StringBuilder template = new StringBuilder();
        int size = StringHelper.countChild("@",templateContent);
        int length = signName.length() + 2;//【短信签名】
        String[] contents = templateContent.split("@");
        String firstTmp = contents[0];
        template.append(firstTmp);
        if (size>0 && StringHelper.isNotEmpty(templateItem)) {
            if (templateContent.indexOf("@") == -1) {
                throw new ClientServiceException("模板格式不正确！", PARAMETERS_IS_ILLEGAL);
            }
            String[] items = templateItem.split(",");
            if (size != items.length) {
                throw new ClientServiceException("模板格式不正确！", PARAMETERS_IS_ILLEGAL);
            }
            length += firstTmp.length();
            Map<String, Integer> repeat = new HashMap<>(items.length);
            for (int i = 0; i < items.length; i++) {
                String code = items[i];
                String action = SmsTemplateItemEnum.getAction(code);
                Integer reNum = repeat.get(code);
                if (reNum == null) {
                    reNum = 0;
                    template.append("${").append(action).append("}");
                } else {
                    template.append("${re").append(reNum).append(action).append("}");
                }
                repeat.put(code, ++reNum);
                if (i+1 < contents.length) {
                    String tmp = contents[i + 1];
                    template.append(tmp);
                    length += tmp.length();
                }
            }
        } else {
            if (size>0 && StringHelper.isEmpty(templateItem)) {
                throw new ClientServiceException("模板参数缺失！", PARAMETERS_IS_ILLEGAL);
            }
            if (size<=0 && StringHelper.isNotEmpty(templateItem)) {
                throw new ClientServiceException("模板参数不一致！", PARAMETERS_IS_ILLEGAL);
            }
        }
        result.put("template", template.toString());
        result.put("length", length);
        return result;
    }

    /**
     * 检查模板名称是否重复
     *
     * @param templateName 模板名称
     * @param id 模板id
     */
    private void uniqueTemplateName(String templateName, Integer id) {
        SmsTemplateSetQueryForm queryForm = new SmsTemplateSetQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setTemplateName(templateName);
        queryForm.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        List<SmsTemplateSetVO> smsTemplateSetVOS = findSmsTemplateSetList(queryForm);
        if (id == null) {//新增
            if (smsTemplateSetVOS!=null && !smsTemplateSetVOS.isEmpty()) {
                throw new ClientServiceException("该模板名称与系统中已有模板重复，不允许新增！", DATA_EXIST);
            }
        } else {
            if (smsTemplateSetVOS != null && !smsTemplateSetVOS.isEmpty()) {
                for (SmsTemplateSetVO smsTemplateSetVO : smsTemplateSetVOS) {
                    if (!id.equals(smsTemplateSetVO.getId())) {
                        throw new ClientServiceException("该模板名称与系统中已有模板重复，不允许新增！", DATA_EXIST);
                    }
                }
            }
        }
    }

    /**
     * 修改短信模板
     *
     * @param smsTemplateSetForm 模板设置修改模型
     */
    public void update(SmsTemplateSetForm smsTemplateSetForm) {
        Integer id = smsTemplateSetForm.getId();
        SmsAutosendEventQueryForm queryForm = new SmsAutosendEventQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setTemplateId(id);
        List<SmsAutosendEventVO> smsAutosendEventVOS = smsAutosendEventBiz.findSmsAutosendEventList(queryForm);
        if (smsAutosendEventVOS!=null && !smsAutosendEventVOS.isEmpty()) {
            throw new ClientServiceException("该模板已关联了自动发送事件，请先取消事件", DELETE_NOT_ALLOW);
        }
        SmsTemplateSetVO smsTemplateSetVO = findSmsTemplateSetById(id);
        if (smsTemplateSetVO == null || StringHelper.isEmpty(smsTemplateSetVO.getTemplateCode())) {
            throw new ClientServiceException("短信签名不存在", DATA_NOT_EXIST);
        }
        if (SmsApprovalStatusEnum.APPROVALING.getCode().equals(smsTemplateSetVO.getTemplateStatus())) {
            throw new ClientServiceException("短信模板正在审核", OPERATION_NOT_ALLOW);
        }
        byte type = checkSense(smsTemplateSetForm.getSense(), smsTemplateSetForm.getTemplateItem());
        boolean needApproval = needApproval(smsTemplateSetForm, smsTemplateSetVO, type);
        JSONObject template = getTemplate(smsTemplateSetForm.getTemplateContent(),
                smsTemplateSetForm.getTemplateItem(), smsTemplateSetVO.getSignName());
        uniqueTemplateName(smsTemplateSetForm.getTemplateName(), id);
        SmsTemplateSet smsTemplateSet = new SmsTemplateSet();
        BeanUtil.copyProperties(smsTemplateSetVO, smsTemplateSet);
        BeanUtil.copyProperties(smsTemplateSetForm, smsTemplateSet);
        if (needApproval) {
            smsTemplateSet.setTemplateStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        }
        smsTemplateSet.setTemplateType(type);
        smsTemplateSet.setTemplateLength(template.getInteger("length"));
        updateSelectiveById(smsTemplateSet);
        if (needApproval) {
            smsTemplateSet.setTemplateContent(template.getString("template"));
            AliyunSmsUtl.modifySmsTemplate(smsTemplateSet);
        }
    }

    /**
     * 判断是否需要阿里云审批模板
     *
     * @param smsTemplateSetForm
     * @param smsTemplateSetVO
     * @return
     */
    private boolean needApproval(SmsTemplateSetForm smsTemplateSetForm, SmsTemplateSetVO smsTemplateSetVO, byte type) {
        boolean needApproval = false;
        String templateContent = smsTemplateSetVO.getTemplateContent();
        String remark = smsTemplateSetVO.getRemark();
        String templateName = smsTemplateSetVO.getTemplateName();
        String templateItem = smsTemplateSetVO.getTemplateItem();
        if (!templateContent.equals(smsTemplateSetForm.getTemplateContent())
            || !templateName.equals(smsTemplateSetForm.getTemplateName())
            || !remark.equals(smsTemplateSetForm.getRemark())
            || !templateItem.equals(smsTemplateSetForm.getTemplateItem())
            || !smsTemplateSetVO.getTemplateType().equals(type)) {
            needApproval = true;
        }
        return needApproval;
    }

    /**
     * 删除短信模板
     *
     * @param id 主键id
     */
    public void delete(Integer id) {
        SmsAutosendEventQueryForm queryForm = new SmsAutosendEventQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setTemplateId(id);
        List<SmsAutosendEventVO> smsAutosendEventVOS = smsAutosendEventBiz.findSmsAutosendEventList(queryForm);
        if (smsAutosendEventVOS!=null && !smsAutosendEventVOS.isEmpty()) {
            throw new ClientServiceException("该模板已关联了自动发送事件，请先取消事件", DELETE_NOT_ALLOW);
        }
        SmsTemplateSetVO smsTemplateSetVO = findSmsTemplateSetById(id);
        if (smsTemplateSetVO == null) {
            throw new ClientServiceException("短信签名不存在", DATA_NOT_EXIST);
        }
        String templateCode = smsTemplateSetVO.getTemplateCode();
        Byte templateStatus = smsTemplateSetVO.getTemplateStatus();
        if (StringHelper.isEmpty(templateCode) || SmsApprovalStatusEnum.APPROVALING.getCode().equals(templateStatus)) {
            throw new ClientServiceException("审核中的短信模板不能删除", DELETE_NOT_ALLOW);
        }
        deleteById(id);
        AliyunSmsUtl.deleteSmsTemplate(templateCode);
    }

    /**
     * 根据主键id获取短信模板设置
     * @param id 主键id
     * @return
     */
    public SmsTemplateSetVO findSmsTemplateSetById(Integer id) {
        return findSmsTemplateSetById(id, false);
    }

    /**
     * 根据主键id获取短信模板设置
     *
     * @param id 主键id
     * @param needPreview 是否需要模板预览
     * @return
     */
    public SmsTemplateSetVO findSmsTemplateSetById(Integer id, boolean needPreview) {
        SmsTemplateSetVO smsTemplateSetVO = mapper.findSmsTemplateSetById(id);
        if (smsTemplateSetVO!=null && needPreview) {
            String preview = getTemplatePreview(smsTemplateSetVO.getTemplateContent(),
                    smsTemplateSetVO.getTemplateItem(), smsTemplateSetVO.getSignName());
            smsTemplateSetVO.setTemplateContentPreview(preview);
        }
        return smsTemplateSetVO;
    }

    /**
     * 获取模板预览
     * @param templateContent 模板内容
     * @param templateItem 模板变量列表
     * @param signName 签名名称
     * @return
     */
    private String getTemplatePreview(String templateContent, String templateItem, String signName) {
        StringBuilder preview = new StringBuilder("【");
        preview.append(signName).append("】");
        String[] contents = templateContent.split("@");
        preview.append(contents[0]);
        if (StringHelper.isNotEmpty(templateItem)) {
            String[] items = templateItem.split(",");
            for (int i = 0; i < items.length; i++) {
                int code = Integer.parseInt(items[i]);
                preview.append("[").append(SmsTemplateItemEnum.getValue(code))
                        .append("]");
                if (i+1 < contents.length) {
                    preview.append(contents[i+1]);
                }
            }
        }
        return preview.toString();
    }

    public void uptSelectiveById(SmsTemplateSetVO smsTemplateSetVO) {
        Date now = new Date(System.currentTimeMillis());
        SmsTemplateSet smsTemplateSet = new SmsTemplateSet();
        BeanUtil.copyProperties(smsTemplateSetVO, smsTemplateSet);
        smsTemplateSet.setUptTime(now);
        smsTemplateSet.setUptId(-999);
        mapper.updateByPrimaryKeySelective(smsTemplateSet);
    }

    /**
     * 根据事件code查询模板信息
     *
     * @param eventCode 事件模板
     * @return
     */
    public SmsTemplateSetVO findSmsTemplateByEventCode(String eventCode) {
        return mapper.findSmsTemplateByEventCode(eventCode);
    }

    /**
     * 阿里云短信模板审核推送通知
     *
     * @param
     */
    public void templateSmsReport(List<SmsTemplateReportVO> smsTemplateReportVOS) {
        SmsTemplateReportVO smsTemplateReportVO = smsTemplateReportVOS.get(0);
        String templateCode = smsTemplateReportVO.getTemplate_code();
        String templateStatus = smsTemplateReportVO.getTemplate_status();
        SmsTemplateSetQueryForm queryForm = new SmsTemplateSetQueryForm();
        queryForm.setTemplateCode(templateCode);
        queryForm.setWhetherPage(false);
        List<SmsTemplateSetVO> smsTemplateSetList = findSmsTemplateSetList(queryForm);
        if (smsTemplateSetList==null || smsTemplateSetList.isEmpty()) {
            return;
        }
        /**
         * approving：审核中。
         * approved：审核通过。
         * rejected：审核未通过。
         */
        SmsTemplateSetVO smsTemplateSetVO = smsTemplateSetList.get(0);
        Byte status = SmsApprovalStatusEnum.APPROVALING.getCode();
        if ("approved".equals(templateStatus)) {
            status = SmsApprovalStatusEnum.APPROVAL_PASS.getCode();
        } else if ("rejected".equals(templateStatus)) {
            status = SmsApprovalStatusEnum.APPROVAL_FAIL.getCode();
        }
        smsTemplateSetVO.setTemplateStatus(status);
        uptSelectiveById(smsTemplateSetVO);
    }
}
