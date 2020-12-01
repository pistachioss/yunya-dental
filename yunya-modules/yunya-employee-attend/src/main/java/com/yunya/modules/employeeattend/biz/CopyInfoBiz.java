package com.yunya.modules.employeeattend.biz;

import com.yunya.feign.employee_attend.vo.CopyInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.employee_attend.CopyInfo;
import com.yunya.modules.employeeattend.form.CopyInfoForm;
import com.yunya.modules.employeeattend.mapper.CopyInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CopyInfoBiz extends BaseBiz<CopyInfoMapper, CopyInfo> {
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    public List<CopyInfoVO> findList(CopyInfoForm copyInfoForm){
        CopyInfo copyInfo =  new CopyInfo();
        BeanUtils.copyProperties(copyInfoForm, copyInfo);
        List<CopyInfo>reList = mapper.select(copyInfo);
        List<CopyInfoVO>list = new ArrayList();
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for(CopyInfo c:reList){
                CopyInfoVO copyInfoVO = new CopyInfoVO();
                BeanUtils.copyProperties(c, copyInfoVO);
                copyInfoVO.setUserName(emMap.get(c.getUserId()+"").getName());
                list.add(copyInfoVO);
            }
        }
        return list;
    }

}
