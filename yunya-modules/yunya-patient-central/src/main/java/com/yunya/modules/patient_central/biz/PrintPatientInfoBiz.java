package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.query.PrintInfoQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientMedicalRecordDetailVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PrintInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: yunya-dental
 * @description: 患者相关信息打印业务
 * @author: LHB
 * @create: 2020-11-17 11:26
 **/
@Service
public class PrintPatientInfoBiz {
    /** 就诊服务Feign */
    @Autowired
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    /** 患者服务 */
    @Autowired
    private PatientBaseInfoBiz patientBaseInfoBiz;
    /** 系统服务Feign */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 返回患者打印相关信息
     * @param treatmentIds 患者就诊记录ID(就诊ID)
     * @return 返回打印信息实例对象
     */
    public PrintInfoVo printInfo(Integer patientId, PrintInfoQuery treatmentIds) {
        PrintInfoVo printInfoVo = new PrintInfoVo();
        List<PatientMedicalRecordDetailVo> medicalRecordDetails = new ArrayList<>();
        // 设置患者名字和病历编号
        PatientTotalInfoVo patientTotalInfo = this.patientBaseInfoBiz.findPatientTotalInfo(patientId);
        printInfoVo.setMedicalNumber(patientTotalInfo.getMedicalNumber());
        printInfoVo.setPatientName(patientTotalInfo.getName());
        // 设置患者会员类型
        Integer memberTypeId = patientTotalInfo.getMemberTypeId();
        if (memberTypeId != null) {
            MemberType memberType = this.remoteSystemServiceFeign.findMemberTypeById(memberTypeId);
            if (null != memberType) {
                printInfoVo.setMemberTypeName(memberType.getName());
            }
        }
        List<Integer> treatmentRecordParams = treatmentIds.getTreatmentIds();
        if (StringHelper.isNotEmpty(treatmentRecordParams)) {
            Set<Integer> treatmentRecordSet = new HashSet<>(treatmentIds.getTreatmentIds());
            List<TreatmentRecordExtendVO> treatmentRecords = this.treatmentServiceFeign.findTreatmentRecordByIds(treatmentRecordSet);
            if (StringHelper.isNotEmpty(treatmentRecords)) {
                // 就诊记录按就诊日期降序排列
                List<TreatmentRecordExtendVO> collect = treatmentRecords.stream().sorted(Comparator.comparing(TreatmentRecord::getTreatStartTime).reversed()).collect(Collectors.toList());
                TreatmentRecordExtendVO treatmentRecord = collect.get(0);
                // 设置末诊日期
                printInfoVo.setLastTreatmentDate(treatmentRecord.getLastTreatmentDate());

                // 设置患者末诊医生和末诊时间
                treatmentRecords.forEach(item -> {
                    PatientMedicalRecordDetailVo patientMedicalRecordDetailVo = new PatientMedicalRecordDetailVo();
                    // 设置就诊ID
                    patientMedicalRecordDetailVo.setTreatmentId(item.getId());
                    Integer dentistId = item.getDentistId();
                    SysEmployee sysEmployee = this.remoteSystemServiceFeign.findSysEmployeeById(dentistId);
                    if (null != sysEmployee) {
                        // 设置医生名字
                        patientMedicalRecordDetailVo.setDentistName(sysEmployee.getName());
                    }
                    patientMedicalRecordDetailVo.setTreatmentDate(item.getTreatStartTime());
                    medicalRecordDetails.add(patientMedicalRecordDetailVo);
                });
                printInfoVo.setMedicalRecordDetails(medicalRecordDetails);
            }
        }
        return printInfoVo;
    }

}
