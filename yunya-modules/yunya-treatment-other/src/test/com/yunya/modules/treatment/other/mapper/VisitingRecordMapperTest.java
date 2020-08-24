package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: yunya-dental
 * @description: 随访记录接口测试
 * @author: LHB
 * @create: 2020-08-24 11:01
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class VisitingRecordMapperTest {
    @Autowired
    private VisitingRecordMapper visitingRecordMapper;

    public void findVisitingRecordByIdTest(){
        VisitingRecordVo visitingRecordVo = visitingRecordMapper.findVisitingRecordById(3);
        System.out.println(visitingRecordVo.toString());
    }
}
