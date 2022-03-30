package com.yunya.modules.emr.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.yunya.feign.emr.domain.model.MedicalPictureRecordModel;
import com.yunya.feign.emr.domain.query.MedicalPictureRecordExistsQuery;
import com.yunya.feign.emr.domain.query.MedicalPictureRecordQuery;
import com.yunya.feign.emr.domain.vo.MedicalPictureRecordVO;
import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.model.MedicalRayFilmModel;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.MedicalPictureRecord;
import com.yunya.modules.emr.mapper.MedicalPictureRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yunya.framework.common.enums.FileSourceTypeEnum.PATIENT_EMR_PIC;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/3/24 10:03
 * @since: 1.0.0
 */
@Slf4j
@Service
public class MedicalPictureRecordBiz extends BaseBiz<MedicalPictureRecordMapper, MedicalPictureRecord> {

    @Autowired
    private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;

    @Autowired
    private RemoteOssServiceFeign remoteOssServiceFeign;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${domainUrl}")
    private String domainUrl;

    private static final String MEDICAL_PIC_KEY = "lock:medical:picture";

    /**
     * 保存病历照片记录
     *
     * @param model
     * @return
     */
    public Integer save(MedicalPictureRecordModel model) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = new Date(System.currentTimeMillis());
        Integer id = model.getId();
        Integer patientId = model.getPatientId();
        String name = model.getName();

        String lockKey = Joiner.on(":").join(MEDICAL_PIC_KEY, patientId);
        boolean lock = redisUtils.setLock(lockKey, name, 30L, TimeUnit.SECONDS);
        if (lock) {
            MedicalPictureRecordVO record = findOneByName(patientId, name);
            List<XUploadFileVO> files = model.getFiles();
            if (!ObjectUtils.isEmpty(record)) {//合并
                if (!record.getId().equals(id)) {
                    // 删除旧
                    tombstoneById(id);
                    fillUploadFile(Arrays.asList(record), null);
                    // 上传文件合并
                    files = prePoseMerge(files, record.getFiles());
                }
                id = record.getId();
            } else if (ObjectUtils.isEmpty(id)){// 新增
                id = insertModel(model, userId, now);
            } else {// 修改
                id = updateModel(model, userId, now);
            }
            saveUploadFile(files, id, userId, now);
            redisUtils.unlock(lockKey, name);
        }
        return id;
    }

    private Integer updateModel(MedicalPictureRecordModel model, Integer userId, Date now) {
        Integer id = model.getId();
        MedicalPictureRecord entity = mapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(entity)) {
            throw new ClientServiceException("保存失败", OperationCodeConstants.DATA_NOT_EXIST);
        }
        entity.setId(id);
        entity.setName(model.getName());
        entity.setUptId(userId);
        entity.setUptTime(now);
        mapper.updateByPrimaryKeySelective(entity);
        return id;
    }

    private Integer insertModel(MedicalPictureRecordModel model, Integer userId, Date now) {
        MedicalPictureRecord entity = new MedicalPictureRecord();
        entity.setName(model.getName());
        entity.setPatientId(model.getPatientId());
        entity.setInservice(true);
        entity.setCrtId(userId);
        entity.setCrtTime(now);
        entity.setUptId(userId);
        entity.setUptTime(now);
        mapper.insertSelective(entity);
        return entity.getId();
    }

    /**
     * 前置合并
     *
     * @param target
     * @param source
     */
    private List<XUploadFileVO> prePoseMerge(List<XUploadFileVO> target, List<XUploadFileVO> source) {
        if (StringHelper.isEmpty(target)) {
            target = new ArrayList<>();
        }
        if (StringHelper.isNotEmpty(source)) {
            for (int i = source.size()-1; i >=0; i--) {
                target.add(0, source.get(i));
            }
        }
        return target;
    }

    /**
     * 保存上传文件
     *
     * @param files
     * @param sourceId
     * @param userId
     * @param now
     */
    private void saveUploadFile(List<XUploadFileVO> files, Integer sourceId, Integer userId, Date now) {
        if (StringHelper.isNotEmpty(files)) {
            files.forEach(vo -> {
                Date uploadTime = vo.getUploadTime();
                if (ObjectUtils.isEmpty(uploadTime)) {
                    vo.setUploadTime(now);
                }
            });
        }
        MedicalRayFilmModel model = new MedicalRayFilmModel();
        model.setSourceId(sourceId);
        model.setSourceType(PATIENT_EMR_PIC.getCode());
        model.setRayFiles(files);
        model.setCrtId(userId);
        model.setCrtTime(now);
        remoteTreatmentOtherFeign.saveXRayFile2XUploadFile(model);
    }

    /**
     * 检查是否重复
     *
     * @param id
     * @param patientId
     * @param name
     *
     */
    private Boolean checkRepeatName(Integer id, Integer patientId, String name) {
        MedicalPictureRecordVO record = findOneByName(patientId, name);
        if (!ObjectUtils.isEmpty(record)) {
            if (ObjectUtils.isEmpty(id)) {// 新增
                return true;
            } else {// 修改
                if (!record.getId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据name查询
     *
     * @param patientId
     * @param name
     * @return
     */
    private MedicalPictureRecordVO findOneByName(Integer patientId, String name) {
        MedicalPictureRecordQuery query = new MedicalPictureRecordQuery();
        query.setName(name);
        query.setPatientId(patientId);
        List<MedicalPictureRecordVO> list = mapper.findMedicalPictureRecordList(query);
        if (StringHelper.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据条件逻辑删除上传文件
     *
     * @param id
     */
    public void tombstoneById(Integer id) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = new Date(System.currentTimeMillis());
        tombstoneById(id, userId, now);
        // 删除照片
        MedicalRayFilmModel query = new MedicalRayFilmModel();
        query.setSourceId(id);
        query.setSourceType(PATIENT_EMR_PIC.getCode());
        query.setCrtId(userId);
        query.setCrtTime(now);
        remoteTreatmentOtherFeign.tombstoneUploadFile(query);
    }

    private void tombstoneById(Integer id, Integer userId, Date now) {
        if (!ObjectUtils.isEmpty(id)) {
            MedicalPictureRecord entity = new MedicalPictureRecord();
            entity.setId(id);
            entity.setInservice(false);
            entity.setUptId(userId);
            entity.setUptTime(now);
            mapper.updateByPrimaryKeySelective(entity);
        }
    }

    /**
     * 条件查询病历照片记录
     *
     * @param query
     * @return
     */
    public PageInfo<MedicalPictureRecordVO> findList(MedicalPictureRecordQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<MedicalPictureRecordVO> result = mapper.findMedicalPictureRecordList(query);
        fillUploadFile(result, query.getPatientId());
        return new PageInfo<>(result);
    }

    /**
     * 填充上传的照片
     *
     * @param result
     */
    private void fillUploadFile(List<MedicalPictureRecordVO> result, Integer patientId) {
        if (StringHelper.isNotEmpty(result)) {
            Map<Integer, List<XUploadFileVO>> map = new HashMap<>(16);
            List<Integer> sourceIds = result.stream().map(MedicalPictureRecordVO::getId).collect(Collectors.toList());
            XUploadFileQuery query = new XUploadFileQuery();
            query.setWhetherPage(false);
            query.setSourceIds(sourceIds);
            query.setSourceType(PATIENT_EMR_PIC.getCode());
            List<XUploadFileVO> files = remoteTreatmentOtherFeign.findXUploadFileList(query);
            if (StringHelper.isNotEmpty(files)) {
                List<OssUrlForm> ossUrlForms = new ArrayList<>();
                files.forEach(file->{
                    Integer sourceId = file.getSourceId();
                    List<XUploadFileVO> list = map.get(sourceId);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(file);
                    map.put(sourceId, list);

                    OssUrlForm form = new OssUrlForm();
                    form.setIsThumb(true);
                    form.setCompanyId(0);
                    form.setObjectId(patientId);
                    form.setOssCategory(3);
                    form.setOssFilename(file.getFileLocation());
                });
                if (!ObjectUtils.isEmpty(patientId)) {
                    Map<String, String> data = remoteOssServiceFeign.getUrlMap(ossUrlForms).getData();
                    files.forEach(file->{
                        String fileLocation = file.getFileLocation();
                        String url = data.get(fileLocation);
                        file.setThumbUrl(domainUrl + url);
                    });
                }
            }
            result.forEach(vo-> vo.setFiles(map.get(vo.getId())));
        }
    }

    /**
     * 查询病历照片记录是否存在
     *
     * @param query
     * @return
     */
    public Boolean isExistsMedicalPictureRecord(MedicalPictureRecordExistsQuery query) {
        return checkRepeatName(query.getId(), query.getPatientId(), query.getName());
    }
}
