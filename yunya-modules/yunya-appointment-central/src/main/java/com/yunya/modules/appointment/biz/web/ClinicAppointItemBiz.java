package com.yunya.modules.appointment.biz.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.query.AppointItemConfigQuery;
import com.yunya.feign.appointment.vo.ClinicAppointItemConfigVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.appointment.ClinicAppointItem;
import com.yunya.feign.appointment.domain.form.ClinicAppointItemForm;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.ClinicAppointItemMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;


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
     * @param query
     * @return
     */
    public ResponseResult<PageInfo<ClinicAppointItemConfigVo>> findByAppointItemId(AppointItemConfigQuery query){
        if (query.getWhetherPage()){
            PageHelper.startPage(query.getPageNum(),query.getPageNum());
        }
        List<ClinicAppointItemConfigVo> clinicAppointItemConfigVos = new ArrayList<>();
        Integer appointItemId = query.getAppointItemId();
        // 门诊可预约项目列表
        List<ClinicAppointItem> clinicAppointItems = mapper.findClinicAppointItemByAppointItemId(appointItemId);
        clinicAppointItems.forEach(clinicAppointItem -> {
            // 查询公司信息
            OrganizationInfo organizationInfo = systemServiceFeign.findOrgInfoByOrgId(clinicAppointItem.getOrgId());
            if (null != organizationInfo) {
                ClinicAppointItemConfigVo clinicAppointItemConfigVo = new ClinicAppointItemConfigVo();
                clinicAppointItemConfigVo.setName(organizationInfo.getName());
                clinicAppointItemConfigVo.setInservice(clinicAppointItem.getInservice());
                clinicAppointItemConfigVos.add(clinicAppointItemConfigVo);
            }
        });
        return ResponseUtil.success(new PageInfo<>(clinicAppointItemConfigVos));
    }

    /**
     * 修改门诊端预约项目
     */
    public Integer addAndUpdateAppItem(ClinicAppointItemForm appItemForm) {
        //将Form对象转换成Entity
        ClinicAppointItem appItem = EntityUtils.build(appItemForm, ClinicAppointItem.class);
        ClinicAppointItem clinicAppointItem = mapper.findClinicAppointItemByOrgIdAndClinicAppointItemId(appItemForm.getOrgId(),appItemForm.getAppointItemId());
        // 修改
        if (clinicAppointItem != null){
            clinicAppointItem.setInservice(appItem.getInservice());
            clinicAppointItem.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicAppointItem.setUpdName(BaseContextHandler.getName());
            clinicAppointItem.setUpdTime(new Date(System.currentTimeMillis()));
            return mapper.updateByPrimaryKeySelective(clinicAppointItem);
        }

        //新增
        appItem.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        return mapper.insertSelective(appItem);
    }

    /**
     * 可预约项目统一设置配置（公司端）
     * @Param types 公司属性0:公司,1:区域管理,2:医疗机构,3:其他
     * @return
     */
    public Integer updateAppointItemWithBatch(Integer appointId){
        OrganizationModel model = new OrganizationModel();
        model.setTypes(new Byte[]{2});
        model.setWhetherPage(false);
        List<OrganizationInfoDetail> orgInfoList = systemServiceFeign.findOrgInfoList(model);
        if (orgInfoList != null && !orgInfoList.isEmpty()){
            orgInfoList.forEach(organizationInfoDetail -> {
                ClinicAppointItem clinicAppointItem = mapper.findClinicAppointItemByOrgIdAndClinicAppointItemId(appointId, organizationInfoDetail.getId());
                if (clinicAppointItem == null){
                    ClinicAppointItem appointItem = new ClinicAppointItem();
                    appointItem.setAppointItemId(appointId);
                    appointItem.setOrgId(organizationInfoDetail.getId());
                    appointItem.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    mapper.insertSelective(appointItem);
                }
            });
            return 1;
        }
        return 0;
    }

    /**
     * 批量查询门使用项目
     * @param list
     */
    public void insertClinicAppointItem(List<ClinicAppointItem> list) {
        mapper.insertClinicAppointItem(list);
    }

}
