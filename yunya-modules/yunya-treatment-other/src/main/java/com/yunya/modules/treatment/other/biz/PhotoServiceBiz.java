package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.PhotoServiceForm;
import com.yunya.feign.treatment_other.domain.model.PhotoDetailListModel;
import com.yunya.feign.treatment_other.domain.model.PhotoServiceModel;
import com.yunya.feign.treatment_other.domain.query.PhotoServiceQuery;
import com.yunya.feign.treatment_other.domain.vo.PhotoServiceVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.emr.MedicalOrthodonticsRecord;
import com.yunya.models.treatment_other.PhotoService;
import com.yunya.modules.treatment.other.mapper.PhotoServiceMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PhotoServiceBiz {

    @Resource
    private PhotoServiceMapper photoServiceMapper;

    public PageInfo<PhotoServiceVo> findPhotoServiceData(PhotoServiceQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<PhotoServiceVo> data = photoServiceMapper.findPhotoServiceData(query);
        return new PageInfo(data);
    }

    public void addBatch(PhotoServiceModel model){
        List<PhotoDetailListModel> photoDetailListModels = model.getPhotoDetailListModels();
        PhotoService photoService = new PhotoService();
        photoService.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        photoService.setDentistId(model.getDentistId());
        photoService.setPatientId(model.getPatientId());
        photoService.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        photoService.setTreatmentRecordId(model.getTreatmentRecordId());
        photoService.setPhotoType(model.getPhotoType());
        List<PhotoService> photoServices = new ArrayList<>();
        photoDetailListModels.forEach(photoDetailListModel -> {
            PhotoService entity = new PhotoService();
            BeanUtils.copyProperties(photoService,entity);
            entity.setFilmName(photoDetailListModel.getFilmName());
            entity.setUri(photoDetailListModel.getUri());
            photoServices.add(entity);
        });
        photoServiceMapper.addBatch(photoServices);
    }
    public void upd(PhotoServiceForm form){
        long currentTimeMs = form.getCurrentTime().getTime();
        long currentTimeInMillis = currentTimeInMillis();
        if (currentTimeMs > currentTimeInMillis) {
            throw new ClientServiceException("已经错过修改日期，不允许修改",OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        Integer updId = Integer.valueOf(BaseContextHandler.getUserID());
        PhotoService photoService = new PhotoService();
        photoService.setUpdId((updId));
        photoService.setUpdTime(new Date(System.currentTimeMillis()));
        BeanUtils.copyProperties(form,photoService);
        photoServiceMapper.upd(photoService);
    }

    public void  del(Integer id) {
        PhotoService photoService = photoServiceMapper.selectByPrimaryKey(id);
        if (null == photoService) {
            throw new ClientServiceException("数据不存在",OperationCodeConstants.DATA_NOT_EXIST);
        }
        long uploadTimeMs = photoService.getCrtTime().getTime();
        long currentTimeMs = System.currentTimeMillis();
        long diffTimeMs = currentTimeMs - uploadTimeMs;
        int dayMs = 24 * 3600 * 1000;
        if (diffTimeMs > dayMs) {
            throw new ClientServiceException("上传图片时间已经超过24小时,不允许删除", OperationCodeConstants.DELETE_NOT_ALLOW);
        }
        photoServiceMapper.del(id);
    }

    /**
     * 获取当前时间(yyyy-MM-dd 23:59:59)距离格林尼治时间的毫秒数
     * @return 获取当前时间距离格林尼治时间的毫秒数
     */
    private long currentTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        return calendar.getTimeInMillis();
    }
}
