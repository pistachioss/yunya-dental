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
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.AppItem;
import com.yunya.modules.appointment.form.AppItemForm;
import com.yunya.modules.appointment.form.AppointItemTypeQueryForm;
import com.yunya.modules.appointment.mapper.AppItemMapper;
import com.yunya.modules.appointment.mapper.AppointmentMapper;
import com.yunya.modules.appointment.vo.AppointmentItemEnableModelVo;
import com.yunya.modules.appointment.vo.AppointmentItemTypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class AppItemBiz extends BaseBiz<AppItemMapper, AppItem> {

    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 根据门诊id、预约项目分类查询门诊端预约项目列表
     *
     * @param from
     * @return
     */
    public List<AppointmentItemTypeVo> findAppItemList(AppointItemTypeQueryForm from) {
        //获取公司端预约项目列表
        List<AppointmentItemTypeVo> ordersTypeList = appointmentMapper.getByOrderTypeId(from);
        return ordersTypeList;
    }


    /**
     * 修改门诊端预约项目
     */
    public Integer updateAppItem(Integer id, AppItemForm appItemForm) {
        //将Form对象转换成Entity
        AppItem appItem = EntityUtils.build(appItemForm, AppItem.class);
        appItem.setId(id);
        return mapper.updateByPrimaryKeySelective(appItem);
    }

    /**
     * 根据预约项目名称模糊查询预约项目
     *
     * @param form
     * @return
     */
    public List<AppointmentItemTypeVo> findByAppItemName(AppointItemTypeQueryForm form) {
        //通过feign查询预约信息，查询门诊端预约信息
        AppointItemTypeQueryForm appointOrderTypeQueryForm = new AppointItemTypeQueryForm();
        appointOrderTypeQueryForm.setName(form.getName());
        List<AppointmentItemTypeVo> ordersTypes = appointmentMapper.getByOrderTypeId(appointOrderTypeQueryForm);
        return ordersTypes;
    }

    /**
     * 查询门诊经理端预约列表
     * @param compClinId
     * @return
     */
    public List<AppointmentItemEnableModelVo> findAvailableAppItemList(String compClinId) {
        AppointItemTypeQueryForm appointOrderTypeQueryForm = new AppointItemTypeQueryForm();
        appointOrderTypeQueryForm.setOrgId(Integer.valueOf(compClinId));

        List<AppointmentItemEnableModelVo> ordersModels = appointmentMapper.selectAllAppointItemByOrgId(appointOrderTypeQueryForm);
        if(ordersModels.isEmpty()){
            return null;
        }
        return ordersModels;
    }

}