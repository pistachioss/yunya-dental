package com.yunya.modules.appointment.biz;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.form.DeviceEditForm;
import com.yunya.feign.appointment.domain.form.DeviceTypeForm;
import com.yunya.feign.appointment.domain.model.DeviceTypeModel;
import com.yunya.feign.appointment.domain.query.DeviceItemQuery;
import com.yunya.feign.appointment.domain.query.DeviceTypeQuery;
import com.yunya.feign.appointment.vo.DeviceItemVo;
import com.yunya.feign.appointment.vo.DeviceTypeVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ClinicDeviceItem;
import com.yunya.models.appointment.ClinicDeviceType;
import com.yunya.modules.appointment.mapper.ClinicDeviceTypeMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 设备类型服务层
 * @author yunya-lihuibin
 * @create 2020-08-03 0:04
 * @update yunya-lihuibin    2020-08-03
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicDeviceTypeBiz extends BaseBiz<ClinicDeviceTypeMapper, ClinicDeviceType> {

    /** 系统服务Feign */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Autowired
    private ClinicDeviceItemBiz clinicDeviceItemBiz;

    /**
     * 新增设备
     * @param typeModel 设备数据表单
     * @return
     */
    public ResponseResult addDeviceType(DeviceTypeModel typeModel) {
        if (typeModel.getOrgId() == null || typeModel.getOrgId() <= 0) {
            return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL, "组织id参数非法！",null);
        }
        // 组织id是否存在
        OrganizationInfo orgInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(typeModel.getOrgId());
        if (orgInfo == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"组织不存在，先添加组织！",null);
        }
        // 检查添加设备是否已经存在
        DeviceTypeQuery query= new DeviceTypeQuery();
        query.setOrgId(typeModel.getOrgId());
        query.setName(typeModel.getName());
        List<DeviceTypeVo> deviceTypeVos = mapper.selectDeviceTypeByExample(query);
        if (deviceTypeVos != null && !deviceTypeVos.isEmpty()){
            return ResponseUtil.fail(OperationCodeConstants.SAME_DATA_EXIST,"设备已经存在！",null);
        }
        // 添加设备信息
        ClinicDeviceType build = EntityUtils.build(typeModel, ClinicDeviceType.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        int result = mapper.insertSelective(build);
        if (result <= 0 ){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"设备添加失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据设备id删除设备类型
     * @param id  设备类型id
     * @return
     */
    public ResponseResult delDeviceTypeById(Integer id){
        // 检测删除的设备是否存在
        ClinicDeviceType clinicDeviceType = mapper.selectByPrimaryKey(id);
        if (clinicDeviceType == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"要删除的设备不存在！",null);
        }
        // 检测删除的设备有没有被使用中
        DeviceItemQuery query = new DeviceItemQuery();
        query.setDeviceId(id);
        List<DeviceItemVo> deviceItemVos = clinicDeviceItemBiz.selectDeviceItemByExample(query);
        if (deviceItemVos != null && !deviceItemVos.isEmpty()){
            return ResponseUtil.fail(OperationCodeConstants.DELETE_NOT_ALLOW,"设备使用中，不能删除！",null);
        }
        // 删除设备
        int result = mapper.deleteByPrimaryKey(id);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"删除失败！",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 修改设备类型信息
     * @param form   修改数据表单
     * @return
     */
    public ResponseResult updateDeviceType(DeviceTypeForm form) {
        // 修改的数据是否存在
        ClinicDeviceType clinicDeviceType = mapper.selectByPrimaryKey(form.getId());
        if (clinicDeviceType == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"要修改的数据不存在！",null);
        }
        // 更新数据
        ClinicDeviceType build = EntityUtils.build(form, ClinicDeviceType.class);
        build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setUpdName(BaseContextHandler.getName());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.updateByPrimaryKeySelective(build);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"修改设备失败!",null);
        }
        return ResponseUtil.success();
    }

    /**
     * 根据设备类型id查询设备类型
     * @param id  设备类型id
     * @return
     */
    public DeviceTypeVo findDeviceTypeById(Integer id){
        ClinicDeviceType clinicDeviceType = mapper.selectByPrimaryKey(id);
        if (clinicDeviceType == null){
            return null;
        }
        // 将查询结果注入视图模型中
        DeviceTypeVo build = EntityUtils.build(clinicDeviceType, DeviceTypeVo.class);
        return build;
    }

    /**
     * 根据条件查询设备类型
     * @param query  查询条件
     * @return
     */
    public PageInfo findDeviceTypeList(DeviceTypeQuery query){
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageSize());
        }
        List<DeviceTypeVo> deviceTypeVos = mapper.selectDeviceTypeByExample(query);
        return new PageInfo<>(deviceTypeVos);
    }

    /**
     * 设备名称修改
     * @param form  参数列表
     * @return
     */
    public ResponseResult editDeviceName(DeviceEditForm form){

        ClinicDeviceType deviceType = mapper.selectByPrimaryKey(form.getDeviceId());
        if (deviceType == null){
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"数据不存在！",null);
        }
        deviceType.setName(form.getName());
        deviceType.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        deviceType.setUpdName(BaseContextHandler.getName());
        deviceType.setUpdTime(new Date(System.currentTimeMillis()));
        int result = mapper.insertSelective(deviceType);
        if (result <= 0){
            return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL,"修改失败！",null);
        }
        return ResponseUtil.success();
    }

}
