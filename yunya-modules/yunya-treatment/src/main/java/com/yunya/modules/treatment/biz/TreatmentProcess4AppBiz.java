package com.yunya.modules.treatment.biz;

import com.yunya.feign.appointment.RemoteAppointmentFeign;
import com.yunya.feign.appointment.vo.AppointmentSplitVo;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.treatment.domain.vo.OrderBill4AppVO;
import com.yunya.feign.treatment.domain.vo.RegisteredVO;
import com.yunya.feign.treatment.domain.vo.TreatmentInfo4AppVO;
import com.yunya.framework.common.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 查询患者就诊信息
     * @param appointId  预约ID
     * @param registeredId 挂号ID
     * @param treatmentId  就诊ID
     */
    public TreatmentInfo4AppVO treatmentInfoDetail(Integer appointId, Integer registeredId, Integer treatmentId) {
        appointId = appointId == null ? 0 : appointId;
        registeredId = registeredId == null ? 0 : registeredId;
        treatmentId = treatmentId == null ? 0 : treatmentId;
        // 查询预约详细信息
        AppointmentVo appointmentDetailById = this.appointmentFeign.findAppointmentDetailById(appointId);
        // 查询患者挂号信息
        List<RegisteredVO> registeredVOs = registeredBiz.registeredInfoDetail(registeredId, appointId,null);
        // 查询账单信息
        OrderBill4AppVO orderBill4AppVO = this.billRecordBiz.findOrderAndBill4App(treatmentId);
        TreatmentInfo4AppVO treatmentInfo4AppVO = new TreatmentInfo4AppVO();
        // 设置预约信息
        this.injectAppointField(appointmentDetailById,treatmentInfo4AppVO);
        // 设置患者挂号信息
        if (StringHelper.isNotEmpty(registeredVOs)) {
            this.injectPatientRegField(registeredVOs.get(0),treatmentInfo4AppVO);
        }
        // 设置账单信息
        this.injectOrderBillField(orderBill4AppVO,treatmentInfo4AppVO);
        return treatmentInfo4AppVO;
    }

    /**
     * 注入账单信息字段
     * @param orderBill4AppVO 数据源实体
     * @param treatmentInfo4AppVO 目标实体
     */
    private void injectOrderBillField(OrderBill4AppVO orderBill4AppVO, TreatmentInfo4AppVO treatmentInfo4AppVO) {
        if (null != orderBill4AppVO) {
            treatmentInfo4AppVO.setActualReceivableAmount(orderBill4AppVO.getActualReceivableAmount());
            treatmentInfo4AppVO.setDebtAmount(orderBill4AppVO.getDebtAmount());
            treatmentInfo4AppVO.setPrivilegeAmount(orderBill4AppVO.getPrivilegeAmount());
            treatmentInfo4AppVO.setReceivableAmount(orderBill4AppVO.getReceivableAmount());
            treatmentInfo4AppVO.setReceivedAmount(orderBill4AppVO.getReceivedAmount());
            treatmentInfo4AppVO.setBillNumber(orderBill4AppVO.getBillNumber());
            treatmentInfo4AppVO.setOrderItems(orderBill4AppVO.getBillItems());
        }

    }

    /**
     * 注入患者挂号信息字段
     * @param registeredVO 数据源实体
     * @param treatmentInfo4AppVO  目标实体
     */
    private void injectPatientRegField(RegisteredVO registeredVO, TreatmentInfo4AppVO treatmentInfo4AppVO) {
        if (null != registeredVO) {
            treatmentInfo4AppVO.setRegAssistantId(registeredVO.getAssistantId());
            treatmentInfo4AppVO.setRegAssistantName(registeredVO.getAssistantName());
            treatmentInfo4AppVO.setRegDentistId(registeredVO.getDentistId());
            treatmentInfo4AppVO.setRegDentistName(registeredVO.getDentistName());
            treatmentInfo4AppVO.setRegistedId(registeredVO.getId());
            treatmentInfo4AppVO.setCrtTime(registeredVO.getCrtTime());
        }
    }

    /**
     * 注入预约信息字段
     * @param appointmentDetailById 数据源实体
     * @param treatmentInfo4AppVO 目标实体
     */
    private void injectAppointField(AppointmentVo appointmentDetailById, TreatmentInfo4AppVO treatmentInfo4AppVO) {
        if (null != appointmentDetailById) {
            treatmentInfo4AppVO.setAppointId(appointmentDetailById.getId());
            treatmentInfo4AppVO.setAppointContent(appointmentDetailById.getAppointContent());
            treatmentInfo4AppVO.setAppointDate(appointmentDetailById.getAppointDate());
            treatmentInfo4AppVO.setAppointDuration(appointmentDetailById.getAppointDuration());
            treatmentInfo4AppVO.setAppointTime(appointmentDetailById.getAppointTime());
            treatmentInfo4AppVO.setAppointType(appointmentDetailById.getAppointType());
            treatmentInfo4AppVO.setSplits(appointmentDetailById.getSplitList());
            treatmentInfo4AppVO.setClinicDeviceItemId(appointmentDetailById.getClinicDeviceItemId());
            treatmentInfo4AppVO.setClinicDeviceItemName(appointmentDetailById.getClinicDeviceItemName());
            treatmentInfo4AppVO.setConfirmStatus(appointmentDetailById.getConfirmStatus());
            treatmentInfo4AppVO.setPatientId(appointmentDetailById.getPatientId());
            treatmentInfo4AppVO.setPatientName(appointmentDetailById.getPatientName());
            treatmentInfo4AppVO.setDeptRoomId(appointmentDetailById.getDeptRoomId());
            treatmentInfo4AppVO.setDeptRoomName(appointmentDetailById.getDeptRoomName());
            treatmentInfo4AppVO.setRemarks(appointmentDetailById.getRemarks());
        }
    }
}
