package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.query.PhotoServiceQuery;
import com.yunya.feign.treatment_other.domain.vo.PhotoServiceVo;
import com.yunya.models.treatment_other.PhotoService;
import com.yunya.modules.treatment.other.mapper.PhotoServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    public void  add(PhotoService photoService){
        photoServiceMapper.add(photoService);
    }
    public void  upd(PhotoService photoService){
        photoServiceMapper.upd(photoService);
    }

    public void  del(Integer id){
        photoServiceMapper.del(id);
    }
}
