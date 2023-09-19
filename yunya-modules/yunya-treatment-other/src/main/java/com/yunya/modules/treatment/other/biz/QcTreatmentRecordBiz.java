package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.treatment.domain.form.QcTreatmentImportForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import com.yunya.feign.treatment_other.domain.form.QcAdviceItemStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadTreatmentForm;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.QcAdviceStatusVO;
import com.yunya.feign.treatment_other.domain.vo.QcDoctorAdviceRecordVO;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondInfoVO;
import com.yunya.feign.treatment_other.domain.vo.QcRecommondOrderVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.treatment_other.QcTreatmentItem;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import com.yunya.modules.treatment.other.mapper.QcTreatmentItemMapper;
import com.yunya.modules.treatment.other.mapper.QcTreatmentRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static java.util.stream.Collectors.toMap;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:42
 * @description:
 * @since: 1.0.0
 */
@Service
public class QcTreatmentRecordBiz extends BaseBiz<QcTreatmentRecordMapper, QcTreatmentRecord> {
    @Autowired
    private QcTreatmentItemMapper qcTreatmentItemMapper;
    @Autowired
    private QcWebServiceClientBiz qcWebServiceClientBiz;
    @Autowired
    private RemotePatientCentralServiceFeign patientFeign;

    public PageInfo<QcRecommondInfoVO> findMallRecommondList(QcRecommondInfoQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        QcDoctorAdviceQuery queryForm = new QcDoctorAdviceQuery();
        queryForm.setStart_date(query.getStartDate());
        queryForm.setEnd_date(query.getEndDate());
        queryForm.setWerif_code(query.getVerifyCode());
        QcDoctorAdviceRecordVO recommondList = qcWebServiceClientBiz.findDoctorAdviceRecommondList(queryForm);

        List<QcRecommondInfoVO> result = mapper.selectMallRecommondList(query);
        return new PageInfo<>(result);
    }

    /**
     * mall平台核销
     *
     * @param id
     * @param verifyCode
     */
    public void verify(Integer id, String verifyCode) {
        QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
        List<QcTreatmentItem> items = qcTreatmentItemMapper.selectListByQcTreatmentId(id);
        if (StringHelper.isNotEmpty(items)) {
            QcAdviceStatusForm statusForm = new QcAdviceStatusForm();
            List<QcAdviceItemStatusForm> orderInfos = Lists.newArrayList();
            items.forEach(item->{
                QcAdviceItemStatusForm form = new QcAdviceItemStatusForm();
                form.setMall_order_no(item.getOrderNo());
                form.setStatus("1");
                form.setVerifCode(verifyCode);
//                form.setForceFlag();
                orderInfos.add(form);
            });
            statusForm.setOrder_infos(orderInfos);
            qcWebServiceClientBiz.updateAdviceItemStatus(statusForm);

            Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
            Date now = DateUtil.now();
            qcTreatmentRecord.setVerifyId(userId);
            qcTreatmentRecord.setVerifyCode(verifyCode);
            qcTreatmentRecord.setVerifyDate(now);
            qcTreatmentRecord.setUpdId(userId);
            qcTreatmentRecord.setUpdTime(now);
            updateSelectiveById(qcTreatmentRecord);
        }
    }

    private QcTreatmentRecord checkQcTreatmentRecord(Integer id) {
        QcTreatmentRecord qcTreatmentRecord = selectById(id);
        if (StringHelper.isNull(qcTreatmentRecord)) {
            throw new ClientServiceException("全程医疗推荐数据不存在", DATA_NOT_EXIST);
        }
        return qcTreatmentRecord;
    }

    public void bindPatient(Integer id, Integer patientId) {
        QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
        PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
        if (StringHelper.isNull(patient)) {
            throw new ClientServiceException("患者信息不存在", DATA_NOT_EXIST);
        }
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        qcTreatmentRecord.setPatientId(patientId);
        String remark = StringHelper.defaultString(qcTreatmentRecord.getRemark());
        if (StringHelper.isNotEmpty(remark)) {
            remark += ";";
        }
        remark += "实际使用人:" + patient.getName();
        qcTreatmentRecord.setRemark(remark);
        qcTreatmentRecord.setUpdId(userId);
        qcTreatmentRecord.setUpdTime(now);
        updateSelectiveById(qcTreatmentRecord);
    }

    public void execute(Integer id) {
        checkQcTreatmentRecord(id);
        List<QcTreatmentItem> items = qcTreatmentItemMapper.selectListByQcTreatmentId(id);
        if (StringHelper.isNotEmpty(items)) {
            QcAdviceStatusForm statusForm = new QcAdviceStatusForm();
            List<QcAdviceItemStatusForm> orderInfos = Lists.newArrayList();
            items.forEach(item -> {
                QcAdviceItemStatusForm form = new QcAdviceItemStatusForm();
                form.setMall_order_no(item.getOrderNo());
                form.setStatus("6");
//                form.setForceFlag();
                orderInfos.add(form);
            });
            statusForm.setOrder_infos(orderInfos);
            qcWebServiceClientBiz.updateAdviceItemStatus(statusForm);
        }
    }

    public void doctorAdviceUpload(List<Integer> qcTreatmentIds) {
        List<QcAdviceUploadTreatmentForm> forms = Lists.newArrayList();
        qcTreatmentIds.forEach(id->{
            QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);

        });

        forms.forEach(form->{
            QcAdviceStatusVO result = qcWebServiceClientBiz.uploadAdvice2MallPlatform(form);
        });
    }

    /**
     * 全程医疗登记单匹配订单
     *
     * @param form
     * @return
     */
    public QcRecommondOrderVO orderMatchQcTreatmentList(QcTreatmentImportForm form) {
        QcRecommondOrderVO result = new QcRecommondOrderVO();
        List<QcTreatmentVO> qcTreatments = Lists.newArrayList();
        Map<String, OrderDetailChargeVO> orderDetailMap = form.getOrderDetails().stream().collect(
                toMap(v->StringHelper.joinWith(",",v.getType(),v.getBillingItemId()), Function.identity()));
        form.getQcTreatmentIds().forEach(id->{
            BigDecimal originAmount = BigDecimal.ZERO;
            List<OrderDetailChargeVO> items = Lists.newArrayList();
            QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
            if (qcTreatmentRecord.getType() ==  1) {
                List<QcTreatmentItem> qcTreatmentItems = qcTreatmentItemMapper.selectListByQcTreatmentId(id);
                for (QcTreatmentItem adviceItem : qcTreatmentItems) {
                    String key = StringHelper.joinWith(",", adviceItem.getItemType(), adviceItem.getItemId());
                    OrderDetailChargeVO orderDetail = orderDetailMap.get(key);
                    if (StringHelper.isNotNull(orderDetail)) {
                        int quantity = orderDetail.getQuantity();
                        int surplus = quantity - adviceItem.getQuantity();
                        if (surplus > 0) {
                            // 艾维项目仍有剩余
                            orderDetail.setQuantity(surplus);
                            quantity = adviceItem.getQuantity();
                        } else {
                            orderDetailMap.remove(key);
                        }
                        BigDecimal itemPrice = BigDecimal.valueOf(quantity).multiply(orderDetail.getPrice());
                        originAmount = originAmount.add(itemPrice);
                        items.add(adviceItem2OrderDetail(quantity, orderDetail.getPrice(), orderDetail));
                    }
                }
            }
            QcTreatmentVO vo = new QcTreatmentVO();
            vo.setQcTreatmentId(id);
            vo.setAdmNo(qcTreatmentRecord.getAdmNo());
            vo.setType(qcTreatmentRecord.getType());
            vo.setOriginAmount(originAmount);
            vo.setReceivedAmount(originAmount);
            vo.setQcAdviceItems(items);
            qcTreatments.add(vo);
        });
        result.setQcTreatments(qcTreatments);
        result.setOrderDetailIds(orderDetailMap.values().stream()
                .map(OrderDetailChargeVO::getOrderDetailId).collect(Collectors.toList()));
        return result;
    }

    /**
     * 获取已绑定账单的全程医疗登记单列表
     *
     * @param orderRecordId
     * @return
     */
    public List<QcTreatmentVO> findBindingQcTreatmentList(Integer orderRecordId) {
        List<QcTreatmentVO> qcTreatments = mapper.selectQcTreatmentListByOrderId(orderRecordId);
        qcTreatments.forEach(treatment->{
            List<OrderDetailChargeVO> orderDetails = Lists.newArrayList();
            List<QcTreatmentItem> items = qcTreatmentItemMapper.selectListByQcTreatmentId(treatment.getQcTreatmentId());
            items.forEach(item->{
                OrderDetailChargeVO itemVO = BeanCopierUtils.generalCopyBean(item, OrderDetailChargeVO.class);
                int quantity = itemVO.getQuantity();
                BigDecimal itemPrice = BigDecimal.valueOf(quantity).multiply(item.getPrice());
                itemVO.setActualAmount(itemPrice);
                itemVO.setReceivableAmount(itemPrice);
                itemVO.setPrivilegeAmount(BigDecimal.ZERO);
                itemVO.setReceivedAmount(itemPrice);
                orderDetails.add(adviceItem2OrderDetail(item.getQuantity(), item.getPrice(), item));
            });
            if (StringHelper.isNotEmpty(orderDetails)) {
                treatment.setQcAdviceItems(orderDetails);
            }
        });
        return qcTreatments;
    }


    public OrderDetailChargeVO adviceItem2OrderDetail(int quantity, BigDecimal price, Object source) {
        OrderDetailChargeVO itemVO = BeanCopierUtils.generalCopyBean(source, OrderDetailChargeVO.class);
        BigDecimal itemPrice = BigDecimal.valueOf(quantity).multiply(price);
        itemVO.setActualAmount(itemPrice);
        itemVO.setReceivableAmount(itemPrice);
        itemVO.setPrivilegeAmount(BigDecimal.ZERO);
        itemVO.setReceivedAmount(itemPrice);
        return itemVO;
    }

    /**
     * 查询患者的可用全程医疗就诊记录列表
     *
     * @param patientId
     * @return
     */
    public List<QcRecommondInfoVO> findPatientEnableQcTreatmentRecord(Integer patientId) {
        List<QcRecommondInfoVO> result = mapper.selectPatientEnableQcTreatmentRecord(patientId);
        PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
        String patientName = null;
        if (StringHelper.isNotNull(patient)) {
            patientName = patient.getName();
        }
        for (QcRecommondInfoVO recommondInfo : result) {
            recommondInfo.setPatientName(patientName);
        }
        return result;
    }

    public void billBindingQcTreatment(QcTreatmentImportForm form) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        Integer billId = form.getBillRecordId();
        Integer orderRecordId = form.getOrderRecordId();

        List<OrderDetailChargeVO> orderDetails = form.getOrderDetails();
        BigDecimal ivyCost = orderDetails.stream().map(OrderDetailChargeVO::getReceivedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, OrderDetailChargeVO> orderDetailMap = orderDetails.stream().collect(
                toMap(v->StringHelper.joinWith(",",v.getType(),v.getBillingItemId()), Function.identity()));
        for (Integer id : form.getQcTreatmentIds()) {
            QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
            if (qcTreatmentRecord.getType() ==  1) {
                List<QcTreatmentItem> qcTreatmentItems = qcTreatmentItemMapper.selectListByQcTreatmentId(id);
                for (QcTreatmentItem adviceItem : qcTreatmentItems) {
                    String key = StringHelper.joinWith(",", adviceItem.getItemType(), adviceItem.getItemId());
                    OrderDetailChargeVO detail = orderDetailMap.get(key);
                    if (StringHelper.isNotNull(detail)) {
                        int quantity = detail.getQuantity();
                        int surplus = quantity - adviceItem.getQuantity();
                        if (surplus > 0) {
                            // 艾维项目仍有剩余
                            detail.setQuantity(surplus);
                            quantity = adviceItem.getQuantity();
                        } else {
                            orderDetailMap.remove(key);
                        }
                        // 全程代收并执行的医嘱项费用
                        BigDecimal itemPrice = BigDecimal.valueOf(quantity).multiply(detail.getPrice());
                        ivyCost = ivyCost.subtract(itemPrice);

                        // 全程医嘱项绑定艾维项目
                        adviceItem.setOrderDetailId(detail.getOrderDetailId());
                        adviceItem.setStatus((byte) 6);
                        adviceItem.setCollectedAmount(itemPrice);
                        adviceItem.setReceivedAmount(detail.getReceivedAmount().subtract(itemPrice));
                        adviceItem.setUptId(userId);
                        adviceItem.setUptTime(now);
                        qcTreatmentItemMapper.updateByPrimaryKeySelective(adviceItem);
                    }
                }
            }

            // 全程就诊记录绑定账单
            qcTreatmentRecord.setOrderRecordId(form.getOrderRecordId());
            qcTreatmentRecord.setUpdId(userId);
            qcTreatmentRecord.setUpdTime(now);
            qcTreatmentRecord.setStatus((byte) 3);
            qcTreatmentRecord.setOrderRecordId(orderRecordId);
            qcTreatmentRecord.setBillId(billId);
            qcTreatmentRecord.setIvyCost(ivyCost);
            updateSelectiveById(qcTreatmentRecord);
        }
    }
}
