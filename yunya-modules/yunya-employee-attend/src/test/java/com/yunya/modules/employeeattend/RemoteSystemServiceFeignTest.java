package com.yunya.modules.employeeattend;

import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
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
    @Autowired
    private PatientCentralServiceFeign patientCentralServiceFeign;

    @Test
    public void testFindOrgInfoInIds() {
        List<Integer> orgIds = Arrays.asList(21, 22, 23, 24, 30);
        List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
        System.out.println(organizationInfoDetails);
    }
}
