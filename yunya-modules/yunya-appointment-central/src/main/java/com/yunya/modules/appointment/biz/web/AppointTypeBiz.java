package com.yunya.modules.appointment.biz.web;

import com.yunya.feign.appointment.domain.form.AppointTypeForm;
import com.yunya.feign.appointment.domain.model.AppointTypeModel;
import com.yunya.feign.appointment.vo.AppointTypeListVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.AppointType;
import com.yunya.modules.appointment.mapper.AppointTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 预约项目类型服务层
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 17:14
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointTypeBiz extends BaseBiz<AppointTypeMapper, AppointType> {

    /**
     * 新增预约项目类型
     * @param model  预约项目参数列表
     * @return
     */
    public Integer insertAppointType(AppointTypeModel model){
        AppointType build = EntityUtils.build(model, AppointType.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        return mapper.insertSelective(build);
    }

    /**
     * 通过预约项目类型名字查询
     * @param name 预约项目名字
     * @return
     */
    public AppointType selectAppointTypeByName(String name){
        return mapper.selectAppointTypeByName(name);
    }

    /**
     * 根据id删除预约项目
     * @param id  预约项目id
     * @return
     */
    public Integer delAppointType(Integer id){
        return mapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新数据
     * @param form 数据表
     * @return
     */
    public Integer updateAppointType(AppointTypeForm form){
        AppointType build = EntityUtils.build(form, AppointType.class);
        build.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setUpdName(BaseContextHandler.getName());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        return mapper.updateByPrimaryKeySelective(build);
    }

    /**
     * 根据id查询预约项目种类
     * @param id
     * @return
     */
    public AppointType selectAppointTypeById(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

    /**
     *  查询可预约类型列表
     * @return
     */
    public List<AppointTypeListVo> findAppointTypeList(){
        return mapper.findAppointTypeList();
    }

}
