package com.yunya.modules.sms.biz;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.yunya.feign.sms.form.SmsSignatureSetForm;
import com.yunya.feign.sms.model.SmsSignatureSetModel;
import com.yunya.feign.sms.query.SmsSignatureSetQueryForm;
import com.yunya.feign.sms.vo.SmsSignatureSetVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.sms.SmsSignatureSet;
import com.yunya.modules.sms.enums.SmsApprovalStatusEnum;
import com.yunya.modules.sms.enums.SmsSignatureSourceEnum;
import com.yunya.modules.sms.mapper.SmsSignatureSetMapper;
import com.yunya.modules.sms.utl.AliyunSmsUtl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介：短信签名设置业务层
 *
 * @author: chenlin
 * @Description: 短信签名设置业务层
 * @Date: 2020/12/10 20:57
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SmsSignatureSetBiz extends BaseBiz<SmsSignatureSetMapper, SmsSignatureSet> {
    /** 延迟2.5个小时 */
    private static final int LATER_TIME = 150;
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * 分页查询短信签名列表
     *
     * @param smsSignatureSetQueryForm 查询参数
     * @return
     */
    public List<SmsSignatureSetVO> findSmsSignatureSetList(SmsSignatureSetQueryForm smsSignatureSetQueryForm) {
        if (smsSignatureSetQueryForm.getWhetherPage()) {
            PageHelper.startPage(smsSignatureSetQueryForm.getPageNum(), smsSignatureSetQueryForm.getPageSize());
        }
        return mapper.findSmsSignatureSetList(smsSignatureSetQueryForm);
    }

    /**
     * 添加短信签名
     *
     * @param files
     * @param smsSignatureSetModel 签名设置添加模型
     */
    public void add(List<MultipartFile> files, SmsSignatureSetModel smsSignatureSetModel) {
        uniqueSignName(smsSignatureSetModel.getSignName(), null);
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        String user = BaseContextHandler.getName();
        Date now = new Date(System.currentTimeMillis());
        SmsSignatureSet smsSignatureSet = new SmsSignatureSet();
        BeanUtil.copyProperties(smsSignatureSetModel, smsSignatureSet);
        smsSignatureSet.setOrgId(orgId);
        smsSignatureSet.setSignStatus(SmsApprovalStatusEnum.APPROVALING.getCode());
        smsSignatureSet.setSignSource(SmsSignatureSourceEnum.ENTERPRISE.getCode());
        smsSignatureSet.setCrtId(userId);
        smsSignatureSet.setCrtUser(user);
        smsSignatureSet.setCrtTime(now);
        smsSignatureSet.setUptId(userId);
        smsSignatureSet.setUptTime(now);
        int count = mapper.insert(smsSignatureSet);
        if (count != 1) {
            throw new ClientServiceException("插入数据失败", OperationCodeConstants.INSERT_MODEL);
        }
        AliyunSmsUtl.addSmsSign(smsSignatureSetModel, files);
        asyncStatus(smsSignatureSet);
    }

    /**
     * 两小时后同步一次阿里云短信的模板状态
     * @param smsSignatureSet
     */
    private void asyncStatus(SmsSignatureSet smsSignatureSet) {
        scheduledExecutorService.schedule(()->{
            JSONObject query = AliyunSmsUtl.querySmsTemplate(smsSignatureSet.getSignName());
            String code = query.getString("Code");
            Byte signStatus = query.getByte("SignStatus");
            if ("OK".equals(code) && !SmsApprovalStatusEnum.APPROVALING.getCode().equals(signStatus)) {
                smsSignatureSet.setSignStatus(signStatus);
                updateSelectiveById(smsSignatureSet);
            }
        }, LATER_TIME, TimeUnit.MINUTES);
    }

    /**
     * 根据id获取短信签名
     *
     * @param id 主键id
     */
    public SmsSignatureSetVO findSmsSignatureSetById(Integer id) {
        SmsSignatureSet smsSignatureSet = selectById(id);
        if (smsSignatureSet == null) {
            return null;
        }
        SmsSignatureSetVO smsSignatureSetVO = new SmsSignatureSetVO();
        BeanUtil.copyProperties(smsSignatureSet, smsSignatureSetVO);
        return smsSignatureSetVO;
    }

    /**
     * 修改短信签名（企业用户每天最多可以申请100个签名）
     *
     * @param smsSignatureSetForm 签名设置修改模型
     */
    @Deprecated
    public void update(SmsSignatureSetForm smsSignatureSetForm) {
        Integer id = smsSignatureSetForm.getId();
        SmsSignatureSetVO smsSignatureSetVO = findSmsSignatureSetById(id);
        if (smsSignatureSetVO == null) {
            throw new ClientServiceException("短信签名不存在", DATA_NOT_EXIST);
        }
        Byte signStatus = smsSignatureSetVO.getSignStatus();
        if (SmsApprovalStatusEnum.APPROVALING.getCode().equals(signStatus)) {
            throw new ClientServiceException("审核中的短信签名不能删除", DELETE_NOT_ALLOW);
        }
        uniqueSignName(smsSignatureSetForm.getSignName(), id);
        SmsSignatureSet smsSignatureSet = new SmsSignatureSet();
        BeanUtil.copyProperties(smsSignatureSetForm, smsSignatureSet);
        smsSignatureSet.setSignSource(SmsSignatureSourceEnum.ENTERPRISE.getCode());
        updateSelectiveById(smsSignatureSet);
        AliyunSmsUtl.modifySmsSign(smsSignatureSetForm, null);
        asyncStatus(smsSignatureSet);
    }

    /**
     * 检查签名名称是否重复
     *
     * @param signName 签名名称
     * @param id 签名id
     */
    private void uniqueSignName(String signName, Integer id) {
        SmsSignatureSetQueryForm queryForm = new SmsSignatureSetQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setSignName(signName);
        List<SmsSignatureSetVO> smsSignatureSetVOS = findSmsSignatureSetList(queryForm);
        if (id == null) {
            if (smsSignatureSetVOS!=null && !smsSignatureSetVOS.isEmpty()) {
                throw new ClientServiceException("该签名名称与系统中已有签名重复，不允许新增！", DATA_EXIST);
            }
        } else {
            if (smsSignatureSetVOS != null && !smsSignatureSetVOS.isEmpty()) {
                for (SmsSignatureSetVO signatureSetVO : smsSignatureSetVOS) {
                    if (!signatureSetVO.getId().equals(id)) {
                        throw new ClientServiceException("该签名名称与系统中已有签名重复，不允许新增！", DATA_EXIST);
                    }
                }
            }
        }
    }

    /**
     * 删除短信签名
     *
     * @param id 主键id
     */
    public void delete(Integer id) {
        SmsSignatureSetVO smsSignatureSetVO = findSmsSignatureSetById(id);
        if (smsSignatureSetVO == null) {
            throw new ClientServiceException("短信签名不存在", DATA_NOT_EXIST);
        }
        Byte signStatus = smsSignatureSetVO.getSignStatus();
        if (SmsApprovalStatusEnum.APPROVALING.getCode().equals(signStatus)) {
            throw new ClientServiceException("审核中的短信签名不能删除", DELETE_NOT_ALLOW);
        }
        deleteById(id);
        AliyunSmsUtl.deleteSmsSign(smsSignatureSetVO.getSignName());
    }

    public void uptSelectiveById(SmsSignatureSetVO smsSignatureSetVO) {
        Date now = new Date(System.currentTimeMillis());
        SmsSignatureSet smsSignatureSet = new SmsSignatureSet();
        BeanUtil.copyProperties(smsSignatureSetVO, smsSignatureSet);
        smsSignatureSet.setUptTime(now);
        smsSignatureSet.setUptId(-999);
        mapper.updateByPrimaryKeySelective(smsSignatureSet);
    }
}
