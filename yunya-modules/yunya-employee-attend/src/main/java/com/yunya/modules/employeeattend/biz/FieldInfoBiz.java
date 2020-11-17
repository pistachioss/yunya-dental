package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.FieldInfoQueryForm;
import com.yunya.feign.employee_attend.vo.FieldInfoListVO;
import com.yunya.feign.employee_attend.vo.FieldInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.employee_attend.FieldInfo;
import com.yunya.modules.employeeattend.form.FieldInfoForm;
import com.yunya.modules.employeeattend.mapper.EmployeeScheduleMapper;
import com.yunya.modules.employeeattend.mapper.FieldInfoMapper;
import com.yunya.modules.employeeattend.vo.EmListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

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
 * import com.github.pagehelper.PageHelper;
 * import com.yunya.feign.employee_attend.form.FieldInfoQueryForm;
 * import com.yunya.feign.employee_attend.vo.FieldInfoVO;
 * import com.yunya.framework.common.biz.BaseBiz;
 * import com.yunya.models.employee_attend.FieldInfo;
 * import com.yunya.modules.employeeattend.mapper.FieldInfoMapper;
 * import org.springframework.stereotype.Service;
 * import org.springframework.transaction.annotation.Transactional;
 * <p>
 * import java.util.Date;
 * import java.util.List;
 * <p>
 * /**
 * 简介：外勤信息业务层
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/10 20:52
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FieldInfoBiz extends BaseBiz<FieldInfoMapper, FieldInfo> {

    @Autowired
    private EmployeeScheduleBiz employeeScheduleBiz;
    @Autowired
    private EmployeeScheduleMapper employeeScheduleMapper;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    public int create(FieldInfoForm fieldInfoForm) {
        //没有其他类型的申请
        if (true) {
            FieldInfo field = new FieldInfo();
            field.setUserId(fieldInfoForm.getUserId());
            List<FieldInfo> fieldInfoList = mapper.findList(field);
            boolean timeConflict = true;
            //判断是否与同类型其他申请时间冲突
            for (FieldInfo fie : fieldInfoList) {
                if (fieldInfoForm.getStartTime().before(fie.getEndTime())
                        && fieldInfoForm.getEndTime().after(fie.getStartTime())) {
                    timeConflict = false;
                }
            }
            //与同类型其他申请时间不冲突
            if (timeConflict) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = simpleDateFormat.format(fieldInfoForm.getStartTime());
                String nowString = simpleDateFormat.format(new Date());
                Date date = null;
                Date now = new Date();
                try {
                    date = simpleDateFormat.parse(dateString);
                    now = simpleDateFormat.parse(nowString);
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                }
                //必须提前一天申请
                if (now.before(date)) {
                    //获取申请外勤当天的排班信息
                    EmployeeSchedule employeeSchedule = new EmployeeSchedule();
                    employeeSchedule.setWorkDate(date);
                    employeeSchedule.setEmployeeId(fieldInfoForm.getUserId());
                    List<EmListVO> emlist = employeeScheduleMapper.findemList(employeeSchedule);
                    //比较外勤开启以及结束时间是否在当天的排班时间内
//                  SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                    //只显示出时分秒
                    DateFormat df = DateFormat.getTimeInstance();
                    Boolean start = false;
                    Boolean end = false;
                    Date startTime = null;
                    Date endTime = null;
                    try {
                        startTime = df.parse(df.format(fieldInfoForm.getStartTime()));
                        endTime = df.parse(df.format(fieldInfoForm.getEndTime()));
                    } catch (ParseException e) {
                        throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                    }
                    for (EmListVO emListVO : emlist) {
                        //判断外勤开始时间是否在班次时间内
                        if (startTime.before(emListVO.getEndTime())
                                && (startTime.after(emListVO.getStartTime()) || startTime.equals(emListVO.getStartTime()))
                        ) {
                            start = true;
                        }
                        //判断外勤结束时间是否在班次时间内
                        if (endTime.after(emListVO.getStartTime())
                                && (endTime.before(emListVO.getEndTime()) || endTime.equals(emListVO.getEndTime()))
                        ) {
                            end = true;
                        }
                    }
                    //若外勤开始时间和结束时间都在班次时间段内才能进行外勤申请
                    if (start && end) {
                        FieldInfo fieldInfo = new FieldInfo();
                        BeanUtils.copyProperties(fieldInfoForm, fieldInfo);
                        fieldInfo.setCrtId(fieldInfoForm.getUserId());
                        fieldInfo.setUpdTime(new Date());
                        return mapper.insert(fieldInfo);
                    }
                    throw new ClientServiceException("外勤申请的开始时间以及结束时间应在当天班次时间段内", INSERT_MODEL);
                }
                throw new ClientServiceException("不可以申请当天及以前的申请事项", INSERT_MODEL);
            }
        }
        throw new ClientServiceException("该申请与其他外勤申请时间冲突", INSERT_MODEL);
    }

    /**
     * 根据日期和用户id列表查询外勤列表
     *
     * @param userIds 用户id
     * @param date    日期
     * @return
     */
    public List<FieldInfoVO> findFieldInfosByUserIdAndDate(List<Integer> userIds, Date date) {
        return mapper.findFieldInfosByUserIdAndDate(userIds, date);
    }

    /**
     * 根据查询条件分页查询外勤列表
     *
     * @param queryForm 查询条件
     * @return
     */
    public List<FieldInfoVO> findFieldInfoList(FieldInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findFieldInfoList(queryForm);
    }

    /**
     * 查询外勤列表
     *
     * @param fieldInfoForm
     * @return
     */
    public List<FieldInfoListVO> findList(FieldInfoForm fieldInfoForm) {
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoForm, fieldInfo);
        List<FieldInfoListVO> list = mapper.findVOList(fieldInfo);
        if (list.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for (FieldInfoListVO fieldInfoListVO : list) {
                fieldInfoListVO.setApprovalPeopleName(emMap.get(fieldInfoListVO.getApprovalPeopleId() + "").getName());
                fieldInfoListVO.setUserName(emMap.get(fieldInfoListVO.getUserId() + "").getName());
            }
        }
        return list;
    }

    /**
     * 审批外勤
     *
     * @param fieldInfoForm
     * @return
     */
    public Integer examine(FieldInfoForm fieldInfoForm) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setId(fieldInfoForm.getId());
        fieldInfo = mapper.selectByPrimaryKey(fieldInfo);
        if(fieldInfo.getApprpvalStatus()==0){
            if(fieldInfo.getApprovalPeopleId().equals(Integer.valueOf(BaseContextHandler.getUserID()))){
                fieldInfo.setApprpvalStatus(fieldInfoForm.getApprpvalStatus());
                return mapper.updateByPrimaryKey(fieldInfo);
            }
            throw new ClientServiceException("当前用户无审批该申请的权限", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
    }

    /**
     * 撤销外勤
     *
     * @param fieldInfoForm
     * @return
     */
    public Integer revoke(FieldInfoForm fieldInfoForm) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setId(fieldInfoForm.getId());
        fieldInfo = mapper.selectByPrimaryKey(fieldInfo);
        if(fieldInfo.getApprpvalStatus()==0){
            if(fieldInfo.getUserId().equals(Integer.valueOf(BaseContextHandler.getUserID()))){
                fieldInfo.setApprpvalStatus(3);
                return mapper.updateByPrimaryKey(fieldInfo);
            }
            throw new ClientServiceException("当前用户无撤销该申请的权限", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
    }
}

