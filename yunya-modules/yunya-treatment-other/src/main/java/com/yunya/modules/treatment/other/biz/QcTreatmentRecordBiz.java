package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.form.QcTreatmentImportForm;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import com.yunya.feign.treatment.domain.vo.TreatOrderRecordVO;
import com.yunya.feign.treatment_other.domain.common.QcPatientInfo;
import com.yunya.feign.treatment_other.domain.common.QcTreatmentInfo;
import com.yunya.feign.treatment_other.domain.form.QcAdviceItemStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadItemForm;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.query.QcRecommondInfoQuery;
import com.yunya.feign.treatment_other.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment_other.QcCustomerInfo;
import com.yunya.models.treatment_other.QcTreatmentItem;
import com.yunya.models.treatment_other.QcTreatmentRecord;
import com.yunya.modules.treatment.other.mapper.QcCustomerInfoMapper;
import com.yunya.modules.treatment.other.mapper.QcTreatmentItemMapper;
import com.yunya.modules.treatment.other.mapper.QcTreatmentRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseQcylTreatment;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static java.util.stream.Collectors.toMap;

/**
 * @author: chenlin
 * @date: 2023/9/11 15:42
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class QcTreatmentRecordBiz extends BaseBiz<QcTreatmentRecordMapper, QcTreatmentRecord> {
    private static final String QC_PATIENT_ADMNO_LIST = "qc:patient:admno:list";
    @Autowired
    private QcTreatmentItemMapper qcTreatmentItemMapper;
    @Autowired
    private QcWebServiceClientBiz qcWebServiceClientBiz;
    @Autowired
    private RemotePatientCentralServiceFeign patientFeign;
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private QcCustomerInfoMapper qcCustomerInfoMapper;
    @Autowired
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Autowired
    private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

    private static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 条件查询mall平台推荐单
     *
     * @param query
     * @return
     */
    public PageInfo<QcRecommondInfoVO> findMallRecommondList(QcRecommondInfoQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }

        syncPatientTreatmentList(query);
        List<QcRecommondInfoVO> result = mapper.selectMallRecommondList(query);
        result.forEach(vo->{
            Integer patientId = vo.getPatientId();
            if (StringHelper.isNotNull(patientId)) {
                PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
                if (StringHelper.isNotNull(patient)) {
                    vo.setPatientName(patient.getName());
                }
            }
            Integer orderRecordId = vo.getOrderRecordId();
            if (StringHelper.isNotNull(orderRecordId)) {
                BillRecord billRecord = treatmentServiceFeign.findBillRecordByOrderRecordId(orderRecordId);
                if (StringHelper.isNotNull(billRecord)) {
                    vo.setBillNum(billRecord.getBillNumber());
                }
            }

        });
        return new PageInfo<>(result);
    }

    /**
     * 同步下载患者就诊列表
     *
     * @param query
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncPatientTreatmentList(QcRecommondInfoQuery query) {
//        List<String> admNos = cacheAdmNos(query, null);
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();

        boolean hadAdd = false;
        QcDoctorAdviceQuery queryForm = new QcDoctorAdviceQuery();
        queryForm.setStart_date(query.getStartDate());
        queryForm.setEnd_date(query.getEndDate());
        queryForm.setWerif_code(query.getVerifyCode());
        List<QcPatientInfoVO> patientTreatments = qcWebServiceClientBiz.findDoctorAdviceRecommondList(queryForm);
        if (StringHelper.isNotEmpty(patientTreatments)) {
            for (QcPatientInfoVO patient : patientTreatments) {
                String idCard = patient.getPAPMI_DVAnumber();
                QcCustomerInfo customer = new QcCustomerInfo();
                customer.setCustomerName(patient.getPAPMI_Name());
                customer.setIdCard(idCard);
                customer.setIdCardType(Integer.parseInt(patient.getPAPMI_CardType_DR()));
                customer.setIdCardTypeDesc(patient.getDVACardType_Desc());
                customer.setMobile(patient.getPAPER_TelH());
                customer.setSex(Byte.parseByte(patient.getPAPMI_Sex_DR()));
                customer.setBirthday(patient.getDOB_Html());
                List<QcTreatmentInfoVO> admInfos = patient.getAdm_info();
                for (QcTreatmentInfoVO adm : admInfos) {
                    String admNo = adm.getAdm_no();
//                    if (admNos.contains(admNo)) {
//                        continue;
//                    }
                    QcTreatmentRecord treatment = new QcTreatmentRecord();
                    treatment.setType(adm.getModeType());
                    treatment.setAdmNo(admNo);
                    String admDate = StringHelper.joinWith(" ", adm.getAdmDate_Html(), adm.getAdmTime_Html());
                    treatment.setAdmDate(DateUtil.parse(admDate));
                    treatment.setUpdId(userId);
                    treatment.setUpdTime(now);
                    String verifCode = query.getVerifyCode();
                    treatment.setVerifyCode(verifCode);
                    mapper.saveByPrimaryKeySelective(treatment);

                    Integer qcTreatmentId = treatment.getId();
                    customer.setQcTreatmentId(qcTreatmentId);
                    qcCustomerInfoMapper.saveByPrimaryKeySelective(customer);

                    List<QcAdviceItemVO> adviceItems = adm.getOrder_infos();
                    if (StringHelper.isNotEmpty(adviceItems)) {
                        adviceItems.forEach(item -> {
                            QcTreatmentItem itEntity = new QcTreatmentItem();
                            itEntity.setOrderNo(item.getMall_order_no());
                            itEntity.setQcTreatmentId(qcTreatmentId);
                            itEntity.setPrice(new BigDecimal(item.getOEORI_UnitCost()));
                            itEntity.setItemNum(item.getItmMast_Code());
                            itEntity.setItemDesc(item.getItmMast_Desc());
                            itEntity.setUnitDesc(item.getPackUOM_Desc());
                            itEntity.setQuantity(item.getOEORI_QtyPackUOM());
                            String openDate = StringHelper.joinWith(" ", item.getDate_Html(), item.getTimeOrd_Html());
                            itEntity.setOpenTime(DateUtil.parse(openDate));
                            itEntity.setRemark(item.getOEORI_DepProcNotes());
                            itEntity.setUpdId(userId);
                            itEntity.setUpdTime(now);
                            qcTreatmentItemMapper.saveByPrimaryKeySelective(itEntity);
                        });
                    }
//                    admNos.add(admNo);
                    hadAdd = true;
                }
            }
//            if (hadAdd) {
//                cacheAdmNos(null, admNos);
//            } else {
//                log.info("本次医嘱查询暂无新数据");
//            }
        }
    }

    /**
     * 获取或存储admNo列表到redis缓存
     *
     * @param query
     * @param datas 为空表示查询数据，否则为存储数据
     * @return
     */
    private List<String> cacheAdmNos(QcRecommondInfoQuery query, List<String> datas) {
        if (StringHelper.isEmpty(datas)) {
            List<String> admNos = redisUtils.getJSONArray(QC_PATIENT_ADMNO_LIST, String.class);
            if (StringHelper.isEmpty(admNos)) {
                List<QcRecommondInfoVO> qcRecommondInfoVOS = mapper.selectMallRecommondList(query);
                admNos = Optional.ofNullable(qcRecommondInfoVOS).orElseGet(ArrayList::new).stream()
                        .map(QcRecommondInfoVO::getAdmNo).collect(Collectors.toList());
            }
            return admNos;
        } else {
            redisUtils.set(QC_PATIENT_ADMNO_LIST, datas);
            return datas;
        }
    }

    /**
     * 医嘱单核销：根据核销码拉取全程就诊记录隐藏数据
     *
     * @param qcTreatmentId
     * @param query
     */
    public void verify(Integer qcTreatmentId, QcRecommondInfoQuery query) {
        QcTreatmentRecord qcTreatment = checkQcTreatmentRecord(qcTreatmentId);
        String verifyCode = query.getVerifyCode();
        if (StringHelper.isEmpty(verifyCode)) {
            throw new ClientServiceException("核销码不能为空", PARAMETERS_IS_ILLEGAL);
        }
        syncPatientTreatmentList(query);
        qcTreatment.setStatus((byte) 2);
        qcTreatment.setUpdTime(BaseContextHandler.getCurTime());
        qcTreatment.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
        updateById(qcTreatment);
    }

    private QcTreatmentRecord checkQcTreatmentRecord(Integer id) {
        QcTreatmentRecord qcTreatmentRecord = selectById(id);
        if (StringHelper.isNull(qcTreatmentRecord)) {
            throw new ClientServiceException("全程医疗就诊记录不存在", DATA_NOT_EXIST);
        }
        return qcTreatmentRecord;
    }

    /**
     * 绑定患者
     *
     * @param id
     * @param patientId
     */
    public void bindPatient(Integer id, Integer patientId) {
        QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
        PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
        if (StringHelper.isNull(patient)) {
            throw new ClientServiceException("患者信息不存在", DATA_NOT_EXIST);
        }
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        qcTreatmentRecord.setPatientId(patientId);
        String remark = "实际使用人:" + patient.getName();
        qcTreatmentRecord.setRemark(remark);
        qcTreatmentRecord.setStatus((byte) 2);
        qcTreatmentRecord.setUpdId(userId);
        qcTreatmentRecord.setUpdTime(now);
        updateSelectiveById(qcTreatmentRecord);
    }

    public void executorAdviceItem(QcTreatmentRecord treatment) {
        List<QcTreatmentItem> items = qcTreatmentItemMapper.selectQcTreatmentItemsByTreatmentId(treatment.getId(), true);
        executorAdviceItem(treatment.getVerifyCode(), treatment.getRemark(), items);
        treatment.setStatus((byte) 3);
        updateById(treatment);
    }

    /**
     * 医嘱执行
     *
     * @param items
     */
    public void executorAdviceItem(String verifyCode, String remark, List<QcTreatmentItem> items) {
        if (StringHelper.isNotEmpty(items)) {
            QcAdviceStatusForm statusForm = new QcAdviceStatusForm();
            List<QcAdviceItemStatusForm> orderInfos = Lists.newArrayList();
            items.forEach(item -> {
                QcAdviceItemStatusForm form = new QcAdviceItemStatusForm();
                form.setMall_order_no(item.getOrderNo());
                Integer orderDetailId = item.getOrderDetailId();
                if (StringHelper.isNotNull(orderDetailId)) {
                    form.setOrg_order_no(String.valueOf(orderDetailId));
                }
                form.setStatus("6");
                form.setVerifCode(verifyCode);
                form.setRemark(remark);
                orderInfos.add(form);
            });
            statusForm.setOrder_infos(orderInfos);
            qcWebServiceClientBiz.updateAdviceItemStatus(statusForm);
        }
    }

    /**
     * 医嘱上传
     *
     * @param qcTreatmentIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void uploadAdviceItems(List<Integer> qcTreatmentIds) {
        List<QcAdviceUploadForm> forms = Lists.newArrayList();
        Map<Integer, QcCustomerInfo> customerMap = Maps.newHashMapWithExpectedSize(16);
        qcTreatmentIds.forEach(id->{
            QcTreatmentRecord qcTreatmentRecord = checkQcTreatmentRecord(id);
            int status = qcTreatmentRecord.getStatus().intValue();
            String admNo = qcTreatmentRecord.getAdmNo();
            if (status != 3) {
                log.error("全程医疗就诊记录：{}, 状态为：{} 不允许同步医嘱上传", id, status);
                String statusMsg = status==1 ? "未核销" : (status==2 ? "未开单" : "已同步");
                throw new ClientServiceException("登记单【" + admNo + "】的状态为:"+ statusMsg +" ，不能同步" , OPERATION_NOT_ALLOW);
            }
            QcAdviceUploadForm form = new QcAdviceUploadForm();
            QcCustomerInfo customer = customerMap.computeIfAbsent(id,
                    key -> qcCustomerInfoMapper.selectByPrimaryKey(key));
            if (StringHelper.isNull(customer)) {
                log.error("全程医疗就诊记录：{} 患者信息不存在，不于同步医嘱上传", id);
                throw new ClientServiceException("登记单【" + admNo + "】的客户信息不存在，不能同步" , OPERATION_NOT_ALLOW);
            }
            form.setPat_info(converPatientInfo(customer));
            form.setAdm_info(convertAdmInfo(qcTreatmentRecord));
            form.setOrder_infos(findAndConvertOrderInfos(id));
            forms.add(form);
        });
        if (StringHelper.isNotEmpty(forms)) {
            forms.forEach(form-> qcWebServiceClientBiz.uploadAdvice2MallPlatform(form));
            Date now = BaseContextHandler.getCurTime();
            Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
            qcTreatmentIds.forEach(id->{
                QcTreatmentRecord entity = new QcTreatmentRecord();
                entity.setId(id);
                entity.setStatus((byte) 4);
                entity.setSyncId(userId);
                entity.setSyncTime(now);
                entity.setUpdId(userId);
                entity.setUpdTime(now);
                updateSelectiveById(entity);
            });
        }
    }

    /**
     * 查找医嘱项明细并转换成医嘱上传的医嘱明细模型
     *
     * @param id
     * @return
     */
    private List<QcAdviceUploadItemForm> findAndConvertOrderInfos(Integer id) {
        List<QcAdviceUploadItemForm> orderInfos = Lists.newArrayList();
        List<OrderAdviceItemVO> orderAdviceItems = qcTreatmentItemMapper.selectOrderAdviceItemsByTreatmentId(id);
        orderAdviceItems.forEach(item->{
            QcAdviceUploadItemForm uploadItem = new QcAdviceUploadItemForm();
            Integer orderDetailId = item.getOrderDetailId();
            if (StringHelper.isNotNull(orderDetailId)) {
                uploadItem.setOrg_order_no(String.valueOf(orderDetailId));
            }
            uploadItem.setItmMast_Code(item.getItemNum());
            uploadItem.setItmMast_Desc(item.getItemName());
            uploadItem.setOEORI_QtyPackUOM(item.getQuantity());
            uploadItem.setPackUOM_Desc(item.getUnitDesc());
            uploadItem.setOEORI_UnitCost(item.getPrice().toString());
            uploadItem.setOEORI_Price(item.getReceivedAmount().toString());
            if (item.getQcCollected()) {
                uploadItem.setMall_order_no(item.getOrderNo());
                uploadItem.setOEORI_Billed("P");
                uploadItem.setOEORI_RecDep_DR("3");
                uploadItem.setRecDep_Desc("艾维口腔门诊");
            }
            Date openTime = item.getOpenTime();
            uploadItem.setDate_Html(DateUtil.format(openTime));
            uploadItem.setTimeOrd_Html(DateUtil.format(openTime, TIME_FORMAT));
            orderInfos.add(uploadItem);
        });
        return orderInfos;
    }

    /**
     * 转换成医嘱上传的患者信息模型
     *
     * @param customer
     * @return
     */
    private QcPatientInfo converPatientInfo(QcCustomerInfo customer) {
        QcPatientInfo qcPatient = new QcPatientInfoVO();
        qcPatient.setPAPMI_DVAnumber(customer.getIdCard());
        qcPatient.setPAPMI_CardType_DR(customer.getIdCardType().toString());
        qcPatient.setDVACardType_Desc(customer.getIdCardTypeDesc());
        qcPatient.setPAPMI_Name(customer.getCustomerName());
        qcPatient.setDOB_Html(customer.getBirthday());
//            1-男，2-女，3-未知性别，4-未说明性别
        qcPatient.setPAPMI_Sex_DR(customer.getSex().toString());
        qcPatient.setPAPER_TelH(customer.getMobile());
        return qcPatient;
    }

    /**
     * 转换医嘱上传的就诊记录模型
     *
     * @param qcTreatmentRecord
     * @return
     */
    private QcTreatmentInfo convertAdmInfo(QcTreatmentRecord qcTreatmentRecord) {
        QcTreatmentInfo admInfo = new QcTreatmentInfo();
        admInfo.setAdm_no(qcTreatmentRecord.getAdmNo());
//        Date admDate = qcTreatmentRecord.getAdmDate();
//        admInfo.setAdmDate_Html(DateUtil.format(admDate));
//        admInfo.setAdmTime_Html(DateUtil.format(admDate, TIME_FORMAT));
        return admInfo;
    }

    /**
     * 全程医疗登记单匹配订单
     *
     * @param form
     * @return
     */
    public TreatOrderRecordVO orderMatchQcTreatmentList(QcTreatmentImportForm form) {
        TreatOrderRecordVO result = new TreatOrderRecordVO();
        List<QcTreatmentVO> qcTreatments = Lists.newArrayList();
        Map<String, OrderDetailChargeVO> orderDetailMap = form.getOrderDetails().stream()
                .collect(toMap(OrderDetailChargeVO::getItemNum, Function.identity()));
        List<QcTreatmentRecord> treatments = mapper.selectQcTreatmentListByIds(form.getQcTreatmentIds());
        int guideTreatmentCount = 0;
        for (QcTreatmentRecord qcTreatment : treatments) {
            Integer id = qcTreatment.getId();
            BigDecimal originAmount = BigDecimal.ZERO;
            List<OrderDetailChargeVO> items = Lists.newArrayList();
            if ("O".equals(qcTreatment.getType())) {
                List<QcTreatmentItem> qcTreatmentItems = qcTreatmentItemMapper.selectAdviceItemsByTreatmentId(id);
                for (QcTreatmentItem adviceItem : qcTreatmentItems) {
                    String itemNum = adviceItem.getItemNum();
                    OrderDetailChargeVO orderDetail = orderDetailMap.get(itemNum);
                    if (StringHelper.isNotNull(orderDetail)) {
                        int quantity = orderDetail.getQuantity();
                        int surplus = quantity - adviceItem.getQuantity();
                        if (surplus > 0) {
                            // 艾维项目仍有剩余
                            orderDetail.setQuantity(surplus);
                            quantity = adviceItem.getQuantity();
                        } else {
                            orderDetailMap.remove(itemNum);
                        }
                        BigDecimal itemPrice = BigDecimal.valueOf(quantity).multiply(adviceItem.getPrice());
                        originAmount = originAmount.add(itemPrice);
                        items.add(adviceItem2OrderDetail(quantity, adviceItem.getPrice(), orderDetail));
                    }
                }
            } else {
                if (++guideTreatmentCount > 1) {
                    throw new ClientServiceException("不能选取多条全程引导单！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
                }
            }
            QcTreatmentVO vo = new QcTreatmentVO();
            vo.setQcTreatmentId(id);
            vo.setAdmNo(qcTreatment.getAdmNo());
            vo.setType(qcTreatment.getType());
            vo.setOriginAmount(originAmount);
            vo.setReceivedAmount(originAmount);
            vo.setQcAdviceItems(items);
            qcTreatments.add(vo);
        }
        result.setQcTreatmentList(qcTreatments);
        result.setItemList(Lists.newArrayList(orderDetailMap.values()));
        return result;
    }

    /**
     * 获取已绑定账单的全程医疗登记单列表
     *
     * @param orderRecordId
     * @return
     */
    public List<QcTreatmentVO> findBindingQcTreatmentList(Integer orderRecordId) {
        List<QcTreatmentVO> qcTreatments = mapper.selectQcTreatmentBindedByOrderId(orderRecordId);
        qcTreatments.forEach(treatment->{
            List<OrderDetailChargeVO> orderDetails = Lists.newArrayList();
            List<QcTreatmentItem> items = qcTreatmentItemMapper.selectAdviceItemsByTreatmentId(treatment.getQcTreatmentId());
            items.forEach(item-> orderDetails.add(adviceItem2OrderDetail(item.getQuantity(), item.getPrice(), item)));
            if (StringHelper.isNotEmpty(orderDetails)) {
                treatment.setQcAdviceItems(orderDetails);
            }
        });
        return qcTreatments;
    }

    /**
     * 全程医嘱项转换为艾维开单项目
     *
     * @param quantity
     * @param price
     * @param source
     * @return
     */
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

    /**
     * 账单收费时绑定or更新实收：
     *  1、账单绑定全程就诊记录，账单明细绑定全程医嘱明细，调用医嘱执行
     *  2、qcTreatmentIds为空时更新项目实收
     *
     * @param form
     */
    public List<OrderDetailChargeVO> updateQcTreatmentAndItems(QcTreatmentImportForm form) {
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = DateUtil.now();
        Integer billPayId = form.getBillPayId();
        Integer orderRecordId = form.getOrderRecordId();
        List<Integer> qcTreatmentIds = form.getQcTreatmentIds();
        Map<String, OrderDetailChargeVO> orderDetailMap = form.getOrderDetails().stream()
                .collect(toMap(OrderDetailChargeVO::getItemNum, Function.identity()));
        List<QcTreatmentRecord> qcTreatments = findQcTreatmentByIdOrOrderId(qcTreatmentIds, orderRecordId);
        qcTreatments.forEach(qcTreatment->{
            Integer id = qcTreatment.getId();
            List<QcTreatmentItem> qcTreatmentItems = qcTreatmentItemMapper.selectQcTreatmentItemsByTreatmentId(id, null);
            for (QcTreatmentItem adviceItem : qcTreatmentItems) {
                String itemNum = adviceItem.getItemNum();
                OrderDetailChargeVO detail = orderDetailMap.get(itemNum);
                if (StringHelper.isNotNull(detail)) {
                    BigDecimal receivedAmount;
                    BigDecimal price = adviceItem.getPrice();
                    int quantity = detail.getQuantity();
                    int surplus = quantity - adviceItem.getQuantity();
                    if (surplus > 0) {
                        // 艾维项目仍有剩余
                        detail.setQuantity(surplus);
                        receivedAmount = BigDecimal.valueOf(adviceItem.getQuantity()).multiply(price);
                        detail.setReceivedAmount(detail.getReceivedAmount().subtract(receivedAmount));
                    } else {
                        orderDetailMap.remove(itemNum);
                        receivedAmount = BigDecimal.valueOf(quantity).multiply(price);
                    }

                    // 全程医嘱项绑定艾维项目
                    adviceItem.setOrderDetailId(detail.getOrderDetailId());
                    adviceItem.setItemNum(detail.getItemNum());
                    adviceItem.setStatus((byte) 6);
                    adviceItem.setRemark(qcTreatment.getRemark());
                    adviceItem.setReceivedAmount(receivedAmount);
                    adviceItem.setUpdId(userId);
                    adviceItem.setUpdTime(now);
                    qcTreatmentItemMapper.updateByPrimaryKeySelective(adviceItem);
                }
            }
            if (StringHelper.isNotEmpty(qcTreatmentIds)) {
                // 账单首次收费时需要调用医嘱执行
                executorAdviceItem(qcTreatment.getVerifyCode(), qcTreatment.getRemark(), qcTreatmentItems);
                rabbitMqServiceFeign.sendMessage(id, 0, BaseQcylTreatment);
            }

            // 全程就诊记录绑定账单
            qcTreatment.setStatus((byte) 3);
            qcTreatment.setOrderRecordId(orderRecordId);
            qcTreatment.setBillPayId(billPayId);
            qcTreatment.setUpdId(userId);
            qcTreatment.setUpdTime(now);
            updateSelectiveById(qcTreatment);
        });

        if (StringHelper.isNotEmpty(orderDetailMap)) {
            // 将剩余开单项目绑定到最后一个全程就诊记录
            QcTreatmentRecord qcTreatmentRecord = qcTreatments.get(qcTreatments.size() - 1);
            orderDetailMap.forEach((itemNum, detail)->{
                QcTreatmentItem adviceItem = orderDetail2AdviceItem(qcTreatmentRecord, detail, now, userId);
                qcTreatmentItemMapper.insertSelective(adviceItem);
            });
        }
        return Lists.newArrayList(orderDetailMap.values());
    }

    /**
     * 根据全程就诊iD或订单id查询全程就诊记录列表
     *
     * @param qcTreatmentIds
     * @param orderRecordId
     * @return
     */
    private List<QcTreatmentRecord> findQcTreatmentByIdOrOrderId(List<Integer> qcTreatmentIds, Integer orderRecordId) {
        if (StringHelper.isNotEmpty(qcTreatmentIds)) {
            return mapper.selectQcTreatmentListByIds(qcTreatmentIds);
        } else {
            Example example = new Example(QcTreatmentRecord.class);
            example.createCriteria().andEqualTo("orderRecordId", orderRecordId)
                    .andEqualTo("inservice", true);
            return mapper.selectByExample(example);
        }
    }

    /**
     * 将剩余未绑定的项目转换成引导单医嘱项
     *
     * @param qcTreatmentRecord 全程医疗就诊记录
     * @param detail
     * @return
     */
    private QcTreatmentItem orderDetail2AdviceItem(QcTreatmentRecord qcTreatmentRecord, OrderDetailChargeVO detail, Date now, Integer userId) {
        Integer quantity = detail.getQuantity();
        QcTreatmentItem adviceItem = new QcTreatmentItem();
        adviceItem.setQcTreatmentId(qcTreatmentRecord.getId());
        adviceItem.setOrderNo(detail.getOrderDetailId().toString());
        adviceItem.setOrderDetailId(detail.getOrderDetailId());
        adviceItem.setItemDesc(detail.getBillingItemName());
        adviceItem.setItemNum(detail.getItemNum());
        adviceItem.setQcCollected(false);
        adviceItem.setStatus((byte) 6);
        adviceItem.setPrice(detail.getPrice());
        adviceItem.setQuantity(quantity);
        adviceItem.setUnitDesc(detail.getUnit());
        adviceItem.setReceivedAmount(detail.getReceivedAmount());
        adviceItem.setExecutorId(detail.getExecutorId());
        adviceItem.setOpenTime(now);
        adviceItem.setRemark(qcTreatmentRecord.getRemark());
        adviceItem.setUpdId(userId);
        adviceItem.setUpdTime(now);
        return adviceItem;
    }

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    public QcRecommondDetailVO findById(Integer id) {
        QcRecommondDetailVO result = new QcRecommondDetailVO();
        QcCustomerTreatmentVO treatment = mapper.selectPatientTreatmentById(id);
        Integer patientId = treatment.getPatientId();
        if (StringHelper.isNotNull(patientId)) {
            PatientBaseInfo patient = patientFeign.findPatientInfoById(patientId);
            if (StringHelper.isNotNull(patient)) {
                treatment.setPatientName(patient.getName());
            }
        }
        Integer orderRecordId = treatment.getOrderRecordId();
        if (StringHelper.isNotNull(orderRecordId)) {
            BillRecord billRecord = treatmentServiceFeign.findBillRecordByOrderRecordId(orderRecordId);
            if (StringHelper.isNotNull(billRecord)) {
                treatment.setBillNum(billRecord.getBillNumber());
            }
        }
        result.setQcCustomerInfoVO(treatment);
        List<OrderAdviceItemVO> adviceItems = qcTreatmentItemMapper.selectOrderAdviceItemsByTreatmentId(id);
        Iterator<OrderAdviceItemVO> it = adviceItems.iterator();
        List<OrderAdviceItemVO> orderDetails = Lists.newArrayList();
        while (it.hasNext()) {
            OrderAdviceItemVO item = it.next();
            Integer executorId = item.getExecutorId();
            if (StringHelper.isNotNull(executorId)) {
                SysEmployee executor = systemServiceFeign.findSysEmployeeById(executorId);
                if (StringHelper.isNotNull(executor)) {
                    item.setExecutorName(executor.getName());
                }
            }
            if (!item.getQcCollected()) {
                orderDetails.add(item);
                it.remove();
            }
        }
        result.setQcAdviceItems(adviceItems);
        result.setOrderDetails(orderDetails);
        return result;
    }
}
