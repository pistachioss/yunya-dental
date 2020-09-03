package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
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

import java.util.Date;

/**
 * 简单介绍:</br>
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
    PatientCentralServiceFeign patientCentralServiceFeign;
    /**
     * 添加设备信息
     * @param model
     */
    public ResponseResult add(EquipmentInfoModel model) {
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
            mapper.insertSelective(equipmentInfo);
            redisUtils.set("PASS",model.getPass());
            redisUtils.set("URL","http://" + model.getIp() + ":" + "8090");
        }else {
            EquipmentInfoVO equipment = mapper.selectOneBySNAndId(equipmentInfo);
            if(equipment != null){
                return ResponseUtil.error("该设备已经存在,不可重复！",equipment);
            }
            EquipmentInfo equipmentvo = mapper.selectByPrimaryKey(model.getId());
            equipmentInfo.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
            equipmentInfo.setUpdName(BaseContextHandler.getName());
            equipmentInfo.setUpdTime(new Date());
            mapper.updateByPrimaryKeySelective(equipmentInfo);
            if(!equipmentvo.getPass().equals(equipmentInfo.getPass())){ //如果数据库中的密码和修改信息里的密码不相等,就调用设备接口修改设备密码
                UpdPassForm updPassForm = new UpdPassForm();
                updPassForm.setOldPass(equipmentvo.getPass());//旧密码
                updPassForm.setNewPass(equipmentInfo.getPass());//新密码
                patientCentralServiceFeign.updPass(updPassForm);
            }
            if(!model.getPass().equals(redisUtils.get("PASS"))){
                redisUtils.set("PASS",model.getPass());
                redisUtils.set("URL","http://" + model.getIp() + ":" + "8090");
            }
        }
        return ResponseUtil.success();
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
}
