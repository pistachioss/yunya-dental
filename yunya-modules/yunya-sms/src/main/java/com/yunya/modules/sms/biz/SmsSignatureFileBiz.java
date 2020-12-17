package com.yunya.modules.sms.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.sms.SmsSignatureFile;
import com.yunya.modules.sms.mapper.SmsSignatureFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简介：短信签名的资质证明文件业务层
 *
 * @author: chenlin
 * @Description: 短信签名的资质证明文件业务层
 * @Date: 2020/12/11 14:44
 * @since: 1.0.0
 */
@Service
@Transactional
public class SmsSignatureFileBiz extends BaseBiz<SmsSignatureFileMapper, SmsSignatureFile> {

    public void saveFile(Integer signId, List<SmsSignatureFile> smsSignatureFiles) {
        saveFile(signId, smsSignatureFiles, false);
    }

    /**
     * 保存短信签名的资质文件
     *
     * @param signId 签名id
     * @param smsSignatureFiles 文件列表
     * @param needDelete 是否先删除
     */
    public void saveFile(Integer signId, List<SmsSignatureFile> smsSignatureFiles, boolean needDelete) {
        if (needDelete) {
            SmsSignatureFile smsSignatureFile = new SmsSignatureFile();
            smsSignatureFile.setSignatureId(signId);
            delete(smsSignatureFile);
        }
        if (smsSignatureFiles!=null && !smsSignatureFiles.isEmpty()) {
            mapper.insertAll(smsSignatureFiles);
        }
    }

    /**
     * 根据签名id查询资质文件列表
     *
     * @param signId 签名id
     * @return
     */
    public List<SmsSignatureFile> findSmsSignatureFilesBySignId(Integer signId) {
        return mapper.findSmsSignatureFilesBySignId(signId);
    }
}
