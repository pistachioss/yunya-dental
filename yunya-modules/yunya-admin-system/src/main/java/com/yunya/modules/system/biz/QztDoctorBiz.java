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
import com.yunya.models.system.Company;
import com.yunya.models.system.QztDoctor;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.mapper.QztDoctorMapper;
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
    private CompanyMapper companyMapper;
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
        example.createCriteria().andEqualTo("qualification", qualification);
        QztDoctor qztDoctor = mapper.selectOneByExample(example);
        QztDoctor doctor = BeanCopierUtils.generalCopyBean(model, QztDoctor.class);
        doctor.setUpdId(userId);
        String userId1 = model.getUserId().toString();
        QztDoctor primary = getByUserId(model.getUserId());
        //医生选择门诊
        List<String> selectClinic = Lists.newArrayList(Splitter.on(",").split(model.getPracticeClinic()));
        if (Objects.isNull(primary)) {
            if (Objects.nonNull(qztDoctor)) {
                if (!Objects.equals(idCard, qztDoctor.getIdCard())) {
                    throw ClientServiceException.wrap(9999, "该资格证号已被关联");
                } else {
                    rebuild(qztDoctor, doctor, userId1, selectClinic);
                    return;
                }
            }
            Company company = companyMapper.selectByPrimaryKey(Integer.valueOf(selectClinic.get(0)));
            doctor.setCrtId(userId);
            doctor.setRelateUserIds(userId1);
            doctor.setInstitutionId(company.getQztInstitutionCode());
            mapper.insertSelective(doctor);
            return;
        }
        // 更新自己信息
        if (Objects.nonNull(qztDoctor)) {
            boolean contains = Lists.newArrayList(Splitter.on(",").split(qztDoctor.getRelateUserIds())).contains(userId1);
            if (contains) {
                List<String> existClinic = Lists.newArrayList(Splitter.on(",").split(primary.getPracticeClinic()));
                doctor.setPracticeClinic(String.join(",", CollectionUtils.union(existClinic, selectClinic)));
                doctor.setId(primary.getId());
                mapper.updateByPrimaryKeySelective(doctor);
            } else {
                if (!Objects.equals(idCard, qztDoctor.getIdCard())) {
                    throw ClientServiceException.wrap(9999, "该资格证号已被关联");
                }
                rebuild(qztDoctor, doctor, userId1, selectClinic);
            }
        } else {
            List<String> existClinic = Lists.newArrayList(Splitter.on(",").split(primary.getPracticeClinic()));
            doctor.setPracticeClinic(String.join(",", CollectionUtils.union(existClinic, selectClinic)));
            doctor.setId(primary.getId());
            mapper.updateByPrimaryKeySelective(doctor);
        }
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
        detailVO.setPracticeClinic(practiceClinic);
        return detailVO;
    }

    /**
     * 同步全诊通
     */
    public void sync() {
        String preDay = LocalDate.now().minusDays(1).toString();
        Example example = new Example(QztDoctor.class);
        example.createCriteria()
                .andEqualTo("enableCert", true).andGreaterThanOrEqualTo("updTime", preDay);
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
    }

    public List<QztDoctor> certDoctors() {
        Example example = new Example(QztDoctor.class);
        example.createCriteria()
                .andEqualTo("enableCert", true);
        example.selectProperties("id", "doctorName", "practiceClinic", "relateUserIds");
        return mapper.selectByExample(example);
    }

    private QztDoctor getByUserId(Integer userId) {
        Example example = new Example(QztDoctor.class);
        example.createCriteria()
                .andCondition("FIND_IN_SET(" + userId + ", relate_user_ids)");
        return mapper.selectOneByExample(example);
    }

    private void reBuildDoctor(String userId) {
        QztDoctor qztDoctor = getByUserId(Integer.valueOf(userId));
        if (Objects.nonNull(qztDoctor)) {
            Integer id = qztDoctor.getId();
            List<String> relateUserIds = Lists.newArrayList(Splitter.on(",").split(qztDoctor.getRelateUserIds()));
            if (Objects.equals(relateUserIds.size(), 1)) {
                mapper.deleteByPrimaryKey(id);
            } else {
                relateUserIds.removeIf(t -> Objects.equals(t, userId));
                qztDoctor.setRelateUserIds(String.join(",", relateUserIds));
                mapper.updateByPrimaryKeySelective(qztDoctor);
            }
        }
    }

    private void rebuild(QztDoctor qztDoctor, QztDoctor doctor, String userId1, List<String> selectClinic) {
        //移除或更新 挂靠医生
        reBuildDoctor(userId1);
        List<String> relateUserIds = Lists.newArrayList(Splitter.on(",").split(qztDoctor.getRelateUserIds()));
        List<String> existClinic = Lists.newArrayList(Splitter.on(",").split(qztDoctor.getPracticeClinic()));
        //多个账号合并 更新
        doctor.setPracticeClinic(String.join(",", CollectionUtils.union(existClinic, selectClinic)));
        doctor.setId(qztDoctor.getId());
        if (!relateUserIds.contains(userId1)) {
            doctor.setRelateUserIds(Joiner.on(",").join(qztDoctor.getRelateUserIds(), userId1));
        }
        mapper.updateByPrimaryKeySelective(doctor);
    }
}
