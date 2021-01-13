package com.yunya.modules.employeeattend.biz;


import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.employee_attend.ClinicSchedule;
import com.yunya.modules.employeeattend.form.ClinicNode;
import com.yunya.modules.employeeattend.mapper.ClinicScheduleMapper;
import com.yunya.modules.employeeattend.vo.ClinicScheduleBaseVO;
import com.yunya.modules.employeeattend.vo.ClinicScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-06-05 11:55
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicScheduleBiz extends BaseBiz<ClinicScheduleMapper, ClinicSchedule> {
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    /**
     * 新增和更新
     *
     * @param clinicSchedule
     */
    public void saveOrUpdate(ClinicSchedule clinicSchedule) {
        ClinicSchedule data = new ClinicSchedule();
        data.setClinicId(clinicSchedule.getClinicId());
        data.setScheduleId(clinicSchedule.getScheduleId());
        ClinicSchedule schedule = mapper.selectOne(data);
        if (schedule == null) {
            // 新增
            insertSelective(clinicSchedule);
        } else {
            // 修改
            clinicSchedule.setId(schedule.getId());
            updateSelectiveById(clinicSchedule);
        }
    }

    /**
     * 获取列表
     *
     * @param clinicId
     * @return
     */
    public List<ClinicScheduleVO> findVOsByClinicId(Integer clinicId) {
        return mapper.selectVOsByClinicId(clinicId);
    }


    /**
     * 批量添加
     *
     * @param id
     * @param clinicNodes
     */
    public int batchInsert(Integer id, List<ClinicNode> clinicNodes) {
        if(clinicNodes.isEmpty()){
            return 0;
        }

        List<ClinicSchedule> clinicSchedules = new ArrayList<>();
        for (ClinicNode clinicNode : clinicNodes) {
            ClinicSchedule clinicSchedule = new ClinicSchedule();
            clinicSchedule.setScheduleId(id);
            clinicSchedule.setClinicId(clinicNode.getClinicId());
            clinicSchedule.setInservice(clinicNode.getInservice());
            clinicSchedule.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicSchedule.setCrtName(BaseContextHandler.getUsername());
            clinicSchedules.add(clinicSchedule);
        }
        return mapper.batchInsert(clinicSchedules);
    }

    /**
     * 获取列表
     *
     * @param clinicId
     * @return
     */
    public List<ClinicScheduleVO> findVOsByClinicIdAndInservice(Integer clinicId) {
        return mapper.selectVOsByClinicIdAndInservice(clinicId);
    }
    /**
     * 获取列表
     *
     * @param scheduleId
     * @return
     */
    public List<ClinicScheduleBaseVO> findVOByScheduleIdAndInservice(Integer scheduleId) {
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

        List<ClinicScheduleBaseVO>list = mapper.findVOByScheduleIdAndInservice(scheduleId);
        List<ClinicScheduleBaseVO>relist = new ArrayList<>();
        if(list.size()>0){
            for(ClinicScheduleBaseVO clinicScheduleBaseVO:list){
                String name = "";
                if(clinicMap.get(clinicScheduleBaseVO.getClinicId().toString())!=null){
                    name = clinicMap.get(clinicScheduleBaseVO.getClinicId().toString()).getName();
                }
                clinicScheduleBaseVO.setClinicName(name);
                relist.add(clinicScheduleBaseVO);
            }
        }
        return relist;
    }
}
