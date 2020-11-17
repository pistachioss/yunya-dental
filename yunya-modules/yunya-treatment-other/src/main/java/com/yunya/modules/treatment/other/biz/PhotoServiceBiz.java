package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.form.PhotoServiceForm;
import com.yunya.feign.treatment_other.domain.model.PhotoServiceModel;
import com.yunya.feign.treatment_other.domain.query.PhotoServiceQuery;
import com.yunya.feign.treatment_other.domain.vo.PhotoServiceVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.treatment_other.PhotoService;
import com.yunya.modules.treatment.other.mapper.PhotoServiceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PhotoServiceBiz {

    @Resource
    private PhotoServiceMapper photoServiceMapper;

    public List<PhotoServiceVo> findPhotoServiceData(PhotoServiceQuery query){
        List<PhotoServiceVo> data = photoServiceMapper.findPhotoServiceData(query);
        return data;
    }

    public void  add(PhotoServiceModel model){
        Integer crtId = Integer.valueOf(BaseContextHandler.getUserID());
        PhotoService photoService = new PhotoService();
        photoService.setCrtId((crtId));
        BeanUtils.copyProperties(model,photoService);
        photoServiceMapper.add(photoService);
    }
    public void upd(PhotoServiceForm form){
        Integer updId = Integer.valueOf(BaseContextHandler.getUserID());
        PhotoService photoService = new PhotoService();
        photoService.setUpdId((updId));
        BeanUtils.copyProperties(form,photoService);
        photoServiceMapper.upd(photoService);
    }

    public void  del(Integer id, String uploadTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        long uploadTimeMs = dateFormat.parse(uploadTime).getTime();
        long currentTimeMs = System.currentTimeMillis();
        long diffTimeMs = currentTimeMs - uploadTimeMs;
        int dayMs = 24 * 3600 * 1000;
        if (diffTimeMs > dayMs) {
            throw new ClientServiceException("上传图片时间已经超过24小时,不允许删除", OperationCodeConstants.DELETE_NOT_ALLOW);
        }
        photoServiceMapper.del(id);
    }
}
