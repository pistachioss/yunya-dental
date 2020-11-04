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
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.modules.discount.form.CouponAllocateDetailForm;
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
    @Autowired
    private CardBiz cardBiz;
    public int insertAll(List<CouponAllocateForm> list){
        return mapper.insertAll(list);
    }

    public List<CouponAllocateVO> findVOList(Integer id){
        //获取用户信息
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(false);
        List<Integer> orgIds = new ArrayList<>();
        model.setOrgIds(orgIds);
        Byte[]userStatus = {0,1,3};
        //离职状态
        model.setWorkStatus(userStatus);
        List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        Map<String, SysUserInfoDetail> employeesMap = new HashMap(16);
        employees.forEach(z -> employeesMap.put(z.getUserId() + "", z));
        //获取配给信息
        List<CouponAllocateVO>list = mapper.findVOList(id);
        for(CouponAllocateVO couponAllocateVO:list){
            SysUserInfoDetail sysUserInfoDetail = employeesMap.get(couponAllocateVO.getCrtId() + "");
            if(sysUserInfoDetail!=null){
                //设置分配人信息
                couponAllocateVO.setAllocateUserName(sysUserInfoDetail.getName());
            }else{
                throw new BaseException("无此分配人信息", OperationCodeConstants.DATA_NOT_EXIST);
            }
            Card card = new Card();
            card.setCouponAllocateId(couponAllocateVO.getId());
            couponAllocateVO.setIsAllocate(false);
            if (cardBiz.selectList(card).size() > 0) {
                couponAllocateVO.setIsAllocate(true);
            }
        }
        return list;
    }

    public  List<CouponAllocateDetailVO> findVODetailList(CouponAllocateDetailForm couponAllocateDetailForm){

        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));
        //获取配给详情
        List<CouponAllocateDetailVO>list =  mapper.findVODetailList(couponAllocateDetailForm);
        for(CouponAllocateDetailVO couponAllocateDetailVO:list){
            OrganizationInfoDetail organizationInfoDetail = clinicMap.get(couponAllocateDetailVO.getOrgId() + "");
            if(organizationInfoDetail!=null){
                //设置门诊信息
                couponAllocateDetailVO.setOrgName(organizationInfoDetail.getName());
            }else{
                throw new BaseException("无此门诊信息", OperationCodeConstants.DATA_NOT_EXIST);
            }
        }

        return list;
    }

}
