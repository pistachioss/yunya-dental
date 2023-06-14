package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.XRayFilmForm;
import com.yunya.feign.treatment_other.domain.model.XRayFilmInfoModel;
import com.yunya.feign.treatment_other.domain.query.ToothRootQuery;
import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment_other.XRayFilm;
import com.yunya.modules.treatment.other.mapper.XRayFilmMapper;
import com.yunya.modules.treatment.other.utils.TreatmentOtherUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class XRayFilmBiz extends BaseBiz<XRayFilmMapper, XRayFilm> {

    /**
     * 查询图片列表
     * @param query 查询条件
     * @return 返回数据列表
     */
    public PageInfo<XRayFilmVO> findList(XRayFilmQuery query){
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<XRayFilmVO> data = mapper.findList(query);
        return new PageInfo<>(data);
    }

    /**
     * 批量上传（可单独上传）
     * @param patientId 患者ID
     * @param models 参数模型
     */
    public ResponseResult addBatch(Integer patientId, List<XRayFilmInfoModel> models){
        List<XRayFilm> xRayFilms = new ArrayList<>();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        String username = BaseContextHandler.getName();
        if (StringHelper.isNotEmpty(models)) {
            models.forEach(xRayFilmModel -> {
                Byte type = xRayFilmModel.getType();
                Integer toothNo = xRayFilmModel.getToothNo();
                if (type == 1 && toothNo == null) {
                    throw new ClientServiceException("根尖片牙位编号不能位空", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
                }
                XRayFilm xRayFilm = new XRayFilm();
                xRayFilm.setCrtId(userId);
                xRayFilm.setCrtName(username);
                xRayFilm.setPatientId(patientId);
                xRayFilm.setPhotoName(xRayFilmModel.getPhotoName());
                xRayFilm.setToothNo(xRayFilmModel.getToothNo());
                xRayFilm.setType(xRayFilmModel.getType());
                xRayFilm.setUrl(xRayFilmModel.getUrl());
                if (null != xRayFilmModel.getUploadTime()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date parse = null;
                    try {
                        parse = dateFormat.parse(xRayFilmModel.getUploadTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    xRayFilm.setUploadTime(parse);
                }
                xRayFilms.add(xRayFilm);
            });
            Integer integer = mapper.addBatch(xRayFilms);
            if (integer > 0) {
                return ResponseUtil.success();
            }
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"上传图片失败",null);
        }
        return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL,"上传图片列表不能为空",null);
    }

    /**
     * 修改图片影像
     * @param id 图片ID
     * @param form 图片信息
     */
    public Integer upd(Integer id, XRayFilmForm form) throws ParseException {
        XRayFilm xRayFilm = mapper.selectByPrimaryKey(id);
        if (null == xRayFilm) {
            throw new ClientServiceException("数据不存在",OperationCodeConstants.DATA_NOT_EXIST);
        }
        Date crtTime = xRayFilm.getCrtTime();
        // 判断图片是否可以编辑
        TreatmentOtherUtils.enableEditImage(crtTime);
        Integer updId = Integer.valueOf(BaseContextHandler.getUserID());
        XRayFilm entity = new XRayFilm();
        entity.setId(id);
        entity.setUpdId((updId));
        entity.setUpdName(BaseContextHandler.getName());
        entity.setUpdTime(new Date(System.currentTimeMillis()));
        entity.setType(form.getType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = dateFormat.parse(form.getUploadTime());
        entity.setUploadTime(parse);
        return mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 删除照片（逻辑删除）
     * @param id 图片ID
     */
    public Integer del(Integer id) {
        XRayFilm xRayFilm = mapper.selectByPrimaryKey(id);
        if (null == xRayFilm) {
            throw new ClientServiceException("数据不存在",OperationCodeConstants.DATA_NOT_EXIST);
        }
//        Date crtTime = xRayFilm.getCrtTime();
//         判断图片是否可以编辑
//        TreatmentOtherUtils.enableEditImage(crtTime);
        xRayFilm.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        xRayFilm.setUpdName(BaseContextHandler.getName());
        xRayFilm.setUpdTime(new Date(System.currentTimeMillis()));
        xRayFilm.setInservice(false);
        return mapper.updateByPrimaryKey(xRayFilm);
    }

    /**
     * 牙根尖图列表
     * @param patientId 患者ID
     * @param query 查询及分页参数
     * @return 牙根尖图列表
     */
    public PageInfo<ToothRootVo> findToothRootPhotos(Integer patientId, ToothRootQuery query) {
        Integer toothNo = query.getToothNo();
        if (query.getWhetherPage()) {
            PageHelper.offsetPage(query.getPageNum()-1,query.getPageSize());
        }
        List<ToothRootVo> toothRootPhotos = mapper.findToothRootPhotos(patientId, toothNo);
        return new PageInfo<>(toothRootPhotos);
    }

    /**
     * 牙位根尖片数量(APP)用
     * @param patientId 患者ID
     * @return 返回列表
     */
    public List<ToothRootCountVo> toothRootCount(Integer patientId) {
        return mapper.findToothRootCountByPatientId(patientId);
    }

    /**
     * 根据患者ID集合和当前时间查询患者照片集合
     * @param patientIds  患者ID列表
     * @param currentDate  当前日期
     * @return 返回图片信息
     */
    public List<XRayFilm> findXRayFilmListByPatientIds(List<Integer> patientIds, String currentDate) {
        return mapper.findXRayFilmListByPatientIds(patientIds,currentDate);
    }
}
