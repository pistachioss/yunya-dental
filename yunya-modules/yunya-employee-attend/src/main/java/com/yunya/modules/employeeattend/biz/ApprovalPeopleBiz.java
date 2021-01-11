package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.ApprovalPeople;
import com.yunya.modules.employeeattend.form.ApprovalPeopleDeleteForm;
import com.yunya.modules.employeeattend.form.ApprovalPeopleForm;
import com.yunya.modules.employeeattend.form.ApprovalPeopleQuery;
import com.yunya.modules.employeeattend.mapper.ApprovalPeopleMapper;
import com.yunya.modules.employeeattend.vo.ApprovalPeopleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.SAME_DATA_EXIST;

/**
 * 简介:审批人员设置
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
@Transactional(rollbackFor = Exception.class)
public class ApprovalPeopleBiz extends BaseBiz<ApprovalPeopleMapper, ApprovalPeople> {

    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    public PageInfo<ApprovalPeopleVO> findlist(ApprovalPeopleQuery approvalPeopleQuery) {
        ApprovalPeople approvalPeople = new ApprovalPeople();
        BeanUtils.copyProperties(approvalPeopleQuery, approvalPeople);
        int total = mapper.selectCount(approvalPeople);
        if (approvalPeopleQuery.getWhetherPage()) {
            PageHelper.startPage(approvalPeopleQuery.getPage(), approvalPeopleQuery.getSize());
        }
         approvalPeople = new ApprovalPeople();
        BeanUtils.copyProperties(approvalPeopleQuery, approvalPeople);
        List<ApprovalPeople> reList = mapper.select(approvalPeople);
        //获取员工信息
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(false);
        Byte[] userStatus = {0, 1,3};
        //离职状态
        model.setWorkStatus(userStatus);
        //当前门诊下全部员工
        List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        Map<String, SysUserInfoDetail> employeeMap = new HashMap(16);
        employees.forEach(z -> employeeMap.put(z.getUserId() + "", z));
        List<ApprovalPeopleVO> list = new ArrayList<>();
        for (ApprovalPeople ap : reList) {
            ApprovalPeopleVO approvalPeopleVO = new ApprovalPeopleVO();
            approvalPeopleVO.setId(ap.getId());
            approvalPeopleVO.setUserId(ap.getUserId());
            approvalPeopleVO.setName(employeeMap.get(ap.getUserId().toString()).getName());
            approvalPeopleVO.setIphone(employeeMap.get(ap.getUserId().toString()).getMobilePhone());
            approvalPeopleVO.setPosts(employeeMap.get(ap.getUserId().toString()).getPosts());
            list.add(approvalPeopleVO);
        }
        PageInfo pageInfo =  new PageInfo<>(list);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    public int create(ApprovalPeopleForm approvalPeopleForm) {
        int num = mapper.findCount(approvalPeopleForm);
        if(num>0){
            throw new ClientServiceException("用户已经添加在此优先级下", SAME_DATA_EXIST);
        }
        List<ApprovalPeople>list = new ArrayList<>();
        for(Integer userId:approvalPeopleForm.getUserId()){
            ApprovalPeople approvalPeople = new ApprovalPeople();
            approvalPeople.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            approvalPeople.setApprovalLevelId(approvalPeopleForm.getApprovalLevelId());
            approvalPeople.setUserId(userId);
            approvalPeople.setCrtTime(new Date());
            list.add(approvalPeople);
        }
        int re = mapper.batchInsert(list);
        return re;
    }

    public int delete(ApprovalPeopleDeleteForm approvalPeopleDeleteForm) {
        ApprovalPeople approvalPeople = new ApprovalPeople();
        BeanUtils.copyProperties(approvalPeopleDeleteForm, approvalPeople);
        int re = mapper.delete(approvalPeople);
        return re;
    }
}
