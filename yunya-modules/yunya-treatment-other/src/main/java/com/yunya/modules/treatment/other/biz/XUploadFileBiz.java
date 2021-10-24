package com.yunya.modules.treatment.other.biz;

import cn.hutool.core.io.FileUtil;
import com.yunya.feign.treatment_other.domain.form.XUploadFileForm;
import com.yunya.feign.treatment_other.domain.model.XUploadFileModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.FileTypeEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment_other.XUploadFile;
import com.yunya.modules.treatment.other.mapper.XUploadFileMapper;
import com.yunya.modules.treatment.other.utils.TreatmentOtherUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
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
@Slf4j
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
            Date uploadDate = parseUploadTime(file.getUploadTime());
            entity.setUploadTime(uploadDate);
            entity.setUpdId(userId);
            entity.setUpdTime(now);
            xUploadFiles.add(entity);
        });
        return mapper.addBatch(xUploadFiles);
    }

    /**
     * 解析上传日期
     *
     * @param uploadTime
     * @return
     */
    private Date parseUploadTime(String uploadTime) {
        Date date = DateTime.now().toDate();
        if (StringHelper.isNotEmpty(uploadTime)) {
            try {
                date = DateUtil.parse(uploadTime,"yyyy-MM-dd");
            } catch (ParseException e) {
                log.error("解析上传日期错误",e);
                throw new ClientServiceException("时间格式转换异常", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
            }
        }
        return date;
    }

    /**
     * 修改图片影像
     * @param form 文件信息
     */
    public Integer editUploadTime(XUploadFileForm form) {
        XUploadFile file = fileIsExists(form.getFileId());
        // 判断图片是否可以编辑
        TreatmentOtherUtils.enableEditImage(file.getCrtTime());
        Integer updId = Integer.valueOf(BaseContextHandler.getUserID());
        file.setUpdId((updId));
        file.setUpdTime(new Date(System.currentTimeMillis()));
        Date uploadTime = parseUploadTime(form.getUploadTime());
        file.setUploadTime(uploadTime);
        return mapper.updateByPrimaryKeySelective(file);
    }

    /**
     * 根据文件ID逻辑删除文件信息
     * @param id
     */
    public void del(Integer id) {
        XUploadFile file = fileIsExists(id);
        TreatmentOtherUtils.enableEditImage(file.getCrtTime());
        file.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        file.setUpdTime(new Date(System.currentTimeMillis()));
        file.setInservice(false);
        mapper.updateByPrimaryKey(file);
    }

    /**
     * 校验文件是否存在
     *
     * @param id
     * @return
     */
    private XUploadFile fileIsExists(Integer id) {
        XUploadFile file = mapper.selectByPrimaryKey(id);
        if (null == file) {
            throw new ClientServiceException("数据不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        return file;
    }
}
