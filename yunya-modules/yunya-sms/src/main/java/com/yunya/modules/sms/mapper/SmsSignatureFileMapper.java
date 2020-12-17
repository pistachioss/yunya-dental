package com.yunya.modules.sms.mapper;

import com.yunya.models.sms.SmsSignatureFile;
import tk.mybatis.mapper.common.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SmsSignatureFileMapper extends Mapper<SmsSignatureFile> {

    /**
     * 根据签名id查询资质文件列表
     *
     * @param signId 签名id
     * @return
     */
    List<SmsSignatureFile> findSmsSignatureFilesBySignId(@Param("signId") Integer signId);

    int insertAll(@Param("list") List<SmsSignatureFile> list);
}