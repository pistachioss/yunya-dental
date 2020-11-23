package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.LeaveInfoQueryForm;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.ApprovalInfo;
import com.yunya.models.employee_attend.ApprovalPeople;
import com.yunya.models.employee_attend.CopyInfo;
import com.yunya.models.employee_attend.LeaveInfo;
import com.yunya.modules.employeeattend.form.LeaveInfoFindForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import com.yunya.modules.employeeattend.mapper.ApprovalInfoMapper;
import com.yunya.modules.employeeattend.mapper.CopyInfoMapper;
import com.yunya.modules.employeeattend.mapper.LeaveInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.INSERT_MODEL;

/**
 * 简介：请假信息业务层
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/10 20:50
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveInfoBiz extends BaseBiz<LeaveInfoMapper, LeaveInfo> {

    @Autowired
    private ApprovalInfoMapper approvalInfoMapper;
    @Autowired
    private CopyInfoMapper copyInfoMapper;

    /**
     * 根据日期和用户id列表查询请假列表
     *
     * @param userIds 用户id列表
     * @param date    日期
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfosByUserIdsAndDate(List<Integer> userIds, Date date) {
        return mapper.findLeaveInfosByUserIdsAndDate(userIds, date);
    }

    /**
     * 根据条件分页查询请假列表
     *
     * @param queryForm
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfoList(LeaveInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findLeaveInfoList(queryForm);
    }

    /**
     * 新增按天请假
     *
     * @param leaveInfoForm
     * @return
     */
    public Integer createDay(LeaveInfoForm leaveInfoForm) {
        //判断是否有其他类型的申请
        if (true) {
            //判断是否与同类型其他申请时间冲突
            LeaveInfo find = new LeaveInfo();
            find.setUserId(leaveInfoForm.getUserId());
            List<LeaveInfo>findlist = mapper.select(find);
            Boolean flag = true;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date listartTime = null;
            Date liendTime = null;
            Date startTime = leaveInfoForm.getStartTime();
            Date endTime = leaveInfoForm.getEndTime();
            for(LeaveInfo li:findlist){
                try {
                    listartTime = format.parse(format.format(li.getStartTime()));
                    liendTime = format.parse(format.format(li.getEndTime()));
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                }
                if((startTime.before(liendTime)&&startTime.after(listartTime))||
                        (leaveInfoForm.getEndTime().before(liendTime)&&endTime.after(listartTime))||
                        startTime.equals(listartTime)||endTime.equals(liendTime)||
                        (startTime.before(listartTime)&&endTime.after(liendTime))
                ){
                    flag = false;
                    break;
                }
            }
            if (flag) {
                LeaveInfo leaveInfo = new LeaveInfo();
                BeanUtils.copyProperties(leaveInfoForm, leaveInfo);
                leaveInfo.setCrtTime(new Date());
                int num = mapper.insert(leaveInfo);
                //插入审批人信息
                int leaveId = leaveInfo.getId();
                List<ApprovalInfo>list = leaveInfoForm.getApprpvalPeopleList();
                for (ApprovalInfo approvalInfo : list) {
                    approvalInfo.setCrtId(leaveInfoForm.getCrtId());
                    approvalInfo.setCrtTime(new Date());
                    approvalInfo.setLeaveId(leaveId);
                }
                approvalInfoMapper.batchInsert(list);
                //插入抄送人信息
                if (leaveInfoForm.getCopyList().size() > 0) {
                    List<CopyInfo> copyInfoList = new ArrayList<>();
                    for (Integer copyId : leaveInfoForm.getCopyList()) {
                        CopyInfo copyInfo = new CopyInfo();
                        copyInfo.setApplyId(leaveId);
                        copyInfo.setApplyType(0);
                        copyInfo.setUserId(copyId);
                        copyInfo.setCrtId(leaveInfoForm.getUserId());
                        copyInfo.setCrtTime(new Date());
                        copyInfoList.add(copyInfo);
                    }
                    copyInfoMapper.batchInsert(copyInfoList);
                }
                return num;
            }
            throw new ClientServiceException("该申请与其他请假申请时间冲突", INSERT_MODEL);
        }
        throw new ClientServiceException("每天只能发起一种类型的申请", INSERT_MODEL);
    }
}
