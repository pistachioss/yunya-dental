package com.yunya.modules.employeeattend.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.models.employee_attend.EmployeePush;
import com.yunya.modules.employeeattend.form.EmployeePushForm;
import com.yunya.modules.employeeattend.mapper.EmployeePushMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class EmployeePushBiz extends BaseBiz<EmployeePushMapper, EmployeePush> {

    /**
     * 保证推送设备与员工绑定关系
     * @param employeePush
     * @return
     */
    public int save(EmployeePush employeePush){
        if(mapper.selectByPrimaryKey(employeePush.getEmployeeId()) == null){
            return mapper.insert(employeePush);
        }
        else{
            return mapper.updateByPrimaryKey(employeePush);
        }
    }

    public List<EmployeePush> query(Set<Integer> employee_ids)
    {
        List<Integer> ids = new ArrayList<>();
        if(employee_ids != null){
            employee_ids.forEach(id ->{
                ids.add(id);
            });
        }
        log.info("ids: " + ids.toString());
        return mapper.selectByEmployeeIds(ids);
    }

    /**
     * 根据员工ID，获取推送号，按平台分类返回
     * @param employeePushForm
     * @return
     */
    public List<EmployeePushForm> makeEmployeePushForm(EmployeePushForm employeePushForm) {
        List<EmployeePushForm> employeePushFormList = new ArrayList<>();
        log.info("employeePushForm: " + employeePushForm.toString());
        if(employeePushForm.getEmpId() != null && !employeePushForm.getEmpId().isEmpty()){
            List<EmployeePush> employeePushList = query(employeePushForm.getEmpId());
            if(employeePushList == null || !employeePushList.isEmpty()){
                EmployeePushForm iosList = new EmployeePushForm();
                EmployeePushForm androidList = new EmployeePushForm();
                try{
                    iosList  = BeanCopierUtils.deepClone(employeePushForm);
                    androidList  = BeanCopierUtils.deepClone(employeePushForm);
                }catch (Exception e){
                    log.info(e.getMessage());
                }
                iosList.setPlatform(1);
                androidList.setPlatform(2);
                List<String> iosUL = iosList.getUserList();
                List<String> androidUL = androidList.getUserList();
                employeePushList.forEach(employeePush -> {
                    switch (employeePush.getPlatform()){
                        case 1:
                            iosUL.add(employeePush.getRegistrationId());
                            break;
                        case  2:
                            androidUL.add(employeePush.getRegistrationId());
                            break;
                    }
                });
                employeePushFormList.add(iosList);
                employeePushFormList.add(androidList);
                return employeePushFormList;
            }
        }
        if(employeePushForm.getUserList()!=null && !employeePushForm.getUserList().isEmpty()){
            employeePushFormList.add(employeePushForm);
            return employeePushFormList;
        }
        List<String> userList = new ArrayList<String>();
        userList.add("160a3797c8cce98edef");
        employeePushForm.setPlatform(2);
        employeePushForm.setUserList(userList);
        if(employeePushForm.getIsSchedule()){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt = new Date(System.currentTimeMillis() + 60 * 1000);
            if(dt.getHours()<8){
                dt.setHours(8);
            }
            employeePushForm.setScheTime(simpleDateFormat.format(dt));
        }
        employeePushFormList.add(employeePushForm);
        return employeePushFormList;
    }
}
