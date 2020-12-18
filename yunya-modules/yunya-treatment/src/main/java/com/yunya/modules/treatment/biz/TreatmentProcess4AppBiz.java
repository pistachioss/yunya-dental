package com.yunya.modules.treatment.biz;

import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.treatment.domain.query.AppTreatmentQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: yunya-dental
 * @description: App端预约列表处理类
 * @author: LHB
 * @create: 2020-12-16 13:34
 **/
@Service
public class TreatmentProcess4AppBiz {
    @Autowired
    private RemoteAppointmentFeign appointmentFeign;
    @Autowired
    private RegisteredBiz registeredBiz;
    @Autowired
    private BillRecordBiz billRecordBiz;
    @Autowired
    private TreatmentRecordBiz treatmentRecordBiz;
    @Autowired
    private RemotePatientCentralServiceFeign patientCentralServiceFeign;

    /**
     * 查询患者就诊信息
     * @param query 查询参数
     */
    public TreatmentInfo4AppVO treatmentInfoDetail(AppTreatmentQuery query) {
        TreatmentInfo4AppVO treatmentInfo4AppVO = new TreatmentInfo4AppVO();
        // 设置预约信息
        Integer appointId = query.getAppointId();
        if (null != appointId) {
            // 查询预约详细信息
            AppointmentVo appointmentDetailById = this.appointmentFeign.findAppointmentDetailById(appointId);
            // 设置预约信息
            this.injectAppointField(appointmentDetailById,treatmentInfo4AppVO);
        }
        // 设置患者挂号信息
        Integer registeredId = query.getRegisteredId();
        if (null != registeredId) {
            // 查询患者挂号信息
            RegisteredVO registeredVO = registeredBiz.registeredInfoDetail(registeredId);
            // 设置患者挂号信息
            this.injectPatientRegField(registeredVO,treatmentInfo4AppVO);
        }
        // 设置账单信息
        Integer treatmentId = query.getTreatmentId();
        if (null != treatmentId) {
            // 查询账单信息
            OrderBill4AppVO orderBill4AppVO = this.billRecordBiz.findOrderAndBill4App(treatmentId);
            // 设置账单信息
            this.injectOrderBillField(orderBill4AppVO,treatmentInfo4AppVO);
        }
        return treatmentInfo4AppVO;
    }

    /**
     * 注入账单信息字段
     * @param orderBill4AppVO 数据源实体
     * @param treatmentInfo4AppVO 目标实体
     */
    private void injectOrderBillField(OrderBill4AppVO orderBill4AppVO, TreatmentInfo4AppVO treatmentInfo4AppVO) {
        if (null != orderBill4AppVO) {
            TreatmentOrderInfo4AppVO orderInfo4AppVO = new TreatmentOrderInfo4AppVO();
            orderInfo4AppVO.setActualReceivableAmount(orderBill4AppVO.getActualReceivableAmount());
            orderInfo4AppVO.setDebtAmount(orderBill4AppVO.getDebtAmount());
            orderInfo4AppVO.setPrivilegeAmount(orderBill4AppVO.getPrivilegeAmount());
            orderInfo4AppVO.setReceivableAmount(orderBill4AppVO.getReceivableAmount());
            orderInfo4AppVO.setReceivedAmount(orderBill4AppVO.getReceivedAmount());
            orderInfo4AppVO.setBillNumber(orderBill4AppVO.getBillNumber());
            orderInfo4AppVO.setOrderItems(orderBill4AppVO.getBillItems());
            treatmentInfo4AppVO.setOrderInfo(orderInfo4AppVO);
        }

    }

    /**
     * 注入患者挂号信息字段
     * @param registeredVO 数据源实体
     * @param treatmentInfo4AppVO  目标实体
     */
    private void injectPatientRegField(RegisteredVO registeredVO, TreatmentInfo4AppVO treatmentInfo4AppVO) {
        if (null != registeredVO) {
            TreatmentRegInfo4AppVO regInfo4AppVO = new TreatmentRegInfo4AppVO();
            // 查询就诊信息
            TreatmentRecord treatQuery = new TreatmentRecord();
            treatQuery.setRegisteredId(registeredVO.getId());
            TreatmentRecord treatmentRecord = treatmentRecordBiz.selectOne(treatQuery);
            if (null != treatmentRecord) {
                regInfo4AppVO.setTreatmentId(treatmentRecord.getId());
            }
            regInfo4AppVO.setRegAssistantId(registeredVO.getAssistantId());
            regInfo4AppVO.setRegAssistantName(registeredVO.getAssistantName());
            regInfo4AppVO.setRegDentistId(registeredVO.getDentistId());
            regInfo4AppVO.setRegDentistName(registeredVO.getDentistName());
            regInfo4AppVO.setRegistedId(registeredVO.getId());
            regInfo4AppVO.setCrtTime(registeredVO.getCrtTime());
            treatmentInfo4AppVO.setRegInfo(regInfo4AppVO);
        }
    }

    /**
     * 注入预约信息字段
     * @param appointmentDetailById 数据源实体
     * @param treatmentInfo4AppVO 目标实体
     */
    private void injectAppointField(AppointmentVo appointmentDetailById, TreatmentInfo4AppVO treatmentInfo4AppVO) {
        if (null != appointmentDetailById) {
            // 设置患者预约信息
            TreatmentAppointInfo4AppVO appointInfo4AppVO = new TreatmentAppointInfo4AppVO();
            PatientTotalInfoVo patientTotalInfo = this.patientCentralServiceFeign.findPatientTotalInfo(appointmentDetailById.getPatientId());
            appointInfo4AppVO.setAge(patientTotalInfo.getAge());
            appointInfo4AppVO.setGender(patientTotalInfo.getGender());
            appointInfo4AppVO.setAppointId(appointmentDetailById.getId());
            appointInfo4AppVO.setAppointContent(appointmentDetailById.getAppointContent());
            appointInfo4AppVO.setAppointDate(appointmentDetailById.getAppointDate());
            appointInfo4AppVO.setAppointDuration(appointmentDetailById.getAppointDuration());
            appointInfo4AppVO.setAppointTime(appointmentDetailById.getAppointTime());
            appointInfo4AppVO.setAppointType(appointmentDetailById.getAppointType());
            appointInfo4AppVO.setSplits(appointmentDetailById.getSplitList());
            appointInfo4AppVO.setClinicDeviceItemId(appointmentDetailById.getClinicDeviceItemId());
            appointInfo4AppVO.setClinicDeviceItemName(appointmentDetailById.getClinicDeviceItemName());
            appointInfo4AppVO.setConfirmStatus(appointmentDetailById.getConfirmStatus());
            appointInfo4AppVO.setPatientId(appointmentDetailById.getPatientId());
            appointInfo4AppVO.setPatientName(appointmentDetailById.getPatientName());
            appointInfo4AppVO.setDeptRoomId(appointmentDetailById.getDeptRoomId());
            appointInfo4AppVO.setDeptRoomName(appointmentDetailById.getDeptRoomName());
            appointInfo4AppVO.setRemarks(appointmentDetailById.getRemarks());
            appointInfo4AppVO.setDentistId(appointmentDetailById.getDentistId());
            appointInfo4AppVO.setDentistName(appointmentDetailById.getDentistName());
            treatmentInfo4AppVO.setAppintInfo(appointInfo4AppVO);
        }
    }
}
