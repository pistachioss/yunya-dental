package com.yunya.modules.system.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.HttpIpUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.EquipmentInfo;
import com.yunya.modules.system.domain.model.EquipmentInfoModel;
import com.yunya.modules.system.domain.query.EquipmentInfoQueryForm;
import com.yunya.modules.system.mapper.EquipmentInfoMapper;
import com.yunya.modules.system.vo.EquipmentInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 设备信息业务成
 *
 * @author: WY
 * @date 2020/9/2 10:12
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EquipmentBiz extends BaseBiz<EquipmentInfoMapper, EquipmentInfo> {

    /** 注入对象 */
    @Autowired private EquipmentInfoMapper equipmentInfoMapper;

    @Autowired RedisUtils redisUtils;

    @Autowired
    RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

    /**
     * 添加设备信息
     * @param model
     */
    public ResponseResult add(EquipmentInfoModel model, HttpServletRequest request) {
        EquipmentInfo equipmentInfo = new EquipmentInfo();
        BeanUtils.copyProperties(model,equipmentInfo);
        if(model.getId() == null){
            EquipmentInfoVO equipment = mapper.selectOneBySN(equipmentInfo);
            if(equipment != null){
                return ResponseUtil.error("该设备已经存在,不可重复！",equipment);
            }
            equipmentInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            equipmentInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            equipmentInfo.setCrtName(BaseContextHandler.getName());
            //mapper.insertSelective(equipmentInfo);
            //redisUtils.set("PASS",model.getPass());
            //redisUtils.set("URL","http://" + model.getIp() + ":" + "8090");
        }else {
            EquipmentInfoVO equipment = mapper.selectOneBySNAndId(equipmentInfo);
            if(equipment != null){
                return ResponseUtil.error("该设备已经存在,不可重复！",equipment);
            }
            if(!model.getPass().equals(redisUtils.get("PASS"))){
                redisUtils.set("PASS",model.getPass());
            }
            //redisUtils.set("URL","http://" + model.getIp() + ":" + "8090");
            EquipmentInfo equipmentvo = mapper.selectByPrimaryKey(model.getId());
            equipmentInfo.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
            equipmentInfo.setUpdName(BaseContextHandler.getName());
            equipmentInfo.setUpdTime(new Date());
            mapper.updateByPrimaryKeySelective(equipmentInfo);
            if(!equipmentvo.getPass().equals(equipmentInfo.getPass())){ //如果数据库中的密码和修改信息里的密码不相等,就调用设备接口修改设备密码
                UpdPassForm updPassForm = new UpdPassForm();
                updPassForm.setOldPass(equipmentvo.getPass());//旧密码
                updPassForm.setNewPass(equipmentInfo.getPass());//新密码
                //patientCentralServiceFeign.updPass(updPassForm);
            }
        }
        // 心跳回调设置
        setCallback(request,model);
        // 获取任务回调设置
        setTaskCallback(request,model);
        // 结果回调设置
        setResultCallback(request,model);
        // 拍照回调设置
        setPhotographCallback(request,model);
        // 识别回调设置
        setFaceRecognitionCallback(request,model);
        return ResponseUtil.success();
    }

    /**
     *  创建硬件任务 心跳回调设置
     * @param request 请求
     * @param model 设备model
     */
    private void setCallback(HttpServletRequest request,EquipmentInfoModel model){
        String portNumber = remotePatientCentralServiceFeign.portNumberGet();
        if (StringHelper.isNotNull(portNumber)){
            String ip = HttpIpUtils.getClientIpAddr(request);
            String callbackIpAdd = "http://"+ip+":"+portNumber+"/api/patient/callback/heartbeatCallback";
            JSONObject object = new JSONObject();
            object.put("taskNo", "setDeviceHeartBeat");
            object.put("interfaceName", "setDeviceHeartBeat");
            object.put("result", true);
            object.put("pass", model.getPass());
            object.put("url",callbackIpAdd);
            object.put("interval",5);
            if ( StringHelper.isNotNull(model.getSerialNumber()) ){
                redisUtils.set(model.getSerialNumber(), object);
            }
        System.out.println("-----------心跳回调设置-------------");
        }
    }

    /**
     * 创建硬件任务 获取任务回调设置
     * @param request 请求
     * @param model 设备model
     */
    private void setTaskCallback(HttpServletRequest request,EquipmentInfoModel model){
        String portNumber = remotePatientCentralServiceFeign.portNumberGet();
        if (StringHelper.isNotNull(portNumber)){
            String ip = HttpIpUtils.getClientIpAddr(request);
            String callbackIpAdd = "http://"+ip+":"+portNumber+"/api/patient/callback/getTask";
            JSONObject object = new JSONObject();
            object.put("taskNo", "setTaskInterfaceAddress");
            object.put("interfaceName", "setTaskInterfaceAddress");
            object.put("result", true);
            object.put("pass", model.getPass());
            object.put("url",callbackIpAdd);
            if ( StringHelper.isNotNull(model.getSerialNumber()) ){
                redisUtils.set(model.getSerialNumber(), object);
            }
            System.out.println("-----------获取任务回调设置-------------");
        }
    }


    /**
     * 创建硬件任务 结果回调设置
     * @param request 请求
     * @param model 设备model
     */
    private void setResultCallback(HttpServletRequest request,EquipmentInfoModel model){
        String portNumber = remotePatientCentralServiceFeign.portNumberGet();
        if (StringHelper.isNotNull(portNumber)){
            String ip = HttpIpUtils.getClientIpAddr(request);
            String callbackIpAdd = "http://"+ip+":"+portNumber+"/api/patient/callback/taskProcessingResultsAddress";
            JSONObject object = new JSONObject();
            object.put("taskNo", "setTaskProcessingResultsAddress");
            object.put("interfaceName", "setTaskProcessingResultsAddress");
            object.put("result", true);
            object.put("pass", model.getPass());
            object.put("url",callbackIpAdd);
            if ( StringHelper.isNotNull(model.getSerialNumber()) ){
                redisUtils.set(model.getSerialNumber(), object);
            }
            System.out.println("-----------结果回调设置-------------");
        }
    }

    /**
     * 创建硬件任务 拍照回调设置
     * @param request 请求
     * @param model 设备model
     */
    private void setPhotographCallback(HttpServletRequest request,EquipmentInfoModel model){
        String portNumber = remotePatientCentralServiceFeign.portNumberGet();
        if (StringHelper.isNotNull(portNumber)){
            String ip = HttpIpUtils.getClientIpAddr(request);
            String callbackIpAdd = "http://"+ip+":"+portNumber+"/api/patient/callback/takePictures";
            JSONObject object = new JSONObject();
            object.put("taskNo", "setImgRegCallBack");
            object.put("interfaceName", "setImgRegCallBack");
            object.put("result", true);
            object.put("pass", model.getPass());
            object.put("url",callbackIpAdd);
            object.put("base64Enable",2);
            if ( StringHelper.isNotNull(model.getSerialNumber()) ){
                redisUtils.set(model.getSerialNumber(), object);
            }
            System.out.println("-----------拍照回调设置-------------");
        }
    }

    /**
     * 创建硬件任务 识别回调设置
     * @param request 请求
     * @param model 设备model
     */
    private void setFaceRecognitionCallback(HttpServletRequest request,EquipmentInfoModel model){
        String portNumber = remotePatientCentralServiceFeign.portNumberGet();
        if (StringHelper.isNotNull(portNumber)){
            String ip = HttpIpUtils.getClientIpAddr(request);
            String callbackIpAdd = "http://"+ip+":"+portNumber+"/api/patient/callback/faceRecognition";
            JSONObject object = new JSONObject();
            object.put("taskNo", "setIdentifyCallBack");
            object.put("interfaceName", "setIdentifyCallBack");
            object.put("result", true);
            object.put("pass", model.getPass());
            object.put("callbackUrl",callbackIpAdd);
            object.put("base64Enable",2);
            if ( StringHelper.isNotNull(model.getSerialNumber()) ){
                redisUtils.set(model.getSerialNumber(), object);
            }
            System.out.println("-----------识别回调设置-------------");
        }
    }

    /**
     * 设备信息列表
     * @param queryForm
     * @return PageInfo<DictionaryTypeVO>
     */
    public PageInfo<EquipmentInfoVO> findList(EquipmentInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }

        return new PageInfo<>(mapper.findListByOrgId(Integer.parseInt(BaseContextHandler.getOrgId())));
    }

    /**
     * 设备信息列表
     * @return PageInfo<DictionaryTypeVO>
     */
    public EquipmentInfo findEquipmentInfoVO() {
        List<EquipmentInfo> equipmentInfoVOS = mapper.selectAll();
        if (StringHelper.isNotEmpty(equipmentInfoVOS)){
            return equipmentInfoVOS.get(0);
        }
        return null;
    }
}
