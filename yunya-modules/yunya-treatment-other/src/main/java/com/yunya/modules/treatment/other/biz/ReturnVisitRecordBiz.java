package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentOrderVO;
import com.yunya.feign.treatment_other.domain.form.ReturnVisitContentForm;
import com.yunya.feign.treatment_other.domain.form.ReturnVisitRecordForm;
import com.yunya.feign.treatment_other.domain.query.PatientReturnVisitQuery;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitContentVO;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitRecordVO;
import com.yunya.feign.treatment_other.domain.vo.ReturnVisitVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment_other.ReturnVisitRecord;
import com.yunya.modules.treatment.other.mapper.ReturnVisitRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_TRANSFORMATION_EXIST;
import static java.util.stream.Collectors.toMap;

/**
 * @author: chenlin
 * @date: 2022/9/26 13:32
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class ReturnVisitRecordBiz extends BaseBiz<ReturnVisitRecordMapper, ReturnVisitRecord> {
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

    @Transactional(rollbackFor = Exception.class)
    public void save(ReturnVisitRecordForm form) {
        String optName = BaseContextHandler.getName();
        Integer optId = Integer.parseInt(BaseContextHandler.getUserID());
        Integer treatmentId = form.getTreatmentId();
        List<ReturnVisitRecord> datas = findReturnVisitListByTreatmentId(treatmentId);
        Map<Integer, ReturnVisitRecord> olds = datas.stream().collect(toMap(ReturnVisitRecord::getId, Function.identity()));
        List<ReturnVisitContentForm> visits = form.getVisitingContents();
        Date now = DateUtil.getCurrentDate();
        visits.forEach(visit->{
            Integer id = visit.getId();
            ReturnVisitRecord entity = new ReturnVisitRecord();
            convertTreatment(form, entity);
            convertReturnVisit(visit, optId, now, entity);
            ReturnVisitRecord old = olds.get(id);
            if (needRegister(old, entity)) {
                entity.setRegisterId(optId);
                entity.setRegisterName(optName);
            }
            if (StringHelper.isNotNull(old)) {
                entity.setId(id);
                mapper.updateByPrimaryKeySelective(entity);
                olds.remove(id);
            } else {
                entity.setCrtId(optId);
                entity.setCrtTime(now);
                entity.setInservice(true);
                mapper.insertSelective(entity);
            }
            remoteRabbitMqServiceFeign.sendMessage(entity.getId(),0,0, MsgCategoryEnum.BaseReturnVisit);
        });
        if (StringHelper.isNotEmpty(olds)) {
            olds.forEach((id, entity)->{
                tombstone(entity);
                remoteRabbitMqServiceFeign.sendMessage(entity.getId(),0,2, MsgCategoryEnum.BaseReturnVisit);
            });
        }
    }

    /**
     * 逻辑删除数据
     *
     * @param entity
     */
    private void tombstone(ReturnVisitRecord entity) {
        entity.setInservice(false);
        mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 是否需要记录登记人信息
     *
     * @param old
     * @param entity
     * @return
     */
    private boolean needRegister(ReturnVisitRecord old, ReturnVisitRecord entity) {
        if (StringHelper.isNull(old) || StringHelper.isNull(entity)) {
            return true;
        }
        if (!old.getReturnContent().equals(entity.getReturnContent())) {
            return true;
        }
        if (!old.getReturnReason().equals(entity.getReturnReason())) {
            return true;
        }
        if (!old.getReturnDate().equals(entity.getReturnDate())) {
            return true;
        }
        if (!old.getReturnTime().equals(entity.getReturnTime())) {
            return true;
        }
        return false;
    }

    private List<ReturnVisitRecord> findReturnVisitListByTreatmentId(Integer treatmentId) {
        Example example = new Example(ReturnVisitRecord.class);
        Example.Criteria c = example.createCriteria();
        c.andEqualTo("treatmentId", treatmentId);
        c.andEqualTo("inservice", true);
        return mapper.selectByExample(example);
    }

    private void convertReturnVisit(ReturnVisitContentForm visit, Integer optId, Date now, ReturnVisitRecord entity) {
        entity.setReturnDate(DateUtil.parse2Date(visit.getReturnDate()));
        try {
            entity.setReturnTime(DateUtil.parse(visit.getReturnTime(), "HH:mm"));
        } catch (Exception e) {
            log.error("convertReturnVisit error: {}", e);
            throw new ClientServiceException("日期转换错误", DATA_TRANSFORMATION_EXIST);
        }
        entity.setReturnReason(visit.getReturnReason());
        entity.setReturnContent(visit.getReturnContent());
        entity.setUpdId(optId);
        entity.setUpdTime(now);
    }

    /**
     * 将就诊数据转换
     *
     * @param form
     * @param entity
     */
    private void convertTreatment(ReturnVisitRecordForm form, ReturnVisitRecord entity) {
        entity.setTreatmentId(form.getTreatmentId());
        entity.setPatientId(form.getPatientId());
        entity.setDentistId(form.getDentistId());
        entity.setOrgId(form.getOrgId());
    }

    public PageInfo<ReturnVisitRecordVO> findReturnVisitRecordList(PatientReturnVisitQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<ReturnVisitRecordVO> result = mapper.selectReturnVisitRecordList(query);
        fillName(result);
        return new PageInfo<>(result);
    }

    /**
     * 填充名称
     *
     * @param result
     */
    private void fillName(List<ReturnVisitRecordVO> result) {
        result.forEach(vo->{
            Integer orgId = vo.getOrgId();
            OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            if (StringHelper.isNotNull(org)) {
                vo.setAbbreviation(org.getAbbreviation());
            }
            Integer dentistId = vo.getDentistId();
            SysEmployee dentist = remoteSystemServiceFeign.findSysEmployeeById(dentistId);
            if (StringHelper.isNotNull(dentist)) {
                vo.setDentistName(dentist.getName());
            }
        });
    }

    /**
     * 根据就诊id获取回访明细
     *
     * @param treatmentId
     * @return
     */
    public ReturnVisitVO findListByTreatmentId(Integer treatmentId) {
        TreatmentOrderVO treatmentOrderVO = remoteTreatmentServiceFeign.findTreatmentOrderByTreatmentId(treatmentId);
        if (StringHelper.isNotNull(treatmentOrderVO)) {
            ReturnVisitVO result = new ReturnVisitVO();
            BeanUtil.copyProperties(treatmentOrderVO, result);
            List<ReturnVisitRecord> visits = mapper.selectReturnVisitByTreatmentId(treatmentId);
            List<ReturnVisitContentVO> data = new ArrayList<>();
            if (StringHelper.isNotEmpty(visits)) {
                visits.forEach(visit->{
                    ReturnVisitContentVO content = new ReturnVisitContentVO();
                    content.setId(visit.getId());
                    content.setReturnDate(visit.getReturnDate());
                    content.setReturnTime(visit.getReturnTime());
                    content.setReturnReason(visit.getReturnReason());
                    content.setReturnContent(visit.getReturnContent());
                    data.add(content);
                });
            }
            result.setVisitingContents(data);
            return result;
        }
        return null;
    }
}
