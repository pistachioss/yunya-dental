package com.yunya.modules.appointment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.ClinicAppointItem;
import com.yunya.feign.appointment.domain.form.ClinicAppointItemForm;
import com.yunya.modules.appointment.mapper.ClinicAppointItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 门诊预约项目业务层
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 14:08
 * @update yunya-lihuibin    2020-07-31    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicAppointItemBiz extends BaseBiz<ClinicAppointItemMapper, ClinicAppointItem> {
    /**
     * 根据预约id删除预约项目
     * @param appointItemId 预约项目id
     * @param orgId  门诊id
     * @return
     */
    public Integer delAppointItemById(Integer orgId, Integer appointItemId){
        return mapper.delClinicAppointItemByIdAndAppointItemId(orgId,appointItemId);
    }

    /**
     * 根据门诊id和预约项目id 查询记录
     * @param orgId
     * @param appointItemId
     * @return
     */
    public ClinicAppointItem findByOrgIdAndAppointItemId(Integer orgId, Integer appointItemId){
        return mapper.findClinicAppointItemByOrgIdAndClinicAppointItemId(orgId,appointItemId);
    }

    /**
     * 根据预约项目id 查询记录
     * @param appointItemId
     * @return
     */
    public List<ClinicAppointItem> findByAppointItemId(Integer appointItemId){
        return mapper.findClinicAppointItemByAppointItemId(appointItemId);
    }

    /**
     * 修改门诊端预约项目
     */
    public Integer updateAppItem(Integer id, ClinicAppointItemForm appItemForm) {
        //将Form对象转换成Entity
        ClinicAppointItem appItem = EntityUtils.build(appItemForm, ClinicAppointItem.class);
        appItem.setId(id);
        appItem.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        appItem.setUpdName(BaseContextHandler.getName());
        appItem.setUpdTime(new Date(System.currentTimeMillis()));
        return mapper.updateByPrimaryKeySelective(appItem);
    }

}
