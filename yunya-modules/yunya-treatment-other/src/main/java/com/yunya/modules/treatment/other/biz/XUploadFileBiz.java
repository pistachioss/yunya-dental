package com.yunya.modules.treatment.other.biz;

import cn.hutool.core.io.FileUtil;
import com.yunya.feign.treatment_other.domain.model.XUploadFileModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.FileTypeEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.treatment_other.XUploadFile;
import com.yunya.modules.treatment.other.mapper.XUploadFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/19 16:13
 * @since: 1.0.0
 */
@Service
@Transactional
public class XUploadFileBiz extends BaseBiz<XUploadFileMapper, XUploadFile> {

    public int addBatch(List<XUploadFileModel> files, Integer sourceId, Byte sourceType, Date now) {
        List<XUploadFile> xUploadFiles = new ArrayList<>();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        files.forEach(file -> {
            String fileName = file.getFileName();
            String extName = FileUtil.extName(fileName);
            Byte fileType = FileTypeEnum.getCode(extName);
            XUploadFile entity = new XUploadFile();
            entity.setCrtId(userId);
            entity.setCrtTime(now);
            entity.setFileName(file.getFileName());
            entity.setFileLocation(file.getFileLocation());
            entity.setFileType(fileType);
            entity.setSourceId(sourceId);
            entity.setSourceType(sourceType);
            entity.setUpdId(userId);
            entity.setUpdTime(now);
            xUploadFiles.add(entity);
        });
        return mapper.addBatch(xUploadFiles);
    }

    /**
     * 根据文件ID逻辑删除文件信息
     * @param id
     */
    public void del(Integer id) {
        XUploadFile file = mapper.selectByPrimaryKey(id);
        if (null == file) {
            throw new ClientServiceException("数据不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        file.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        file.setUpdTime(new Date(System.currentTimeMillis()));
        file.setInservice(false);
        mapper.updateByPrimaryKey(file);
    }
}
