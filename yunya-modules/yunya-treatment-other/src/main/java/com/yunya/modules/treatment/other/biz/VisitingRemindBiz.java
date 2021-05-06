package com.yunya.modules.treatment.other.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment_other.domain.form.VisitingRemindForm;
import com.yunya.feign.treatment_other.domain.model.VisitingRemindModel;
import com.yunya.feign.treatment_other.domain.query.VisitingRemindQuery;
import com.yunya.feign.treatment_other.domain.vo.VisitingRemindContentVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRemindExecuteVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRemindVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment_other.VisitingRemind;
import com.yunya.modules.treatment.other.mapper.VisitingRemindMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: yunya-dental
 * @description: 随访提醒业务层
 * @author: LHB
 * @create: 2020-08-24 19:50
 **/
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class VisitingRemindBiz extends BaseBiz<VisitingRemindMapper, VisitingRemind> {

    /** 注入redis缓冲服务 */
    @Autowired
    private RedisUtils redisUtils;

    /** 注入患者服务 */
    @Autowired
    private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

    /** 注入系统基础服务 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /** 就诊服务 */
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

    /** 消息服务 */
    @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

    /**
     * 新增随访提醒
     * @param model 新增随访表单
     * @return ResponseResult
     */
    public ResponseResult insertVisitingRemind(VisitingRemindModel model){
        Integer patientId = model.getPatientId();
        Date remindDate = model.getRemindDate();
        String remindTime = model.getRemindTime();
        List<VisitingRemind> hasSameDatas = mapper.findVisitingRemindByPatientIdAndDateTime(patientId, remindDate, remindTime);
        if (!StringHelper.isEmpty(hasSameDatas)){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = dateFormat.format(remindDate);
            return ResponseUtil.success(format + " " + remindTime + "时间段内已经存在一条提醒记录！");
        }
        VisitingRemind build = EntityUtils.build(model, VisitingRemind.class);

        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setCrtName(BaseContextHandler.getName());
        build.setCrtTime(new Date(System.currentTimeMillis()));
        build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setUpdName(BaseContextHandler.getName());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.insertSelective(build);
        if (result <= 0) {
            return ResponseUtil.success("数据插入失败！");
        }
        // 发送消息-新建提醒
        remoteRabbitMqServiceFeign.sendMessage(build.getId(),1,0, MsgCategoryEnum.BaseVisitRemind);
        return ResponseUtil.success();
    }

    /**
     * 根据id删除随访提醒
     * @param id 提醒记录id
     * @return ResponseResult
     */
    public ResponseResult deleteVisitingRemindById(Integer id) {

        VisitingRemind visitingRemind = mapper.selectByPrimaryKey(id);
        if (visitingRemind == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        String redisLockStr = redisUtils.get(RedisConstants.LOCK_VISITING_REMIND);
        if (StringHelper.isEmpty(redisLockStr)){
            try{
                redisUtils.setLock(RedisConstants.LOCK_VISITING_REMIND,String.valueOf(id), BusinessConstants.MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
                int result = mapper.deleteByPrimaryKey(id);
                if (result <= 0){
                    return ResponseUtil.success("删除记录失败！");
                } else {
                    // 发送消息-删除提醒
                    remoteRabbitMqServiceFeign.sendMessage(id,1,2, MsgCategoryEnum.BaseVisitRemind);
                    return ResponseUtil.success();
                }
            } finally {
                redisUtils.unlock(RedisConstants.LOCK_VISITING_REMIND,String.valueOf(id));
            }
        }
        return ResponseUtil.success("该条记录正在使用中，不允许删除！");
    }

    /**
     * 修改随访记录
     * @param form 修改随访表单
     * @return ResponseResult
     */
    public ResponseResult updateVisitingRemind(VisitingRemindForm form) {
        Integer id = form.getId();
        VisitingRemind visitingRemind = mapper.selectByPrimaryKey(id);
        if (visitingRemind == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }
        String orgId = BaseContextHandler.getOrgId();
        String updateLock = redisUtils.get(RedisConstants.LOCK_VISITING_REMIND);
        if (StringHelper.isEmpty(updateLock)){
            try{
                redisUtils.setLock(RedisConstants.LOCK_VISITING_REMIND,String.valueOf(id), BusinessConstants.MEDICAL_APPLY_LOCK_SEC,TimeUnit.SECONDS);
                VisitingRemind build = EntityUtils.build(form, VisitingRemind.class);
                build.setOrgId(Integer.valueOf(orgId));
                build.setCrtId(visitingRemind.getCrtId());
                build.setCrtName(visitingRemind.getCrtName());
                build.setCrtTime(visitingRemind.getCrtTime());
                build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
                build.setUpdName(BaseContextHandler.getName());
                build.setUpdTime(new Date(System.currentTimeMillis()));
                build.setInservice(form.getInservice());
                build.setStatus(form.getStatus());
                int result = mapper.updateByPrimaryKey(build);
                if (result <= 0){
                    return ResponseUtil.success("数据修改失败！");
                } else {
                    // 发送消息-修改提醒
                    remoteRabbitMqServiceFeign.sendMessage(id,1,1, MsgCategoryEnum.BaseVisitRemind);
                    return ResponseUtil.success();
                }
            } finally {
                redisUtils.unlock(RedisConstants.LOCK_VISITING_REMIND,String.valueOf(id));
            }
        }
        return ResponseUtil.success("该条数据正在使用中，不允许修改！");
    }

    /**
     * 根据提醒记录id查询提醒内容
     * @param id 提醒记录id
     * @return ResponseResult
     */
    public ResponseResult findVisitingRemindById(Integer id){
        VisitingRemind visitingRemind = mapper.selectByPrimaryKey(id);
        if (visitingRemind == null) {
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"没有数据",null);
        }
        VisitingRemindContentVo build = EntityUtils.build(visitingRemind, VisitingRemindContentVo.class);
        if (id != null){
            PatientTotalInfoVo patientTotalInfo = remotePatientCentralServiceFeign.findPatientTotalInfo(id);
            if (patientTotalInfo != null){
                build.setMobile(patientTotalInfo.getMobile());
            } else {
                build.setMobile("---");
            }
        }
        return ResponseUtil.success(build);
    }

    /**
     * 根据条件查询随访提醒
     * @param query 查询条件
     * @return  ResponseResult
     */
    public ResponseResult findVisitingRemindByCondition(VisitingRemindQuery query){
        query.setInservice(true);
        String medicalNumber = query.getMedicalNumber();
        String distentName = query.getDistentName();
        // 分页
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        // 组合随访提醒信息列表
        List<VisitingRemindVo> visitingRemindVos = new ArrayList<>();
        // 检索随访提醒内容列表
        List<VisitingRemindVo> searchVisitingRemindVo = null;
        String search = query.getSearch();
        if (StringHelper.isNotBlank(search)) {
            if (!search.matches(BusinessConstants.NAME_REGEXP) && !search.matches(BusinessConstants.MOBILE_REGEXP)) {
                return  ResponseUtil.success(new PageInfo(new ArrayList<>()));
            }
            PatientLikeFinleQueryForm patientLikeQuery = new PatientLikeFinleQueryForm();
            patientLikeQuery.setCondition(search);
            patientLikeQuery.setWhetherPage(false);
            // 根据患者姓名/手机号/拼音/病历号/医生名字 检索随访提醒内容
            List<PatientBaseInfoVo> patientByNameAndMobile = remotePatientCentralServiceFeign.findPatientByNameAndMobile(patientLikeQuery);
            if (StringHelper.isNotEmpty(patientByNameAndMobile)) {
                List<Integer> collect = patientByNameAndMobile.stream().map(PatientBaseInfoVo::getId).collect(Collectors.toList());
                query.setPatientIds(collect);
            }
        }

        // 根据医生名字查询患者提醒记录
        if (StringHelper.isNotBlank(distentName)) {
            SysUserEmployeeModel userQuery = new SysUserEmployeeModel();
            userQuery.setWhetherPage(false);
            userQuery.setName(query.getDistentName());
            List<SysUserInfoDetail> sysUserEmployeeInfoList = remoteSystemServiceFeign.findSysUserEmployeeInfoList(userQuery);
            if (StringHelper.isNotEmpty(sysUserEmployeeInfoList)) {
                List<Integer> collect = sysUserEmployeeInfoList.stream().map(SysUserInfoDetail::getUserId).collect(Collectors.toList());
                query.setDentistIds(collect);
            } else {
                return ResponseUtil.success(new PageInfo<>(new ArrayList<>()));
            }
        }
        List<VisitingRemind> visitingReminds = mapper.findVisitingRemindByCondition(query);

        PageInfo visitingRemindVoPageInfo = new PageInfo(visitingReminds);
        // 获取医生ID集合
        if (!StringHelper.isEmpty(visitingReminds)) {
            // 获取医生信息列表
            List<Integer> dentistIds = visitingReminds.stream().map(VisitingRemind::getDentistId).collect(Collectors.toList());
            List<SysUserInfoDetail> dentistInfoList = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserIds(dentistIds);
            // 获取患者信息列表
            List<Integer> patientIds = visitingReminds.stream().map(VisitingRemind::getPatientId).collect(Collectors.toList());
            List<PatientTotalInfoVo> patientTotalInfoVoList = remotePatientCentralServiceFeign.findPatientTotalInfo(patientIds);

            // 获取患者会员类型
            List<MemberType> memberTypeList = null;
            if (StringHelper.isNotEmpty(patientTotalInfoVoList)) {
                List<Integer> memberTypeIds = patientTotalInfoVoList.stream().map(PatientTotalInfoVo::getMemberTypeId).collect(Collectors.toList());
                memberTypeList = remoteSystemServiceFeign.findMemberTypeByIds(memberTypeIds);
            }
            // 患者欠费总额列表
            List<DebtAmountModel> debtAmountModelList = null;
            if(StringHelper.isNotEmpty(patientIds)) {
                // 获取患者欠费总额列表
                debtAmountModelList = this.remoteTreatmentServiceFeign.findDebtAmountList(patientIds);
            }
            List<MemberType> finalMemberTypeList = memberTypeList;
            List<DebtAmountModel> finalDebtAmountModelList = debtAmountModelList;
            visitingReminds.forEach(visitingRemind -> {
                VisitingRemindVo build = EntityUtils.build(visitingRemind, VisitingRemindVo.class);
                // 设置患者信息
                this.setPatientInfo(dentistInfoList,patientTotalInfoVoList,finalMemberTypeList,finalDebtAmountModelList,build);
                visitingRemindVos.add(build);
            });
            // 排序
            searchVisitingRemindVo = this.customSort(visitingRemindVos,query,visitingRemindVoPageInfo);

        }
        // 如果 searchVisitingRemindVo 为空
        if (StringHelper.isEmpty(searchVisitingRemindVo)) {
            searchVisitingRemindVo = new ArrayList<>();
        }
        // 设置分页数据
        visitingRemindVoPageInfo.setList(searchVisitingRemindVo);
        return ResponseUtil.success(visitingRemindVoPageInfo);
    }

    /**
     * 自定义排序
     * @param list
     * @param query
     * @return
     */
    private List<VisitingRemindVo> customSort(List<VisitingRemindVo> list, VisitingRemindQuery query, PageInfo visitingRemindVoPageInfo) {
        String orderBy = query.getOrderBy();
        String sort = query.getSort();
        if (StringHelper.isNotEmpty(list) && StringHelper.isNotBlank(orderBy) && StringHelper.isNotBlank(sort)) {
            List<VisitingRemindVo> result = null;
            if ("dentistName".equalsIgnoreCase(orderBy)) {
                return  this.sortByDentistName(list,sort);
            } else if ("patientName".equalsIgnoreCase(orderBy)) {
                return this.sortByPatientName(list,sort);
            } else if ("mobile".equalsIgnoreCase(orderBy)) {
                return this.sortByMobileName(list,sort);
            }
        } else {
            // 预约档案画面接口为3
            Integer SEARCH_ID = 3;
            String search = query.getSearch();
            String medicalNumber = query.getMedicalNumber();
            String distentName = query.getDistentName();
            if (StringHelper.isEmpty(search) && StringHelper.isEmpty(medicalNumber) && StringHelper.isEmpty(distentName) && query.getSearchId() < 3) {
                // 按照时间正序排序
                return this.sort(list);
            } else if (null != query.getPatientId() && query.getSearchId().equals(SEARCH_ID)){
                return list;
            } else {
                // 设置分页插件总数量=条件检索出来的结果数量
                visitingRemindVoPageInfo.setTotal(list.size());
                // 按照时间正序排序
                return this.sort(list);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 根据医生名字排序
     * @param list
     * @param sort
     * @return
     */
    private List<VisitingRemindVo> sortByDentistName(List<VisitingRemindVo> list, String sort) {
        Comparator<VisitingRemindVo> dentistNamecomparing = Comparator.comparing(VisitingRemindVo::getDentistName, (a, b) -> {
            a = null == a ? "" : a;
            b = null == b ? "" : b;
            if ("asc".equalsIgnoreCase(sort)) {
                return a.compareTo(b);
            } else if ("desc".equalsIgnoreCase(sort)) {
                return b.compareTo(a);
            } else {
                return 0;
            }
        });
        return list.stream().sorted(dentistNamecomparing).collect(Collectors.toList());
    }

    /**
     * 根据患者名字排序
     * @param list
     * @param sort
     * @return
     */
    private List<VisitingRemindVo> sortByPatientName(List<VisitingRemindVo> list, String sort) {
        Comparator<VisitingRemindVo> patientNamecomparing = Comparator.comparing(VisitingRemindVo::getPatientName, (a, b) -> {
            a = null == a ? "" : a;
            b = null == b ? "" : b;
            if ("asc".equalsIgnoreCase(sort)) {
                return a.compareTo(b);
            } else if ("desc".equalsIgnoreCase(sort)) {
                return b.compareTo(a);
            } else {
                return 0;
            }
        });
        return list.stream().sorted(patientNamecomparing).collect(Collectors.toList());
    }

    /**
     * 根据手机号排序
     * @param list
     * @param sort
     * @return
     */
    private List<VisitingRemindVo> sortByMobileName(List<VisitingRemindVo> list, String sort) {
        Comparator<VisitingRemindVo> mobileNamecomparing = Comparator.comparing(VisitingRemindVo::getMobile, (a, b) -> {
            a = null == a ? "" : a;
            b = null == b ? "" : b;
            if ("asc".equalsIgnoreCase(sort)) {
                return a.compareTo(b);
            } else if ("desc".equalsIgnoreCase(sort)) {
                return b.compareTo(a);
            } else {
                return 0;
            }
        });
        return list.stream().sorted(mobileNamecomparing).collect(Collectors.toList());
    }


    /**
     * 设置患者信息
     * @param dentistInfoList 医生信息列表
     * @param patientTotalInfoVoList 患者信息列表
     * @param memberTypeList 会员卡信息
     * @param debtAmountModelList 患者欠费总额信息列表
     * @param build 要注入患者信息的类实例
     */
    private void setPatientInfo(List<SysUserInfoDetail> dentistInfoList,
                                List<PatientTotalInfoVo> patientTotalInfoVoList,
                                List<MemberType> memberTypeList,
                                List<DebtAmountModel> debtAmountModelList,
                                VisitingRemindVo build) {
        // 设置医生信息
        Integer dentistId = build.getDentistId();
        if (null != dentistId && StringHelper.isNotEmpty(dentistInfoList)) {
            boolean b = dentistInfoList.stream().anyMatch(sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(dentistId));
            if (b) {
                SysUserInfoDetail userInfoDetail = dentistInfoList.stream().filter(sysUserInfoDetail -> sysUserInfoDetail.getUserId().equals(dentistId)).findAny().get();
                build.setDentistName(userInfoDetail.getName());
            }
        }
        // 设置患者信息
        Integer patientId = build.getPatientId();
        if (patientId != null && StringHelper.isNotEmpty(patientTotalInfoVoList)){
            boolean b = patientTotalInfoVoList.stream().anyMatch(patientTotalInfoVo -> patientTotalInfoVo.getId().equals(patientId));
            if (b){
                PatientTotalInfoVo patientTotalInfo = patientTotalInfoVoList.stream().filter(patientTotalInfoVo -> patientTotalInfoVo.getId().equals(patientId)).findAny().get();
                build.setPatientName(patientTotalInfo.getName());
                build.setAge(patientTotalInfo.getAge());
                build.setGender(patientTotalInfo.getGender());
                build.setBirthday(patientTotalInfo.getBirthday());
                build.setMedicalNumber(patientTotalInfo.getMedicalNumber());
                build.setMobile(patientTotalInfo.getMobile());
                build.setAllergen(patientTotalInfo.getAllergens());
                build.setPatientRemark(patientTotalInfo.getRemarks());
                build.setPinyinName(patientTotalInfo.getPinyinName());
                build.setPatientKind(patientTotalInfo.getPatientKindName());
                // 设置会员图标
                Integer memberTypeId = patientTotalInfo.getMemberTypeId();
                if (memberTypeId != null && StringHelper.isNotEmpty(memberTypeList)){
                    boolean b1 = memberTypeList.stream().anyMatch(memberType -> memberType.getId().equals(memberTypeId));
                    if (b1){
                        MemberType memberType = memberTypeList.stream().filter(memberType1 -> memberType1.getId().equals(memberTypeId)).findAny().get();
                        build.setMemberIcon(memberType.getIcon());
                    }
                }
                // 欠费总额
                if (StringHelper.isNotEmpty(debtAmountModelList)) {
                    boolean b1 = debtAmountModelList.stream().anyMatch(debtAmountModel -> debtAmountModel.getPatientId().equals(patientId));
                    if (b1) {
                        DebtAmountModel debtAmountModel = debtAmountModelList.stream().filter(entity -> entity.getPatientId().equals(patientId)).findAny().get();
                        build.setArrears(debtAmountModel.getDebtAmount());
                    }
                }
                // 设置初复诊
                Registered registered = new Registered();
                registered.setPatientId(patientId);
                List<Registered> registereds = remoteTreatmentServiceFeign.findRegisteredList(registered);
                if (StringHelper.isNotEmpty(registereds)) {
                    if (registereds.size() > 1) {
                        build.setFirstVisit((byte) 1);
                    }else {
                        build.setFirstVisit((byte) 0);
                    }
                }

            }
        }
    }

    /**
     * 完成提醒
     * @param id 提醒id
     * @return ResponseResult
     */
    public ResponseResult finishVisitingRemind(Integer id) {
        VisitingRemind visitingRemind = mapper.selectByPrimaryKey(id);
        if (visitingRemind == null){
            return ResponseUtil.success("修改的提醒不存在！");
        }
        String remindLockStr = redisUtils.get(RedisConstants.LOCK_VISITING_REMIND);
        if (StringHelper.isEmpty(remindLockStr)){
            try{
                redisUtils.setLock(RedisConstants.LOCK_VISITING_REMIND,String.valueOf(id),BusinessConstants.MEDICAL_APPLY_LOCK_SEC,TimeUnit.SECONDS);
                visitingRemind.setStatus(true);
                visitingRemind.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
                visitingRemind.setUpdName(BaseContextHandler.getName());
                visitingRemind.setUpdTime(new Date(System.currentTimeMillis()));
                int result = mapper.updateByPrimaryKeySelective(visitingRemind);
                if (result <= 0){
                    return ResponseUtil.success("修改提醒状态失败!");
                }
                return ResponseUtil.success();
            } finally {
                redisUtils.unlock(RedisConstants.LOCK_VISITING_REMIND,String.valueOf(id));
            }
        } else {
            return ResponseUtil.success("提醒被占用，不允许修改！");
        }
    }

    /**
     * 根据患者姓名/手机号/拼音/病历号检索随访提醒内容（包括时间正序排序）
     * @param visitingRemindVos 随访提醒列表
     * @param search 患者姓名/手机号/拼音
     * @param medicalNumber 病历号
     * @param distentName 医生名字
     * @return 检索后的列表
     */
    private List<VisitingRemindVo> searchVisitingRemind(List<VisitingRemindVo> visitingRemindVos,
                                                        String search,
                                                        String medicalNumber,
                                                        String distentName) {
        // 按条件检索
        // 匹配患者名字
        String patientNameReg = "^[\\u4e00-\\u9fa5]{0,}$";
        // 匹配手机号
        String mobileReg = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|16[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        // 匹配拼音名字
        String pinyinReg = "^[A-Za-z]+$";
        // 检索随访提醒结果列表
        List<VisitingRemindVo> searchVisitingRemindVo = visitingRemindVos.stream().filter(visitingRemindVo -> {
            boolean result = false;
            if (!StringHelper.isEmpty(search)) {
                String patientName = visitingRemindVo.getPatientName();
                String mobile = visitingRemindVo.getMobile();
                String pinyinName = visitingRemindVo.getPinyinName();
                if (search.matches(patientNameReg) && !StringHelper.isEmpty(patientName)) {
                    // 按名字模糊检索
                    result = result | patientName.contains(search);
                } else if (search.matches(mobileReg) && !StringHelper.isEmpty(mobile)) {
                    // 按手机检索
                    result = result | mobile.contains(search);
                } else if (search.matches(pinyinReg) && !StringHelper.isEmpty(pinyinName)) {
                    // 按拼音检索
                    result = result | pinyinName.contains(search);
                }
            }
            // 按病历号检索
            String currentMedicalNumber = visitingRemindVo.getMedicalNumber();
            if (!StringHelper.isEmpty(currentMedicalNumber) && !StringHelper.isEmpty(medicalNumber)) {
                result = result | currentMedicalNumber.contains(medicalNumber);
            }
            // 按医生名字模糊检索
            String dentistNameStr = visitingRemindVo.getDentistName();
            if (!StringHelper.isEmpty(dentistNameStr) && !StringHelper.isEmpty(distentName)) {
                result = result | dentistNameStr.equals(distentName);
            }
            return result;
        }).collect(Collectors.toList());
        return searchVisitingRemindVo;
    }

    /**
     * 按照时间将检随访提醒列表按时间正序排序
     * @param searchVisitingRemindVos 随访列表
     * @return 返回排序之后的列表
     */
    private List<VisitingRemindVo> sort(List<VisitingRemindVo> searchVisitingRemindVos) {
        return searchVisitingRemindVos.stream()
                .sorted(Comparator.comparing(VisitingRemindVo::getRemindTime,
                (obj1,obj2)-> compareHourMinute(obj1, obj2)))
                .collect(Collectors.toList());
    }

    private int compareHourMinute(String obj1, String obj2) {
        if (StringHelper.isEmpty(obj1) || StringHelper.isEmpty(obj2)){
            return -1;
        }
        String[] objSplit1 = obj1.trim().split(":");
        Integer objMinute1 = Integer.parseInt(objSplit1[0]) * 60 + Integer.parseInt(objSplit1[1]);
        String[] objSplit2 = obj2.trim().split(":");
        Integer objMinute2 = Integer.parseInt(objSplit2[0]) * 60 + Integer.parseInt(objSplit2[1]);
        if (objMinute1 < objMinute2){
            return -1;
        } else if (objMinute1 > objMinute2){
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 根据条件导出执行提醒列表
     *
     * @param query 查询条件
     * @return
     */
    public void executeRemindExport(HttpServletResponse response, VisitingRemindQuery query) throws IOException {
        List<VisitingRemindExecuteVo> data = executeRemindList(query).getList();
        ExcelUtil<VisitingRemindExecuteVo> excelUtil = new ExcelUtil<>(VisitingRemindExecuteVo.class);
        String date = DateUtil.format(query.getRemindDate(), "yyyy-MM-dd");
        OrganizationInfo orgInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(query.getOrgId());
        String abbreviation = "";
        if (orgInfo != null) {
             abbreviation = orgInfo.getAbbreviation();
        }
        String fileName = excelUtil.getFileName(date,"",abbreviation,"患者提醒事项报表");
        excelUtil.exportExcel(response,data,"患者提醒事项报表",fileName);
    }

    /**
     * 根据条件查询执行提醒列表
     *
     * @param query 查询条件
     * @return
     */
    private PageInfo<VisitingRemindExecuteVo> executeRemindList(VisitingRemindQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        String search = query.getSearch();
        if (StringHelper.isNotEmpty(search)) {
            PatientLikeFinleQueryForm patientLikeQuery = new PatientLikeFinleQueryForm();
            patientLikeQuery.setCondition(search);
            patientLikeQuery.setWhetherPage(false);
            // 根据患者姓名/手机号/拼音/病历号/医生名字 检索随访提醒内容
            List<PatientBaseInfoVo> patients = remotePatientCentralServiceFeign.findPatientByNameAndMobile(patientLikeQuery);
            if (StringHelper.isNotEmpty(patients)) {
                query.setPatientIds(patients.stream().map(PatientBaseInfoVo::getId).collect(Collectors.toList()));
            }
        }
        query.setInservice(true);
        List<VisitingRemindExecuteVo> result = new ArrayList<>();
        List<VisitingRemind> reminds = mapper.findVisitingRemindByCondition(query);
        PageInfo pageInfo = new PageInfo<>(reminds);
        if (StringHelper.isNotEmpty(reminds)) {
            reminds = reminds.stream().sorted((remind1, remind2)
                    ->compareHourMinute(remind1.getRemindTime(), remind2.getRemindTime())).collect(Collectors.toList());
            reminds.forEach(vo->{
                VisitingRemindExecuteVo executeVo = new VisitingRemindExecuteVo();
                Integer patientId = vo.getPatientId();
                if (patientId != null) {
                    PatientBaseInfo patient = remotePatientCentralServiceFeign.findPatientInfoById(patientId);
                    if (patient != null) {
                        executeVo.setPatientName(patient.getName());
                    }
                }
                Integer dentistId = vo.getDentistId();
                if (dentistId != null) {
                    SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(dentistId);
                    if (employee != null) {
                        executeVo.setDentistName(employee.getName());
                    }
                }
                executeVo.setRemindDate(vo.getRemindDate());
                executeVo.setRemindContent(vo.getRemindContent());
                result.add(executeVo);
            });
        }
        pageInfo.setList(result);
        return pageInfo;
    }
}
