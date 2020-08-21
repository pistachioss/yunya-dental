package com.yunya.modules.discount.biz;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.form.CouponAllocateForm;
import com.yunya.modules.discount.vo.CouponAllocateDetailVO;
import com.yunya.modules.discount.vo.CouponAllocateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杨柳絮
 * @className CouponAllocateBiz
 * @description
 * @date 2020/8/21 9:58
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CouponAllocateBiz extends BaseBiz<CouponAllocateMapper, CouponAllocate> {
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    public int insertAll(List<CouponAllocateForm> list){
        return mapper.insertAll(list);
    }

    public List<CouponAllocateVO> findVOList(Integer id){
        SysUserEmployeeModel model = new SysUserEmployeeModel();//获取用户信息
        model.setWhetherPage(false);
        List<Integer> orgIds = new ArrayList<>();
        model.setOrgIds(orgIds);
        model.setWorkStatus(BusinessConstants.USER_RESIGNATION_STATUS);//离职状态
        List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        Map<String, SysUserInfoDetail> employeesMap = new HashMap();
        employees.forEach(z -> employeesMap.put(z.getUserId() + "", z));

        List<CouponAllocateVO>list = mapper.findVOList(id);//获取配给信息
        for(CouponAllocateVO couponAllocateVO:list){
            SysUserInfoDetail sysUserInfoDetail = employeesMap.get(couponAllocateVO.getAllocateUserId() + "");
            if(sysUserInfoDetail!=null){
                couponAllocateVO.setAllocateUserName(sysUserInfoDetail.getName());//设置分配人信息
            }else{
                throw new BaseException("无此分配人信息", OperationCodeConstants.DATA_NOT_EXIST);
            }
        }
        return list;
    }

    public  List<CouponAllocateDetailVO> findVODetailList(CouponAllocateVO couponAllocateVO){

        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

        List<CouponAllocateDetailVO>list =  mapper.findVODetailList(couponAllocateVO);//获取配给详情
        for(CouponAllocateDetailVO couponAllocateDetailVO:list){
            OrganizationInfoDetail organizationInfoDetail = clinicMap.get(couponAllocateDetailVO.getOrgId() + "");
            if(organizationInfoDetail!=null){
                couponAllocateDetailVO.setOrgName(organizationInfoDetail.getName());//设置们正信息
            }else{
                throw new BaseException("无此门诊信息", OperationCodeConstants.DATA_NOT_EXIST);
            }
        }

        return list;
    }

}
