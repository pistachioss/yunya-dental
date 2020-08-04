/**
 * Copyright (C), 2015-2019, 上海云牙医疗信息科技有限公司
 * FileName: AppItemBiz
 * Author:   yzg
 * Date:     6/6/2019 10:35 AM
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.yunya.modules.appointment.biz;
import com.yunya.feign.appointment.domain.model.AppointItemConfigModel;
import com.yunya.feign.appointment.domain.model.AppointmentItemModel;
import com.yunya.feign.appointment.domain.query.AppointItemQuery;
import com.yunya.feign.appointment.domain.query.AppointItemTypeQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.AppointItem;
import com.yunya.modules.appointment.mapper.AppointItemMapper;
import com.yunya.modules.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.modules.appointment.vo.AppointmentItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈门诊经理端预约项目servicev层〉
 *
 * @author yzg
 * @create 6/6/2019
 * @since 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointItemBiz extends BaseBiz<AppointItemMapper, AppointItem> {


    @Autowired
    private ClinicAppointItemBiz clinicAppointItemBiz;

    /**
     * 添加可预约项目
     * @param appItemForm
     * @return
     */
    public Integer insertAppointItem(AppointmentItemModel appItemForm){

        AppointItem appointItem = new AppointItem();
        AppointItem build = EntityUtils.build(appItemForm, AppointItem.class);
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));

        return mapper.insertSelective(build);
    }

    /**
     * 根据门诊id、预约项目分类查询门诊端预约项目列表
     *
     * @param from
     * @return
     */
    public List<AppointmentItemVo> findAppItemList(AppointItemQuery from) {
        //获取公司端预约项目列表
        List<AppointmentItemVo> ordersTypeList = mapper.getByOrderTypeId(from);
        return ordersTypeList;
    }


    /**
     * 修改门诊端预约项目
     */
    public Integer updateAppItem(Integer id, AppointmentItemModel appItemForm) {
        //将Form对象转换成Entity
        AppointItem appItem = EntityUtils.build(appItemForm, AppointItem.class);
        appItem.setId(id);
        appItem.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appItem.setUpdName(BaseContextHandler.getName());
        appItem.setUpdTime(new Date(System.currentTimeMillis()));
        return mapper.updateByPrimaryKeySelective(appItem);
    }

    /**
     * 根据预约项目名称模糊查询预约项目
     *
     * @param form
     * @return
     */
    public List<AppointmentItemVo> findByAppItemName(AppointItemQuery form) {
        //通过feign查询预约信息，查询门诊端预约信息
        List<AppointmentItemVo> ordersTypes = mapper.getByOrderTypeId(form);
        return ordersTypes;
    }

    /**
     * 查询门诊经理端预约列表
     * @param compClinId
     * @return
     */
    public List<AppointmentItemEnableModelVo> findAvailableAppItemList(String compClinId) {
        AppointItemTypeQuery appointOrderTypeQueryForm = new AppointItemTypeQuery();
        appointOrderTypeQueryForm.setOrgId(Integer.valueOf(compClinId));

        List<AppointmentItemEnableModelVo> ordersModels = mapper.selectAllAppointItemByOrgId(appointOrderTypeQueryForm);
        if(ordersModels.isEmpty()){
            return null;
        }
        return ordersModels;
    }

    /**
     * 根据条件删除预约项目
     * @param id
     * @return
     */
    public Integer delAppointItemById(Integer id){
        return mapper.deleteByPrimaryKey(id);
    }

}