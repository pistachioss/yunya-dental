package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.employee_attend.vo.PeriodontalRecordsVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment_other.domain.form.PeriodontalRecordsAddForm;
import com.yunya.feign.treatment_other.domain.form.PeriodontalRecordsForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.treatment_other.PeriodontalRecords;
import com.yunya.modules.treatment.other.mapper.PeriodontalRecordsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PeriodontalRecordsBiz extends BaseBiz<PeriodontalRecordsMapper, PeriodontalRecords> {
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    public List<PeriodontalRecordsVO> findList(PeriodontalRecordsForm periodontalRecordsForm) {
        PeriodontalRecords periodontalRecords = new PeriodontalRecords();
        BeanUtils.copyProperties(periodontalRecordsForm, periodontalRecords);
        periodontalRecords.setIsDelete(0);
        List<PeriodontalRecords> list = mapper.select(periodontalRecords);
        List<PeriodontalRecordsVO> reList = new ArrayList<>();
        if (list.size() > 0) {
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            //查询总数不分页
            model.setWhetherPage(false);
            Byte[] userStatus = {0, 1,2, 3};
            //离职状态
            model.setWorkStatus(userStatus);
            //当前门诊下全部员工
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> employeeMap = new HashMap(16);
            employees.forEach(z -> employeeMap.put(z.getUserId() + "", z));
            //获取门诊信息
            OrganizationModel organizationModel = new OrganizationModel();
            organizationModel.setWhetherPage(false);
            //全部门诊信息
            List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
            Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
            clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

            for (PeriodontalRecords per : list) {
                PeriodontalRecordsVO periodontalRecordsVO = new PeriodontalRecordsVO();
                BeanUtils.copyProperties(per, periodontalRecordsVO);
                periodontalRecordsVO.setCompanyName(clinicMap.get(periodontalRecordsVO.getCompanyId().toString()).getName());
                periodontalRecordsVO.setDoctorName(employeeMap.get(periodontalRecordsVO.getDoctorId().toString()).getName());

                reList.add(periodontalRecordsVO);
            }
        }
        return reList;
    }

    public int create(PeriodontalRecordsAddForm periodontalRecordsAddForm) {
        PeriodontalRecords periodontalRecords = new PeriodontalRecords();
        BeanUtils.copyProperties(periodontalRecordsAddForm, periodontalRecords);
        periodontalRecords.setIsDelete(0);
        periodontalRecords.setCheckDate(new Date());
        periodontalRecords.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        periodontalRecords.setCrtTime(new Date());
        return mapper.insert(periodontalRecords);
    }

    public int update(PeriodontalRecordsAddForm periodontalRecordsAddForm) {
        PeriodontalRecords periodontalRecords = new PeriodontalRecords();
        BeanUtils.copyProperties(periodontalRecordsAddForm, periodontalRecords);
        periodontalRecords.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        periodontalRecords.setUpdTime(new Date());
        return mapper.updateByPrimaryKeySelective(periodontalRecords);
    }

    public int delete(PeriodontalRecordsForm periodontalRecordsForm) {
        PeriodontalRecords periodontalRecords = new PeriodontalRecords();
        BeanUtils.copyProperties(periodontalRecordsForm, periodontalRecords);
        periodontalRecords.setIsDelete(1);
        return mapper.updateByPrimaryKeySelective(periodontalRecords);
    }
}
