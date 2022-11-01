package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.model.PatientCommunicationModel;
import com.yunya.feign.patient_central.domain.query.PatientCommunicationForm;
import com.yunya.feign.patient_central.domain.query.PatientCommunicationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientCommunicationInfoVO;
import com.yunya.feign.patient_central.domain.vo.web.PatientCommunicationVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientCommunication;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.patient_central.mapper.PatientCommunicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;

/**
 * 患者沟通记录业务层
 *
 * @author: chenlin
 * @date: 2022/11/1 9:15
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class PatientCommunicationBiz extends BaseBiz<PatientCommunicationMapper, PatientCommunication> {

    /** 系统服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    
    /**
     * 添加患者沟通记录
     *
     * @param model
     */
    public void add(PatientCommunicationModel model) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Date now = new Date(System.currentTimeMillis());
        PatientCommunication entity = new PatientCommunication();
        entity.setOrgId(orgId);
        entity.setContent(model.getContent());
        entity.setPatientId(model.getPatientId());
        entity.setCrtTime(now);
        entity.setCrtId(userId);
        entity.setUpdTime(now);
        entity.setUpdId(userId);
        mapper.insertSelective(entity);
    }

    /**
     * 修改患者沟通记录
     *
     * @param form
     */
    public void edit(PatientCommunicationForm form) {
        PatientCommunication entity = checkDataExists(form.getId());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
        Date now = new Date(System.currentTimeMillis());
        entity.setOrgId(orgId);
        entity.setContent(form.getContent());
        entity.setPatientId(form.getPatientId());
        entity.setUpdTime(now);
        entity.setUpdId(userId);
        mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 检查记录是否存在
     *
     * @param id
     * @return
     */
    private PatientCommunication checkDataExists(Integer id) {
        PatientCommunication entity = selectById(id);
        if (StringHelper.isNull(entity)) {
            throw new ClientServiceException("沟通记录不存在", DATA_NOT_EXIST);
        }
        return entity;
    }

    /**
     * 删除患者沟通记录
     *
     * @param id
     */
    public void delete(Integer id) {
        PatientCommunication entity = checkDataExists(id);
        entity.setInservice(false);
        mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 根据条件查询患者沟通记录列表
     *
     * @param query
     * @return
     */
    public PageInfo<PatientCommunicationVO> findList(PatientCommunicationQueryForm query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<PatientCommunicationVO> data = mapper.selectPatientCommunicationList(query);
        fillEmployeeName(data);
        return new PageInfo<>(data);
    }

    /**
     * 填充员工姓名
     *
     * @param data
     */
    private void fillEmployeeName(List<PatientCommunicationVO> data) {
        data.forEach(vo->{
            String crtId = vo.getCrtName();
            if (StringHelper.isNotEmpty(crtId)) {
                SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(Integer.parseInt(crtId));
                if (StringHelper.isNotNull(employee)) {
                    vo.setCrtName(employee.getName());
                }
            }
            String updId = vo.getUpdName();
            if (StringHelper.isNotEmpty(updId)) {
                SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(Integer.parseInt(updId));
                if (StringHelper.isNotNull(employee)) {
                    vo.setUpdName(employee.getName());
                }
            }
        });
    }

    /**
     * 根据id查询沟通详情
     *
     * @param id
     * @return
     */
    public PatientCommunicationInfoVO findOneById(Integer id) {
        PatientCommunication entity = selectById(id);
        if (StringHelper.isNull(entity)) {
            return null;
        }
        PatientCommunicationInfoVO info = new PatientCommunicationInfoVO();
        info.setId(entity.getId());
        info.setPatientId(entity.getPatientId());
        info.setContent(entity.getContent());
        return info;
    }
}
