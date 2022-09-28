package com.yunya.modules.system.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.system.vo.QztSyncDoctorVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.*;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.QztDoctor;
import com.yunya.models.system.QztSyncDoctor;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.mapper.QztDoctorMapper;
import com.yunya.modules.system.mapper.QztSyncDoctorMapper;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import com.yunya.modules.system.vo.QztDoctorDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 全诊通
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class QztDoctorBiz extends BaseBiz<QztDoctorMapper, QztDoctor> {

    @Resource
    private OrganizationBiz organizationBiz;
    @Resource
    private QztSyncDoctorMapper syncDoctorMapper;
    @Resource
    private QztRestTemplateApi qztRestTemplateApi;
    @Resource
    private RedisUtils redisUtils;
    @Value("${qzt.prefix}")
    private String qztPrefix;

    public static final Map<String, Map<String, String>> qztSelect = Maps.newHashMap();
    public static final String DOCTOR_URL = "/docking/api/doctor/addDoctor?short-access-token=%s";

    @PostConstruct
    public void init() {
        //职称
        Map<String, String> technicalTitle = Arrays.stream(TechnicalTitleEnum.values()).reduce(Maps.newHashMap(), (u, t) -> {
            u.put(t.getCode(), t.getValue());
            return u;
        }, (u, t) -> u);
        //职业范围
        Map<String, String> scopePractice = Arrays.stream(ScopePracticeEnum.values()).reduce(Maps.newHashMap(), (u, t) -> {
            u.put(t.getCode(), t.getValue());
            return u;
        }, (u, t) -> u);
        //抗菌药物处方权
        Map<String, String> antibiosisAuthority = Arrays.stream(AntibiosisAuthorityEnum.values()).reduce(Maps.newHashMap(), (u, t) -> {
            u.put(t.getCode(), t.getValue());
            return u;
        }, (u, t) -> u);
        qztSelect.put("technicalTitle", technicalTitle);
        qztSelect.put("scopePractice", scopePractice);
        qztSelect.put("antibiosisAuthority", antibiosisAuthority);
    }

    public void complete(QztAddDoctorModel model) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        String idCard = model.getIdCard();
        //医生资格证号
        String qualification = model.getQualification();
        Example example = new Example(QztDoctor.class);
        example.createCriteria().andEqualTo("idCard", idCard)
                .andEqualTo("qualification", qualification);
        QztDoctor qztDoctor = mapper.selectOneByExample(example);
        QztDoctor doctor = BeanCopierUtils.generalCopyBean(model, QztDoctor.class);
        doctor.setUpdId(userId);
        String userId1 = model.getUserId().toString();
        if (Objects.isNull(qztDoctor)) {
            doctor.setCrtId(userId);
            doctor.setRelateUserIds(userId1);
            mapper.insertSelective(doctor);
            return;
        }
        if (!Objects.equals(model.getDoctorName(), qztDoctor.getDoctorName())) {
            throw ClientServiceException.wrap(9999, "该医生的资格证号已被关联");
        }
        String relateUserIds = qztDoctor.getRelateUserIds();
        boolean contains = Lists.newArrayList(Splitter.on(",").split(relateUserIds)).contains(userId1);
        if (!contains) {
            doctor.setRelateUserIds(Joiner.on(",").join(qztDoctor.getRelateUserIds(), userId1));
        }
        doctor.setId(qztDoctor.getId());
        mapper.updateByPrimaryKeySelective(doctor);
    }

    public Map<String, Map<String, String>> select() {
        return qztSelect;
    }

    public QztDoctorDetailVO detail(Integer userId) {
        QztDoctor doctor = mapper.selectByUserId(userId);
        if (Objects.isNull(doctor)) {
            return null;
        }
        QztDoctorDetailVO detailVO = BeanCopierUtils.generalCopyBean(doctor, QztDoctorDetailVO.class);
        String practiceClinic = doctor.getPracticeClinic();
        List<Integer> clinicIds = Lists.newArrayList(Splitter.on(",").split(practiceClinic)).stream().map(Integer::valueOf).collect(Collectors.toList());
        List<OrganizationInfoVO> orgInfoInIds = organizationBiz.findOrgInfoInIds(clinicIds);
        String collect = orgInfoInIds.stream().map(OrganizationInfoVO::getName).collect(Collectors.joining(","));
        detailVO.setPracticeClinic(collect);
        return detailVO;
    }

    /**
     * 同步全诊通
     */
    public void sync() {
        String preDay = LocalDate.now().minusDays(1).toString();
        QztSyncDoctor preTask = preTask(0);
        Example example = new Example(QztDoctor.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("enableCert", true);
        if (Objects.nonNull(preTask)) {
            criteria.andGreaterThan("id", preTask.getLastId());
        }
        example.orderBy("id").asc();
        List<QztDoctor> doctors = mapper.selectByExample(example);
        String shortToken = redisUtils.get(RedisConstants.QZT_TOKEN);
        if (CollectionUtils.isEmpty(doctors) && StringUtils.isBlank(shortToken)) {
            return;
        }
        List<QztSyncDoctorVO> syncDoctorVOS = doctors.stream().map(t -> {
            QztSyncDoctorVO doctorVO = BeanCopierUtils.generalCopyBean(t, QztSyncDoctorVO.class);
            doctorVO.setEntid(t.getId());
            doctorVO.setDepartmentId(t.getDepartId());
            doctorVO.setDepartmentName(t.getDepartName());
            doctorVO.setName(t.getDoctorName());
            return doctorVO;
        }).collect(Collectors.toList());
        log.info("全诊通医生数据同步开始，同步数量：{}", syncDoctorVOS.size());
        JSONObject jsonObject = qztRestTemplateApi.postObject(String.format(qztPrefix + DOCTOR_URL, shortToken), syncDoctorVOS);
        log.info("全诊通医生数据同步完成：{}", jsonObject);
        updateTask(doctors.get(doctors.size() - 1).getId(),0);
    }

    private QztSyncDoctor preTask(Integer type) {
        Example example = new Example(QztSyncDoctor.class);
        example.createCriteria().andEqualTo("type", type);
        return syncDoctorMapper.selectOneByExample(example);
    }

    public void updateTask(Integer id, Integer type) {
        log.info("同步任务类型：{}，id：{}", type, id);
        Example example = new Example(QztSyncDoctor.class);
        example.createCriteria().andEqualTo("type", type);
        QztSyncDoctor qztSyncDoctor = syncDoctorMapper.selectOneByExample(example);
        if (Objects.isNull(qztSyncDoctor)) {
            QztSyncDoctor syncDoctor = new QztSyncDoctor();
            syncDoctor.setType(type);
            syncDoctor.setLastId(id);
            syncDoctorMapper.insertSelective(syncDoctor);
        } else {
            qztSyncDoctor.setLastId(id);
            syncDoctorMapper.updateByPrimaryKeySelective(qztSyncDoctor);
        }
    }
}
