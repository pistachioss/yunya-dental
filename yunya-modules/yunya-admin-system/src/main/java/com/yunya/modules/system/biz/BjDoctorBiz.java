package com.yunya.modules.system.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.system.vo.BjSyncDoctorVO;
import com.yunya.feign.system.vo.QztSyncDoctorVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.service.BjRestTemplateApi;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.BjDoctor;
import com.yunya.models.system.Company;
import com.yunya.modules.system.domain.model.BjAddDoctorModel;
import com.yunya.modules.system.mapper.BjDoctorMapper;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.vo.BjDoctorDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 全诊通
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BjDoctorBiz extends BaseBiz<BjDoctorMapper, BjDoctor> {

    public static final Map<String, Map<String, String>> bjSelect = Maps.newHashMap();
    public static final String DOCTOR_URL = "/api/outer/doctor/save?his-auth=%s";
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private BjRestTemplateApi bjRestTemplateApi;
    @Resource
    private RedisUtils redisUtils;
    @Value("${bj.prefix}")
    private String bjPrefix;

    public void complete(BjAddDoctorModel model) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        String idCard = model.getIdCard();
        //医生资格证号
        String qualification = model.getQualification();
        Example example = new Example(BjDoctor.class);
        example.createCriteria().andEqualTo("qualification", qualification);
        BjDoctor qztDoctor = mapper.selectOneByExample(example);
        BjDoctor doctor = BeanCopierUtils.generalCopyBean(model, BjDoctor.class);
        doctor.setUpdId(userId);
        doctor.setDepartId(285);
        doctor.setDepartName("口腔科");
        String userId1 = model.getUserId().toString();
        BjDoctor primary = getByUserId(model.getUserId());
        //医生选择门诊
        String practiceClinic = model.getPracticeClinic();
        List<String> selectClinic = Lists.newArrayList(Splitter.on(",").split(practiceClinic));
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
            doctor.setInstitutionId(company.getBjInstitutionCode());
            mapper.insertSelective(doctor);
            return;
        }
        // 更新自己信息
        if (Objects.nonNull(qztDoctor)) {
            boolean contains = Lists.newArrayList(Splitter.on(",").split(qztDoctor.getRelateUserIds())).contains(userId1);
            if (contains) {
                doctor.setPracticeClinic(practiceClinic);
                doctor.setId(primary.getId());
                mapper.updateByPrimaryKeySelective(doctor);
            } else {
                if (!Objects.equals(idCard, qztDoctor.getIdCard())) {
                    throw ClientServiceException.wrap(9999, "该资格证号已被关联");
                }
                rebuild(qztDoctor, doctor, userId1, selectClinic);
            }
        } else {
            doctor.setPracticeClinic(practiceClinic);
            doctor.setId(primary.getId());
            mapper.updateByPrimaryKeySelective(doctor);
        }
    }

    public BjDoctorDetailVO detail(Integer userId) {
        BjDoctor doctor = mapper.selectByUserId(userId);
        if (Objects.isNull(doctor)) {
            return null;
        }
        BjDoctorDetailVO detailVO = BeanCopierUtils.generalCopyBean(doctor, BjDoctorDetailVO.class);
        String practiceClinic = doctor.getPracticeClinic();
        detailVO.setPracticeClinic(practiceClinic);
        return detailVO;
    }

    /**
     * 同步全诊通
     */
    public void sync() {
        String preDay = LocalDate.now().minusDays(1).toString();
        Example example = new Example(BjDoctor.class);
        example.createCriteria()
                .andEqualTo("enableCert", true).andGreaterThanOrEqualTo("updTime", preDay);
        example.orderBy("id").asc();
        List<BjDoctor> doctors = mapper.selectByExample(example);
        String shortToken = redisUtils.get(RedisConstants.BJ_TOKEN);
        if (CollectionUtils.isEmpty(doctors) && StringUtils.isBlank(shortToken)) {
            return;
        }
        List<BjSyncDoctorVO> syncDoctorVOS = doctors.stream().map(t -> {
            BjSyncDoctorVO doctorVO = BeanCopierUtils.generalCopyBean(t, BjSyncDoctorVO.class);
            doctorVO.setDoctorId(t.getRelateUserIds().split(",")[0]);
            doctorVO.setDepartmentName(t.getDepartName());
            doctorVO.setOrganizationCode(t.getInstitutionId());
            return doctorVO;
        }).collect(Collectors.toList());
        log.info("滨江医生数据同步开始，同步数量：{}", syncDoctorVOS.size());
        JSONObject jsonObject = bjRestTemplateApi.postObject(String.format(bjPrefix + DOCTOR_URL, shortToken), syncDoctorVOS);
        log.info("滨江医生数据同步完成：{}", jsonObject);
    }

    public List<BjDoctor> certDoctors() {
        Example example = new Example(BjDoctor.class);
        example.createCriteria()
                .andEqualTo("enableCert", true);
        example.selectProperties("id", "doctorName", "practiceClinic", "relateUserIds");
        return mapper.selectByExample(example);
    }

    private BjDoctor getByUserId(Integer userId) {
        Example example = new Example(BjDoctor.class);
        example.createCriteria()
                .andCondition("FIND_IN_SET(" + userId + ", relate_user_ids)");
        return mapper.selectOneByExample(example);
    }

    private void reBuildDoctor(String userId) {
        BjDoctor qztDoctor = getByUserId(Integer.valueOf(userId));
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

    private void rebuild(BjDoctor qztDoctor, BjDoctor doctor, String userId1, List<String> selectClinic) {
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

    public List<Company> certBjCompanys() {
        Example example = new Example(Company.class);
        example.createCriteria()
                .andEqualTo("enableBjSync", true);
        example.selectProperties("id", "name", "bjInstitutionCode");
        return companyMapper.selectByExample(example);
    }
}
