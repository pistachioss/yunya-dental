package com.yunya.modules.appointment.biz;

import com.yunya.feign.appointment.domain.form.DeviceItemManageForm;
import com.yunya.feign.appointment.domain.model.DeviceItemModel;
import com.yunya.feign.appointment.domain.query.AppointmentQuery;
import com.yunya.feign.appointment.domain.query.DeviceItemQuery;
import com.yunya.feign.appointment.vo.AppointmentVo;
import com.yunya.feign.appointment.vo.DeviceItemVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ClinicDeviceItem;
import com.yunya.modules.appointment.mapper.ClinicDeviceItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 预约设备服务
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 20:58
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicDeviceItemBiz extends BaseBiz<ClinicDeviceItemMapper, ClinicDeviceItem> {

    @Autowired
    private AppointmentBiz appointmentBiz;

    /**
     * 根据条件该门诊的设备id列表
     * @param query 条件列表
     * @return
     */
    public List<DeviceItemVo> selectDeviceItemByExample(DeviceItemQuery query) {
        List<DeviceItemVo> devices = mapper.selectDeviceItemByExample(query);
        return devices;
    }

    /**
     * 根据设备id查询设备信息
     * @param id
     * @return
     */
    public DeviceItemVo selectDeviceItemById(Integer id){
        DeviceItemVo deviceItemVo = mapper.selectDeviceItemById(id);
        return deviceItemVo;
    }


    /**
     * 新增门诊端可用设备
     * @param deviceForm 设备信息
     * @return
     */
    public ResponseResult addDevice(DeviceItemModel deviceForm){
        ClinicDeviceItem device = EntityUtils.build(deviceForm,ClinicDeviceItem.class);
        device.setNumber(deviceForm.getNumber());
        List<ClinicDeviceItem> deviceItems = mapper.select(device);
        if (deviceItems != null && !deviceItems.isEmpty()) {
            // 相同的设备已经存在，将设备删除重新添加
            return  ResponseUtil.fail(OperationCodeConstants.SAME_DATA_EXIST,"已经存在相同编号的设备！",null);
        } else {
            device.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            device.setCrtName(BaseContextHandler.getName());
            device.setCrtTime(new Date(System.currentTimeMillis()));
            int insert = mapper.insertSelective(device);
            if (insert <= 0) {
                return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"设备添加失败！",null);
            }
        }
        return ResponseUtil.success();
    }

    /**
     * 新增门诊端可用设备(批量)
     *
     * @param deviceFormArr 设备信息
     * @return
     */
    public ResponseResult addDevices(DeviceItemModel[] deviceFormArr) {
        List errList = new ArrayList<Map<String, Object>>();
        List<DeviceItemModel> deviceForms = new ArrayList<>(Arrays.asList(deviceFormArr));
        if (deviceForms.size() > 0) {
            deviceForms.forEach(deviceForm -> {
                ClinicDeviceItem device = new ClinicDeviceItem();
                device.setOrgId(deviceForm.getOrgId());
                device.setDeviceId(deviceForm.getDeviceId());
                device.setNumber(deviceForm.getNumber());
                ClinicDeviceItem one = mapper.selectOne(device);
                if (one != null) {
                    // 相同的设备已经存在，将设备删除重新添加
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("id", one.getId());
                    resultMap.put("orgId", one.getOrgId());
                    resultMap.put("deviceId", one.getDeviceId());
                    resultMap.put("number", one.getNumber());
                    resultMap.put("errMsg", "设备已存在！请先删除存在的设备再次重新添加");
                    errList.add(resultMap);
                } else {
                    device.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    int insert = mapper.insertSelective(device);
                    if (insert <= 0) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("id", device.getId());
                        resultMap.put("orgId", device.getOrgId());
                        resultMap.put("deviceId", device.getDeviceId());
                        resultMap.put("number", device.getNumber());
                        resultMap.put("errMsg", "设备添加失败！");
                        errList.add(resultMap);
                    }
                }
            });
            // 如果没有错误信息，返回成功
            if (errList.isEmpty()){
                return ResponseUtil.success();
            }
        }
        return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"操作失败！",errList);
    }

    /**
     * 根据设备id删除设备
     * @param id  设备id
     * @return
     */
    public ResponseResult deleteDeviceItemById(Integer id){
        ClinicDeviceItem clinicDeviceItem = mapper.selectByPrimaryKey(id);
        if (clinicDeviceItem == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"删除的设备不存在！",null);
        }

        // 检测设备是否使用中
        AppointmentQuery query = new AppointmentQuery();
        query.setClinicDeviceItemId(id);
        List<AppointmentVo> appointmentByExample = appointmentBiz.findAppointmentByExample(query);
        if (appointmentByExample != null && !appointmentByExample.isEmpty()){
            return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW,"设备使用中，不能删除！",null);
        }

        int result = mapper.deleteByPrimaryKey(id);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"删除设备失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 修改设备项目
     * @param form  修改数据表单
     * @return
     */
    public ResponseResult updateDeviceItem(DeviceItemManageForm form){

        DeviceItemVo deviceItemVo = mapper.selectDeviceItemById(form.getId());
        if (deviceItemVo == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"修改的数据不存在！",null);
        }
        // 将form表单转化为实体
        ClinicDeviceItem build = EntityUtils.build(form, ClinicDeviceItem.class);
        build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setUpdName(BaseContextHandler.getName());
        build.setUpdTime(new Date(System.currentTimeMillis()));

        // 更新数据
        int result = mapper.updateByPrimaryKeySelective(build);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"设备项目修改失败！",null);
        }
        return ResponseUtil.success();
    }

}
