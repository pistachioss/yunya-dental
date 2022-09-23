package com.yunya.modules.system.biz;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.*;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.system.QztDoctor;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.mapper.QztDoctorMapper;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import com.yunya.modules.system.vo.QztDoctorDetailVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 全诊通
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class QztDoctorBiz extends BaseBiz<QztDoctorMapper, QztDoctor> {

    @Resource
    private OrganizationBiz organizationBiz;

    public static final Map<String, Map<String, String>> qztSelect = Maps.newHashMap();

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
        QztDoctorDetailVO detailVO = BeanCopierUtils.generalCopyBean(doctor, QztDoctorDetailVO.class);
        String practiceClinic = doctor.getPracticeClinic();
        List<Integer> clinicIds = Lists.newArrayList(Splitter.on(",").split(practiceClinic)).stream().map(Integer::valueOf).collect(Collectors.toList());
        List<OrganizationInfoVO> orgInfoInIds = organizationBiz.findOrgInfoInIds(clinicIds);
//        orgInfoInIds.stream().reduce("", (u, t) -> u, )
//        detailVO.setPracticeClinic();
        return detailVO;
    }
}
