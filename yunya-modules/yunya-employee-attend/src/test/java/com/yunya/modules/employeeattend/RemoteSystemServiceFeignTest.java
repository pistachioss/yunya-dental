package com.yunya.modules.employeeattend;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/5 10:14
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RemoteSystemServiceFeignTest {
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Test
    public void testFindOrgInfoInIds() {
        List<Integer> orgIds = Arrays.asList(21, 22, 23, 24, 30);
        List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
        System.out.println(organizationInfoDetails);
    }

    @Test
    public void testFindSysUserEmployeeListInIds() {
        Integer[] ids = new Integer[56];
        for (int i = 514; i < 570; i++) {
            ids[i-514] = i;
        }
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(true);
        model.setPageNum(1);
        model.setPageSize(5);
        model.setUserIds(ids);
        List<SysUserInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        System.out.println(organizationInfoDetails);
    }
}
